package com.piskunov.xmlconverter.unmarshal;

import com.piskunov.xmlconverter.mapping.InputData;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Created by Vladimir Piskunov on 2/27/16.
 */
public class XMLUnmarshaller implements Converter {


    @Override
    public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {

    }

    @Override
    public InputData unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {

        InputData obj = new InputData();

        StringBuilder builder = new StringBuilder();

        String openTag = "<";
        String closeTag = ">";
        String openCloseTag = "</";

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            String value = reader.getValue();
            obj.getPairs().put(childNode, value);

            builder.append(openTag)
                    .append(childNode)
                    .append(closeTag)
                    .append(value)
                    .append(openCloseTag)
                    .append(childNode)
                    .append(closeTag);
            reader.moveUp();
        }

        obj.setSource(builder.toString());

        return obj;
    }

    @Override
    public boolean canConvert(Class aClass) {
        //we only need "Report" object
        return aClass.equals(InputData.class);
    }

}
