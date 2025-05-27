package com.tech.blog.crawling;

import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import com.tech.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tech.blog.service.PostService.getCurrentDate;

@RequiredArgsConstructor
@Component("carrotCrawler")
public class CarrotCrawler implements TechBlogCrawler {
    private final PostService postService;

    @Override
    public List<Post> crawling() throws ParseException {
        int startYear = 2015;
        int endYear = 2025;
        String crawling_url = "https://medium.com/daangn/archive/";
        WebDriver driver = new ChromeDriver();
        List<Post> postList = new ArrayList<>();

        for (int year = startYear; year <= endYear; year++) {
            driver.get(crawling_url + year);  // 올바르게 각 URL을 탐색
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".streamItem.streamItem--postPreview.js-streamItem"), 1));
            List<WebElement> allItem = driver.findElements(By.cssSelector("..streamItem.streamItem--postPreview.js-streamItem"));

            for (WebElement item : allItem) {
                String title = item.findElement(By.tagName("h3")).getAttribute("innerText");
                String url = item.findElement(By.tagName("a")).getAttribute("href");
                WebElement time = item.findElement(By.cssSelector("time"));
                String datetime = time.getAttribute("datetime");
                String visibleDate = time.getText();
                Date collected_at = getCurrentDate();
                Date created_at = PostService.convertIsoToFormatted(datetime);

                Post newPost = Post.builder()
                        .title(title)
                        .url(url)
                        .created_at(created_at)
                        .collected_at(collected_at)
                        .type(PostType.Daangn)
                        .build();
                if (postService.existsByTitleAndUrl(title, url)) {
                    postList.add(newPost);
                }

            }
        }

        return postList;
    }

    @Override
    public String getSiteKey() {
        return "carrot";
    }
}
