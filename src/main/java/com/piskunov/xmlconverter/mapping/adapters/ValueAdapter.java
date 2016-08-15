package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.List;

/**
 * Created by echo on 15/08/16.
 */
public class ValueAdapter extends BaseMappingAdapter{
    private List<String> value;

    public void setValue(List<String> value) {
        this.value = value;
    }

    @Override
    protected List<String> processInternal(MappingRule rule, InputData data) throws MappingException {
        return value;
    }
}
