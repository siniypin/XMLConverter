package com.piskunov.xmlconverter.mapping.adapters;

import java.util.List;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

/**
 * Created by Vladimir Piskunov on 2/28/16.
 */
public interface MappingAdapter {

    public List<String> process(MappingRule rule, InputData data) throws MappingException;

}
