package org.example.projectfinder.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keywordId;
    @Column(length = 30, nullable = false)
    @Setter
    private String keyword;

    public Keyword(String keyword) {
        this.keyword = keyword;
    }
}
