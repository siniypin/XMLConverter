package com.piskunov.xmlconverter.mapping.adapters;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.MappingException;
import com.piskunov.xmlconverter.mapping.MappingRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Piskunov on 2/29/16.
 */
public class DictionaryAdapter implements MappingAdapter {


    private Resource resource;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public List<String> process(MappingRule rule, InputData data) throws MappingException {

        List<String> resultValues = new ArrayList<>();

        String key = data.getPairs().get(rule.getSource());

        if(key == null) {
            throw new MappingException(this.getClass().getSimpleName() + ": Source not found: " + rule.getSource());
        }


        BufferedReader br = null;

        try{
            InputStream is = resource.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");

                if(key.equals(values[0])) {

                    for(int i = 1; i < values.length; i++){
                        resultValues.add(values[i]);
                    }
                }
            }


        }catch(IOException e){
            e.printStackTrace();
        } finally {
            if(br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }


        return resultValues;
    }
}
