package com.tdd.post.controller;

import com.tdd.dto.Post;
import com.tdd.exception.PostNotFoundException;
import com.tdd.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;

    @GetMapping("")
    List<Post> findAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Post> findById(@PathVariable Integer id) {
        return Optional.ofNullable(postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    Post create(@RequestBody @Valid Post post) {
        return postRepository.save(post);
    }

    @PutMapping("{id}")
    Post update(@RequestBody @Valid Post post) {
        Optional<Post> existing = postRepository.findById(post.id());
        if(existing.isPresent()) {
            Post updated = new Post(
              existing.get().id(),
              existing.get().userId(),
              post.title(),
              post.body(),
              existing.get().version()
            );
            return postRepository.save(updated);
        } else {
            throw new PostNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    void delete(@PathVariable Integer id) {
        postRepository.deleteById(id);
    }

 }
