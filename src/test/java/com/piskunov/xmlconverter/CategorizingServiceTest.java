package com.piskunov.xmlconverter;

import com.piskunov.xmlconverter.categorizer.CategorizingService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Queue;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by echo on 03/11/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class CategorizingServiceTest {
    @Autowired
    private CategorizingService categorizingService;


    @Test
    public void testMoreLikeThis(){
        String description = "\"Designs zu fast jedem Bett kombiniert werden. Ob im Schlaf-, Jugend- oder Kinderzimmer das Nachtschränkchen macht überall eine gute Figur\"";

        List<Pair<Integer, Float>> byProduct = categorizingService
                .findCategoriesAlikeByProduct(description);

        List<Pair<Integer, Float>> byCategory = categorizingService
                .findCategoriesAlikeByCategory(description);

        assertThat(byProduct, is(not(empty())));

        assertThat(byProduct.get(0).getLeft(), is(equalTo(784)));
    }
}
