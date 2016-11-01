package com.piskunov.xmlconverter.model;

import com.aggregator.common.model.BaseEntity;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by echo on 02/11/2016.
 */
@Entity
@Indexed
@Table(schema = "xmlconverter", name = "products")
public class Product extends BaseEntity {
    @Column(name = "category_id")
    private int categoryId;

    @Field
    @Column(name = "name")
    private String name;

    @Field
    @Column(name = "description")
    private String description;


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
