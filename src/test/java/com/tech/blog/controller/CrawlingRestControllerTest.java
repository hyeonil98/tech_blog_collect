package com.tech.blog.controller;

import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import com.tech.blog.service.PostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
class CrawlingRestControllerTest {

    @Autowired
    private PostService postService;  // @Autowired로 주입
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Test
    @DisplayName("당근 마켓 크롤링 테스트")
    public void 당근_크롤링_테스트() throws Exception {
        // WebDriver 설정 (Chrome 예시)
        WebDriver driver = new ChromeDriver();
        String engineer_url = "https://medium.com/daangn/development/home";
        String machine_learning_url = "https://medium.com/daangn/machine-learning/home";
        String data_url = "https://medium.com/daangn/data/home";
        String search_url = "https://medium.com/daangn/search-home/home";
        String startup_url = "https://medium.com/daangn/startup/home";
        String[] urls = {engineer_url, machine_learning_url, data_url, search_url, startup_url};
        int totalSize = 0;
        List<Post> allPosts = postService.getAllPosts();

        for (String url : urls) {
            driver.get(url);  // 올바르게 각 URL을 탐색

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 페이지가 로드될 때까지 대기 (예: 10개 이상의 .js-streamItem 요소가 나타날 때까지)
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".js-streamItem"), 0));

            // 요소 찾기
            List<WebElement> items = driver.findElements(By.cssSelector(".js-streamItem"));
            System.out.println("초기 로드된 총 개수: " + items.size());

            // 페이지 스크롤을 내려서 추가 콘텐츠 로드
            JavascriptExecutor js = (JavascriptExecutor) driver;

            int previousSize = 0;
            while (true) {
                // 페이지 끝까지 스크롤
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");


                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 새로운 요소들을 찾고 개수를 출력
                List<WebElement> updatedItems = driver.findElements(By.cssSelector(".js-streamItem"));
                System.out.println("업데이트된 총 개수: " + updatedItems.size());

                // 새로운 요소가 로드되지 않았다면 루프 종료
                if (updatedItems.size() == previousSize) {
                    System.out.println("더 이상 새로운 요소가 없습니다.");
                    break;
                }

                // 이전 크기 갱신
                previousSize = updatedItems.size();


                List<WebElement> allItems = driver.findElements(By.cssSelector(".js-streamItem"));
                System.out.println("최종 총 개수: " + allItems.size());

                for (WebElement item : allItems) {
                    List<WebElement> posts = item.findElements(By.cssSelector("div.col.u-xs-size12of12"));

                    for (WebElement post : posts) {
                        try {
                            // 링크
                            WebElement link = post.findElement(By.cssSelector("a"));
                            String href = link.getAttribute("href");

                            // 타이틀
                            String title = "";
                            List<WebElement> spanElements = post.findElements(By.cssSelector("div:first-child > a"));
                            if (!spanElements.isEmpty()) {
                                title = spanElements.get(0).getText();
                            }

                            WebElement time = post.findElement(By.cssSelector("time"));
                            String datetime = time.getAttribute("datetime");
                            String visibleDate = time.getText();
                            Date formatted_date = PostService.convertIsoToFormatted(datetime);
                            if(!title.isEmpty() && !visibleDate.isEmpty()) {
                                Post newPost = Post.builder()
                                        .title(title)
                                        .url(href)
                                        .created_at(formatted_date)
                                        .type(PostType.Daangn)
                                        .build();
                                System.out.println("newPost = " + newPost.toString());
                                postService.insertPost(newPost);
                            }

                        } catch (Exception e) {
                            System.out.println("게시물 파싱 중 예외 발생: " + e.getMessage());
                        }
                    }
                }

                }

        }
        driver.quit();
    }
}
