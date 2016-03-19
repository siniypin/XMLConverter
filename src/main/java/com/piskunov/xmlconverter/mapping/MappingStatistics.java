package com.piskunov.xmlconverter.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Vladimir Piskunov on 3/19/16.
 */
public class MappingStatistics {

    Logger logger = Logger.getLogger(MappingStatistics.class.getName());

    int processedItems;
    int skippedItems;
    int multipliedItems;

    Map<String, Integer> failedWithErrorRules = new HashMap<>();
    Map<String, Integer> failedWithWarningsRules = new HashMap<>();

    public void resetStatistics(){
        processedItems = 0;
        skippedItems = 0;
        multipliedItems = 0;

        failedWithErrorRules = new HashMap<>();
        failedWithWarningsRules = new HashMap<>();
    }

    public void increaseSkippedItems(){
        skippedItems++;
    }

    public void increaseProcessedItems(){
        processedItems++;

        if(processedItems % 1000 == 0){
            logger.info("Items processed: " + processedItems);
        }


    }

    public void increaseMultipliedItems(int number){
        multipliedItems += number;
    }

    public void addFailedWithErrorRule(String target) {
        Integer value = failedWithErrorRules.get(target);
        if(value != null){
            failedWithErrorRules.put(target, ++value);
        } else {
            failedWithErrorRules.put(target, 1);
        }
    }

    public void addFailedWithWarningsRule(String target) {
        Integer value = failedWithWarningsRules.get(target);
        if(value != null){
            failedWithWarningsRules.put(target, ++value);
        } else {
            failedWithWarningsRules.put(target, 1);
        }
    }

    public void printStatistics(){
        logger.info("----------------------------------");
        logger.info("Processed Items: " + processedItems);
        logger.info("Skipped Items: " + skippedItems);
        logger.info("One-to-Many Items: " + multipliedItems);
        logger.info("----------------------------------");
        logger.info("Failed mapping rules with WARNING, ignored: ");
        logger.info("----------------------------------");
        for(Map.Entry<String, Integer> entry : failedWithWarningsRules.entrySet()){
            logger.info("Target " + entry.getKey().toUpperCase() + " failed " + entry.getValue() + " times.");
        }
        logger.info("----------------------------------");
        logger.info("Failed mapping rules with ERROR^ item skipped: ");
        logger.info("----------------------------------");
        for(Map.Entry<String, Integer> entry : failedWithErrorRules.entrySet()){
            logger.info("Target " + entry.getKey().toUpperCase() + " failed " + entry.getValue() + " times.");
        }

    }
}
