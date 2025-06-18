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
@Component("kakaoCrawler")
public class KakaoCrawler implements TechBlogCrawler {
    private final PostService postService;

    @Override
    public List<Post> crawling() throws ParseException, InterruptedException {
        WebDriver driver = new ChromeDriver();
        String kakao_url = "https://tech.kakao.com";
        int totalSize = 0;
        List<Post> postList = postService.getAllPosts();
        driver.get(kakao_url+"/blog");

        for (int i = 1; i <= 30; i++) {
            if(driver.getCurrentUrl().contains("error")) {
                break;
            }

            Thread.sleep(2000);
            WebElement element = driver.findElement(By.className("list_post"));
            List<WebElement> items = element.findElements(By.tagName("li"));
            for (WebElement item : items) {
                WebElement link = item.findElement(By.tagName("a"));
                String href = link.getAttribute("href");

                String title = item.findElement(By.tagName("h4")).getText();

                WebElement span_time = item.findElement(By.className("txt_date"));
                String time = span_time.getText();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Date collected_at = getCurrentDate();
                Date formatted_date = PostService.convertKakaoFormattedToIso(time);
                if(!title.isEmpty() && !time.isEmpty()) {
                    Post newPost = Post.builder()
                            .title(title)
                            .url(href)
                            .created_at(formatted_date)
                            .collected_at(collected_at)
                            .type(PostType.Kakao)
                            .build();
                    postList.add(newPost);
                    totalSize += 1;
                }
            }
            WebElement webElement = driver.findElement(By.className("btn_next"));
            webElement.click();

        }
        return postList;
    }


    @Override
    public String getSiteKey() {
        return "toss";
    }
}
