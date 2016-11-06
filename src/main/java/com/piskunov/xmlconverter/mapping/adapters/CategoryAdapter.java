package com.piskunov.xmlconverter.mapping.adapters;

import java.util.Collections;
import java.util.List;

import com.piskunov.xmlconverter.categorizer.CategorizingService;
import org.springframework.util.StringUtils;

import com.piskunov.xmlconverter.mapping.Dictionary;
import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class CategoryAdapter extends BaseMappingAdapter {

    private Dictionary categoryDictionary;
    private Dictionary nameDictionary;
    private String categorySource;
    private String secondaryCategorySource;
    private String nameSource;
    private String descriptionSource;
    private CategorizingService categorizingService;

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

    public String getSecondaryCategorySource() {
		return secondaryCategorySource;
	}

	public void setSecondaryCategorySource(String secondaryCategorySource) {
		this.secondaryCategorySource = secondaryCategorySource;
	}

    public CategorizingService getCategorizingService() {
        return categorizingService;
    }

    public void setCategorizingService(CategorizingService categorizingService) {
        this.categorizingService = categorizingService;
    }

    public Dictionary getNameDictionary() {
        return nameDictionary;
    }

    public void setNameDictionary(Dictionary nameDictionary) {
        this.nameDictionary = nameDictionary;
    }

    public String getDescriptionSource() {
        return descriptionSource;
    }

    public void setDescriptionSource(String descriptionSource) {
        this.descriptionSource = descriptionSource;
    }

    @Override
    public List<String> processInternal(MappingRule rule, InputData data) throws MappingException {
        List<String> resultValues = null;

        if (nameDictionary != null) {
            resultValues = nameDictionary.search(data.getPairs().get(nameSource), true);
            if (resultValues != null && !resultValues.isEmpty()){
                return resultValues;
            }
        }

        if (categoryDictionary == null) {
            throw new MappingException(this.getClass().getSimpleName() + ": Category dictionary not set");
        }

        String key = getCategoryKey(data);
        resultValues = categoryDictionary.search(key, false);

        if (resultValues != null && !resultValues.isEmpty()){
            return resultValues;
        }

        if (descriptionSource !=null && categorizingService != null){
            String description = data.getPairs().get(descriptionSource);

            Integer categoryId = categorizingService.findCategory(description);
            if (categoryId != null){
                return Collections.singletonList(String.valueOf(categoryId));
            }
        }

        return Collections.emptyList();
    }
    
    private String getCategoryKey(InputData data){
    	String key = data.getPairs().get(categorySource);
    	if (!StringUtils.isEmpty(secondaryCategorySource)){
    		String secondaryKey = data.getPairs().get(secondaryCategorySource);
    		key = StringUtils.isEmpty(secondaryKey) ? key : (key + " | " + secondaryKey);
    	}
    	return key;
    }
}
