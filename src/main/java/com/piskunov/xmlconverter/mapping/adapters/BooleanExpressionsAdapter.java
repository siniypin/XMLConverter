package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Vladimir Piskunov on 2/28/16.
 */
public class BooleanExpressionsAdapter extends BaseMappingAdapter {

    static Logger logger = Logger.getLogger(BooleanExpressionsAdapter.class.getName());

    @Override
    public List<String> processInternal(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();

        String exp = source;

        if(exp == null)
            throw new MappingException(this.getClass().getSimpleName() + "Adapter Expression is not set for rule: " + rule.getTarget());

        String[] arguments = exp.split(" ");

        for (String s : arguments) {

            if (s.startsWith("$")) {

                String value = data.getPairs().get(s.replace("$", ""));

                if (value != null) {

                    try {
                        Float.parseFloat(value);
                        exp = exp.replace(s, value);

                    } catch (NumberFormatException e) {
                        try {
                            value = value.replace(",", ".");
                            Float.parseFloat(value);
                            exp = exp.replace(s, value);

                        } catch (NumberFormatException ee) {
                            throw new MappingException(this.getClass().getSimpleName() + " argument " + s.toUpperCase() + " is not a float/int: |" + value + "|");
                        }
                    }

                } else {
                    throw new MappingException(this.getClass().getSimpleName() + " argument is not found in source: " + s);
                }

            } else if (s.equals("less")) {
                exp = exp.replace("less", "<");
            } else if (s.equals("more")) {
                exp = exp.replace("more", ">");
            }

        }

        ExpressionParser parser = new SpelExpressionParser();
        resultValues.add(String.valueOf(parser.parseExpression(exp).getValue(Boolean.class)));

        return resultValues;

    }
}
