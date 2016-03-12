package com.piskunov.xmlconverter.mapping.adapters;

import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
    public void init() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(dictionaryFile.getInputStream()))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 1)
                    dictionary.put(values[0], values[1]);
            }
        }
    }

    public List<String> search(String key, boolean searchByRegExp) {

        ArrayList<String> ret = new ArrayList<>();

        for (String dictionaryKey : dictionary.keySet()) {

            Logger.getLogger(getClass().getName()).severe("" + searchByRegExp + " " + key + "/" + dictionaryKey);


            if ((searchByRegExp && key.matches(dictionaryKey))
                    || (!searchByRegExp && key.equals(dictionaryKey))) {

                Logger.getLogger(getClass().getName()).severe(dictionary.get(dictionaryKey));
                ret.add(dictionary.get(dictionaryKey));
                Logger.getLogger(getClass().getName()).severe("true");
            }
            else {
                Logger.getLogger(getClass().getName()).severe("false");
            }

        }
        return ret;
    }

}
