package com.proj.togedutch.controller;


import com.proj.togedutch.dao.ChatMessageDao;
import com.proj.togedutch.dao.ChatRoomDao;
import com.proj.togedutch.entity.ChatMessage;
import com.proj.togedutch.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatWebSockController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatMessageDao chatMessageDao;
    private final ChatRoomDao chatRoomDao;

    // 채팅 입장
    //"/pub/chat/enter"
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessage message){
        String roomIdName = Integer.toString(message.getChatRoom_id());
        message.setRoomId(roomIdName);
        String userName = chatMessageDao.userName(message.getUserId());
        message.setContent(userName + "님이 채팅방에 참여하였습니다.");

        //채팅리스트
        List<ChatMessage> chatList = chatMessageService.findAllChatByRoomId(message.getRoomId());
        if(chatList != null) {
            for (ChatMessage c : chatList) {
                message.setWriter(c.getWriter());
                message.setContent(c.getContent());
            }
        }
        simpMessagingTemplate.convertAndSend("/sub/chat/room/"+ message.getRoomId(),message);

        //ChatRoom chatRoom = chatRoomDao.getChatRoomById(message.getChatRoom_id());
        //ChatMessageSaveDto chatMessageSaveDto = new ChatMessageSaveDto(message.getRoomId(),message.getWriter(),message.getContent());
        chatMessageDao.save(message);
    }

    // 채팅 퇴장
    //"/pub/chat/quit"
    @MessageMapping(value = "/chat/quit")
    public void quit(ChatMessage message){
        String roomIdName = Integer.toString(message.getChatRoom_id());
        message.setRoomId(roomIdName);
        String userName = chatMessageDao.userName(message.getUserId());
        message.setContent(userName + "님이 채팅방에서 나갔습니다.");
        simpMessagingTemplate.convertAndSend("/sub/chat/room/"+ message.getRoomId(),message);
        chatMessageDao.save(message);
    }


    // 채팅 메시지
    // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        String roomIdName = Integer.toString(message.getChatRoom_id());
        message.setRoomId(roomIdName);
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + roomIdName, message);
        log.info("전송완료");
        chatMessageDao.save(message);
    }
}