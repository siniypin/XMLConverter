package com.piskunov.xmlconverter;

import com.piskunov.xmlconverter.mapping.*;
import com.piskunov.xmlconverter.unmarshal.CSVLineMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vladimir Piskunov on 3/15/16.
 */

public class JobWrapper {

	@Autowired
	Job job;

	@Autowired
	MultiResourceItemReader dataSourceReader;

	@Autowired
	public ResourceLoader resourceLoader;

	@Autowired
	StaxEventItemReader<InputData> xmlReader;

	@Autowired
	FlatFileItemReader<InputData> csvReader;

	@Autowired
	FlatFileItemWriter<OutputData> csvWriter;

	@Autowired
	MappingProcessor processor;

	@Autowired
	CSVLineMapper lineMapper;

	public final String XML_TYPE = "xml";
	public final String CSV_TYPE = "csv";

	private boolean active = true;

	private String inputDataType = XML_TYPE;
	private String outputDataType = CSV_TYPE;
	private String inputFolder = "input";
	private String outputFolder = "output";
	private DataMapping dataMapping;

	private String outputFileName = "report";
	private String inputCSVDelimiter = ",";
	private String sessionName;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getInputDataType() {
		return inputDataType;
	}

	public void setInputDataType(String inputDataType) {
		this.inputDataType = inputDataType;
	}

	public String getOutputDataType() {
		return outputDataType;
	}

	public void setOutputDataType(String outputDataType) {
		this.outputDataType = outputDataType;
	}

	public String getInputFolder() {
		return inputFolder;
	}

	public void setInputFolder(String inputFolder) {
		this.inputFolder = inputFolder;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	public DataMapping getDataMapping() {
		return dataMapping;
	}

	public void setDataMapping(DataMapping dataMapping) {
		this.dataMapping = dataMapping;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getInputCSVDelimiter() {
		return inputCSVDelimiter;
	}

	public void setInputCSVDelimiter(String inputCSVDelimiter) {
		this.inputCSVDelimiter = inputCSVDelimiter;
	}

	public Job getJob() throws MappingException, IOException {

		if (!active) {
			return null;
		}

		if (dataMapping == null) {
			throw new MappingException("Data Mapping is not set for JobConfigurator");
		}

		processor.resetProcessor();

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
		Date now = new Date();
		String fileBaseName = sessionName.replace(" ", "") + "_" + sdfDate.format(now);

		dataSourceReader.setResources(ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
				.getResources("file:" + inputFolder + File.separator + "*." + inputDataType));

		if (inputDataType.equals(XML_TYPE)) {
			dataSourceReader.setDelegate(xmlReader);
		} else if (inputDataType.equals(CSV_TYPE)) {
			lineMapper.setDelimiter(inputCSVDelimiter);
			dataSourceReader.setDelegate(csvReader);
		}

		String baseFolder = File.separator + sessionName.replace(" ", "") + File.separator;

		if (outputDataType.equals(CSV_TYPE)) {
			csvWriter.setResource(new FileSystemResource(
					outputFolder + File.separator + fileBaseName + "_" + outputFileName + "." + outputDataType));
		}

		processor.setDataMapping(dataMapping);
		processor.setSkippedItemsLog(new FileSystemResource("logs" + baseFolder + fileBaseName + "_skipped.log"));

		return job;
	}
}
