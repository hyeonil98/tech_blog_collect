package com.tech.blog.batch;

import com.tech.blog.crawling.TechBlogCrawler;
import com.tech.blog.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CrawlingSiteReader implements ItemReader<Post>{
    private final List<TechBlogCrawler> crawlers;
    private Iterator<Post> iterator;

    @Override
    public Post read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (iterator == null) {
            List<Post> postList = new ArrayList<>();
            for (TechBlogCrawler crawler : crawlers) {
                try {
                    List<Post> posts = crawler.crawling();
                    postList.addAll(posts);
                }catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            iterator = postList.iterator();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }
}
