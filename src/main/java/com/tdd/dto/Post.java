package com.tdd.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

public record Post(
        @Id
        Integer id,
        Integer userId,
        String title,
        String body,
        @Version
        Integer version
) {

}