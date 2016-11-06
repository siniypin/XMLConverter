package com.piskunov.xmlconverter.model;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by echo on 06/11/2016.
 */
public class ProductListFieldBridge implements FieldBridge {
    @Override
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
        if (value == null) {
            return;
        }

        List<Product> products = (List<Product>) value;

        String categoryDescription = products.stream()
                .map(Product::getDescription).collect(Collectors.joining("\n"));

//        Field field = new StringField(name, categoryDescription, luceneOptions.getStore());
//        field.setBoost(luceneOptions.getBoost());
//        field.set
//        luceneOptions.a
//
//
//        document.add(new StringField(name, categoryDescription, Field.Store.NO));
    }
}
