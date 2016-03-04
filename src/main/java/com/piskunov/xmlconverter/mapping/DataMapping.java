package com.piskunov.xmlconverter.mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Piskunov on 2/27/16.
 */
public class DataMapping {

    private String result;

    private List<MappingRule> rules = new ArrayList<>();

    private String delimiter = ",";

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

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

}



