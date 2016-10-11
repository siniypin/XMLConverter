package com.piskunov.xmlconverter.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.core.io.Resource;

/**
 * Created by Vladimir Piskunov on 3/11/16.
 */
public class Dictionary {

	private Map<String, List<String>> dictionary = new HashMap<>();
	private Map<String, Pattern> keyPatterns = new HashMap<>();

	private Resource dictionaryFile;

	public Resource getDictionaryFile() {
		return dictionaryFile;
	}

	public void setDictionaryFile(Resource dictionaryFile) {
		this.dictionaryFile = dictionaryFile;
	}

	@PostConstruct
	public void init() throws Exception {

		Scanner scanner = new Scanner(dictionaryFile.getFile());
		// scanner.useDelimiter("\n");

		while (scanner.hasNextLine()) {

			Scanner lineScanner = new Scanner(scanner.nextLine());
			lineScanner.useDelimiter(",");

			String key = null;
			ArrayList<String> values = new ArrayList<>();

			if (lineScanner.hasNext())
				key = lineScanner.next();

			while (lineScanner.hasNext()) {
				values.add(lineScanner.next());
			}

			if (key == null || values.size() == 0)
				continue;

			List<String> currentValues = dictionary.get(key);

			if (currentValues != null) {
				currentValues.addAll(values);
				dictionary.put(key, currentValues);
			} else {
				dictionary.put(key, values);
			}
		}

	}

	public List<String> search(String key, boolean searchByRegExp) {
		// collect all keys that match and choose one with the longest match/key

		Map<Integer, String> ret = new HashMap<>();

		if (key == null)
			return new ArrayList<>();

		if (!searchByRegExp) {
			return dictionary.get(key);
		}

		for (String dictionaryKey : dictionary.keySet()) {
			Pattern pattern = keyPatterns.get(dictionaryKey);
			if (pattern == null) {
				pattern = Pattern.compile(dictionaryKey);
				keyPatterns.put(dictionaryKey, pattern);
			}
			Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
				ret.put(matcher.end() - matcher.start(), dictionary.get(dictionaryKey).get(0));
			}
		}

		if (ret.keySet().isEmpty()) {
			return new ArrayList<>();
		} else {
			return new ArrayList<String>() {
				{
					ret.get(ret.keySet().stream().max(Integer::compare).get());
				}
			};
		}
	}
}
