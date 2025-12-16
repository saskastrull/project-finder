package org.example.projectfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.projectfinder.model.dto.KeywordDto;
import org.example.projectfinder.model.entity.Keyword;
import org.example.projectfinder.repository.KeywordRepository;
import org.example.projectfinder.utility.KeywordMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public List<KeywordDto> getAllKeywords() {
        try {
            List<Keyword> keywords = keywordRepository.findAll();
            return keywords.stream()
                    .map(KeywordMapper::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // test
    public Keyword createKeyword(Keyword keyword) {
        return keywordRepository.save(keyword);
    }
}
