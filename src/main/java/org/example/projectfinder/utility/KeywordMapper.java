package org.example.projectfinder.utility;

import org.example.projectfinder.model.dto.KeywordDto;
import org.example.projectfinder.model.entity.Keyword;
import org.springframework.stereotype.Component;

@Component
public class KeywordMapper {

    public static KeywordDto toDto(Keyword keyword) {
        return new KeywordDto(
                keyword.getKeywordId(),
                keyword.getKeyword()
        );
    }

    public static Keyword toEntity(KeywordDto keywordDto) {
        return new Keyword(
                keywordDto.getId(),
                keywordDto.getKeyword()
        );
    }
}
