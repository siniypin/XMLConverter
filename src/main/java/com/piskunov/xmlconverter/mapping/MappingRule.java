package com.piskunov.xmlconverter.mapping;

import com.piskunov.xmlconverter.mapping.adapters.MappingAdapter;

/**
 * Created by Vladimir Piskunov on 2/27/16.
 */
public class MappingRule {

    //source field
    private String source;

    //target field
    private String target;

    //true if value is required
    private boolean required = false;

    //max string length
    private int maxSize;

    //should be equal to value
    private String equalsTo;

    //required type: float, int, default: string
    private String type;

    //custom adapter
    private MappingAdapter adapter;
    private String adapterAgrs;

    private boolean skipRecordOnError = false;


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getEqualsTo() {
        return equalsTo;
    }

    public void setEqualsTo(String equalsTo) {
        this.equalsTo = equalsTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MappingAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(MappingAdapter adapter) {
        this.adapter = adapter;
    }

    public String getAdapterAgrs() {
        return adapterAgrs;
    }

    public void setAdapterAgrs(String adapterAgrs) {
        this.adapterAgrs = adapterAgrs;
    }

    public boolean isSkipRecordOnError() {
        return skipRecordOnError;
    }

    public void setSkipRecordOnError(boolean skipRecordOnError) {
        this.skipRecordOnError = skipRecordOnError;
    }

}
