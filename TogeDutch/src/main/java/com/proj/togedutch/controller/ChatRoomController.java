package com.proj.togedutch.controller;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.config.BaseResponse;
import com.proj.togedutch.entity.ChatLocation;
import com.proj.togedutch.entity.ChatRoom;
import com.proj.togedutch.entity.ChatRoomUser;
import com.proj.togedutch.entity.Post;
import com.proj.togedutch.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    // 채팅방에 유저 추가
    @PostMapping("/{chatRoom_id}/user/{user_id}")
    public BaseResponse<ChatRoomUser> inviteUserController (@PathVariable("chatRoom_id") int chatRoomIdx, @PathVariable("user_id") int userId) throws Exception{
        try{
            ChatRoomUser chatRoomUser = chatRoomService.inviteUser(chatRoomIdx,userId);
            return new BaseResponse<>(chatRoomUser);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 채팅방에 존재하는 있는 유저모두 출력
    @GetMapping("/{chatRoom_id}/users")
    public BaseResponse<List<ChatRoomUser>> getChatRoomUsers(@PathVariable("chatRoom_id") int chatRoomIdx) throws Exception{
        try {
            List<ChatRoomUser> chatRoomUsers = chatRoomService.getChatRoomUsers(chatRoomIdx);
            return new BaseResponse<>(chatRoomUsers);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // /chatRoom/:chatRoom_id/user/:user_id/in
    // 현재 채팅방안에서 채팅을 보는중
    @PutMapping("/{chatRoom_id}/user/{user_id}/in")
    public BaseResponse<ChatRoomUser> inChatRoomUser(@PathVariable("chatRoom_id") int chatRoomIdx, @PathVariable("user_id") int userId){
        try{
            ChatRoomUser chatRoomUser = chatRoomService.inChatRoomUser(chatRoomIdx,userId);
            return new BaseResponse<>(chatRoomUser);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 현재 채팅방을 보지 않는 중
    @PutMapping("/{chatRoom_id}/user/{user_id}/out")
    public BaseResponse<ChatRoomUser> outChatRoomUser(@PathVariable("chatRoom_id") int chatRoomIdx, @PathVariable("user_id") int userId){
        try{
            ChatRoomUser chatRoomUser = chatRoomService.outChatRoomUser(chatRoomIdx,userId);
            return new BaseResponse<>(chatRoomUser);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
