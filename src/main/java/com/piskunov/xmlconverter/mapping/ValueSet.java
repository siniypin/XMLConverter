package com.piskunov.xmlconverter.mapping;

import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by Vladimir Piskunov on 3/11/16.
 */
public class ValueSet {
    private Set<String> set = new HashSet<>();

    private Resource file;

    public Resource getFile() {
        return file;
    }

    public void setDictionaryFile(Resource file) {
        this.file = file;
    }

    @PostConstruct
    public void init() throws Exception {
        Scanner scanner = new Scanner(file.getFile());

        while (scanner.hasNextLine()) {
            String value = scanner.nextLine();
            if (value != null) {
                value = value.trim();
                if (!value.isEmpty()) {
                    set.add(value.toLowerCase());
                }
            }
        }
    }

    public List<String> search(String s) {
        List<String> result = new ArrayList<>();

        s = s.toLowerCase();
        String[] words = s.split(" ");

        for (String word : words){
            if (set.contains(word) && !result.contains(word)){
                result.add(word);
            }
        }

        return result;
    }
}
