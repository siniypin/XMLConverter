package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.Dictionary;
import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

import java.util.List;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class CategoryAdapter extends BaseMappingAdapter {

    private Dictionary categoryDictionary;
    private Dictionary nameDictionary;
    private String categorySource;
    private String nameSource;

    public String getCategorySource() {
        return categorySource;
    }

    public void setCategorySource(String categorySource) {
        this.categorySource = categorySource;
    }

    public String getNameSource() {
        return nameSource;
    }

    public void setNameSource(String nameSource) {
        this.nameSource = nameSource;
    }

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

    @Override
    public List<String> processInternal(MappingRule rule, InputData data) throws MappingException {

        if(categoryDictionary == null) {
            throw new MappingException(this.getClass().getSimpleName() + ": Category dictionary not set");
        }

        String key = data.getPairs().get(categorySource);
        List<String> resultValues = categoryDictionary.search(key, false);

        if(resultValues.size() != 0 || nameDictionary == null) {
            return resultValues;
        }

        return nameDictionary.search(data.getPairs().get(nameSource), true);
    }
}
