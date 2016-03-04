package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vladimir Piskunov on 2/28/16.
 */
public class BooleanExpressionsAdapter implements MappingAdapter {

    @Override
    public List<String> process(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();

        String args = rule.getAdapterAgrs();

        if(args == null)
            throw new MappingException(this.getClass().getSimpleName() + " args not set for rule: " + rule.getTarget());

        String[] arguments = args.split(" ");

        for (String s : arguments) {

            if (s.startsWith("$")) {

                String value = data.getPairs().get(s.replace("$", ""));

                if (value != null) {

                    try {
                        Float.parseFloat(value);
                        args = args.replace(s, value);

                    } catch (NumberFormatException e) {
                        throw new MappingException(this.getClass().getSimpleName() + " argument " + s.toUpperCase() + " is not a float/int: " + value );
                    }

                } else {
                    throw new MappingException(this.getClass().getSimpleName() + " argument is not found in source: " + s);
                }

            } else if (s.equals("less")) {
                args = args.replace("less", "<");
            } else if (s.equals("more")) {
                args = args.replace("more", ">");
            }

        }

        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(args);

        resultValues.add(String.valueOf(exp.getValue(Boolean.class)));

        return resultValues;

    }
}
