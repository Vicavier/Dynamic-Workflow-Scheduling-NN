package controller.impl;

import controller.DynamicSimulation;
import entity.DoubleStringDataPool;
import entity.Individual;
import service.crash.Crash;
import utils.ConfigUtils;

import java.util.LinkedList;
import java.util.List;

abstract class AbstractDoubleStringController implements DynamicSimulation.DoubleStringPopulationController {
    private final int size;
    private final int generation;
    private final double mutation;
    public Crash crash;
    private List<Individual> fa;
    private final List<Individual> son;
    private List<List<Individual>> rank;

    public List<List<Individual>> getRank(){return rank;}

    public void setRank(List<List<Individual>> rank) {this.rank = rank;}

    public AbstractDoubleStringController(){
        size = Integer.parseInt(ConfigUtils.get("evolution.population.size"));
        generation = Integer.parseInt(ConfigUtils.get("evolution.population.generation"));
        mutation = Double.parseDouble(ConfigUtils.get("evolution.population.mutation"));
        fa = new LinkedList<>();
        son = new LinkedList<>();
    }
    @Override
    public void initialPopulation() {
        doInitial();
    }

    @Override
    public void produceOffspring() {
        doProduce();
    }

    @Override
    public void sorting() {
        doSort();
    }
    @Override
    public void eliminate() {
        doEliminate();
    }

    @Override
    public List<List<Individual>> iterate() {
        int generation = Integer.parseInt(ConfigUtils.get("evolution.population.generation"));
        doInitial();
        for (int i = 0; i < generation; ++i) {
            iterateACycle(i, DoubleStringDataPool.allIndividual);
        }
//        DataUtils.operateHV(DataPool.all);
        return rank;
    }
    public abstract void doInitial();

    public abstract void doProduce();

    public abstract void doSort();

    public abstract void doEliminate();
    public List<List<Individual>> iterateACycle(int i, List<List<Individual>> HVRecoder){
//        if (crash!=null) crash.crash(i,fa,this);
        System.out.println("DoubleString iterate a cycle");
        doProduce();
        doSort();
        doEliminate();
        son.clear();
        for (Individual individual: fa){

        }
        return null;
    }
}
