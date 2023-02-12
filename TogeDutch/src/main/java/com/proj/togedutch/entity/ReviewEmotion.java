package com.proj.togedutch.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class ReviewEmotion {
    private int post_id;
    private Double avg;

}
