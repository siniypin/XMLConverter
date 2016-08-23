package com.piskunov.xmlconverter.unmarshal;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.text.StrTokenizer;
import org.springframework.batch.item.file.LineMapper;

import com.piskunov.xmlconverter.mapping.InputData;

/**
 * Created by Vladimir Piskunov on 3/15/16.
 */
public class CSVLineMapper implements LineMapper {

	private static List<String> headers;

	private String delimiter = ",";
	private String quote = "\"";

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public InputData mapLine(String s, int i) throws Exception {

		InputData data = new InputData();
		StrTokenizer tokenizer = new StrTokenizer(s, delimiter.charAt(0), quote.charAt(0));
		tokenizer.setIgnoreEmptyTokens(false);

		if (i == 1) {
			headers = new LinkedList<>();
			// fill headers

			while (tokenizer.hasNext()) {
				headers.add(trimColons(tokenizer.next()));
			}
		} else {
			String[] tokens = tokenizer.getTokenArray();
			for (int j = 0; j < headers.size(); j++) {
				data.getPairs().put(headers.get(j), trimColons(tokens[j]));
			}
			data.setSource(s);
		}
		return data;
	}

	private String trimColons(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}

		if (s.startsWith("\"")) {
			return s.length() > 2 && s.endsWith("\"") ? s.substring(1, s.length() - 1) : s;
		}

		if (s.startsWith("\'")) {
			return s.length() > 2 && s.endsWith("\'") ? s.substring(1, s.length() - 1) : s;
		}

		return s;
	}
}
