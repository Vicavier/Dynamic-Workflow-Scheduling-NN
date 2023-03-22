package controller.impl;

import controller.DynamicSimulation;
import entity.Chromosome;
import service.io.Output;
import service.io.impl.ChartOutputImpl;
import utils.DynamicUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDynamicSimulation implements DynamicSimulation {
    @Override
    public List<List<Chromosome>> sim(List<Chromosome> list1,List<Chromosome> list2) {
        try {
            double beforeHV = DynamicUtils.HV(list1);
            List<Chromosome> temp = new ArrayList<>();
            for(Chromosome chromosome:list1){
                temp.add(chromosome.clone());
            }
            doSim(temp);
            double afterHV = DynamicUtils.HV(temp);
            Output output = new ChartOutputImpl();
            List<List<Chromosome>> ans = new ArrayList<>();
            ans.add(list1);
            ans.add(temp);
            System.out.println("HV{before: "+ beforeHV + " | after: " + afterHV + " | " + "reduce: " + (beforeHV - afterHV)/beforeHV + "}");
            output.output(ans);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    abstract void doSim(List<Chromosome> list);
}