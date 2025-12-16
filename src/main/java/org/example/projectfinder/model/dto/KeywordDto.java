package org.example.projectfinder.model.dto;

import lombok.Getter;

public class KeywordDto {

    @Getter
    private String keyword;

    public KeywordDto(String keyword) {
        this.keyword = keyword;
    }
}