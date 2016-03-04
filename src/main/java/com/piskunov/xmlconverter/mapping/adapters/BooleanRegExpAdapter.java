package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class BooleanRegExpAdapter implements MappingAdapter {


    @Override
    public List<String> process(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();

        String value = data.getPairs().get(rule.getSource());

        if(value == null) {
            throw new MappingException(this.getClass().getSimpleName() + ": Source not found: " + rule.getSource());
        }

        resultValues.add(String.valueOf(value.matches(rule.getAdapterAgrs())));

        return resultValues;

    }


}
