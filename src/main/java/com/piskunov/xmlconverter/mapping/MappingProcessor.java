package com.piskunov.xmlconverter.mapping;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;

/**
 * Created by Vladimir Piskunov on 2/25/16.
 */


public class MappingProcessor implements ItemProcessor<InputData, OutputData> {

    @Autowired
    MappingStatistics mappingStatistics;

    private static Logger logger = Logger.getLogger(MappingProcessor.class.getName());
    private DataMapping dataMapping;
    private Resource skippedItemsLog;

    private boolean printHeaders = true;

    public Resource getSkippedItemsLog() {
        return skippedItemsLog;
    }

    public void setSkippedItemsLog(Resource skippedItemsLog) {
        this.skippedItemsLog = skippedItemsLog;
    }

    public DataMapping getDataMapping() {
        return dataMapping;
    }

    public void setDataMapping(DataMapping dataMapping) {
        this.dataMapping = dataMapping;
    }

    public boolean isPrintHeaders() {
        return printHeaders;
    }

    public void setPrintHeaders(boolean printHeaders) {
        this.printHeaders = printHeaders;
    }

    @Override
    public OutputData process(InputData data) throws Exception {

        mappingStatistics.increaseProcessedItems();

        List<String> resultLines = new LinkedList<>();

        //add headers
        if(data.getSource() == null && printHeaders) {
            return new OutputData(dataMapping.getHeaders());
        }

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
                        line += fieldMappingValue + dataMapping.getDelimiter();
                        tmpLines.add(line);
                    }
                }

            } catch (MappingException e) {

                //mapping exception handling
                //logger.warning(e.getMessage());

                if (rule.isSkipRecordOnError()) {
                    logSkippedRecord(e, data.getSource());
                    mappingStatistics.increaseSkippedItems();
                    mappingStatistics.addFailedWithErrorRule(rule.getTarget());
                    return null;
                } else {
                    mappingStatistics.addFailedWithWarningsRule(rule.getTarget());
                }

                for (String line : resultLines) {
                    line += dataMapping.getDelimiter();
                    tmpLines.add(line);
                }
            }
            resultLines = tmpLines;
        }


        //make ret value: remove last delimiter, add line separator

        mappingStatistics.increaseMultipliedItems(resultLines.size() - 1);

        String ret = "";

        for (String line : resultLines) {
            if (line.endsWith(dataMapping.getDelimiter())) {
                line = line.substring(0, line.length() - 1);
            }
            ret += line + "\n";
        }
        ret = ret.substring(0, ret.length() - 1);

        return new OutputData(ret);
    }

    private void logSkippedRecord(MappingException e, String line) {

        Writer output = null;
        try {
            File file = skippedItemsLog.getFile();
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            output = new BufferedWriter(new FileWriter(file, true));
            output.append(e.getMessage() + "\n");
            output.append(line + "\n");

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


}