package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Piskunov on 3/19/16.
 */
public abstract class BaseMappingAdapter implements MappingAdapter{

    protected MappingAdapter nextAdapter;

    protected String source;

    public MappingAdapter getNextAdapter() {
        return nextAdapter;
    }

    public void setNextAdapter(MappingAdapter nextAdapter) {
        this.nextAdapter = nextAdapter;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    abstract protected List<String> processInternal(MappingRule rule, InputData data) throws MappingException;

    public List<String> process(MappingRule rule, InputData data) throws MappingException {

        List<String> ret = new ArrayList<>();

        List<String> values = processInternal(rule, data);
        if(nextAdapter != null) {
            for (String s : values) {
                nextAdapter.setSource(s);
                ret.addAll(nextAdapter.process(rule, data));
            }
        } else {
            ret = values;
        }
        return ret;
    }

}
