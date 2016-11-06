package com.piskunov.xmlconverter;

import com.piskunov.xmlconverter.search.SearchService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Queue;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by echo on 03/11/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class SearchServiceTest {
    @Autowired
    private SearchService searchService;


    @Test
    public void testMoreLikeThis(){
        Queue<Pair<Integer, Float>> result = searchService
                .findCategoriesAlike("Designs zu fast jedem Bett kombiniert werden. Ob im Schlaf-, Jugend- oder Kinderzimmer das Nachtschränkchen macht überall eine gute Figur");

        assertThat(result, is(not(empty())));

        assertThat(result.peek().getLeft(), is(equalTo(784)));
    }
}
