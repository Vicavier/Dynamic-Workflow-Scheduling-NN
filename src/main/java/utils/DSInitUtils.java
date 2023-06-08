package utils;

import entity.DataPool;
import entity.DoubleStringDataPool;
import entity.Type;
import service.io.Input;
import service.io.impl.XMLInputImpl;

public class DSInitUtils {
    public static void init(){
        initType();
        initDAG();
        initIns();
    }

    public static void initIns(){
        for(int i=0;i<8;++i){
            String conf = "ins.quantity.type"+i;
            int quantity = Integer.parseInt(ConfigUtils.get(conf));
            for(int j=0;j<quantity;++j){
                DoubleStringDataPool.insToType.add(i);
            }
        }
        for(int ins=0;ins<DoubleStringDataPool.insToType.size();++ins){
            DoubleStringDataPool.accessibleIns.add(ins);
        }
        DoubleStringDataPool.insNum = DoubleStringDataPool.accessibleIns.size();
    }

    public static void initType(){
        Type[] types = new Type[8];
        DoubleStringDataPool.weightVector = new double[3];
        DoubleStringDataPool.weightVector[0] = 8/30.0;
        DoubleStringDataPool.weightVector[1] = 1/131072000.0;
        DoubleStringDataPool.weightVector[2] = 10/9.0;
        types[0] = new Type(0, 1.7/8, 39321600, 0.06);
        types[1] = new Type(1, 3.75/8, 85196800, 0.12);
        types[2] = new Type(2, 3.75/8, 85196800, 0.113);
        types[3] = new Type(3, 7.5/8, 85196800, 0.24);
        types[4] = new Type(4, 7.5/8, 85196800, 0.225);
        types[5] = new Type(5, 15/8.0, 131072000, 0.48);
        types[6] = new Type(6, 15/8.0, 131072000, 0.45);
        types[7] = new Type(7, 30/8.0, 131072000, 0.9);

        DoubleStringDataPool.typeNum = 8;
        DoubleStringDataPool.types = types;
    }

    public static void initDAG(){
        Input input=new XMLInputImpl();
        input.input();
    }
}
