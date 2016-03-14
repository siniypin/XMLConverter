package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.Dictionary;
import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.List;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class CategoryAdapter implements MappingAdapter {

    public Dictionary getCategoryDictionary() {
        return categoryDictionary;
    }

    public void setCategoryDictionary(Dictionary categoryDictionary) {
        this.categoryDictionary = categoryDictionary;
    }

    public Dictionary getNameDictionary() {
        return nameDictionary;
    }

    public void setNameDictionary(Dictionary nameDictionary) {
        this.nameDictionary = nameDictionary;
    }

    private Dictionary categoryDictionary;
    private Dictionary nameDictionary;

    @Override
    public List<String> process(MappingRule rule, InputData data) throws MappingException {

        if(categoryDictionary == null) {
            throw new MappingException(this.getClass().getSimpleName() + ": Category dictionary not set");
        }

        String key = data.getPairs().get(rule.getSource());
        List<String> resultValues = categoryDictionary.search(key, false);

        if(resultValues.size() != 0 || nameDictionary == null) {
            return resultValues;
        }

        return nameDictionary.search(data.getPairs().get(rule.getAdapterAgrs()), true);
    }
}
