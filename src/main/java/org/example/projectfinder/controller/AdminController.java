package org.example.projectfinder.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectfinder.model.entity.Keyword;
import org.example.projectfinder.service.KeywordService;
import org.example.projectfinder.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final KeywordService keywordService;
    private final ProjectService projectService;

    @PostMapping("/createkeyword")
    public ResponseEntity<List<Keyword>> createKeyword() {
        return null;
    }

    @PostMapping("/updatekeyword")
    public ResponseEntity<List<Keyword>> updateKeyword() {
        return null;
    }

    @PostMapping("/deletekeyword")
    public ResponseEntity<List<Keyword>> deleteKeyword() {
        return null;
    }


}
