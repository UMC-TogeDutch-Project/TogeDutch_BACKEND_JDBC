package com.proj.togedutch.controller;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.config.BaseResponse;
import com.proj.togedutch.entity.ChatRoom;
import com.proj.togedutch.entity.Post;
import com.proj.togedutch.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatRoom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService){
        this.chatRoomService = chatRoomService;
    }

    // 채팅방 생성
    @PostMapping("")
    public BaseResponse<ChatRoom> createChatRoom(){
        try{
            ChatRoom chatRoom = chatRoomService.createChatRoom();
            return new BaseResponse<>(chatRoom);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }

    //채팅방 리스트 조회
    @GetMapping("")
    public BaseResponse<List<ChatRoom>> getAllChatRooms() throws BaseException {
        try {
            List<ChatRoom> getChatRoomsRes = chatRoomService.getAllChatRooms();
            return new BaseResponse<>(getChatRoomsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    //채팅방 하나 조회
    @GetMapping("/{chatRoom_id}")
    public BaseResponse<ChatRoom> getChatRoomById(@PathVariable("chatRoom_id") int chatRoomIdx){
        try {
            ChatRoom chatRoom = chatRoomService.getChatRoomById(chatRoomIdx);
            return new BaseResponse<>(chatRoom);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 채팅방 삭제
    @DeleteMapping("/{chatRoom_id}")
    public BaseResponse<Integer> deleteChatRoom (@PathVariable("chatRoom_id") int chatRoomIdx) throws Exception{
        try{
            int deleteChatRoom = chatRoomService.deleteChatRoom(chatRoomIdx);
            return new BaseResponse<>(deleteChatRoom);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }

}
