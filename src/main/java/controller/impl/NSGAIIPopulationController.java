package controller.impl;

import entity.Chromosome;
import entity.DataPool;
import service.algorithm.impl.NSGAII;
import service.multi.MultiPopulationFixed;
import utils.ConfigUtils;
import utils.DataUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class NSGAIIPopulationController extends AbstractPopulationController {
    private final NSGAII nsgaii = DataPool.nsgaii;
    public boolean isChanged = false;

    public NSGAIIPopulationController(){};
    @Override
    public void doInitial() {
//        Chromosome heft=HEFT.generateChromosome();
//        heft.setCost(DataUtils.getCost(heft));
//        heft.setMakeSpan(DataUtils.getMakeSpan(heft));
//        getFa().add(heft);
//        Chromosome chromosome1= HEFT.generateChromosome();
//        chromosome1.setCost(DataUtils.getCost(chromosome1));
//        chromosome1.setMakeSpan(DataUtils.getMakeSpan(chromosome1));
//        getFa().add(chromosome1);
//        Chromosome chromosome2= CostMin.getMinCostChromosome();
//        chromosome2.setCost(DataUtils.getCost(chromosome2));
//        chromosome2.setMakeSpan(DataUtils.getMakeSpan(chromosome2));
//        getFa().add(chromosome2);

        while (getFa().size() < getSize()) {
            Chromosome chromosome = DataUtils.getInitialChromosome();
            if (!getFa().contains(chromosome)) getFa().add(chromosome);
        }
    }

    @Override
    public void doProduce() {
        try {
            if(!isChanged) {
                while (getSon().size() < getFa().size()) {
                    int num1 = DataPool.random.nextInt(getSize());
                    int num2 = DataPool.random.nextInt(getSize());
                    while (num1 == num2) {
                        num2 = DataPool.random.nextInt(getSize());
                    }

                    Chromosome parent1 = getFa().get(num1).clone();
                    Chromosome parent2 = getFa().get(num2).clone();
                    Chromosome child1;
                    Chromosome child2;

                    List<Chromosome> childList = DataPool.nsgaii.crossover(parent1, parent2);
                    child1 = childList.get(0);
                    child2 = childList.get(1);
                    if (DataPool.random.nextInt(10000) < Double.parseDouble(ConfigUtils.get("evolution.population.mutation")) * 10000) {
                        child1 = DataPool.nsgaii.mutate(child1);
                        child2 = DataPool.nsgaii.mutate(child2);
                    }
                    DataUtils.refresh(child1);
                    DataUtils.refresh(child2);

                    getSon().add(child1);
                    getSon().add(child2);
                }
            }else {
                while (getSon().size() < getFa().size()) {
                    int num1 = DataPool.random.nextInt(getSize());
                    int num2 = DataPool.random.nextInt(getSize());
                    while (num1 == num2) {
                        num2 = DataPool.random.nextInt(getSize());
                    }

                    Chromosome parent1 = getFa().get(num1).clone();
                    Chromosome parent2 = getFa().get(num2).clone();
                    Chromosome child1;
                    Chromosome child2;
                    if(DataPool.random.nextInt(10)<5){
                        NSGAIIPopulationController controller = MultiPopulationFixed.DA;
                        num2 = DataPool.random.nextInt(controller.getSize());
                        parent2 = controller.getFa().get(num2).clone();
                    }

                    List<Chromosome> childList = DataPool.nsgaii.crossover(parent1, parent2);
                    child1 = childList.get(0);
                    child2 = childList.get(1);
                    if (DataPool.random.nextInt(10000) < Double.parseDouble(ConfigUtils.get("evolution.population.mutation")) * 10000) {
                        child1 = DataPool.nsgaii.mutate(child1);
                        child2 = DataPool.nsgaii.mutate(child2);
                    }
                    DataUtils.refresh(child1);
                    DataUtils.refresh(child2);

                    getSon().add(child1);
                    getSon().add(child2);
                }
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doSort() {
        List<Chromosome> list = new LinkedList<>();
        list.addAll(getFa());
        list.addAll(getSon());

        for (Chromosome chromosome : list) {
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getPoor().clear();
            chromosome.getBetter().clear();
//            DataUtils.refresh(chromosome);
        }
        for (int i = 0; i < list.size(); ++i) {
            Chromosome chromosome = list.get(i);
            for (int j = i + 1; j < list.size(); ++j) {
                Chromosome other = list.get(j);
                if (chromosome.getMakeSpan() >= other.getMakeSpan()
                        && chromosome.getCost() >= other.getCost()) {
                    if (chromosome.getMakeSpan() - other.getMakeSpan() > 0.0000000001
                            || chromosome.getCost() - other.getCost() > 0.0000000001) {
                        setBetterAndPoor(other, chromosome);
                    }
                }
                if (chromosome.getMakeSpan() <= other.getMakeSpan()
                        && chromosome.getCost() <= other.getCost()
                ) {
                    if ((chromosome.getMakeSpan() - other.getMakeSpan()) < -0.000000001
                            || chromosome.getCost() - other.getCost() < -0.000000001
                    ) {
                        setBetterAndPoor(chromosome, other);
                    }
                }
            }
        }
        setRank(new LinkedList<>());
        while (hasBetter(list)) {
            LinkedList<Chromosome> rankList = new LinkedList<>();
            List<Chromosome> temp = new LinkedList<>();
            for (Chromosome chromosome : list) {
                if (chromosome.getBetterNum() == 0) {
                    chromosome.reduceBetter();
                    rankList.add(chromosome);
                    temp.add(chromosome);
                }
            }
            for (Chromosome chromosome : temp) {
                for (Chromosome worse : chromosome.getPoor()) {
                    worse.reduceBetter();
                }
            }
            getRank().add(rankList);
        }
    }

    private boolean hasBetter(List<Chromosome> list) {
        for (Chromosome chromosome : list) {
            if (chromosome.getBetterNum() >= 0) return true;
        }
        return false;
    }

    @Override
    public void doEliminate() {
        getFa().clear();
        List<List<Chromosome>> rank = getRank();
        for (List<Chromosome> list : rank) {
            list.sort((o1, o2) -> {
                if (o1.getMakeSpan() - o2.getMakeSpan() > 0.000000001) return 1;
                else if (o1.getMakeSpan() - o2.getMakeSpan() < -0.000000001) return -1;
                return 0;
            });
            list.get(0).setCrowding(Double.MAX_VALUE);
            list.get(list.size() - 1).setCrowding(Double.MAX_VALUE);
            for (int i = 1; i < list.size() - 1; ++i) {
                list.get(i).setCrowding(Math.abs(list.get(i + 1).getMakeSpan() - list.get(i - 1).getMakeSpan()) * Math.abs(list.get(i + 1).getCost() - list.get(i - 1).getCost()));
            }
            list.sort((o1, o2) -> {
                double num = o1.getCrowding() - o2.getCrowding();
                if (num > 0) return -1;
                if (num < 0) return 1;
                return 0;
            });
        }
        for (List<Chromosome> list : getRank()) {
            for (Chromosome chromosome : list) {
                if (!getFa().contains(chromosome)) getFa().add(chromosome);
                if (getFa().size() >= getSize()) return;
            }
        }
    }

    public void setBetterAndPoor(Chromosome better, Chromosome poor) {
        better.getPoor().add(poor);
        poor.getBetter().add(better);
        better.addPoor();
        poor.addBetter();
    }

}
