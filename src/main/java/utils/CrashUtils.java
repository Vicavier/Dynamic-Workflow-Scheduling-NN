package utils;

import controller.impl.AbstractPopulationController;
import entity.Chromosome;
import entity.DataPool;
import entity.Task;
import entity.Type;
import io.jenetics.util.ProxySorter;
import service.io.Output;
import service.io.impl.ChartOutputImpl;

import java.awt.*;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CrashUtils {
    public static final HashSet<Integer> generations = new HashSet<>();
    private static final double severity;

    static {
        String generation = ConfigUtils.get("ins.crash.generation");
        if (!generation.equals("-1")) {
            String[] generations = generation.split(",");
            for (String num : generations) {
                CrashUtils.generations.add(Integer.parseInt(num));
            }
        }
        severity = Double.parseDouble(ConfigUtils.get("ins.crash.severity"));
    }

    public static void commonCrash(int generation, List<Chromosome> list) {
        if (!generations.contains(generation)) return;
        randomShutdownMachine();
        StringBuilder sb = new StringBuilder();
        for (Chromosome c : list) {
            sb.append(Arrays.toString(c.getTask())).append(" ").append(Arrays.toString(c.getTask2ins())).append("\n");
        }
        for (int x = 0; x < list.size(); ++x) {
            boolean flag = false;
            Chromosome chromosome = list.get(x);
            for (int i = 0; i < chromosome.getTask2ins().length; ++i) {
                if (DataPool.disabledIns.contains(chromosome.getTask2ins()[i])) {
                    chromosome.getTask2ins()[i] = DataPool.accessibleIns.get(DataPool.random.nextInt(DataPool.accessibleIns.size()));
                    flag = true;
                }
            }
//            if (flag)
//                System.out.println("出问题的基因： "+x);
            DataUtils.refresh(chromosome);
        }
        sb.append("------------------------");
        for (Chromosome c : list) {
            sb.append(Arrays.toString(c.getTask())).append(" ").append(Arrays.toString(c.getTask2ins())).append("\n");
        }
//        WriterUtils.write("src\\main\\resources\\output\\kk.txt", sb.toString());
    }

    public static void totalInsteadCrash(int generation, List<Chromosome> list) {
        if (!generations.contains(generation)) return;
        randomShutdownMachine();
        for (int x = 0; x < list.size(); ++x) {
            Chromosome chromosome = list.get(x);
            for (int i = 0; i < chromosome.getTask2ins().length; ++i) {
                if (DataPool.disabledIns.contains(chromosome.getTask2ins()[i])) {
                    list.set(x, DataUtils.getInitialChromosome());
                    break;
                }
            }
            DataUtils.refresh(list.get(x));
        }
    }

    public static void restartCrash(int generation, List<Chromosome> list, AbstractPopulationController controller) {
        if (!generations.contains(generation)) return;
        randomShutdownMachine();
        controller.getFa().clear();
        controller.getSon().clear();
        controller.getRank().clear();
        controller.doInitial();
    }

    public static void similarityCrash(int generation, List<Chromosome> list) {
        if (!generations.contains(generation)) return;
        randomShutdownMachine();
        for (int x = 0; x < list.size(); ++x) {
            Chromosome chromosome = list.get(x);
            for (int i = 0; i < chromosome.getTask2ins().length; ++i) {
                if (DataPool.disabledIns.contains(chromosome.getTask2ins()[i])) {
                    int similar = getMostSimilarIns(chromosome.getTask2ins()[i], chromosome);
                    for (int j = 0; j < chromosome.getTask2ins().length; ++j) {
                        if (chromosome.getTask2ins()[i] == chromosome.getTask2ins()[j]) {
                            chromosome.getTask2ins()[j] = similar;
                        }
                    }
                }
            }
            DataUtils.refresh(list.get(x));
        }
    }

    public static void nnNSGAIICrash(int generation, List<Chromosome> list) {
        if (!generations.contains(generation)) return;
        randomShutdownMachine();
        //TODO:
        List<Chromosome> n_1 = DataPool.all.get(DataPool.all.size() - 2);   //第n-1代帕累托前沿
        List<Chromosome> n = DataPool.all.get(DataPool.all.size() - 1);     //第n代帕累托前沿
        List<List<Chromosome>> generations = new ArrayList<>();
        generations.add(n_1);
        generations.add(n);
        StringBuilder fronts = new StringBuilder();
        for (Chromosome c : n_1) {
            fronts.append(c.getMakeSpan()).append(";").append(c.getCost()).append(";").append(Arrays.toString(c.getTask2ins())).append("\n");
        }
        fronts.append("$");
        for (Chromosome c_ : n) {
            fronts.append(c_.getMakeSpan()).append((";")).append(c_.getCost()).append(";").append(Arrays.toString(c_.getTask2ins())).append("\n");
        }
        fronts.append("#");

        int min_index = (n_1.size() < n.size()) ? 0 : 1;
        List<Chromosome> g3 = new ArrayList<>();
        for (int i = 0; i < generations.get(min_index).size(); i++) {
            try {
                g3.add(generations.get(min_index).get(i).clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        generations.add(g3);

        try {

            Socket socket = new Socket("localhost", 8000);
//            System.out.println("[INFO]连接成功:" + socket.getInetAddress());

            // 获取输出流和输入流
            OutputStream out = socket.getOutputStream();
//            System.out.println("[INFO]发送第n-1个POF");
            out.write(fronts.toString().getBytes());
            out.flush();
//            System.out.println("[INFO]数据已发送");


//            接收python神经网络传回的预测结果
//            System.out.println("[INFO]等待接收数据...");
            InputStreamReader in = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(in);
//            System.out.println("[INFO]接收到数据：");
            for (int i = 0; i < generations.get(2).size(); i++) {
                String[] recvData = reader.readLine().trim().split(" ");
                generations.get(2).get(i).setTask2ins(str2Int(recvData));
                DataUtils.refresh(generations.get(2).get(i));
            }
            String javaRecv = "ok";
            out.write(javaRecv.getBytes());
            out.flush();
            int size = list.size();
//            System.out.println("list length:"+size);
//            System.out.println("g3 length"+g3.size());
            //--------------------------原论文思路----------------------------
            for (Chromosome c:g3){
                list.add(c.clone());
            }
            while(list.size()>size){
                list.remove(DataPool.random.nextInt(list.size()));
            }
            //--------------------------------------------------------------

            //--------------------------多种群-------------------------------
            for (int x = 0; x < list.size(); ++x) {
                Chromosome chromosome = list.get(x);
                for (int i = 0; i < chromosome.getTask2ins().length; ++i) {
                    if (DataPool.disabledIns.contains(chromosome.getTask2ins()[i])) {
                        list.set(x, DataUtils.getInitialChromosome());
                        break;
                    }
                }
                DataUtils.refresh(list.get(x));
            }
//
//            List<List<Chromosome>> rank;
//            List<Chromosome> hugeList = new ArrayList<>();
//            hugeList.addAll(list);
//            hugeList.addAll(g3);
//
//            for (Chromosome chromosome : hugeList) {
//                chromosome.setBetterNum(0);
//                chromosome.setPoorNum(0);
//                chromosome.getPoor().clear();
//                chromosome.getBetter().clear();
////            DataUtils.refresh(chromosome);
//            }
//            for (int i = 0; i < hugeList.size(); ++i) {
//                Chromosome chromosome = hugeList.get(i);
//                for (int j = i + 1; j < hugeList.size(); ++j) {
//                    Chromosome other = hugeList.get(j);
//                    if (chromosome.getMakeSpan() >= other.getMakeSpan()
//                            && chromosome.getCost() >= other.getCost()) {
//                        if (chromosome.getMakeSpan() - other.getMakeSpan() > 0.0000000001
//                                || chromosome.getCost() - other.getCost() > 0.0000000001) {
//                            setBetterAndPoor(other, chromosome);
//                        }
//                    }
//                    if (chromosome.getMakeSpan() <= other.getMakeSpan()
//                            && chromosome.getCost() <= other.getCost()
//                    ) {
//                        if ((chromosome.getMakeSpan() - other.getMakeSpan()) < -0.000000001
//                                || chromosome.getCost() - other.getCost() < -0.000000001
//                        ) {
//                            setBetterAndPoor(chromosome, other);
//                        }
//                    }
//                }
//            }
//            rank = new LinkedList<>();
//            while (hasBetter(hugeList)) {
//                LinkedList<Chromosome> rankList = new LinkedList<>();
//                List<Chromosome> temp = new LinkedList<>();
//                for (Chromosome chromosome : hugeList) {
//                    if (chromosome.getBetterNum() == 0) {
//                        chromosome.reduceBetter();
//                        rankList.add(chromosome);
//                        temp.add(chromosome);
//                    }
//                }
//                for (Chromosome chromosome : temp) {
//                    for (Chromosome worse : chromosome.getPoor()) {
//                        worse.reduceBetter();
//                    }
//                }
//                rank.add(rankList);
//            }
//            int cnt = 0;
//            l:for (int i = 0; i < rank.size(); i++) {
//                for (int j = 0; j < rank.get(i).size(); j++) {
//                    list.set(cnt,rank.get(i).get(j));
//                    cnt++;
//                    if (cnt == list.size())
//                        break l;
//                }
//            }
            //----------------------画图--------------------------
//            generations.remove(0);
//            generations.remove(0);
//            generations.remove(0);
//            generations.add(list);
//            Output output = new ChartOutputImpl();
//            output.output(generations);

            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int[] str2Int(String[] recvData) {
        int[] task2ins = new int[recvData.length];
        for (int i = 0; i < recvData.length; i++) {
            int No_ins = Integer.parseInt(recvData[i]);
            if (No_ins < 0) No_ins = 0;
            if (No_ins > 79) No_ins = 79;

            while (DataPool.disabledIns.contains(No_ins))
                No_ins = ((int)(No_ins / 10)) * 10 + DataPool.random.nextInt(10);
            task2ins[i] = No_ins;
        }
        return task2ins;
    }

    public static void setBetterAndPoor(Chromosome better, Chromosome poor) {
        better.getPoor().add(poor);
        poor.getBetter().add(better);
        better.addPoor();
        poor.addBetter();
    }

    public static boolean hasBetter(List<Chromosome> list) {
        for (Chromosome chromosome : list) {
            if (chromosome.getBetterNum() >= 0) return true;
        }
        return false;
    }

    public static int getMostSimilarIns(int ins, Chromosome chromosome) {
        int type = DataPool.insToType.get(ins);
        Type currType = DataPool.types[type];
        double cu = currType.cu * DataPool.weightVector[0];
        double bw = currType.bw * DataPool.weightVector[1];
        double p = currType.p * DataPool.weightVector[2];
        double maxWorkload = 0;
//        double[] workload = new double[chromosome.getTask2ins().length];
//        double start = chromosome.launchTime[ins];
//        for(int i=0;i<chromosome.getTask2ins().length;++i){
//            if(chromosome.shutdownTime[i]<start) workload[i] = 0;
//            else wor
//        }

        List<Pair> list = new ArrayList<>();
        for (int num : DataPool.accessibleIns) {
            Type insType = DataPool.types[DataPool.insToType.get(num)];
            Pair pair = new Pair();
            pair.cu = insType.cu * DataPool.weightVector[0];
            pair.bw = insType.bw * DataPool.weightVector[1];
            pair.p = insType.p * DataPool.weightVector[2];
            pair.ins = num;
            double workLoad = 0;
            for (int x = 0; x < chromosome.getTask2ins().length; ++x) {
                Task task = DataPool.tasks[x];
                if (chromosome.getTask2ins()[x] == num) {
                    workLoad += task.getReferTime();
                }
            }
            maxWorkload = Math.max(maxWorkload, workLoad);
            list.add(pair);
        }

        list.sort(Comparator.comparingDouble(o -> (Math.pow(Math.pow(Math.abs(cu - o.cu), 3) + Math.pow(Math.abs(bw - o.bw), 3) + Math.pow(Math.abs(p - o.p), 3), 1.0 / 3))));
//        list.sort(new Comparator<Pair>() {
//            @Override
//            public int compare(Pair o1, Pair o2) {
//                double o_1 = Math.pow(Math.pow(Math.abs(cu-o1.cu),3)+Math.pow(Math.abs(bw-o1.bw),3)+Math.pow(Math.abs(p-o1.p),3),1.0/3);
//                double o_2 = Math.pow(Math.pow(Math.abs(cu-o2.cu),3)+Math.pow(Math.abs(bw-o2.bw),3)+Math.pow(Math.abs(p-o2.p),3),1.0/3);
//                if(Math.abs(o_1-o_2)<0.0000001){
//                    return Double.compare(o1.workload,o2.workload);
//                }
//                return Double.compare(o_1,o_2);
//            }
//        });


        return list.get(0).ins;
    }

    public static int getMinLoadIns(Chromosome chromosome) {
        List<Pair> list = new ArrayList<>();
        for (int num : DataPool.accessibleIns) {
            int workLoad = 0;
            Pair pair = new Pair();
            for (int x : chromosome.getTask2ins()) {
                if (x == num) workLoad++;
            }
            pair.ins = num;
            pair.workload = workLoad;
            list.add(pair);
        }
        list.sort(Comparator.comparingDouble(o -> o.workload));
        return list.get(0).ins;
    }

    private static void randomShutdownMachine() {
        int num = (int) (DataPool.accessibleIns.size() * severity);
        for (int i = 0; i < num; ++i) {
            int index = DataPool.random.nextInt(DataPool.accessibleIns.size());
            int ins = DataPool.accessibleIns.get(index);
            DataPool.accessibleIns.remove(index);
            DataPool.disabledIns.add(ins);
        }
    }


    static class Pair {
        int ins;
        double cu;
        double bw;
        double p;
        double workload;

    }
}
