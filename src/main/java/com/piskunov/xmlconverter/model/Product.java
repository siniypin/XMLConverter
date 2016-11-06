package com.piskunov.xmlconverter.model;

import com.aggregator.common.model.BaseEntity;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.de.GermanStemFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by echo on 02/11/2016.
 */
@Entity
@Indexed
@AnalyzerDefs({
        @AnalyzerDef(name = "de",
                tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
                filters = {
                        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
//                        @TokenFilterDef(factory = StopFilterFactory.class, params = {
//                                @Parameter(name = "words", value = "org/apache/lucene/analysis/snowball/german_stop.txt"),
//                                @Parameter(name = "format", value = StopFilterFactory.FORMAT_SNOWBALL),
//                                @Parameter(name = "ignoreCase", value = "true")
//                        }),
                        @TokenFilterDef(factory = StopFilterFactory.class, params = {
                                @Parameter(name = "words", value = "german.stop"),
                                @Parameter(name = "format", value = StopFilterFactory.FORMAT_WORDSET),
                                @Parameter(name = "ignoreCase", value = "true")
                        }),
                        @TokenFilterDef(factory = GermanStemFilterFactory.class)
                })
})

@Table(schema = "xmlconverter", name = "products")
public class Product extends BaseEntity {
    @Field(index = Index.NO, analyze = Analyze.NO, store = Store.YES)
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "name")
    private String name;

    @Field(index = Index.YES, analyze = Analyze.YES, termVector = TermVector.YES, store = Store.NO)
    @Analyzer(definition = "de")
    @Column(name = "description")
    private String description;

//    @ManyToOne(fetch = FetchType.LAZY, optional = true)
//    @JoinColumn(name = "category_id", nullable = false, updatable = false, insertable = false)
//    private Category category;

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

//    public Category getCategory() {
//        return category;
//    }
//
//    public void setCategory(Category category) {
//        this.category = category;
//    }
}
