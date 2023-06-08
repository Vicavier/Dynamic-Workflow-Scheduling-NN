package service.multi;

import entity.Chromosome;
import entity.DataPool;
import utils.DataUtils;
import utils.InitUtils;
import utils.WriterUtils;

import java.util.List;

public class MultiGlobalFixed {
    public static void main(String[] args) {
        for (int i = 0; i < 15; ++i) {
            InitUtils.init();
            System.out.println("--------------------repeat times:" + " " + i + "--------------------");
            System.out.println("----------cold start running----------");
            MultiColdStart.runColdStart(i);
            System.out.println("----------cold start finished----------");
            System.out.println("----------cloning paretoFront----------");
            List<List<Chromosome>> coldStart = DataUtils.cloneList(DataPool.all);
            System.out.println("----------cloning finished----------");

            System.out.println();

            System.out.println("----------partial fixed running----------");
            MultiCommonInstead.runCommonInstead(i);
            System.out.println("----------partial fixed finished----------");
            System.out.println("----------cloning paretoFront----------");
            List<List<Chromosome>> partialFixed = DataUtils.cloneList(DataPool.all);
            System.out.println("----------cloning finished----------");

            System.out.println();

            System.out.println("----------partial replace running----------");
            MultiPartialReplace.runPartialReplace(i);
            System.out.println("----------partial replace finished----------");
            System.out.println("----------cloning paretoFront----------");
            List<List<Chromosome>> partialReplace = DataUtils.cloneList(DataPool.all);
            System.out.println("----------cloning finished----------");

            System.out.println();

            System.out.println("----------similarity fixed running----------");
            MultiSimilarityReplace.runSimilarityReplace(i);
            System.out.println("----------similarity fixed finished----------");
            System.out.println("----------cloning paretoFront----------");
            List<List<Chromosome>> similarityFixed = DataUtils.cloneList(DataPool.all);
            System.out.println("----------cloning finished----------");

            System.out.println();

            System.out.println("----------population fixed running----------");
            MultiPopulationFixed.runPopulationFixed(i);
            System.out.println("----------population fixed finished----------");
            System.out.println("----------cloning paretoFront----------");
            List<List<Chromosome>> populationFixed = DataUtils.cloneList(DataPool.all);
            System.out.println("----------cloning finished----------");

            System.out.println();

            System.out.println("----------NN-DNSGA-II running----------");
            MultiNNDNSGAII.runNNDNSGAII(i);
            System.out.println("----------NN-DNSGA-II finished----------");
            System.out.println("----------cloning paretoFront----------");
            List<List<Chromosome>> NN_DNSGA_II = DataUtils.cloneList(DataPool.all);
            System.out.println("----------cloning finished----------");

            System.out.println();

            System.out.println("----------generating POF----------");
            double maxMakeSpan = 0;
            double minMakeSpan = Integer.MAX_VALUE;
            double maxCost = 0;
            double minCost = Integer.MAX_VALUE;
            for (List<Chromosome> list : coldStart) {
                for (Chromosome chromosome : list) {
                    maxMakeSpan = Math.max(maxMakeSpan, chromosome.getMakeSpan());
                    minMakeSpan = Math.min(minMakeSpan, chromosome.getMakeSpan());
                    maxCost = Math.max(maxCost, chromosome.getCost());
                    minCost = Math.min(minCost, chromosome.getCost());
                }
            }

            for (List<Chromosome> list : partialFixed) {
                for (Chromosome chromosome : list) {
                    maxMakeSpan = Math.max(maxMakeSpan, chromosome.getMakeSpan());
                    minMakeSpan = Math.min(minMakeSpan, chromosome.getMakeSpan());
                    maxCost = Math.max(maxCost, chromosome.getCost());
                    minCost = Math.min(minCost, chromosome.getCost());
                }
            }

            for (List<Chromosome> list : partialReplace) {
                for (Chromosome chromosome : list) {
                    maxMakeSpan = Math.max(maxMakeSpan, chromosome.getMakeSpan());
                    minMakeSpan = Math.min(minMakeSpan, chromosome.getMakeSpan());
                    maxCost = Math.max(maxCost, chromosome.getCost());
                    minCost = Math.min(minCost, chromosome.getCost());
                }
            }

            for (List<Chromosome> list : similarityFixed) {
                for (Chromosome chromosome : list) {
                    maxMakeSpan = Math.max(maxMakeSpan, chromosome.getMakeSpan());
                    minMakeSpan = Math.min(minMakeSpan, chromosome.getMakeSpan());
                    maxCost = Math.max(maxCost, chromosome.getCost());
                    minCost = Math.min(minCost, chromosome.getCost());
                }
            }

            for (List<Chromosome> list : populationFixed) {
                for (Chromosome chromosome : list) {
                    maxMakeSpan = Math.max(maxMakeSpan, chromosome.getMakeSpan());
                    minMakeSpan = Math.min(minMakeSpan, chromosome.getMakeSpan());
                    maxCost = Math.max(maxCost, chromosome.getCost());
                    minCost = Math.min(minCost, chromosome.getCost());
                }
            }

            for (List<Chromosome> list : NN_DNSGA_II) {
                for (Chromosome chromosome : list) {
                    maxMakeSpan = Math.max(maxMakeSpan, chromosome.getMakeSpan());
                    minMakeSpan = Math.min(minMakeSpan, chromosome.getMakeSpan());
                    maxCost = Math.max(maxCost, chromosome.getCost());
                    minCost = Math.min(minCost, chromosome.getCost());
                }
            }

            String str1 = DataUtils.operateHV(coldStart, maxMakeSpan, minMakeSpan, minCost, maxCost);
            WriterUtils.write("src\\main\\resources\\output\\ColdStart_" + i + ".txt", str1);

            String str2 = DataUtils.operateHV(partialFixed, maxMakeSpan, minMakeSpan, minCost, maxCost);
            WriterUtils.write("src\\main\\resources\\output\\PartialFixed_" + i + ".txt", str2);

            String str3 = DataUtils.operateHV(partialReplace, maxMakeSpan, minMakeSpan, minCost, maxCost);
            WriterUtils.write("src\\main\\resources\\output\\PartialReplace_" + i + ".txt", str3);

            String str4 = DataUtils.operateHV(similarityFixed, maxMakeSpan, minMakeSpan, minCost, maxCost);
            WriterUtils.write("src\\main\\resources\\output\\SimilarityFixed_" + i + ".txt", str4);

            String str5 = DataUtils.operateHV(populationFixed, maxMakeSpan, minMakeSpan, minCost, maxCost);
            WriterUtils.write("src\\main\\resources\\output\\PopulationFixed_" + i + ".txt", str5);

            String str6 = DataUtils.operateHV(NN_DNSGA_II, maxMakeSpan, minMakeSpan, minCost, maxCost);
            WriterUtils.write("src\\main\\resources\\output\\NN-NSGA-II_" + i + ".txt", str6);
        }
    }
}
