package com.tdd.dataloader;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdd.dto.Posts;
import com.tdd.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostDataLoader implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;

    @Override
    public void run(String... args) throws Exception {
        if(postRepository.count() == 0) {
            String postJson  = "/data/posts.json";
            log.info("Loading posts into database fomr JSON {}", postJson);
            try(InputStream inputStream = TypeReference.class.getResourceAsStream(postJson)) {
                Posts response = objectMapper.readValue(inputStream, Posts.class);
                postRepository.saveAll(response.post());
            }
            catch (IOException ex) {
                throw new RuntimeException("Failed to read JSON data", ex);
            }
        }

    }
}
