package com.tech.blog.controller;

import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import com.tech.blog.service.PostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static com.tech.blog.service.PostService.getCurrentDate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class CrawlingRestControllerTest {

    @Autowired
    private PostService postService;  // @Autowired로 주입
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @BeforeAll
    public void setUp() {
        postService.deleteAllPost(); // 이제 접근 가능!
    }

    @Test
    @DisplayName("페이징 테스트")
    public void 페이징_테스트() {
        PageRequest pageRequest = PageRequest.of(0, 10);

    }

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
                            Date collected_at = getCurrentDate();
                            Date formatted_date = PostService.convertIsoToFormatted(datetime);
                            if(!title.isEmpty() && !visibleDate.isEmpty()) {
                                Post newPost = Post.builder()
                                        .title(title)
                                        .url(href)
                                        .created_at(formatted_date)
                                        .collected_at(collected_at)
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

    @Test
    @DisplayName("토스 크롤링 테스트")
    public void 토스_크롤링_테스트() throws Exception {
        // WebDriver 설정 (Chrome 예시)
        WebDriver driver = new ChromeDriver();
        String toss_url = "https://toss.tech/?page=";
        int totalSize = 0;
        List<Post> allPosts = postService.getAllPosts();

        for (int i = 1; i < 1000; i++) {
            driver.get(toss_url+i);

            if(driver.getCurrentUrl().contains("error")){
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
                if(!title.isEmpty() && !time.isEmpty()) {
                    Post newPost = Post.builder()
                            .title(title)
                            .url(href)
                            .created_at(formatted_date)
                            .collected_at(collected_at)
                            .type(PostType.Toss)
                            .build();
                    System.out.println("newPost = " + newPost.toString());
                    postService.insertPost(newPost);
                    totalSize += 1;
                }
            }
        }

        List<Post> postByType = postService.getPostByType(PostType.Toss);
        Assertions.assertThat(postByType.size()).isEqualTo(totalSize);
        driver.quit();
    }

    @Test
    @DisplayName("카카오 크롤링 테스트")
    public void 카카오_크롤링_테스트() throws Exception {
        // WebDriver 설정 (Chrome 예시)
        WebDriver driver = new ChromeDriver();
        String kakao_url = "https://tech.kakao.com";
        int totalSize = 0;
        List<Post> allPosts = postService.getAllPosts();
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
                    postService.insertPost(newPost);
                    totalSize += 1;
                }
            }
            WebElement webElement = driver.findElement(By.className("btn_next"));
            webElement.click();
        }

        List<Post> postByType = postService.getPostByType(PostType.Kakao);
        Assertions.assertThat(postByType.size()).isEqualTo(totalSize);
        driver.quit();
    }

    @Test
    @DisplayName("배민 크롤링 테스트")
    public void 배민_크롤링_테스트() throws Exception {
        // WebDriver 설정 (Chrome 예시)
        WebDriver driver = new ChromeDriver();
        String woowahan_url = "https://techblog.woowahan.com/?paged=";
        int totalSize = 0;
        List<Post> allPosts = postService.getAllPosts();

        for (int i = 1; i <= 46; i++) {
            driver.get(woowahan_url+i);
            if(driver.getCurrentUrl().contains("error")) {
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
                if(!title.isEmpty() && !time.isEmpty()) {
                    Post newPost = Post.builder()
                            .title(title)
                            .url(href)
                            .created_at(formatted_date)
                            .collected_at(collected_at)
                            .type(PostType.BaeMin)
                            .build();
                    postService.insertPost(newPost);
                    totalSize += 1;
                }
            }
        }

        List<Post> postByType = postService.getPostByType(PostType.BaeMin);
        Assertions.assertThat(postByType.size()).isEqualTo(totalSize);
        driver.quit();
    }

    @Test
    @DisplayName("라인 크롤링 테스트")
    public void 라인_크롤링_테스트() throws Exception {
        // WebDriver 설정 (Chrome 예시)
        WebDriver driver = new ChromeDriver();
        String line_url = "https://engineering.linecorp.com/ko/blog";
        int totalSize = 0;
        List<Post> allPosts = postService.getAllPosts();

        for (int i = 1; i <= 46; i++) {
            if(i == 1){
                driver.get(line_url);
            }else{
                driver.get(line_url+"/page/"+i);
            }
            if(driver.getCurrentUrl().contains("error")) {
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

                if(!title.isEmpty() && !time.isEmpty()) {
                    Post newPost = Post.builder()
                            .title(title)
                            .url(href)
                            .created_at(created_at)
                            .collected_at(collected_at)
                            .type(PostType.Line)
                            .build();
                    postService.insertPost(newPost);
                    totalSize += 1;
                }
            }
        }

        List<Post> postByType = postService.getPostByType(PostType.Line);
        Assertions.assertThat(postByType.size()).isEqualTo(totalSize);
        driver.quit();
    }
}
