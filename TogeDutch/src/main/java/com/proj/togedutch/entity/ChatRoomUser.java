package com.proj.togedutch.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor
public class ChatRoomUser {
    private int chatRoom_id;
    private int user_id;
    private byte status;
    private int is_read;
    private Timestamp updated_at;
    private String userName;


    public ChatRoomUser(int chatRoom_id, int user_id, byte status, int is_read, Timestamp updated_at) {
        this.chatRoom_id=chatRoom_id;
        this.user_id=user_id;
        this.status = status;
        this.is_read = is_read;
        this.updated_at = updated_at;
    }

}
