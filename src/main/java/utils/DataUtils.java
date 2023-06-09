package utils;

import entity.*;
import service.algorithm.impl.NSGAII;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DataUtils {

    public static double getMakeSpan(Chromosome chromosome) {
        double[] availableTime = new double[DataPool.insNum];
        double exitTime = 0;
        for (int taskIndex : chromosome.getTask()) {
            Task task = DataPool.tasks[taskIndex].clone();
            int insIndex = chromosome.getTask2ins()[taskIndex];
            int typeIndex = DataPool.insToType.get(insIndex);
            if (task.getPredecessor().size() == 0) {
                chromosome.getStart()[taskIndex] = Math.max(0, availableTime[insIndex]);
                chromosome.getEnd()[taskIndex] = chromosome.getStart()[taskIndex] + task.getReferTime() / DataPool.types[typeIndex].cu;
                availableTime[insIndex] = chromosome.getEnd()[taskIndex];
            } else {
                chromosome.getStart()[taskIndex] = Math.max(getStartTime(chromosome, taskIndex), availableTime[insIndex]);
                chromosome.getEnd()[taskIndex] = chromosome.getStart()[taskIndex] + DataPool.tasks[taskIndex].getReferTime() / DataPool.types[typeIndex].cu;
                availableTime[insIndex] = chromosome.getEnd()[taskIndex];
            }
            if (chromosome.launchTime[insIndex] == 0) {
                chromosome.launchTime[insIndex] = chromosome.getStart()[taskIndex];
            }
            chromosome.shutdownTime[insIndex] = chromosome.getEnd()[taskIndex];
            if (task.getSuccessor().size() == 0) {
                exitTime = Math.max(exitTime, chromosome.getEnd()[taskIndex]);
            }
        }
        return exitTime;
    }

    public static double getCost(Chromosome chromosome) {
        double sum = 0;
        int[] ins_flags = new int[DataPool.insNum];
        for (int ins_index : chromosome.getTask2ins()) {
            if (ins_flags[ins_index] == 0) {
                int type_index =  DataPool.insToType.get(ins_index);
                int hours = (int) ((chromosome.shutdownTime[ins_index] - chromosome.launchTime[ins_index]) / 3600) + 1;
                sum += hours * DataPool.types[type_index].p;
                ins_flags[ins_index] = 1;
            }
        }
//        int[] ins_flags = new int[DataPool.insNum];
//        for (int ins_index : chromosome.getTask2ins()){
//            if(ins_flags[ins_index] == 0){
//                int type_index = chromosome.getIns2type()[ins_index];
//                sum += (chromosome.shutdownTime[ins_index] - chromosome.launchTime[ins_index])/3600 * DataPool.types[type_index].p;
//                ins_flags[ins_index] = 1;
//            }
//        }


        return sum;
    }

    public static void refresh(Chromosome chromosome) {
        chromosome.setMakeSpan(getMakeSpan(chromosome));
        chromosome.setCost(getCost(chromosome));
    }

    @Deprecated
    public static Chromosome getBetter(Chromosome chromosome1, Chromosome chromosome2) throws CloneNotSupportedException {

        Chromosome c1 = chromosome1.clone();
        Chromosome c2 = chromosome2.clone();

        NSGAII.crossoverOrder(c1, c2);
        NSGAII.crossoverIns(c1, c2);

        if (DataPool.random.nextInt(10000) < Double.parseDouble(ConfigUtils.get("evolution.population.mutation")) * 10000) {
            DataPool.nsgaii.mutate(c1);
            DataPool.nsgaii.mutate(c2);
        }
        DataUtils.refresh(c1);
        DataUtils.refresh(c2);

        if (c1.getCost() - c2.getCost() < -0.0001 && c1.getMakeSpan() - c2.getMakeSpan() < -0.0001) {
            return c1;
        }

        if (c2.getCost() - c1.getCost() < -0.0001 && c2.getMakeSpan() - c1.getMakeSpan() < -0.0001) {
            return c2;
        } else return DataPool.random.nextInt(2) == 0 ? c1 : c2;

    }
    public static Chromosome getRandomChromosome() {
        int[] taskOrder = DataUtils.getRandomTopologicalSorting();
        Chromosome chromosome = new Chromosome();
        chromosome.setTask(taskOrder);
        chromosome.setTask2ins(NSGAII.getRandomInstance(taskOrder.length));
        DataUtils.refresh(chromosome);
        return chromosome;
    }
    public static Chromosome getInitialChromosome() {
        int[] order = DataUtils.getRandomTopologicalSorting();
        int[] ins = new int[order.length];
        int num = DataPool.random.nextInt(10);
        if (num < 5) {
            for (int i = 0; i < ins.length; ++i) {
                ins[i] = DataPool.accessibleIns.get(DataPool.random.nextInt(DataPool.accessibleIns.size()));
            }
        }else {
            int insNum = DataPool.accessibleIns.get(DataPool.random.nextInt(DataPool.accessibleIns.size()));
            Arrays.fill(ins, insNum);
        }
        Chromosome chromosome = new Chromosome(order, ins);
        DataUtils.refresh(chromosome);
        return chromosome;
    }
    public static int[] getRandomTopologicalSorting() {
        TaskGraph graph = DataPool.graph.clone();
        return graph.TopologicalSorting();
    }
    /**
     * Utilization: the ratio of total working times of resources
     * to serviceable times of resources
     *
     * @param chromosome
     * @return utilization
     */
    public static double getUtilization(Chromosome chromosome) {
        // the total processing times of all the tasks
        // the total serviceable times of resources
        double processing_times = 0;
        double serviceable_times = 0;
        for (int i = 0; i < chromosome.getTask().length; i++) {
            int task = chromosome.getTask()[i];
            int instance = chromosome.getTask2ins()[task];
            int type =  DataPool.insToType.get(instance);
            double cu = DataPool.types[type].cu;
            double referTime = DataPool.tasks[task].getReferTime();
            double actualTime = referTime / cu;
            processing_times += actualTime;
        }
        for (int i = 0; i < chromosome.getTask2ins().length; i++) {
            serviceable_times += chromosome.shutdownTime[i] - chromosome.launchTime[i];
        }
        return -processing_times / serviceable_times;
    }

    /**
     * Degree Imbalance: states the balance of task workloads among all available resources.
     * The degree of imbalance with utilization objective may lead to more accurate assessment
     * of the performance of a cloud system
     *
     * @param chromosome
     * @return
     */
    public static double getDegreeImbalance(Chromosome chromosome) {
        double[] imbalanceDegree = new double[chromosome.getTask().length];
        for (int i = 0; i < chromosome.getTask().length; i++) {
            int task = chromosome.getTask()[i];
            int instance = chromosome.getTask2ins()[task];
            int type =  DataPool.insToType.get(instance);
            Type t = DataPool.types[type];
            imbalanceDegree[instance] += DataPool.tasks[task].getReferTime() / t.cu;
        }
        double DImax = 0;
        double DImin = Double.MAX_VALUE;
        double DIsum = 0;
        int size = 0;
        for (double DI : imbalanceDegree) {
            if (DI != 0) {
                if (DI > DImax) {
                    DImax = DI;
                }
                if (DI < DImin) {
                    DImin = DI;
                }
                DIsum += DI;
                size++;
            }
        }
        double DIavg = DIsum / size;
        return (DImax - DImin) / DIavg;

    }

    public static double getStartTime(Chromosome chromosome, int taskIndex) {
        List<Integer> preTaskIndexes = DataPool.tasks[taskIndex].getPredecessor();
        int n = preTaskIndexes.size();
        int instanceIndex = chromosome.getTask2ins()[taskIndex];
        int typeIndex =  DataPool.insToType.get(instanceIndex);
        double[] communicationTime = new double[n];
        double[] after_communicationTime = new double[n];
        for (int i = 0; i < n; i++) {
            int preTaskIndex = preTaskIndexes.get(i);
            int preInstanceIndex = chromosome.getTask2ins()[preTaskIndex];
            int preTypeIndex =  DataPool.insToType.get(preInstanceIndex);
            double bw_min = Math.min(DataPool.types[typeIndex].bw, DataPool.types[preTypeIndex].bw);
            communicationTime[i] = DataPool.tasks[preTaskIndex].getOutputSize() / bw_min;
            after_communicationTime[i] = chromosome.getEnd()[preTaskIndex] + communicationTime[i];
        }
        double max_after_communication_time = 0;
        for (double time : after_communicationTime) {
            if (time > max_after_communication_time) {
                max_after_communication_time = time;
            }
        }
        return max_after_communication_time;
    }

//    public static double operateHV(List<Chromosome> list) {
//        PISAHypervolume hypervolume = new PISAHypervolume();
//        double[][] targets = new double[list.size()][4];
//        double[][] refer = new double[1][4];
//
//        double max0 = 0;
//        double max1 = 0;
//        double max2 = 0;
//        double max3 = 0;
//
//        for (int i = 0; i < list.size(); ++i) {
//            targets[i][0] = list.get(i).getMakeSpan();
//            max0 = Math.max(max0,targets[i][0]);
//            targets[i][1] = list.get(i).getCost();
//            max1 = Math.max(max1,targets[i][1]);
//            targets[i][2] = list.get(i).getUtilization();
//            max2 = Math.max(max2,targets[i][2]);
//            targets[i][3] = list.get(i).getDegreeImbalance();
//            max3 = Math.max(max3,targets[i][3]);
//        }
//
//        refer[0][0] = max0;
//        refer[0][1] = max1;
//        refer[0][2] = max2;
//        refer[0][3] = max3;
//
//        hypervolume.setReferenceFront(refer);
//        return hypervolume.calculateHypervolume(targets, list.size(), 4);
//    }

    public static String operateHV(List<List<Chromosome>> list) {
        double maxMakeSpan = 0;
        double maxCost = 0;
        double minMakeSpan = Integer.MAX_VALUE;
        double minCost = Integer.MAX_VALUE;
        //找到所有代中，最大和最小的值
        for(List<Chromosome> chromosomes:list){
            for(Chromosome chromosome:chromosomes){
                maxMakeSpan = Math.max(maxMakeSpan,chromosome.getMakeSpan());
                maxCost = Math.max(maxCost,chromosome.getCost());
                minMakeSpan = Math.min(minCost,chromosome.getMakeSpan());
                minCost = Math.min(minCost,chromosome.getCost());
            }
        }
        StringBuilder str = new StringBuilder();
        for(List<Chromosome> chromosomes:list){
            chromosomes.sort(Comparator.comparingDouble(Chromosome::getMakeSpan));
            double makespan[]=new double[chromosomes.size()];
            double cost[] = new double[chromosomes.size()];
            //按照makespan的顺序计算HV
            for(int i=0;i<chromosomes.size();++i){
                double makespan_i = chromosomes.get(i).getMakeSpan();
                double cost_i = chromosomes.get(i).getCost();
                makespan[i] = (makespan_i-minMakeSpan)/(maxMakeSpan-minMakeSpan);
                cost[i] = (cost_i-minCost)/(maxCost-minCost);
            }
            double HV = (1.1-makespan[0])*(1.1-cost[0]);
            for(int i=1;i<makespan.length;++i){
                HV+=(1.1-makespan[i])*(cost[i-1]-cost[i]);
            }
            str.append(HV).append("\n");
        }
//        WriterUtils.write("src\\main\\resources\\output\\ParetoFront.txt",str.toString());
        return str.toString();
    }

    public static String operateHV(List<List<Chromosome>> list,double maxMS,double minMS,double minC,double maxC){
        StringBuilder str = new StringBuilder();
        for(List<Chromosome> chromosomes:list){
            chromosomes.sort(Comparator.comparingDouble(Chromosome::getMakeSpan));
            double[] makespan =new double[chromosomes.size()];
            double[] cost = new double[chromosomes.size()];
            //按照makespan的顺序计算HV
            for(int i=0;i<chromosomes.size();++i){
                double makespan_i = chromosomes.get(i).getMakeSpan();
                double cost_i = chromosomes.get(i).getCost();
                makespan[i] = (makespan_i- minMS)/(maxMS - minMS);
                cost[i] = (cost_i- minC)/(maxC - minC);
            }
            double HV = (1.1-makespan[0])*(1.1-cost[0]);
            for(int i=1;i<makespan.length;++i){
                HV+=(1.1-makespan[i])*(cost[i-1]-cost[i]);
            }
            str.append(HV).append("\n");
        }
//        WriterUtils.write("src\\main\\resources\\output\\ParetoFront.txt",str.toString());
        return str.toString();
    }

    public static List<List<Chromosome>> cloneList(List<List<Chromosome>> list){
        try {
            List<List<Chromosome>> newList = new ArrayList<>();
            for(List<Chromosome> l:list){
                List<Chromosome> newL = new ArrayList<>();
                for(Chromosome chromosome:l){
                    newL.add(chromosome.clone());
                }
                newList.add(newL);
            }

            return newList;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


}
