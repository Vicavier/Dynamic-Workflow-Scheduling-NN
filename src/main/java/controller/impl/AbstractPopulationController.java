package controller.impl;

import controller.PopulationController;
import entity.Chromosome;
import entity.DataPool;
import service.crash.Crash;
import utils.ConfigUtils;
import utils.CrashUtils;
import utils.DataUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractPopulationController implements PopulationController {
    private int size;
    private final int generation;
    private final double mutation;
    public Crash crash;

    private List<Chromosome> fa;
    private final List<Chromosome> son;

    private List<List<Chromosome>> rank;

    public List<List<Chromosome>> getRank() {
        return rank;
    }

    public void setRank(List<List<Chromosome>> rank) {
        this.rank = rank;
    }

    public AbstractPopulationController() {
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
    public List<List<Chromosome>> iterate() {
        int generation = Integer.parseInt(ConfigUtils.get("evolution.population.generation"));
        doInitial();
        for (int i = 0; i < generation; ++i) {
            iterateACycle(i,DataPool.all);
        }
//        DataUtils.operateHV(DataPool.all);
        return rank;
    }

    public List<List<Chromosome>> iterateACycle(int i,List<List<Chromosome>> HVRecorder){
        if(crash!=null) crash.crash(i,fa,this);
        doProduce();
        doSort();
        doEliminate();
        son.clear();
        for (Chromosome chromosome : fa) {
            DataUtils.refresh(chromosome);
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getBetter().clear();
            chromosome.getPoor().clear();
        }
        List<Chromosome> list = new ArrayList<>();
        for(int k=0;k<rank.get(0).size();++k){
            try {
                list.add(rank.get(0).get(k).clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
//            List<List<Chromosome>> fronts = new ArrayList<>();
//            fronts.add(list);
//            System.out.println(i+" "+DataUtils.operateHV(fronts));
        if(HVRecorder!=null) HVRecorder.add(list);
        return rank;
    }

    public List<List<Chromosome>> rankReturnIterate(int x,int y){
        int generation = Math.max(x,y) + 1;
        doInitial();
        if(generation==0) {
            List<List<Chromosome>> list=new ArrayList<>();
            list.add(fa);
            return list;
        }

        List<List<Chromosome>> ans = new ArrayList<>();
        for (int i = 0; i < generation; ++i) {
            doProduce();
            doSort();
            doEliminate();
            son.clear();
            for (Chromosome chromosome : fa) {
                chromosome.setBetterNum(0);
                chromosome.setPoorNum(0);
                chromosome.getBetter().clear();
                chromosome.getPoor().clear();
            }
            List<Chromosome> list = rank.get(0);
            if(i==x||i==y) ans.add(list);
            DataPool.all.add(list);
        }
        return ans;
    }


    public abstract void doInitial();

    public abstract void doProduce();

    public abstract void doSort();

    public abstract void doEliminate();

    public int getSize() {
        return size;
    }
    public void setSize(int size){this.size = size;}
    public int getGeneration() {
        return generation;
    }

    public double getMutation() {
        return mutation;
    }

    public List<Chromosome> getFa() {
        return fa;
    }
    public void setFa(List<Chromosome> fa){this.fa = fa;}
    public List<Chromosome> getSon() {
        return son;
    }
}
