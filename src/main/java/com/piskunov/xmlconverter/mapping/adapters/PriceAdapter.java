package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Piskunov on 3/17/16.
 */
public class PriceAdapter implements MappingAdapter {

    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public List<String> process(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();

        String sourceValue = data.getPairs().get(source);

        if(source == null) {
            throw new MappingException(this.getClass().getSimpleName() + ": Source not found: " + source);
        }

        if(sourceValue != null) {

            int intValue;

            try {
                sourceValue = sourceValue.replace(",", ".");
                intValue = Math.round(Float.valueOf(sourceValue) * 100);
            } catch(NumberFormatException e) {
                throw new MappingException(this.getClass().getSimpleName() + ": Price format is incorrect: " +  sourceValue);
            }

            resultValues.add(String.valueOf(intValue));
        }

        return resultValues;
    }
}
