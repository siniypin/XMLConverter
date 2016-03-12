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
import java.util.logging.Logger;

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

    @Bean
    public MultiResourceItemReader multiResourceItemReader(StaxEventItemReader<InputData> xmlReader) throws IOException {
        MultiResourceItemReader mReader = new MultiResourceItemReader();
        mReader.setResources(ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("file:xml/*.xml"));
        mReader.setDelegate(xmlReader);
        return mReader;
    }


    @Bean
    public StaxEventItemReader<InputData> xmlReader(Unmarshaller unmarshaller) {
        StaxEventItemReader<InputData> reader = new StaxEventItemReader<InputData>();
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
    public MappingProcessor processor() {
        MappingProcessor processor =  new MappingProcessor();
        processor.setSkippedItemsLog(new FileSystemResource("logs/failedRecords.log"));
        return processor;
    }

    @Bean
    public Job xml2csvJob(Step xml2csvJobStep) throws IOException {
        return jobBuilderFactory.get("xml2csvJob")
                .incrementer(new RunIdIncrementer())
                .flow(xml2csvJobStep)
                .end()
                .build();
    }

    @Bean
    public Step xml2csvJobStep(MappingProcessor processor, FlatFileItemWriter<DataMapping> csvWriter, MultiResourceItemReader multiResourceItemReader) throws IOException {
        return stepBuilderFactory.get("xml2csvJobStep")
                .<InputData, DataMapping> chunk(1)
                .reader(multiResourceItemReader)
                .processor(processor)
                .writer(csvWriter)
                .build();
    }


}
