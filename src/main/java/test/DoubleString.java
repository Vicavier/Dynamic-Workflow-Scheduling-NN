package test;

import controller.impl.DoubleStringNSGAIIController;
import controller.impl.NSGAIIPopulationController;
import entity.DataPool;
import entity.DoubleStringDataPool;
import service.crash.nnNSGAIICrash;
import utils.DSInitUtils;
import utils.InitUtils;

import java.util.Random;

public class DoubleString {
    public static void main(String[] args) {
        DSInitUtils.init();
        runDoubleStringNSGAII(0);
    }

    public static void runDoubleStringNSGAII(int i){
        DoubleStringDataPool.clear();
        DoubleStringDataPool.random = new Random(i);
        InitUtils.init();
        DoubleStringNSGAIIController controller = new DoubleStringNSGAIIController();
        controller.iterate();
    }
}
