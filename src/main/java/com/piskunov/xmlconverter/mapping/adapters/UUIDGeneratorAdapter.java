package com.piskunov.xmlconverter.mapping.adapters;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDGenerator;
import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class UUIDGeneratorAdapter implements MappingAdapter {
    @Override
    public List<String> process(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();
        if(rule.getAdapterAgrs() == null)
            throw new MappingException(this.getClass().getSimpleName() + ": Adapter arguments are not set");

        String[] sources = rule.getAdapterAgrs().split(",");

        String base = sources[0];

        if(sources.length > 1) {
            for (int i = 1; i < sources.length; i++) {
                base += data.getPairs().get(sources[i]);
            }
        }

        resultValues.add(Generators.nameBasedGenerator().generate(base).toString());
        return resultValues;
    }
}
