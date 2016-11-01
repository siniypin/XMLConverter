package com.piskunov.xmlconverter.search;

import com.piskunov.xmlconverter.model.Product;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by echo on 02/11/2016.
 */
@Service
public class SearchService {
    private final static Log log = LogFactory.getLog(SearchService.class);

    @PersistenceContext
    private EntityManager entityManager;


    public void index() {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            log.error("Failed to index products...", e);
        }
    }

    public void search() {
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);
        entityManager.getTransaction().begin();

        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Product.class).get();

        org.apache.lucene.search.Query query = qb
                .keyword()
                .onFields("title", "subtitle", "authors.name")
                .matching("Java rocks!")
                .createQuery();

        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(query, Product.class);

        List result = persistenceQuery.getResultList();

        entityManager.getTransaction().commit();
    }
}
