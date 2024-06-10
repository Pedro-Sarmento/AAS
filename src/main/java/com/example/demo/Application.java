package com.example.demo;

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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ZookeeperClient zookeeperClient() throws IOException {
        return new ZookeeperClient();
    }

    @Bean
    public ServerChat serverChat(ZookeeperClient zookeeperClient) throws IOException {
        return new ServerChat(zookeeperClient, messagingTemplate);
    }
}




