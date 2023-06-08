package controller.impl;

import entity.DoubleStringDataPool;
import entity.Individual;
import service.algorithm.impl.NSGAII;

import java.util.List;

public class DoubleStringNSGAIIController extends AbstractDoubleStringController{
    private final NSGAII nsgaii = DoubleStringDataPool.nsgaii;

    public boolean isChanged = false;

    @Override
    public List<List<Individual>> rankReturnIterate(int x, int y) {
        return null;
    }

    @Override
    public void doInitial() {
        System.out.println("DoubleString doInitial");
    }

    @Override
    public void doProduce() {
        System.out.println("DoubleString doProduce");
    }

    @Override
    public void doSort() {
        System.out.println("DoubleString doSort");
    }

    @Override
    public void doEliminate() {
        System.out.println("DoubleString doEliminate");
    }
}
