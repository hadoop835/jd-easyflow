package com.jd.easyflow.el;

import java.util.Map;

/**
 * 
 * @author liyuliang5
 *
 */
public class ElFactory {
    
    private static Map<String, ElEvaluator> evaluatorMap;
    
    private static ElEvaluator defaultEvaluator = new SpelEvaluator();
    
    public static ElEvaluator get() {
        return defaultEvaluator;
    }

    public static ElEvaluator get(String type) {
        if (type == null) {
            return defaultEvaluator;
        }
        return evaluatorMap.get(type);
    }
    
    public static void setDefaultEvaluator(ElEvaluator evaluator) {
        defaultEvaluator = evaluator;
    }
    
    
    public static Map<String, ElEvaluator> getEvaluatorMap() {
        return evaluatorMap;
    }

    public static void setEvaluatorMap(Map<String, ElEvaluator> evaluatorMap) {
        ElFactory.evaluatorMap = evaluatorMap;
    }

    public void setDefault(ElEvaluator evaluator) {
        ElFactory.defaultEvaluator = evaluator;
    }
}
