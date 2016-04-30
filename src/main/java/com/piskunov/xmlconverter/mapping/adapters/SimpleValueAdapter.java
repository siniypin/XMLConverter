package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class SimpleValueAdapter extends BaseMappingAdapter{


    @Override
    public List<String> processInternal(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();
        resultValues.add(source);

        return resultValues;
    }
}
