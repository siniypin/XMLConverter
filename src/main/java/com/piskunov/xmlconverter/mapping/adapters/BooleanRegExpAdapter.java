package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class BooleanRegExpAdapter extends BaseMappingAdapter {

    static Logger logger = Logger.getLogger(BooleanExpressionsAdapter.class.getName());

    private String expression;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }


    @Override
    public List<String> processInternal(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();

        String value = data.getPairs().get(source);

        if(value == null) {
            throw new MappingException(this.getClass().getSimpleName() + ": Source not found: " + source);
        }

        resultValues.add(String.valueOf(value.matches(expression)));

        return resultValues;

    }


}
