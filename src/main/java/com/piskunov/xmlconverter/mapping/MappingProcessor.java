package com.piskunov.xmlconverter.mapping;


import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Vladimir Piskunov on 2/25/16.
 */


public class MappingProcessor implements ItemProcessor<InputData, DataMapping> {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private DataMapping dataMapping;

    private boolean validated = false;

    private Resource skippedItemsLog;

    public Resource getSkippedItemsLog() {
        return skippedItemsLog;
    }

    public void setSkippedItemsLog(Resource skippedItemsLog) {
        this.skippedItemsLog = skippedItemsLog;
    }

    @Override
    public DataMapping process(InputData data) throws Exception {

        if (!validated) {
            validateMappingRules();
        }

        List<String> resultLines = new LinkedList<>();
        resultLines.add("");

        //iterator over mapping rules
        for (MappingRule rule : dataMapping.getRules()) {

            List<String> fieldMappingValues;
            LinkedList<String> tmpLines = new LinkedList<>();

            try {

                fieldMappingValues = applyRule(rule, data);
                if (fieldMappingValues.size() == 0) {
                    fieldMappingValues.add("");
                }

                //add screening characters and delimiter
                for (String fieldMappingValue : fieldMappingValues) {

                    for (String line : resultLines) {
                        line += "\"" + fieldMappingValue + "\"" + dataMapping.getDelimiter();
                        tmpLines.add(line);
                    }
                }

            } catch (MappingException e) {

                //mapping exception handling
                logger.warning(e.getMessage());

                if (rule.isSkipRecordOnError()) {
                    logSkippedRecord(data);
                    return null;
                }

                for (String line : resultLines) {
                    line += dataMapping.getDelimiter();
                    tmpLines.add(line);
                }
            }
            resultLines = tmpLines;
        }


        //make ret value: remove last delimiter, add line separator
        String ret = "";

        for (String line : resultLines) {
            if (line.endsWith(dataMapping.getDelimiter())) {
                line = line.substring(0, line.length() - 1);
            }
            ret += line + "\n";
        }
        ret = ret.substring(0, ret.length() - 1);
        dataMapping.setResult(ret);

        return dataMapping;
    }

    private void logSkippedRecord(InputData data) {

        logger.warning("Record skipped: " + data.getSource());
        Writer output = null;
        try {
            File file = skippedItemsLog.getFile();
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            output = new BufferedWriter(new FileWriter(file, true));
            output.append(data.getSource() + "\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (output != null)
                try {
                    output.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }

    }


    /**
     * Apply mapping rule and check constraints
     *
     * @param rule
     * @param data
     * @return
     * @throws MappingException
     */

    private List<String> applyRule(MappingRule rule, InputData data) throws MappingException {


        List<String> resultValues = new ArrayList<>();

        if (rule.getAdapter() != null) {

            resultValues = rule.getAdapter().process(rule, data);

        } else if (rule.getSource() != null && rule.getSource().length() != 0) {
            String s = data.getPairs().get(rule.getSource());
            if (s != null) {
                resultValues.add(s);
            }
        } else {
            return resultValues;
            //throw new MappingException(rule, "Neither SOURCE nor ADAPTER is specified.");
        }

        //check required rule
        if (rule.isRequired() && resultValues.size() == 0) {
            throw new MappingException(rule, "Not empty value is required.");
        }

        for (String result : resultValues) {

            if (rule.isRequired() && result.length() == 0) {
                throw new MappingException(rule, "Not empty value is required.");
            }

            //check equal rule
            if (rule.getEqualsTo() != null && !result.equals(rule.getEqualsTo())) {
                throw new MappingException(rule, "Value has to be equals to " + rule.getEqualsTo());
            }

            //
            if (rule.getType() != null) {

                if (result.length() == 0) {
                    result = "0";
                } else {


                    try {
                        Float.parseFloat(result);
                    } catch (NumberFormatException e) {
                        throw new MappingException(rule, "Value type has to be " + rule.getType());
                    }
                }

            }

            if (rule.getType() == null && rule.getMaxSize() != 0) {

                if (result.length() > rule.getMaxSize()) {
                    throw new MappingException(rule, "Value lenth is greater than maximum " + rule.getMaxSize());
                }

            }
        }

        return resultValues;
    }

    private void validateMappingRules() {

        for (MappingRule rule : dataMapping.getRules()) {

            if (rule.getSource() == null && rule.getAdapter() == null) {
                logger.warning("Mapping rule for target field " + rule.getTarget().toUpperCase() + " is incorrect: neither SOURCE nor ADAPTER is specified.");
            }

        }

        validated = true;
    }


}