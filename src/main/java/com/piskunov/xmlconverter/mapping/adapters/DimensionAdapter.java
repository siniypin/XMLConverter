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
    private boolean removeDelimiter = true;

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

    public boolean isRemoveDelimiter() {
        return removeDelimiter;
    }

    public void setRemoveDelimiter(boolean removeDelimiter) {
        this.removeDelimiter = removeDelimiter;
    }

    @Override
    public List<String> process(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();

        String source = data.getPairs().get(rule.getSource());

        if(source == null) {
            throw new MappingException(this.getClass().getSimpleName() + ": Source not found: " + rule.getSource());
        }

        String[] values = source.split(delimiter);

        if(values.length > position) {
            resultValues.add((values[position] + (removeDelimiter ? "" : delimiter)).trim());
        }

        return resultValues;
    }
}
