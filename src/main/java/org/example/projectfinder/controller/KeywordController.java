package org.example.projectfinder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectfinder.model.dto.KeywordDto;
import org.example.projectfinder.service.KeywordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping()
    public ResponseEntity<List<KeywordDto>> getAllKeywords() {
        return ResponseEntity.ok(keywordService.getKeywords());
    }

    @PostMapping
    public ResponseEntity<KeywordDto> createKeyword(
            @Valid @RequestBody KeywordDto keywordDto) {
        KeywordDto created = keywordService.createKeyword(keywordDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KeywordDto> updateKeyword(
            @PathVariable Long id,
            @Valid @RequestBody KeywordDto keywordDto) {
        return ResponseEntity.ok(keywordService.updateKeyword(id, keywordDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable Long id) {
        keywordService.deleteKeyword(id);
        return ResponseEntity.noContent().build();
    }
}