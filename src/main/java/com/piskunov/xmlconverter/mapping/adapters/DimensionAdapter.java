package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class DimensionAdapter implements MappingAdapter {


    @Override
    public List<String> process(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();

        String value = data.getPairs().get(rule.getSource());

        if(value == null) {
            throw new MappingException(this.getClass().getSimpleName() + ": Source not found: " + rule.getSource());
        }

        //Breite: 54 cm H̦he : 175 cm Tiefe: 2 cm
        //Ширина: 54 см Высота: 175 см Глубина: 2 см
        //Width, length, depth

        try {
            String[] dimentions = value.split(":");

            if (rule.getAdapterAgrs().equals("width")) {

                String[] result = dimentions[1].split(" ");
                resultValues.add(result[1] + " " + result[2]);
                return resultValues;

            } else if (rule.getAdapterAgrs().equals("length")) {

                String[] result = dimentions[2].split(" ");
                resultValues.add(result[1] + " " + result[2]);
                return resultValues;


            } else if (rule.getAdapterAgrs().equals("depth")) {

                String[] result = dimentions[3].split(" ");
                resultValues.add(result[1] + " " + result[2]);
                return resultValues;

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            //todo error handling
        }

        return resultValues;
    }
}
