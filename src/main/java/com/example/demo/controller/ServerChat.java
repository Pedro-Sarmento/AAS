package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.atomic.AtomicInteger;
import com.example.demo.Zookeeper.ZookeeperClient;

import com.example.demo.model.User;
import com.example.demo.model.Message;
import com.example.demo.services.UserService;

@SpringBootApplication
@EnableWebSocketMessageBroker
public class ServerChat implements WebSocketMessageBrokerConfigurer {
    private ZookeeperClient zookeeperClient;
    private AtomicInteger currentLoad = new AtomicInteger(0);
    private String serverNodePath;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ServerChat(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerChat.class, args);
    }
    @Bean
    public ZookeeperClient zooKeeperClient() throws IOException {
        return new ZookeeperClient();
    }

    @PostConstruct
    public void init() {
        try {
            this.zookeeperClient = zooKeeperClient();
            serverNodePath = "/servers/server-" + System.currentTimeMillis();
            zookeeperClient.registerServer(serverNodePath, (currentLoad.get()));
            measureSystemLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-socket").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @PostMapping("/send")
    public void sendMessage(@RequestParam String from, @RequestParam String to, @RequestParam String content) {
        String destination = "/topic/messages/" + to;
        Message message = new Message(from, content);
        updateLoad(true);
        messagingTemplate.convertAndSend(destination, message);
    }

    //Method that will receive the parameters from the zookeeperclient and now verify them in mongoDB
    @PostMapping("/send-login")
    public String sendLogin(@RequestParam String username, @RequestParam String password) {
        User client = UserService.findByUsername(username);
        return (client != null) ?  "Valid" :  "Invalid";

        //String destination = "/topic/client/";
        //Client client = new Client(username,password);
        //updateLoad(true);
        //messagingTemplate.convertAndSend(destination, client);
    }

    @PostMapping("/send-register")
    public String sendRegister(@RequestParam String username, @RequestParam String password){
        User usercheck = UserService.findByUsername(username);
        if(usercheck == null){
            User newUser = new User(username, password);
            UserService.saveUser(newUser);
            return "The User as been Registered";
        }
        else {
            return "User already exists";
        }
    }









    public void updateLoad(boolean increase) {
        int newLoad = increase ? currentLoad.incrementAndGet() : currentLoad.decrementAndGet();
        try {
            zookeeperClient.updateServerLoad(serverNodePath, zookeeperClient.getHost(), Integer.parseInt(String.valueOf(currentLoad.get())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void measureSystemLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoad = osBean.getSystemLoadAverage();
        int adjustedLoad = Math.min(100, (int) (systemLoad * 100.0));
        updateLoad(adjustedLoad);
    }*/
    private void measureSystemLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoad = osBean.getSystemLoadAverage();
        boolean increase = systemLoad > 1.0;
        updateLoad(increase);
    }
}