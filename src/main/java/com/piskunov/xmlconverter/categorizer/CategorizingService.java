package com.piskunov.xmlconverter.categorizer;

import com.piskunov.xmlconverter.model.Category;
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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Created by echo on 02/11/2016.
 */
@Service
public class CategorizingService implements ApplicationContextAware{
    private static final int MAX_PRODUCTS = 20;
    private static final int MIN_DESCRIPTION_SIZE = 20;
    private static final float MIN_SCORE_DIF = 0.3f;
    private final static Log log = LogFactory.getLog(CategorizingService.class);
    private final static String MISSING_CATEGORY_IDS_QUERY =
            "SELECT DISTINCT p.category_id " +
                    "FROM xmlconverter.products p " +
                    "LEFT JOIN xmlconverter.categories c ON p.category_id = c.id " +
                    "WHERE c.id IS NULL";

    @PersistenceContext
    private EntityManager entityManager;

    private CategorizingService self;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.self = applicationContext.getBean(CategorizingService.class);
    }

    public void rebuildAll() throws InterruptedException {
        self.createMissingCategories();
        self.reindex();
    }

    @Transactional
    public void createMissingCategories() {
        log.info("Looking for missing categories...");
        List<Integer> missingIds = entityManager.createNativeQuery(MISSING_CATEGORY_IDS_QUERY).getResultList();

        missingIds.stream()
                .map(Category::new)
                .forEach(entityManager::merge);

        log.info("Created " + missingIds.size() + " missing categories...");
    }

    public void reindex() throws InterruptedException {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        log.info("Reindexing entities...");
        fullTextEntityManager.createIndexer().purgeAllOnStart(true).startAndWait();
        log.info("Reindexing finished...");
    }

    public Integer findCategory(String description){
        if (description == null || description.isEmpty()){
            log.warn("Empty description for category search...");
            return null;
        }

        if (description.length() < MIN_DESCRIPTION_SIZE){
            log.warn("Very short description: " + description);
        }

        List<Pair<Integer, Float>> results = findCategoriesAlikeByProduct(description);

        if (results.isEmpty()){
            log.warn("No category matching: " + description);
        }

        if (results.size() > 1 && results.get(0).getRight() - results.get(1).getRight() < MIN_SCORE_DIF){
            log.warn("Ambiguous category matching: " + description
                    + "\nresults are almost even: " + results.get(0) + " vs " + results.get(1));
        }

        return results.get(0).getLeft();
    }

    public List<Pair<Integer, Float>> findCategoriesAlikeByProduct(String description) {
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

        FullTextQuery persistenceQuery =
                fullTextEntityManager.createFullTextQuery(query, Product.class);

        persistenceQuery.setMaxResults(MAX_PRODUCTS);

        persistenceQuery.setProjection("categoryId", ProjectionConstants.SCORE);

        Map<Integer, Float> queryResult = ((List<Object[]>) persistenceQuery.getResultList()).stream()
                .collect(toMap(r -> (Integer) r[0], r -> (Float) r[1], Float::sum));

        List<Pair<Integer, Float>> result = queryResult.keySet().stream()
                .map(k -> Pair.of(k, queryResult.get(k))).collect(toList());

        Collections.sort(result, (a, b) -> Float.compare(b.getRight(), a.getRight()));

        return result;
    }

    public List<Pair<Integer, Float>> findCategoriesAlikeByCategory(String description) {
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

        SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
        QueryBuilder qb = searchFactory.buildQueryBuilder().forEntity(Category.class).get();

        Product product = new Product();
        product.setDescription(description);

        Category category = new Category();
        category.setProducts(Collections.singletonList(product));

        org.apache.lucene.search.Query query = qb
                .moreLikeThis()
                .comparingField("products")
                .toEntity(category)
                .createQuery();

        FullTextQuery persistenceQuery =
                fullTextEntityManager.createFullTextQuery(query, Category.class);

        persistenceQuery.setMaxResults(10);

        persistenceQuery.setProjection("id", ProjectionConstants.SCORE);

        List<Pair<Integer, Float>> result = ((List<Object[]>) persistenceQuery.getResultList()).stream()
                .map(r -> Pair.of((Integer) r[0], (Float) r[1])).collect(toList());

        Collections.sort(result, (a, b) -> Float.compare(b.getRight(), a.getRight()));

        return result;
    }
}

