package com.vol.solunote.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CustomHttpSessionListener implements HttpSessionListener, HttpSessionIdListener {

    private final Map <String, HttpSession> sessions = new HashMap <>();

    public List <HttpSession> getActiveSessions() {
        return new ArrayList <>(sessions.values());
    }

    @Override
    public void sessionCreated(HttpSessionEvent hse) {

        sessions.put(hse.getSession().getId(), hse.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        sessions.remove(hse.getSession().getId());
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        HttpSession session = event.getSession();
        sessions.put(session.getId(), event.getSession());
        sessions.remove(oldSessionId);
        log.info("CHANGED  : {} --> {}, {}", oldSessionId, session.getId(), Long.valueOf(session.getCreationTime()));
    }
}