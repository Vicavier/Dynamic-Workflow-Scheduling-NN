package service.multi;

import controller.impl.NSGAIIPopulationController;
import entity.DataPool;
import service.crash.nnNSGAIICrash;
import utils.InitUtils;

import java.util.Random;

public class MultiNNDNSGAII {
    public static void main(String[] args) {
        InitUtils.init();
        runNNDNSGAII(0);
    }
    public static void runNNDNSGAII(int i){
        DataPool.clear();
        DataPool.random = new Random(i);
        InitUtils.init();
        NSGAIIPopulationController controller = new NSGAIIPopulationController();
        controller.crash = new nnNSGAIICrash();
        controller.iterate();
    }
}
