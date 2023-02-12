package com.proj.togedutch.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationWaiting {
    private int application_id;
    private String status;
    private int post_id;
    private String uploader;    // 공고 업로드한 유저 이름
    private int user_id;
    private int chatRoom_id;
    private String title;
    private String applicant;       // 일반 : 신청자 이름 or 랜덤매칭 : 추천된 유저 이름
}
