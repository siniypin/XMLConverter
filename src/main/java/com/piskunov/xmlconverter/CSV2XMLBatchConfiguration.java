package com.piskunov.xmlconverter;

import com.piskunov.xmlconverter.mapping.InputData;
import com.piskunov.xmlconverter.model.XMLModel;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.lang.reflect.Field;

/**
 * Created by Vladimir Piskunov on 2/27/16.
 *
 */


/*
todo remove: for test purposes only
 */

//@Configuration
//@EnableBatchProcessing

public class CSV2XMLBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<XMLModel> csvReader() {
        FlatFileItemReader<XMLModel> csvReader = new FlatFileItemReader<>();
        csvReader.setResource(new ClassPathResource("csv/test.csv"));

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        Field fields[] = InputData.class.getDeclaredFields();
        String fieldNames[] = new String[fields.length];
        for(int i = 0; i < fields.length; i++){
            fieldNames[i] = fields[i].getName();
        }

        tokenizer.setNames(fieldNames);

        DefaultLineMapper<XMLModel> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);

        BeanWrapperFieldSetMapper<XMLModel> fieldSetMapper = new BeanWrapperFieldSetMapper<>();

        fieldSetMapper.setTargetType(XMLModel.class);

        lineMapper.setFieldSetMapper(fieldSetMapper);
        csvReader.setLineMapper(lineMapper);

        return csvReader;
    }


    @Bean
    StaxEventItemWriter xmlWriter() {

        StaxEventItemWriter<XMLModel> xmlWriter = new StaxEventItemWriter<>();
        xmlWriter.setResource(new FileSystemResource("xml/test.xml"));
        xmlWriter.setRootTagName("products");

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XMLModel.class);

        xmlWriter.setMarshaller(marshaller);

        return xmlWriter;
    }


    @Bean
    public Job csv2xmlJob() {
        Job csv2xmlJob = jobBuilderFactory.get("csv2xmlJob")
                .incrementer(new RunIdIncrementer())
                .flow(csv2xmlJobStep())
                .end()
                .build();

        return csv2xmlJob;
    }


    @Bean
    public Step csv2xmlJobStep() {
        return stepBuilderFactory.get("csv2xmlJobStep")
                .<XMLModel, XMLModel> chunk(1)
                .reader(csvReader())
                .writer(xmlWriter())
                .build();
    }


}
