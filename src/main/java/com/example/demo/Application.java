package com.example.demo;

import com.example.demo.services.MessageService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.demo.Zookeeper.ZookeeperClient;
import com.example.demo.controller.ServerChat;

import java.io.IOException;

@SpringBootApplication
public class Application {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ZookeeperClient zookeeperClient() throws IOException {
        return new ZookeeperClient();
    }

    @Bean
    public ServerChat serverChat(ZookeeperClient zookeeperClient) throws IOException {
        return new ServerChat(zookeeperClient, messagingTemplate, userService, messageService);
    }
}




