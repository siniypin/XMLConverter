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
        List<Pair<Integer, Float>> result = categorizingService
                .findCategoriesAlikeByProduct("Designs zu fast jedem Bett kombiniert werden. Ob im Schlaf-, Jugend- oder Kinderzimmer das Nachtschränkchen macht überall eine gute Figur");

        assertThat(result, is(not(empty())));

        assertThat(result.get(0).getLeft(), is(equalTo(784)));
    }
}
