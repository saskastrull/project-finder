package org.example.projectfinder.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectfinder.model.dto.KeywordDto;
import org.example.projectfinder.model.dto.ProjectDto;
import org.example.projectfinder.service.KeywordService;
import org.example.projectfinder.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GeneralController {

    private final ProjectService projectService;
    private final KeywordService keywordService;

    @GetMapping("/keywords")
    public ResponseEntity<List<KeywordDto>> getAllKeywords() {
        return new ResponseEntity<>(keywordService.getAllKeywords(), HttpStatus.OK);
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
    }

    // get projects by keyword id
    // get projects mellan datum
}
