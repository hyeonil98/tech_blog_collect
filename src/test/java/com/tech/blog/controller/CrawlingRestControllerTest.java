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

import java.time.Duration;
import java.util.List;

@SpringBootTest
class CrawlingRestControllerTest {

    @Autowired
    private PostService postService;  // @Autowired로 주입

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

            // 요소가 모두 로드될 때까지 스크롤을 반복해서 내림
            int previousSize = 0;
            while (true) {
                // 페이지 끝까지 스크롤
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

                // 스크롤 후 대기 (2초 동안)
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
            }

            List<WebElement> allItems = driver.findElements(By.cssSelector(".js-streamItem"));
            System.out.println("최종 총 개수: " + allItems.size());

            for (WebElement findItems : allItems) {
                // 게시물 섹션을 찾기
                List<WebElement> posts = findItems.findElements(By.cssSelector("section > div > div"));
                totalSize += posts.size();
                for (WebElement post : posts) {
                    // a 태그를 찾고, 첫 번째 링크를 얻음
                    WebElement link = post.findElement(By.cssSelector("div > a"));
                    String href = link.getAttribute("href");

                    // 제목 (span 요소가 있을 경우)
                    List<WebElement> spanElements = post.findElements(By.cssSelector("div > a > span"));
                    String title = "";

                    // span 요소가 존재할 경우, 첫 번째 span의 텍스트를 가져옴
                    if (!spanElements.isEmpty()) {
                        title = spanElements.get(0).getText();
                    }

                    Post newPost = Post.builder()
                            .title(title)
                            .url(href)
                            .type(PostType.Daangn)
                            .build();
                    postService.insertPost(newPost);  // DB에 게시물 삽입
                }
            }
            System.out.println("totalSize = " + totalSize);
            // 크롬 드라이버 종료
        }

        List<Post> allPosts = postService.getAllPosts();

        Assertions.assertThat(allPosts.size()).isEqualTo(totalSize);
        driver.quit();
    }
}
