package com.example.batch.job;

import com.example.batch.entity.Customer;
import com.example.batch.listener.JobCompletionNotificationListener;
import com.example.batch.processor.CustomerProcessor;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CustomerJobConfig {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public JobCompletionNotificationListener jobCompletionNotificationListener() {
        return new JobCompletionNotificationListener();
    }

    @Bean
    public CustomerProcessor customerProcessor() {
        return new CustomerProcessor();
    }

    @Bean
    public JpaItemWriter<Customer> jpaWriter() {
        JpaItemWriter<Customer> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public FlatFileItemWriter<Customer> csvWriter() {
        // Ensure output directory exists
        File dir = new File("output");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new FlatFileItemWriterBuilder<Customer>()
                .name("customerCsvWriter")
                .resource(new FileSystemResource("output/customers.csv"))
                .delimited()
                .delimiter(",")
                .names("customerId", "firstName", "lastName", "email", "city", "country")
                .headerCallback(writer -> writer.write("customerId,firstName,lastName,email,city,country"))
                .build();
    }

    @Bean
    public CompositeItemWriter<Customer> compositeWriter() throws Exception {
        CompositeItemWriter<Customer> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(jpaWriter(), csvWriter()));
        return writer;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> reader(@Value("#{jobParameters['filePath']}") String path) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerReader")
                .resource(new FileSystemResource(path))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("index", "customerId", "firstName", "lastName", "company", "city", "country", "phone1", "phone2", "email", "subscriptionDate", "website")
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(reader(null))
                .writer(compositeWriter())
                .processor(customerProcessor())
                .build();
    }

    @Bean
    public Job importCustomerJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importCustomerJob", jobRepository)
                .start(step1)
                .listener(jobCompletionNotificationListener())
                .build();
    }
}
