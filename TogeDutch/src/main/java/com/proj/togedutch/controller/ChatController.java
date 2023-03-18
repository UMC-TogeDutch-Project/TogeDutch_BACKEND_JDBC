package com.proj.togedutch.controller;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.config.BaseResponse;
import com.proj.togedutch.config.BaseResponseStatus;
import com.proj.togedutch.entity.*;
import com.proj.togedutch.service.AWSS3Service;
import com.proj.togedutch.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/chatRoom/{chatRoom_id}")
public class ChatController {
    @Autowired
    ChatService chatService;
    @Autowired
    AWSS3Service awsS3Service;
    @Value("${cloud.aws.url}")
    private String url;

    // 채팅 가져오기
    @GetMapping("/chatmessage/{chat_id}")
    public BaseResponse<ChatMessage> getChatMessage(@PathVariable("chatRoom_id") int chatRoomId, @PathVariable("chat_id") int chatId) throws BaseException {
        try{
            ChatMessage chatMessage = chatService.getChatMessage(chatRoomId,chatId);
            return new BaseResponse<>(chatMessage);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
    
    //채팅 저장
    @ResponseBody
    @PostMapping("/chatmessage")
    public BaseResponse<ChatMessage> createChatMessage(@PathVariable("chatRoom_id") int chatRoomId, @RequestParam int user,@RequestBody ChatMessage message) throws IOException, NullPointerException {
        try {
            int message_id = chatService.createChatMessage(chatRoomId,user,message);
            ChatMessage chatMessage = chatService.getChatMessage(chatRoomId,message_id);
            chatMessage.setType(ChatMessage.MessageType.TALK);
            return new BaseResponse<>(chatMessage);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    //채팅내용 가져오기
    @GetMapping("/conversation")
    public BaseResponse<List<ChatMessage>> getChatMessages(@PathVariable("chatRoom_id") int chatRoomId) throws BaseException {
        try{
            List<ChatMessage> chatMessages = chatService.getChatMessages(chatRoomId);
            return new BaseResponse<>(chatMessages);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 채팅이미지 생성
    @PostMapping("/chatPhoto")
    public BaseResponse<ChatPhoto> PostChatPhoto(@PathVariable("chatRoom_id") int chatRoomId,@RequestParam int user, @RequestPart MultipartFile file) throws IOException {
        try {
            String fileUrl = null;
            fileUrl = url + awsS3Service.uploadChatFile(file);
            ChatPhoto chatPhoto = chatService.createChatPhoto(chatRoomId,user,fileUrl);
            return new BaseResponse<>(chatPhoto);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    //채팅 이미지 가져오기
    @GetMapping("/chatPhoto/{chatPhoto_id}")
    public BaseResponse<ChatPhoto> GetChatPhoto(@PathVariable("chatRoom_id") int chatRoomId,@PathVariable("chatPhoto_id") int chatPhotoId) throws BaseException{
        try {
            ChatPhoto getChatPhoto = chatService.getChatPhoto(chatRoomId,chatPhotoId);
            return new BaseResponse<>(getChatPhoto);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 채팅 이미지 내역 전체조회
    @GetMapping("/chatPhotos")
    public BaseResponse<List<ChatPhoto>> GetChatPhotos(@PathVariable("chatRoom_id") int chatRoomId) throws BaseException {
        try{
            List<ChatPhoto> getChatPhotos = chatService.getChatPhotos(chatRoomId);
            return  new BaseResponse<>(getChatPhotos);
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    //만남시간 설정
    @PostMapping("/chatMeetTime")
    public BaseResponse<ChatMeetTime> postChatMeetTime(@PathVariable("chatRoom_id") int chatRoomId, @RequestParam int user, @RequestParam String time) throws BaseException{
        try {
            ChatMeetTime chatMeetTime = chatService.createChatMeetTime(chatRoomId,user,time);
            return new BaseResponse<>(chatMeetTime);
        }catch (BaseException e){
            return new BaseResponse<>(BaseResponseStatus.CHAT_MEETTIME_ERROR);
        }
    }

    // 만남시간 조회
    @GetMapping("/chatMeetTime/{chatMeetTime_id}")
    public BaseResponse<ChatMeetTime> getChatMeetTime(@PathVariable("chatRoom_id") int chatRoomId,@PathVariable("chatMeetTime_id") int chatMeetTimeId) throws BaseException{
        try{
            ChatMeetTime getChatMeetTime = chatService.getChatMeetTime(chatRoomId,chatMeetTimeId);
            return new BaseResponse<>(getChatMeetTime);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PutMapping("/chatMeetTime/{chatMeetTime_id}")
    public BaseResponse<ChatMeetTime> putChatMeetTime(@PathVariable("chatRoom_id") int chatRoom_id,@PathVariable("chatMeetTime_id") int chatMeetTime_id,@RequestParam String time) throws BaseException{
        try {
            ChatMeetTime chatMeetTime = chatService.putChatMeetTime(chatRoom_id, chatMeetTime_id, time);
            return new BaseResponse<>(chatMeetTime);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 위도경도 위치 저장
    @PostMapping("/chatLocation")
    public BaseResponse<ChatLocation> postChatLocation(@PathVariable("chatRoom_id")int chatRoom_id, @RequestParam int user,
                                                       @RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude) {
        try {
            ChatLocation chatLocation = chatService.createChatLocation(chatRoom_id, user, latitude, longitude);
            return new BaseResponse<>(chatLocation);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/chatLocation/{chatLocation_id}")
    public BaseResponse<ChatLocation> getChatLocation(@PathVariable("chatRoom_id")int chatRoom_id,
                                                      @PathVariable("chatLocation_id")int chatLocationIdx) {
        try {
            ChatLocation chatLocation = chatService.getChatLocationById(chatRoom_id, chatLocationIdx);
            return new BaseResponse<>(chatLocation);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    // 위치 정보 수정
    @PutMapping("/chatLocation/{chatLocation_id}")
    public BaseResponse<ChatLocation> putChatLocation(@PathVariable("chatRoom_id")int chatRoom_id, @PathVariable("chatLocation_id")int chatLocationIdx,
                                                      @RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude) {
        try {
            ChatLocation chatLocation = chatService.putChatLocation(chatRoom_id, chatLocationIdx, latitude, longitude);
            return new BaseResponse<>(chatLocation);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

//    // 채팅방 내 인원수
//    @GetMapping("/member")
//    public BaseResponse<ChatRoomUser> getChatRoomMember(@PathVariable("chatRoom_id") int chatRoom_id){
//        try{
//            ChatRoomUser chatMember = chatService.getChatRoomMember(chatRoom_id);
//            return new BaseResponse<>(chatMember);
//        } catch (BaseException e){
//            return new BaseResponse<>(e.getStatus());
//        }
//    }
//
//    // 현 채팅방에 있는 당사자 이름 가져오기
//    @GetMapping("/name/{user_id}")
//    public BaseResponse<ChatRoomUser> getUserName(@PathVariable("chatRoom_id") int chatRoom_id, @PathVariable("user_id") int user_id){
//        try{
//            ChatRoomUser userName = chatService.getUserName(chatRoom_id,user_id);
//            return new BaseResponse<>(userName);
//        } catch (BaseException e){
//            return new BaseResponse<>(e.getStatus());
//        }
//    }

}
