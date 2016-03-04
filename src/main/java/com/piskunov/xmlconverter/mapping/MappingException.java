package com.piskunov.xmlconverter.mapping;

/**
 * Created by Vladimir Piskunov on 2/28/16.
 */
public class MappingException extends Exception {


    public MappingException(String s) {
        super(s);
    }

    public MappingException(MappingRule rule, String msg) {

        this("Mapping error: " + msg + " Target field: " + rule.getTarget() + " Source field: " + rule.getSource());
    }
}
