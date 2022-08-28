package com.example.demo;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableBatchProcessing
@SpringBootApplication
public class DemoApplication {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFatory;
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.start(step())
				.build();
	}

	private int CHUNK_SIZE = 5;

	@Bean
	public Step step() {
		return stepBuilderFatory.get("step")
				.<Customer, Customer>chunk(CHUNK_SIZE)
				.reader(itemReader())
				.writer(itemWriter())
				.build();
	}

	private MyBatisCursorItemReader<Customer> itemReader() {
		return new MyBatisCursorItemReaderBuilder<Customer>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.example.demo.CustomerMapper.selectCustomer")
				.build();
	}

	private MyBatisBatchItemWriter<Customer> itemWriter() {
		return new MyBatisBatchItemWriterBuilder<Customer>()
				.sqlSessionFactory(sqlSessionFactory)
				.statementId("com.example.demo.CustomerMapper.insertCustomer")
				.build();
	}


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}