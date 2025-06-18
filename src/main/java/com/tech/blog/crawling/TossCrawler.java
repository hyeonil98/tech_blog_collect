package com.tech.blog.crawling;

import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import com.tech.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tech.blog.service.PostService.getCurrentDate;

@RequiredArgsConstructor
@Component("tossCrawler")
public class TossCrawler implements TechBlogCrawler {
    private final PostService postService;

    @Override
    public List<Post> crawling() throws ParseException {
        WebDriver driver = new ChromeDriver();
        String toss_url = "https://toss.tech/?page=";
        int totalSize = 0;
        List<Post> postList = postService.getAllPosts();

        for (int i = 1; i < 1000; i++) {
            driver.get(toss_url + i);

            if (driver.getCurrentUrl().contains("error")) {
                break;
            }


            WebElement element = driver.findElement(By.className("e143n5sn1"));
            List<WebElement> items = element.findElements(By.tagName("li"));
            for (WebElement item : items) {
                WebElement link = item.findElement(By.cssSelector("a"));
                String href = link.getAttribute("href");

                WebElement span_title = item.findElement(By.cssSelector("a > div > span:first-child"));
                String title = span_title.getText();

                WebElement span_time = item.findElement(By.cssSelector("a > div > span:last-child"));
                String time = span_time.getText().split("·")[0].strip();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Date collected_at = getCurrentDate();
                Date formatted_date = PostService.convertTossFormattedToIso(time);
                if (!title.isEmpty() && !time.isEmpty()) {
                    Post newPost = Post.builder()
                            .title(title)
                            .url(href)
                            .created_at(formatted_date)
                            .collected_at(collected_at)
                            .type(PostType.Toss)
                            .build();
                    postList.add(newPost);
                    totalSize += 1;
                }
            }
        }
        return postList;
    }


    @Override
    public String getSiteKey() {
        return "toss";
    }
}
