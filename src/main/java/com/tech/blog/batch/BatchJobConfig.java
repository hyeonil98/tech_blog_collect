package com.tech.blog.batch;

import com.tech.blog.crawling.TechBlogCrawler;
import com.tech.blog.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BatchJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private CrawlingSiteReader crawlingSiteReader;
    private PostDeduplicationProcessor postDeduplicationProcessor;
    private PostWriter postWriter;

    @Bean
    public Job crawlingJob() {
        return new JobBuilder("crawler", jobRepository)
                .start(crawling())
                .build();
    }

    @Bean
    public Step crawling() {
        return new StepBuilder("newsCrawlingStep", jobRepository)
                .<Post, Post>chunk(10, transactionManager)
                .reader(crawlingSiteReader)
                .processor(postDeduplicationProcessor)
                .writer(postWriter)
                .build();
    }
}
