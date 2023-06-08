package entity;

import service.algorithm.impl.NSGAII;

import java.util.*;

public class DoubleStringDataPool {
    public static Task[] tasks=new Task[0];
    public static TaskGraph graph;
    public static List<int[]> edges;
    public static NSGAII nsgaii = new NSGAII();
    public static int insNum=0;
    public static int typeNum=0;
    public static Type[] types;
    public static double weightVector[];
    public static Random random;
    public static List<Integer> insToType=new ArrayList<>(250);
    public static List<Integer> accessibleIns=new ArrayList<>();
    public static HashSet<Integer> disabledIns=new HashSet<>();

    public static List<List<Individual>> allIndividual = new LinkedList<>();
    public static void clear(){
        tasks = new Task[0];
        graph = graph.clone();
        if(edges!=null) edges.clear();
        nsgaii = new NSGAII();
        insNum=0;
        typeNum = 0;
        types = null;
        weightVector = null;
        insToType.clear();
        accessibleIns.clear();
        disabledIns.clear();
        allIndividual.clear();
    }
}
