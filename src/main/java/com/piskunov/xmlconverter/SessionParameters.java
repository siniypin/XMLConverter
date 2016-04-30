package com.piskunov.xmlconverter;

import java.util.Map;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class SessionParameters {

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {

        for(Map.Entry<String, String> entry: parameters.entrySet()){
            String value = System.getProperty(entry.getKey());
            if(value != null){
                entry.setValue(value);
            }
        }
        this.parameters = parameters;
    }

    public String getProperty(String property){
        return parameters.get(property);
    }

    private Map<String, String> parameters;



}
