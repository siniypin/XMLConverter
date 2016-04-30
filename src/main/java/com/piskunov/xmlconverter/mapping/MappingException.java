package com.piskunov.xmlconverter.mapping;

/**
 * Created by Vladimir Piskunov on 2/28/16.
 */
@SuppressWarnings("serial")
public class MappingException extends Exception {


    public MappingException(String s) {
        super(s);
    }

    public MappingException(MappingRule rule, String msg) {

        this(rule, msg, null);
    }

    public MappingException(MappingRule rule, String msg, InputData data) {

        this("Mapping error: " + msg + " TargetField: " + rule.getTarget() + " SourceField: " + rule.getSource() +
                (data == null ? "" : " SourceValue: " + data.getPairs().get(rule.getSource())));
    }
}
