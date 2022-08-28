package com.example.demo;

import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBatchTest
@RunWith(SpringRunner.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DemoApplication.class })
@TestPropertySource("classpath:application-test.properties")
class DemoApplicationTests {
	
	private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDatasource(DataSource datasource) {
        jdbcTemplate = new JdbcTemplate(datasource);
    }
	
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;

	@BeforeEach
    public void setUp() throws Exception {
		jobRepositoryTestUtils.removeJobExecutions();
		
        Arrays.asList(
                "DELETE FROM CUSTOMER_ABC;")
                .forEach(s -> jdbcTemplate.execute(s));
    }
	
	
	@Test
	public void testJob() throws Exception {
		// given
		JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParameters();

		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

		// then
		Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
	}
}