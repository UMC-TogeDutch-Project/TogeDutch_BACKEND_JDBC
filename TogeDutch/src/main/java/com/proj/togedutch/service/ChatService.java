package com.proj.togedutch.service;

import com.proj.togedutch.config.BaseException;
import com.proj.togedutch.dao.ChatMessageDao;
import com.proj.togedutch.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.proj.togedutch.config.BaseResponseStatus.DATABASE_ERROR;
import static com.proj.togedutch.config.BaseResponseStatus.RESPONSE_ERROR;

@Service
public class ChatService {
    private final ChatMessageDao chatMessageDao;

    @Autowired
    public ChatService(ChatMessageDao chatMessageDao) {
        this.chatMessageDao = chatMessageDao;
    }
    //채팅 메세지 조회
    public ChatMessage getChatMessage(int chatRoomId, int chatId) throws  BaseException {
        try {
            ChatMessage chatMessage = chatMessageDao.getChatMessage(chatRoomId,chatId);
            System.out.println(chatMessage.getCreateAt());
            chatMessage.setType(ChatMessage.MessageType.TALK);
            return chatMessage;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //채팅내역 전체조회
    public List<ChatMessage> getChatMessages (int chatRoom_id) throws BaseException {
        try {
            List<ChatMessage> chatMessages = chatMessageDao.getChatMessages(chatRoom_id);
            chatMessages.forEach(c->c.setType(ChatMessage.MessageType.TALK));
            return chatMessages;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 채팅 메세지 생성
    public int createChatMessage(int chatRoomId, int user ,ChatMessage chatMessage) throws BaseException {
        try {
            int message_id = chatMessageDao.createChatMessage(chatRoomId,user,chatMessage);
            return message_id;
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 채팅 메시지 삭제
    public int deleteChat(int chatRoomIdx) throws BaseException {
        try{
            int result = chatMessageDao.deleteChat(chatRoomIdx);
            return result;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 채팅 이미지 생성
    public ChatPhoto createChatPhoto(int chatRoomId,int user,String file) throws BaseException{
        try {
            int chatPhoto_id = chatMessageDao.createChatPhoto(chatRoomId,user,file);
            ChatPhoto chatPhoto = chatMessageDao.getChatPhoto(chatRoomId,chatPhoto_id);
            return chatPhoto;
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 채팅 이미지 조회
    public ChatPhoto getChatPhoto(int chatRoomId, int chatPhotoId) throws BaseException{
        try{
            ChatPhoto getChatPhoto = chatMessageDao.getChatPhoto(chatRoomId,chatPhotoId);
            return getChatPhoto;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 채팅방에서 이미지 전체 조회
    public List<ChatPhoto> getChatPhotos(int chatRoomId) throws  BaseException {
        try{
            List<ChatPhoto> getChatPhotos = chatMessageDao.getChatPhotos(chatRoomId);
            return getChatPhotos;
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 채팅 이미지 삭제
    public int deleteChatPhoto(int chatRoomIdx) throws BaseException {
        try{
            int result = chatMessageDao.deleteChatPhoto(chatRoomIdx);
            return result;
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 위치 설정
    public ChatLocation createChatLocation(int chatRoom_id, int user, BigDecimal latitude, BigDecimal longitude) throws BaseException {
        try {
            int chatLocationIdx = chatMessageDao.createChatLocation(chatRoom_id, user, latitude, longitude);
            ChatLocation chatLocation = chatMessageDao.getChatLocation(chatRoom_id,chatLocationIdx);
            return chatLocation;
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 위치 조회
    public ChatLocation getChatLocationById(int chatRoom_id, int chatLocationIdx) throws BaseException {
        try{
            ChatLocation getChatLocation = chatMessageDao.getChatLocation(chatRoom_id, chatLocationIdx);
            return getChatLocation;
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 위치 수정
    public ChatLocation putChatLocation(int chatRoom_id, int chatLocationIdx, BigDecimal latitude, BigDecimal longitude) throws BaseException {
        try {
            chatMessageDao.putChatLocation(chatRoom_id, chatLocationIdx, latitude, longitude);
            return getChatLocationById(chatRoom_id, chatLocationIdx);
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 만남시간 설정
    public ChatMeetTime createChatMeetTime(int chatRoom_id, int user, String time) throws BaseException{
        try {
            int chatMeetTime_id = chatMessageDao.createChatMeetTime(chatRoom_id,user,time);
            ChatMeetTime chatMeetTime = chatMessageDao.getChatMeetTime(chatRoom_id,chatMeetTime_id);
            return chatMeetTime;
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 만남시간 조회
    public ChatMeetTime getChatMeetTime(int chatRoomId, int chatMeetTimeId) throws BaseException {
        try{
            ChatMeetTime getChatMeetTime = chatMessageDao.getChatMeetTime(chatRoomId, chatMeetTimeId);
            return getChatMeetTime;
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 만날시간 수정
    public ChatMeetTime putChatMeetTime(int chatRoom_id, int chatMeetTime_id, String time) throws BaseException{
        try {
            chatMessageDao.putChatMeetTime(chatRoom_id, chatMeetTime_id, time);
            return getChatMeetTime(chatRoom_id, chatMeetTime_id);
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

//    // 채팅방 인원 수
//    public ChatRoomUser getChatRoomMember(int chatRoom_id) throws BaseException{
//        try{
//            ChatRoomUser chatRoomMember = chatMessageDao.getChatRoomMember(chatRoom_id);
//            return chatRoomMember;
//        } catch (Exception e){
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 현 채팅방에 있는 당사자 이름 가져오기
//    public ChatRoomUser getUserName(int chatRoom_id, int user_id) throws BaseException{
//        try{
//            ChatRoomUser userName = chatMessageDao.getUserName(chatRoom_id,user_id);
//            return userName;
//        } catch (Exception e){
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}
