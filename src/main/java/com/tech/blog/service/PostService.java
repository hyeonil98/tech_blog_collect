package com.tech.blog.service;

import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import com.tech.blog.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public static Date getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = now.format(formatter);
        LocalDateTime parsed = LocalDateTime.parse(formatted, formatter);
        return Date.from(parsed.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date convertIsoToFormatted(String isoDateString) throws ParseException {
        try {
            Instant instant = Instant.parse(isoDateString);
            Date date = Date.from(instant);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            String formattedDate = sdf.format(date);

            return sdf.parse(formattedDate);

        } catch (Exception e) {
            e.printStackTrace();
            return null;  // 예외 발생 시 null 반환
        }
    }

    public static Date convertTossFormattedToIso(String isoDateString) throws ParseException {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        LocalDate date = LocalDate.parse(isoDateString, inputFormatter);
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date convertKakaoFormattedToIso(String isoDateString) throws ParseException {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate date = LocalDate.parse(isoDateString, inputFormatter);
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date convertWoohanFormattedToIso(String isoDateString) throws ParseException {
//        isoDateString = isoDateString.replace(".", " ");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM.dd.yyyy", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(isoDateString, inputFormatter);
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public List<Post> getAllPosts() {
        return postRepository.getAllPosts();
    }

    public List<Post> getPostByType(PostType postType) {
        return postRepository.getPostByType(postType);
    }

    public Page<Post> getPostByPaging(Pageable pageable, PostType postType, String keyword) {
        return postRepository.getPostByPaging(pageable, postType, keyword);
    }

    public boolean existsByTitleAndUrl(String title, String url) {
        return postRepository.existsByTitleAndUrl(title, url);
    }

    @Transactional
    public void save(Post post) {
        postRepository.save(post);
    }

    @Transactional
    public void saveAll(List<Post> posts) {
        List<Post> newPosts = posts.stream().filter(post -> !postRepository.existsByTitleAndUrl(post.getTitle(), post.getUrl())).toList();
        postRepository.saveAll(newPosts);
    }

    @Transactional
    public void deleteAllPost() {
        postRepository.deleteAllPost();
    }

    @Transactional
    public void deletePostByType(PostType postType) {
        postRepository.deletePostByType(postType);
    }
}
