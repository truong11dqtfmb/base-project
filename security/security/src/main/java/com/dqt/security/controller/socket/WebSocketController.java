package com.dqt.security.controller.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

//    Client:
//          subscribe   ~  @SendTo("/topic/messages")
//                      ~ simpMessagingTemplate.convertAndSend("/chat/group/" + groupId, message);
//                      ~ simpMessagingTemplate.convertAndSendToUser(username, "/private", message);
//
//
//          send        ~ @MessageMapping

// Server
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/websocket")
//                .setAllowedOrigins("*")
////                comment  .withSockJS() => debug Postman
//                .withSockJS()
//        ;
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic");
//        config.setApplicationDestinationPrefixes("/app");
//        config.setUserDestinationPrefix("/user");
//    }

// Client
//    let Sock = new SockJS('http://localhost:9090/websocket');
//    stompClient = over(Sock);


    //    stompClient.subscribe('/topic/messages', message);
    //    stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String send(@Payload String message) throws Exception {
        return "test";
    }

    //    stompClient.subscribe('/chat/group/{groupId}', message);
    //    stompClient.send("/app/chat/{groupId}", {}, JSON.stringify(chatMessage));
    @MessageMapping("/chat/{groupId}")
    public void sendToGroup(@DestinationVariable String groupId, @Payload String message) {
        simpMessagingTemplate.convertAndSend("/chat/group/" + groupId, message);
    }

    //    stompClient.subscribe('/user/'+username+'/private', onPrivateMessage);
    //    stompClient.send("/app/chat-private", {}, JSON.stringify(chatMessage));
    @MessageMapping("/chat-private")
    public String chatPrivate(@Payload String message, @DestinationVariable String username) {
        simpMessagingTemplate.convertAndSendToUser(username, "/private", message);
        return message;
    }
}
