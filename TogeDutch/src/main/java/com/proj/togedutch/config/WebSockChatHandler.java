package com.proj.togedutch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.togedutch.entity.ChatMessage;
import com.proj.togedutch.repo.ChatRoomRepository;
import com.proj.togedutch.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;

import java.security.Principal;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSockChatHandler implements ChannelInterceptor { //유저인증과정을 거친다.

    private final ChatMessageService chatMessageService;
    private final ChatRoomRepository chatRoomRepository;
    private final ObjectMapper objectMapper;
    /*
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청,로그인 여부 확인
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);
            // Header의 jwt token 검증
            //jwtTokenProvider.validateToken(jwtToken);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청 / 유저가 일치하는지 확인하고
            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            Integer chatRoom_id = chatMessageService.getChatRoomId(Optional.ofNullable((Integer) message.getHeaders().get()).orElse("InvalidRoomId"));
            String roomId = chatMessageService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomRepository.setUserEnterInfo(sessionId, roomId);
            // 채팅방의 인원수를 +1한다.
            chatRoomRepository.plusUserCount(roomId);
            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatMessageService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.ENTER).chatRoom_id(roomId).sender(name).build());
            log.info("SUBSCRIBED {}, {}", name, roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);
            // 채팅방의 인원수를 -1한다.
            chatRoomRepository.minusUserCount(roomId);
            // 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatMessageService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).sender(name).build());
            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatRoomRepository.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }
        return message;
    }
     */
}
