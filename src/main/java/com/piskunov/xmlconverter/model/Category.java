package com.piskunov.xmlconverter.model;

import com.aggregator.common.model.BaseEntity;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.util.List;

/**
 * Created by echo on 02/11/2016.
 */
//@Entity
//@Indexed
//@Table(schema = "xmlconverter", name = "categories")
public class Category {
    @Id
    private int id;

    @Field(index = Index.YES, analyze = Analyze.YES, termVector = TermVector.YES, store = Store.NO)
    @Analyzer(definition = "de")
    @FieldBridge(impl = ProductListFieldBridge.class)
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;

    public Category() {
    }

    public Category(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
