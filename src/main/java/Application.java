import controller.DynamicSimulation;
import controller.PopulationController;
import controller.impl.GaussianDynamicSimulation;
import controller.impl.NSGAIIPopulationController;
import entity.Chromosome;
import entity.DataPool;
import entity.Type;
import service.io.Input;
import service.io.Output;
import service.io.impl.ChartOutputImpl;
import service.io.impl.ConsoleOutputImpl;
import service.io.impl.FileOutputImpl;
import service.io.impl.XMLInputImpl;
import utils.DataUtils;
import utils.DynamicUtils;
import utils.InitUtils;
import utils.WriterUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Application {
    public static void main(String[] args) {

        InitUtils.init();
        PopulationController controller = new NSGAIIPopulationController();
        List<List<Chromosome>> list=controller.iterate();
        Output output = new ConsoleOutputImpl();
        output.output(list);
        List<Chromosome> front = list.get(0);
        DataUtils.operateHV(DataPool.all);
//        List<List<Chromosome>> list = controller.rankReturnIterate(400,500);
//        DynamicSimulation sim = new GaussianDynamicSimulation();
//        Output output=new ChartOutputImpl();
//        output.output(list);

//        StringBuilder str1 = new StringBuilder();
//        StringBuilder str2 = new StringBuilder();
//        for(Chromosome chromosome:list.get(0)){
//            str1.append(chromosome.getMakeSpan()).append(" ").append(chromosome.getCost()).append("\n");
//        }
//        for(Chromosome chromosome:list.get(1)){
//            str2.append(chromosome.getMakeSpan()).append(" ").append(chromosome.getCost()).append("\n");
//        }
//        WriterUtils.write("src\\main\\resources\\output\\500_static.txt",str1.toString());
//        WriterUtils.write("src\\main\\resources\\output\\400_static.txt",str2.toString());


//        for(Chromosome chromosome:list.get(0)){
//            chromosome.setCost(0);
//            chromosome.setMakeSpan(0);
//        }
//
//        for(Chromosome chromosome:list.get(1)){
//            chromosome.setMakeSpan(0);
//            chromosome.setCost(0);
//        }
//
//        sim.sim(list.get(0),list.get(1));



//        List<Chromosome> front = new LinkedList<>();

//        for(int i=0;i<1;++i) {

//        for(int i=0;i<15;++i) {
//            PopulationController controller = new NSGAIIPopulationController();
//            List<List<Chromosome>> list = controller.iterate();
////            front.addAll(list.get(0));
////        }
//
////
////        front.forEach(x->x.setBetterNum(0));
////
////        for (int i = 0; i < front.size(); ++i) {
////            Chromosome chromosome = front.get(i);
////            for (int j = i + 1; j < front.size(); ++j) {
////                Chromosome other = front.get(j);
////                if (chromosome.getMakeSpan() > other.getMakeSpan()
////                        && chromosome.getCost() > other.getCost()
////                        && chromosome.getUtilization() > other.getUtilization()
////                        && chromosome.getDegreeImbalance() > other.getDegreeImbalance()) {
////                    if((chromosome.getMakeSpan() - other.getMakeSpan()) > 0.000000001
////                            || chromosome.getCost() - other.getCost() > 0.000000001
////                            || chromosome.getUtilization() - other.getUtilization() > 0.000000001
////                            || chromosome.getDegreeImbalance() - other.getDegreeImbalance() > 0.000000001){
////                        setBetterAndPoor(other,chromosome);
////                    }
////                }
////                if (chromosome.getMakeSpan() < other.getMakeSpan()
////                        && chromosome.getCost() < other.getCost()
////                        && chromosome.getUtilization() < other.getUtilization()
////                        && chromosome.getDegreeImbalance() < other.getDegreeImbalance()) {
////                    if((chromosome.getMakeSpan() - other.getMakeSpan()) < -0.000000001
////                            || chromosome.getCost() - other.getCost() < -0.000000001
////                            || chromosome.getUtilization() - other.getUtilization() < -0.000000001
////                            || chromosome.getDegreeImbalance() - other.getDegreeImbalance() < -0.000000001){
////                        setBetterAndPoor(chromosome,other);
////                    }
////                }
////            }
////        }
////        List<Chromosome> ans = new LinkedList<>();
////        double makeSpanMax = 0;
////        double costMax = 0;
////        double utilizationMax = 0;
////        double DIMax = 0;
////        double makeSpanMin = Double.MAX_VALUE;
////        double costMin = Double.MAX_VALUE;
////        double utilizationMin = Double.MAX_VALUE;
////        double DIMin = Double.MAX_VALUE;
////        for(Chromosome chromosome:front){
////            if(chromosome.getBetterNum()==0) ans.add(chromosome);
////            double makeSpan = chromosome.getMakeSpan();
////            double cost = chromosome.getCost();
////            double utilization = chromosome.getUtilization();
////            double DI = chromosome.getDegreeImbalance();
////
////            makeSpanMax = Math.max(makeSpan, makeSpanMax);
////            costMax = Math.max(cost, costMax);
////            utilizationMax = Math.max(utilization, utilizationMax);
////            DIMax = Math.max(DI, DIMax);
////
////            makeSpanMin = Math.min(makeSpan, makeSpanMin);
////            costMin = Math.min(cost, costMin);
////            utilizationMin = Math.min(utilization, utilizationMin);
////            DIMin = Math.min(DI, DIMin);
////        }
////
////        for (int i = 0; i < ans.size(); ++i) {
////            Chromosome chromosome = ans.get(i);
////            double makeSpan = chromosome.getMakeSpan();
////            double cost = chromosome.getCost();
////            double utilization = chromosome.getUtilization();
////            double DI = chromosome.getDegreeImbalance();
////            chromosome.setMakeSpan((makeSpan - makeSpanMin) / (makeSpanMax - makeSpanMin));
////            chromosome.setCost((cost - costMin) / (costMax - costMin));
////            chromosome.setUtilization((utilization - utilizationMin) / (utilizationMax - utilizationMin));
////            chromosome.setDegreeImbalance((DI - DIMin) / (DIMax - DIMin));
////        }
////        StringBuilder stringBuilder=new StringBuilder();
////        ans.forEach(x->stringBuilder.append(x.getMakeSpan()).append(" ").append(x.getCost()).append(" ").append(x.getUtilization()).append(" ").append(x.getDegreeImbalance()).append("\n"));
////        Output output1=new ConsoleOutputImpl();
////        Output output2=new FileOutputImpl();
////        output1.output(front);
////        output2.output(list);
////        WriterUtils.write("src\\main\\resources\\output\\avg.txt",stringBuilder.toString());
//
//            WriterUtils.write("src\\main\\resources\\output\\HV"+i+".txt", DataUtils.operateHV(DataPool.all));
//            DataPool.all=new LinkedList<>();
//        System.out.println(DataUtils.getHV(500,100,list.get(0)));
//        }
    }

    public static void setBetterAndPoor(Chromosome better, Chromosome poor) {
        better.getPoor().add(poor);
        poor.getBetter().add(better);
        better.addPoor();
        poor.addBetter();
    }
}
