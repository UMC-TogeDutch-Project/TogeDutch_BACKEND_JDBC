package com.proj.togedutch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker //SimpleMessage broker를 사용하기 위해
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {

    // configureMessageBroker는 한 클라이언트에서 다른 클라이언트로 메시지를 라우팅 할 때 사용하는 브로커를 구성한다.
    // 첫번째 라인에서 정의된 /app로 시작하는 메시지만 메시지 헨들러로 라우팅한다고 정의한다.
    // 두번째 라인에서 정의된 /topic로 시작하는 주제를 가진 메시지를 핸들러로 라우팅하여 해당 주제에 가입한 모든 클라이언트에게 메시지를 방송한다.

    //  registry.setApplicationDestinationPrefixes("/app");
    //        registry.enableSimpleBroker("/topic");
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); //수신
        config.setApplicationDestinationPrefixes("/pub"); //송신
    }

    // 메소드 이름에 STOMP(Simple Text Oriented Messaging Protocol)라는 단어가 있다. 이는 스프링프레임워크의 STOMP 구현체를 사용한다는 의미다.
    // STOMP가 필요 한 이유는 websocket은 통신 프로토콜이지 특정 주제에 가입한 사용자에게 메시지를 전송하는 기능을 제공하지 않는다.
    // 이를 쉽게 사용하기 위해 STOMP를 사용한다.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)  {
        registry.addEndpoint("/stomp/chat")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())){
                    String user = accessor.getFirstNativeHeader("user");
//                    if(user != null){
//                        List<GrantedAuthority> authorities = new ArrayList<>();
//                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//                        Authentication auth = new UsernamePasswordAuthenticationToken(user,user,authorities);
//                        accessor.setUser(auth);
//                    }
                }
                return message;
                //return ChannelInterceptor.super.preSend(message, channel);
            }
        });
    }
}
