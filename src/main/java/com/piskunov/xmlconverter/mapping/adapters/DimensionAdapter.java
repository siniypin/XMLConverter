package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class DimensionAdapter implements MappingAdapter {

    private String delimiter;
    private int position;
    private String source;

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

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

        String[] values = sourceValue.split(delimiter);

        if(values.length > position) {

            String stringValue = values[position];

            int intValue;

            try {
                stringValue = stringValue.replace(",", ".");
                intValue = Math.round(Float.valueOf(stringValue) * 10);
            } catch(NumberFormatException e) {
                throw new MappingException(this.getClass().getSimpleName() + ": Dimentions format is incorrect: " +  sourceValue);
            }

            resultValues.add(String.valueOf(intValue));
        }

        return resultValues;
    }
}
