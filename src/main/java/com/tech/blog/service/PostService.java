package com.tech.blog.service;

import com.tech.blog.controller.PostController;
import com.tech.blog.domain.Post;
import com.tech.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public static Date convertIsoToFormatted(String isoDateString) throws ParseException {
        try {
            // ISO 8601 형식 문자열을 Instant로 변환
            Instant instant = Instant.parse(isoDateString);

            // Instant를 Date로 변환
            Date date = Date.from(instant);

            // 만약 한국 시간(KST)을 적용하려면 아래와 같이 변환할 수 있습니다.
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            // Date를 한국 시간으로 포맷
            String formattedDate = sdf.format(date);

            // 변환된 날짜 문자열을 출력
            System.out.println("변환된 날짜 (KST): " + formattedDate);

            // 한국 시간으로 변환된 Date 객체 반환
            return sdf.parse(formattedDate);

        } catch (Exception e) {
            e.printStackTrace();
            return null;  // 예외 발생 시 null 반환
        }
    }

    public List<Post> getAllPosts() {
        return postRepository.getAllPosts();
    }

    public void insertPost(Post post) {
        postRepository.insertPost(post);
    }
}
