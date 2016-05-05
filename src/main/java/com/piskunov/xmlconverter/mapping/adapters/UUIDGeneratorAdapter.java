package com.piskunov.xmlconverter.mapping.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.uuid.Generators;
import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class UUIDGeneratorAdapter extends BaseMappingAdapter {

	private HashSet<String> duplicateGuard = new HashSet<String>();
	private String baseValue;
	private List<String> baseSources;

	public String getBaseValue() {
		return baseValue;
	}

	public void setBaseValue(String baseValue) {
		this.baseValue = baseValue;
	}

	public List<String> getBaseSources() {
		return baseSources;
	}

	public void setBaseSources(List<String> baseSources) {
		this.baseSources = baseSources;
	}

	@Override
	public List<String> processInternal(MappingRule rule, InputData data) throws MappingException {
		List<String> resultValues = new ArrayList<>();
		if (baseValue == null && baseSources == null)
			throw new MappingException(this.getClass().getSimpleName() + ": Adapter parameters are not set");

		String base = "";

		if (baseValue != null) {
			base += baseValue;
		}

		for (String source : baseSources) {
			String v = data.getPairs().get(source);
			if (v != null) {
				base += v;
			}
		}

		String uid = Generators.nameBasedGenerator().generate(base).toString();
		if (duplicateGuard.contains(uid)) {
			throw new MappingException("Non unique record: " + base);
		} else {
			duplicateGuard.add(uid);
			resultValues.add(uid);
			return resultValues;
		}
	}
}
