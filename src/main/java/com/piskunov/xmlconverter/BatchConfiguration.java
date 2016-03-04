package com.piskunov.xmlconverter;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.mapping.DataMapping;
import com.piskunov.xmlconverter.mapping.MappingProcessor;
import com.piskunov.xmlconverter.mapping.adapters.MappingAdapter;
import com.piskunov.xmlconverter.xmlprocessing.XMLUnmarshaller;
import com.thoughtworks.xstream.converters.Converter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Vladimir Piskunov on 2/27/16.
 */

@Configuration
@EnableBatchProcessing
@ImportResource("file:mapping/mapping.xml")

public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ResourceLoader resourceLoader;


            /*<bean id="multiResourceReader"
    class=" org.springframework.batch.item.file.MultiResourceItemReader">
    <property name="resources" value="file:csv/inputs/domain-*.csv" />
    <property name="delegate" ref="flatFileItemReader" />
    </bean>
    */


    @Bean
    public MultiResourceItemReader multiResourceItemReader() {

        MultiResourceItemReader mReader = new MultiResourceItemReader();

        try {
            mReader.setResources(ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("file:xml/*.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mReader.setDelegate(xmlReader());
        return mReader;
    }


    @Bean
    public StaxEventItemReader<InputData> xmlReader() {
        StaxEventItemReader<InputData> reader = new StaxEventItemReader<InputData>();
        reader.setFragmentRootElementName("product");
     //   reader.setResource(new FileSystemResource("xml/test.xml"));
        reader.setUnmarshaller(unmarshaller());
        return reader;
    }


    @Bean
    public Unmarshaller unmarshaller() {
        XStreamMarshaller unmarshaller = new XStreamMarshaller();

        HashMap<String, Class> aliases = new HashMap<>();
        aliases.put("product", InputData.class);
        unmarshaller.setAliases(aliases);

        unmarshaller.setConverters(inputDataConverter());

        return unmarshaller;
    }

    @Bean
    public Converter inputDataConverter() {
        return new XMLUnmarshaller();
    }

   /*
    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(InputData.class);

        return unmarshaller;
    }
    */

    @Bean
    public MappingProcessor processor() {
        MappingProcessor processor =  new MappingProcessor();
        processor.setSkippedItemsLog(new FileSystemResource("logs/failedRecords.log"));
        return processor;
    }

    @Bean
    public FlatFileItemWriter<DataMapping> csvWriter() {
        FlatFileItemWriter<DataMapping> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("cvs/report.csv"));
        writer.setShouldDeleteIfExists(true);

        BeanWrapperFieldExtractor<DataMapping> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();

        beanWrapperFieldExtractor.setNames(new String[]{"result"});

        DelimitedLineAggregator<DataMapping> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);

        writer.setLineAggregator(delimitedLineAggregator);

        return writer;
    }

    @Bean
    public Job xml2csvJob() {
        return jobBuilderFactory.get("xml2csvJob")
                .incrementer(new RunIdIncrementer())
                .flow(xml2csvJobStep())
                .end()
                .build();
    }


    @Bean
    public Step xml2csvJobStep() {
        return stepBuilderFactory.get("xml2csvJobStep")
                .<InputData, DataMapping> chunk(1)
                .reader(multiResourceItemReader())
                .processor(processor())
                .writer(csvWriter())
                .build();
    }


}
