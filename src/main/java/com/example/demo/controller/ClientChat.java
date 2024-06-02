package com.example.demo.controller;

import com.example.demo.Zookeeper.ZookeeperClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import com.example.demo.model.User;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@RestController
public class ClientChat{

    private final ZookeeperClient zooKeeperClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public ClientChat(ZookeeperClient zooKeeperClient) throws IOException {
        this.zooKeeperClient = zooKeeperClient;
    }

    /*@PostMapping("/send-message")
    public String sendMessage(@RequestParam String from, @RequestParam String to, @RequestParam String content) {
        try {
            String bestServer = zooKeeperClient.selectBestServer();
            String serverUrl = "http://" + bestServer + ":8080/send";
            restTemplate.postForObject(serverUrl, null, String.class, from, to, content);
            return "Message sent to " + to + " via server " + bestServer;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }*/

    //Method that will receive the parameters from the front-end and send them to the zookeeperclient
    @PostMapping("/send-login")
    public String sendLogin(@RequestBody String username, @RequestBody String password) {
        try {
            String bestServer = zooKeeperClient.selectBestServer();
            String serverUrl = "https://" + bestServer + ":8081/send";
            return restTemplate.postForObject(serverUrl, null, String.class, username,password);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/send-register")
    public String sendRegister(@RequestParam String username, @RequestParam String password) {
        try {
            String bestServer = zooKeeperClient.selectBestServer();
            String serverUrl = "https://" + bestServer + ":8081/send-register";
            return restTemplate.postForObject(serverUrl, null, String.class, username,password);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
