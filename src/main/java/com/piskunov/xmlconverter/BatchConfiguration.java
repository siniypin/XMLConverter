package com.piskunov.xmlconverter;

import com.piskunov.xmlconverter.mapping.*;
import com.piskunov.xmlconverter.unmarshal.CSVLineMapper;
import com.piskunov.xmlconverter.unmarshal.XMLUnmarshaller;
import com.thoughtworks.xstream.converters.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vladimir Piskunov on 2/27/16.
 */

@Configuration
@EnableBatchProcessing
@EnableTransactionManagement(proxyTargetClass = true)
@ImportResource("file:mapping/*.xml")
public class BatchConfiguration {
    private static final String SESSION_KEY = "session";

    private final static Log log = LogFactory.getLog(BatchConfiguration.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public MultiResourceItemReader dataSourceReader() throws IOException {
        MultiResourceItemReader mReader = new MultiResourceItemReader();
        return mReader;
    }

    @Bean
    public StaxEventItemReader<InputData> xmlReader(Unmarshaller unmarshaller) {
        StaxEventItemReader<InputData> reader = new StaxEventItemReader<>();
        reader.setFragmentRootElementName("product");
        reader.setUnmarshaller(unmarshaller);
        return reader;
    }

    @Bean
    public Unmarshaller unmarshaller(Converter inputDataConverter) {
        XStreamMarshaller unmarshaller = new XStreamMarshaller();
        HashMap<String, Class> aliases = new HashMap<>();
        aliases.put("product", InputData.class);
        unmarshaller.setAliases(aliases);
        unmarshaller.setConverters(inputDataConverter);
        return unmarshaller;
    }

    @Bean
    public Converter inputDataConverter() {
        return new XMLUnmarshaller();
    }

    @Bean
    public FlatFileItemWriter<OutputData> csvWriter() {
        FlatFileItemWriter<OutputData> writer = new FlatFileItemWriter<>();
        writer.setShouldDeleteIfExists(true);
        BeanWrapperFieldExtractor<OutputData> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
        beanWrapperFieldExtractor.setNames(new String[]{"result"});
        DelimitedLineAggregator<OutputData> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);
        writer.setLineAggregator(delimitedLineAggregator);
        return writer;
    }

    @Bean
    public FlatFileItemReader<InputData> csvReader(LineMapper lineMapper) {
        FlatFileItemReader<InputData> reader = new FlatFileItemReader<>();
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public CSVLineMapper lineMapper() {
        return new CSVLineMapper();
    }

    @Bean
    public MappingProcessor processor() {
        return new MappingProcessor();
    }

    @Bean
    public Job job(Step step, JobCleanUpListener jobCleanUpListener) throws IOException {
        return jobBuilderFactory.get("Job").listener(jobCleanUpListener).incrementer(new RunIdIncrementer()).flow(step)
                .end().build();
    }

    @Bean
    public Step Step(MappingProcessor processor, ItemWriter<OutputData> csvWriter, ItemReader dataSourceReader)
            throws IOException {
        return stepBuilderFactory.get("Step").allowStartIfComplete(true).<InputData, OutputData>chunk(1)
                .reader(dataSourceReader).processor(processor).writer(csvWriter).build();
    }

    @Bean
    JobCleanUpListener jobCleanUpListener() {
        return new JobCleanUpListener();
    }

    @Bean
    MappingStatistics mappingStatistics() {
        return new MappingStatistics();
    }

}
