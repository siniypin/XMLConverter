package com.piskunov.xmlconverter.mapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Vladimir Piskunov on 2/27/16.
 */
public class DataMapping {

    private static Logger logger = Logger.getLogger(DataMapping.class.getName());

    private List<MappingRule> rules = new ArrayList<>();

    private String delimiter = ",";

    public List<MappingRule> getRules() {
        return rules;
    }

    public void setRules(List<MappingRule> rules) {
        this.rules = rules;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @PostConstruct
    public void validateDataMapping() {
        for (MappingRule rule : rules) {
            if (rule.getSource() == null && rule.getAdapter() == null) {
                logger.warning("Mapping rule for target field " + rule.getTarget().toUpperCase() + " is incorrect: neither SOURCE nor ADAPTER is specified.");
            }
        }
    }

    public String getHeaders()
    {
        String header = "";
        for(MappingRule rule: rules){
            header += rule.getTarget() + delimiter;
        }
        header = header.substring(0, header.length() - 1);
        return header;
    }
}



