package com.piskunov.xmlconverter.search;

import com.piskunov.xmlconverter.model.Product;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.engine.ProjectionConstants;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
            fullTextEntityManager.createIndexer().purgeAllOnStart(true).startAndWait();
        } catch (InterruptedException e) {
            log.error("Failed to index products...", e);
        }
    }

    @Transactional
    public Queue<Pair<Integer, Float>> findCategoriesAlike(String description) {
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

        SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
        QueryBuilder qb = searchFactory.buildQueryBuilder().forEntity(Product.class).get();

        Product product = new Product();
        product.setDescription(description);

        org.apache.lucene.search.Query query = qb
                .moreLikeThis()
                .comparingField("description")
                .toEntity(product)
                .createQuery();
//                qb.phrase().onField("description").sentence(description).createQuery();

        FullTextQuery persistenceQuery =
                fullTextEntityManager.createFullTextQuery(query, Product.class);

        persistenceQuery.setMaxResults(10);

        persistenceQuery.setProjection("categoryId", ProjectionConstants.SCORE);

        Map<Integer, Float> queryResult = ((List<Object[]>)persistenceQuery.getResultList()).stream()
                .collect(toMap(r -> (Integer) r[0], r -> (Float) r[1], Float::sum));

        PriorityQueue<Pair<Integer, Float>> result = new PriorityQueue<>(
                (a, b) -> Float.compare(b.getRight(), a.getRight()));

        result.addAll(queryResult.keySet().stream()
                .map(k -> Pair.of(k, queryResult.get(k))).collect(toList()));

        return result;
    }
}

