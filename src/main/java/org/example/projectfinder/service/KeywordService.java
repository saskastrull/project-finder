package org.example.projectfinder.service;

import jakarta.transaction.Transactional;
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

    public List<KeywordDto> getKeywords() {
        try {
            List<Keyword> keywords = keywordRepository.findAll();
            return keywords.stream()
                    .map(KeywordMapper::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public KeywordDto createKeyword(KeywordDto keywordDto) {
        return null;
    }

    @Transactional
    public KeywordDto updateKeyword(Long id, KeywordDto keywordDto) {
        return null;
    }

    public void deleteKeyword(Long id) {
        keywordRepository.deleteById(id);
    }
}
