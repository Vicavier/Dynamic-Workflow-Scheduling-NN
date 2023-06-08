package controller;

import entity.Chromosome;
import entity.Individual;

import java.util.List;

public interface DynamicSimulation {
    List<List<Chromosome>> sim(List<Chromosome> list1,List<Chromosome> list2);

    interface DoubleStringPopulationController {
        void initialPopulation();
        void produceOffspring();
        void sorting();
        void eliminate();
        List<List<Individual>> iterate();
        List<List<Individual>> rankReturnIterate(int x,int y);
    }
}
