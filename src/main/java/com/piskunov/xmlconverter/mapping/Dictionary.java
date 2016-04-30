package com.piskunov.xmlconverter.mapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.core.io.Resource;

/**
 * Created by Vladimir Piskunov on 3/11/16.
 */
public class Dictionary {

    private Map<String, String> dictionary = new HashMap<>();

    private Resource dictionaryFile;

    public Resource getDictionaryFile() {
        return dictionaryFile;
    }

    public void setDictionaryFile(Resource dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }

    @PostConstruct
    public void init() throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(dictionaryFile.getInputStream()))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 1) {
                    dictionary.put(values[0], values[1]);
                }
            }
        }
    }

    public List<String> search(String key, boolean searchByRegExp) {

        ArrayList<String> ret = new ArrayList<>();

        if(key == null)
            return ret;

        for (String dictionaryKey : dictionary.keySet()) {
            if ((searchByRegExp && key.matches(dictionaryKey))
                    || (!searchByRegExp && key.equals(dictionaryKey))) {
                ret.add(dictionary.get(dictionaryKey));
            }
        }
        return ret;
    }

}
