package com.tech.blog.crawling;

import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import com.tech.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.tech.blog.service.PostService.getCurrentDate;

@RequiredArgsConstructor
@Component("lineCrawler")
public class LineCrawler implements TechBlogCrawler {
    private final PostService postService;

    @Override
    public List<Post> crawling() throws ParseException, InterruptedException {
        WebDriver driver = new ChromeDriver();
        String line_url = "https://engineering.linecorp.com/ko/blog";
        int totalSize = 0;
        List<Post> postList = postService.getAllPosts();

        for (int i = 1; i <= 46; i++) {
            if (i == 1) {
                driver.get(line_url);
            } else {
                driver.get(line_url + "/page/" + i);
            }
            if (driver.getCurrentUrl().contains("error")) {
                break;
            }

            Thread.sleep(2000);
            WebElement element = driver.findElement(By.className("post_list"));
            List<WebElement> items = element.findElements(By.cssSelector("li.post_list_item"));
            for (WebElement item : items) {
                WebElement titleElement = item.findElement(By.cssSelector("h2.title > a"));
                String title = titleElement.getText().strip();
                String href = titleElement.getAttribute("href");

                WebElement timeElement = item.findElement(By.cssSelector("div.text_area > span.text_date"));
                String time = timeElement.getText().strip();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Date collected_at = getCurrentDate();
                LocalDate date = LocalDate.parse(time, formatter);
                LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
                Date created_at = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

                if (!title.isEmpty() && !time.isEmpty()) {
                    Post newPost = Post.builder()
                            .title(title)
                            .url(href)
                            .created_at(created_at)
                            .collected_at(collected_at)
                            .type(PostType.Line)
                            .build();
                    postService.save(newPost);
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
