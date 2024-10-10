package com.example.bookstore.component;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@ServerEndpoint("/placeOrder/{userId}")
@Component
public class OrderWebSocket {
  private static final Map<Long, Session> SESSIONS = new ConcurrentHashMap<>();

  public void sendMessage(long userId, String message) {
    Session session = SESSIONS.get(userId);
    if (session != null) {
      session.getAsyncRemote().sendText(message);
      System.out.println("Send message to user " + userId + ": " + message);
    }
  }

  @OnOpen
  public void onOpen(Session session, @PathParam("userId") long userId) {
    SESSIONS.put(userId, session);
    System.out.println("User " + userId + " connected.");
  }

  @OnClose
  public void onClose(Session session, @PathParam("userId") long userId) {
    SESSIONS.remove(userId);
    System.out.println("User " + userId + " disconnected.");
  }
}
