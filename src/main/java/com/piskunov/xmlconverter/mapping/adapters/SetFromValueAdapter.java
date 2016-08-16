package com.piskunov.xmlconverter.mapping.adapters;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;
import com.piskunov.xmlconverter.mapping.ValueSet;

/**
 * Created by echo on 15/08/16.
 */
public class SetFromValueAdapter extends BaseMappingAdapter {
	private ValueSet valueSet;

	public void setValueSet(ValueSet valueSet) {
		this.valueSet = valueSet;
	}

	@Override
	protected List<String> processInternal(MappingRule rule, InputData data) throws MappingException {
		String sourceValue = data.getPairs().get(source);
		return Arrays.asList(StringUtils.collectionToCommaDelimitedString(valueSet.search(sourceValue)));
	}
}
