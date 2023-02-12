package com.proj.togedutch.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import software.amazon.ion.Decimal;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Post {
    private int post_id;
    private String title;
    private String url;
    private int delivery_tips;
    private int minimum;
    private String order_time;
    private int num_of_recruits;
    private int recruited_num;
    private String status;
    private Timestamp created_at;
    private Timestamp updated_at;
    private int user_id;
    private String image;
    private Double latitude;
    private Double longitude;
    private int chatRoom_id;
    private String category;

    public Post(String title){
        this.title = title;
    }
}
