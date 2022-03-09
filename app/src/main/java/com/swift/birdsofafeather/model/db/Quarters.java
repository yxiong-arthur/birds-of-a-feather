package com.swift.birdsofafeather.model.db;

import java.util.HashMap;
import java.util.Map;

public class Quarters {
    private static Map<String, Integer> singletonQuarterOrder;

    public static Map<String, Integer> getQuarterOrder(){
        if(singletonQuarterOrder == null){
            singletonQuarterOrder = new HashMap<>();
            singletonQuarterOrder.put("wi", 0);
            singletonQuarterOrder.put("sp", 1);
            singletonQuarterOrder.put("ss1", 2);
            singletonQuarterOrder.put("ss2", 2);
            singletonQuarterOrder.put("sss", 2);
            singletonQuarterOrder.put("fa", 3);
        }
        return singletonQuarterOrder;
    }
}
