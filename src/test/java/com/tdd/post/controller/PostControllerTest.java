package com.tdd.post.controller;

import com.tdd.dto.Post;
import com.tdd.exception.PostNotFoundException;
import com.tdd.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest --> if you are looking to load your full application configuration
@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository postRepository;

    List<Post> posts;

    // *** stubbing mock class and method
    record MockArgsApis (
            HttpStatus statusToTest,
            Optional<Post> post,
            ResultMatcher responseStatus)
    {};

    static Stream<MockArgsApis> sourcePostMethod() {
        return Stream.of(
                new MockArgsApis(
                        HttpStatus.OK, Optional.of(new Post(1, 1, "This is a new post", "My new post ..", null)), status().isOk()),
                new MockArgsApis(
                        HttpStatus.NOT_FOUND, Optional.of(new Post(999, 999, "", "", null)), status().isNotFound())
        );
    }

    // ***

    @BeforeEach
    void setUp()  {
        posts = List.of(
                new Post(1, 1, "Hello World", "This is my first post", null),
                new Post(2, 2, "Second post", "This is my second post", null)
        );
    }

    //REST APIs
    @Test
    void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id": 1,
                        "userId": 1,
                        "title": "Hello World",
                        "body": "This is my first post",
                        "version": null
                    },
                    {
                        "id": 2,
                        "userId": 2,
                        "title": "Second post",
                        "body": "This is my second post",
                        "version": null
                    }
                ]
                """;

        when(postRepository.findAll()).thenReturn(posts);


        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {

        Post post = posts.get(0);

        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        //string interpolation by java 21
        String json = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldNotFindPostWhenGivenInvalidId() throws Exception {
        when(postRepository.findById(999)).thenThrow(PostNotFoundException.class);

        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPostWhenPostIsValid() throws Exception {
        Post post = posts.get(0);

        when(postRepository.save(post)).thenReturn(post);

        //string interpolation by java 21
        String json = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    //@Test
    @ParameterizedTest
    @MethodSource("sourcePostMethod")
    void testUpdatePostWhenGivenValidPostOrInvalidPost(MockArgsApis arg) throws Exception {
        Post post = arg.post().get();

        if(arg.statusToTest().is2xxSuccessful()) {
            when(postRepository.findById(post.id())).thenReturn(Optional.of(post));
            when(postRepository.save(post)).thenReturn(post);
        } else {
            when(postRepository.findById(post.id())).thenReturn(Optional.empty());
        }

        //string interpolation by java 21
        String requestBody = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(arg.responseStatus());
    }

    @Test
    void shouldDeletePostWhenGivenValidId() throws Exception {
        doNothing().when(postRepository).deleteById(1);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        verify(postRepository, times(1)).deleteById(1);
    }
}
