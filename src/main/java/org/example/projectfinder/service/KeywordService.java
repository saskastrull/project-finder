package org.example.projectfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.projectfinder.exception.ResourceNotFoundException;
import org.example.projectfinder.model.dto.KeywordDto;
import org.example.projectfinder.model.entity.Keyword;
import org.example.projectfinder.repository.KeywordRepository;
import org.example.projectfinder.repository.ProjectRepository;
import org.example.projectfinder.utility.KeywordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KeywordService {

    private final KeywordRepository keywordRepository;

    @Transactional(readOnly = true)
    public List<KeywordDto> getKeywords() {
        return keywordRepository.findAll()
                .stream()
                .map(KeywordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public KeywordDto getKeywordById(Long id) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Keyword", id));
        return KeywordMapper.toDto(keyword);
    }

    public KeywordDto createKeyword(KeywordDto keywordDto) {
        Keyword keyword = KeywordMapper.toEntity(keywordDto);
        Keyword saved = keywordRepository.save(keyword);
        return KeywordMapper.toDto(saved);
    }

    public KeywordDto updateKeyword(Long id, KeywordDto keywordDto) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Keyword", id));
        keyword.setKeyword(keywordDto.getKeyword());
        return KeywordMapper.toDto(keyword);
    }

    public void deleteKeyword(Long id) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Keyword", id));
        keywordRepository.delete(keyword);
    }
}
