package com.proj.togedutch.service;

import com.proj.togedutch.dao.ChatMessageDao;
import com.proj.togedutch.entity.ChatLocation;
import com.proj.togedutch.entity.ChatMeetTime;
import com.proj.togedutch.entity.ChatMessage;
import com.proj.togedutch.entity.ChatPhoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class ChatMessageService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChatMessageDao chatMessageDao;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatMessageService(ChatMessageDao chatMessageDao, SimpMessagingTemplate simpMessagingTemplate, JdbcTemplate jdbcTemplate){
        this.chatMessageDao = chatMessageDao;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    //채팅에서 메세지 전송
    public void sendChatMessage(ChatMessage message) {
        String roomIdName = Integer.toString(message.getChatRoomId());
        if (ChatMessage.MessageType.ENTER.equals(message.getType())){
            message.setContent(message.getWriter() + "님이 방에 입장했습니다.");
        } else if (ChatMessage.MessageType.QUIT.equals(message.getContent())) {
            message.setContent(message.getWriter() + "님이 방에서 나갔습니다.");
        }
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + roomIdName, message); //message 전송
        chatMessageDao.saveMessage(message);
    }

    // 채팅에서 이미지 전송
    public void sendChatImgFile(ChatPhoto photo){
        String roomIdName = Integer.toString(photo.getChatRoom_id());
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + roomIdName, photo); //photo 전송
        chatMessageDao.createChatPhoto(photo.getChatRoom_id(),photo.getUser_id(),photo.getImage()); // DB에 이미지 저장
    }

    // 채팅에서 위치 전송
    public void sendChatLocation(ChatLocation location){
        String roomIdName = Integer.toString(location.getChatRoomId());
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + roomIdName, location); //location 전송
    }

    // 채팅에서 만남시간 전송
    public void sendChatMeetTime(ChatMeetTime meetTime){
        String roomIdName = Integer.toString(meetTime.getChatRoomId());
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + roomIdName, meetTime); //location 전송
    }

}
