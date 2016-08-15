package com.piskunov.xmlconverter.unmarshal;

import com.piskunov.xmlconverter.mapping.InputData;
import org.springframework.batch.item.file.LineMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Created by Vladimir Piskunov on 3/15/16.
 */
public class CSVLineMapper implements LineMapper {

    private static List<String> headers;

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    private String delimiter = ",";

    @Override
    public InputData mapLine(String s, int i) throws Exception {

        InputData data = new InputData();
        Scanner scanner = new Scanner(s);
        scanner.useDelimiter(delimiter);

        if(i == 1){
            headers = new LinkedList<>();
            //fill headers
            while(scanner.hasNext()){
                headers.add(trimColons(scanner.next()));
            }
        } else {
            for(String header: headers) {
                if(scanner.hasNext()){
                    data.getPairs().put(header, trimColons(scanner.next()));
                }
            }
            data.setSource(s);
        }
        return data;
    }

    private String trimColons(String s){
        if (s == null || s.isEmpty()){
            return s;
        }

        if (s.startsWith("\"")){
            return s.length() > 2 && s.endsWith("\"") ? s.substring(1,  s.length() - 1) : s;
        }

        if (s.startsWith("\'")){
            return s.length() > 2 && s.endsWith("\'") ? s.substring(1,  s.length() - 1) : s;
        }

        return s;
    }
}
