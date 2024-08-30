package com.tdd.repository;

import com.tdd.dto.Post;
import org.springframework.data.repository.ListCrudRepository;

public interface PostRepository extends ListCrudRepository<Post, Integer> {

}
