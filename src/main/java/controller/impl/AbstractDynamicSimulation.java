package controller.impl;

import controller.DynamicSimulation;
import entity.Chromosome;

import java.util.List;

public abstract class AbstractDynamicSimulation implements DynamicSimulation {
    @Override
    public List<List<Chromosome>> sim(List<List<Chromosome>> list) {
        return null;
    }
    abstract void doSim(List<List<Chromosome>> list);
}
