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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.tech.blog.service.PostService.getCurrentDate;

@RequiredArgsConstructor
@Component("baeminCrawler")
public class BaeminCrawler implements TechBlogCrawler {
    private final PostService postService;

    @Override
    public List<Post> crawling() throws ParseException, InterruptedException {
        // WebDriver 설정 (Chrome 예시)
        WebDriver driver = new ChromeDriver();
        String woowahan_url = "https://techblog.woowahan.com/?paged=";
        int totalSize = 0;
        List<Post> postList = postService.getAllPosts();

        for (int i = 1; i <= 46; i++) {
            driver.get(woowahan_url + i);
            if (driver.getCurrentUrl().contains("error")) {
                break;
            }

            Thread.sleep(2000);
            WebElement element = driver.findElement(By.className("post-list"));
            List<WebElement> items = element.findElements(By.tagName("div"));
            for (WebElement item : items) {
                WebElement link = item.findElement(By.tagName("a"));
                String href = link.getAttribute("href");

                WebElement titleBox = item.findElement(By.cssSelector("h2.post-title"));
                String title = titleBox.getAttribute("textContent").strip();

                WebElement timeElement = item.findElement(By.cssSelector("time.post-author-date"));
                String time = timeElement.getAttribute("textContent").strip();

//                // CSS 선택자 사용 (정상)
//                WebElement titleBox = item.findElement(By.cssSelector("a > h2"));
//                String title = titleBox.getText().strip();
//
//                WebElement timeElement = item.findElement(By.cssSelector("p > time"));
//                String time = timeElement.getText().strip();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Date collected_at = getCurrentDate();
                Date formatted_date = PostService.convertWoohanFormattedToIso(time);
                if (!title.isEmpty() && !time.isEmpty()) {
                    Post newPost = Post.builder()
                            .title(title)
                            .url(href)
                            .created_at(formatted_date)
                            .collected_at(collected_at)
                            .type(PostType.BaeMin)
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
