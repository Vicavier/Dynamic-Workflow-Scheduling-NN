package service.multi;

import controller.impl.NSGAIIPopulationController;
import entity.Chromosome;
import entity.DataPool;
import service.crash.SimilarityFixedCrash;
import utils.ConfigUtils;
import utils.CrashUtils;
import utils.InitUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultiPopulationFixed {
    public static NSGAIIPopulationController DA;
    public static void main(String[] args) {
        for(int i=0;i<30;++i){
            runPopulationFixed(i);
        }
    }

    public static void runPopulationFixed(int i) {
        DataPool.clear();
        DataPool.random = new Random(i);
        InitUtils.init();
        NSGAIIPopulationController controller = new NSGAIIPopulationController();
        controller.crash = new SimilarityFixedCrash();
        int generation = Integer.parseInt(ConfigUtils.get("evolution.population.generation"));
        controller.doInitial();

        for(int k=0;k<generation;++k){
            if(CrashUtils.generations.contains(k)){
                controller.isChanged = true;
                controller.setSize(Integer.parseInt(ConfigUtils.get("evolution.population.size2")));
                DA = new NSGAIIPopulationController();
                DA.setSize(Integer.parseInt(ConfigUtils.get("evolution.population.size2")));
                DA.doInitial();
                if (controller.getFa().size() == Integer.parseInt(ConfigUtils.get("evolution.population.size"))){
                    List<Chromosome> newFa = new ArrayList<>();
                    int [] chosen = new int[Integer.parseInt(ConfigUtils.get("evolution.population.size"))];
                    while(newFa.size()<Integer.parseInt(ConfigUtils.get("evolution.population.size2"))) {
                        try {
                            int temp = DataPool.random.nextInt(Integer.parseInt(ConfigUtils.get("evolution.population.size")));
                            if (chosen[temp] == 0){
                                chosen[temp] = 1;
                                newFa.add(controller.getFa().get(temp).clone());
                            }
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    controller.setFa(newFa);
                }
            }
            controller.iterateACycle(k,DataPool.all);
            if(DA!=null) DA.iterateACycle(0,null);
        }
        DA = null;

//        Output output = new FileOutputImpl();
//        output.output(list);
//            Output output = new ConsoleOutputImpl();
//            output.output(list);
//            String str = DataUtils.operateHV(DataPool.all);
//            WriterUtils.write("src\\main\\resources\\output\\Similarity_Crash_" + i + ".txt", str);
    }

    public static NSGAIIPopulationController getDA(){
        return DA;
    }
}
