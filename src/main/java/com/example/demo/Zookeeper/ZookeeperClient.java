package com.example.demo.Zookeeper;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


import org.bson.Document;

@Configuration
public class ZookeeperClient {
    private ZooKeeper zoo;
    private static final String host = "localhost:2181";

    private final RestTemplate restTemplate = new RestTemplate();

    public ZookeeperClient() throws IOException {
        this.zoo = new ZooKeeper(host, 3000, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired) {
                try {
                    reconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void reconnect() throws IOException{
        this.zoo = new ZooKeeper(host, 3000, null);
    }

    public void close() throws InterruptedException {
        zoo.close();
    }

    public String getHost(){
        return host;
    }

    public void registerNewServer(String path,String ip, int load) throws KeeperException, InterruptedException{
        String data = ip + ":" + load;
        Stat stat = zoo.exists(path, false);
        if(stat == null){
            zoo.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } else{
            zoo.setData(path, data.getBytes(), stat.getVersion());
        }
    }

    public String selectBestServer() throws KeeperException, InterruptedException{
        List<String> servers= zoo.getChildren("/servers", false);
        String bestServer = null;
        int minLoad = Integer.MAX_VALUE;
        String bestServerIp = null;
        for(String server : servers){
            byte[] serverData = zoo.getData("/servers/ +server", false, null);
            String[] dataParts = new String(serverData).split(":");
            String ip = dataParts[0];
            int load = Integer.parseInt(new String(serverData));
            if(load < minLoad){
                minLoad = load;
                bestServer = server;
                bestServerIp = ip;
            }
        }
        return bestServerIp;
    }

    public void createEPH(String path, String name) throws KeeperException, InterruptedException{
            byte[] data = name.getBytes();
            zoo.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }

    public void createPerm(String path, String name) throws KeeperException, InterruptedException{
        byte[] data = name.getBytes();
        zoo.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

/*    public List<List<String>> getMessages(String chatName){
        List<List<String>> messageList = new ArrayList<>();

        MongoCollection<Document> collection = database.GetCollection("Chats", chatName);
        FindIterable<Document> docs = collection.find();
        for(Document doc: docs){
            List <String> messageInfo = List.of(doc.getString("sender"), doc.getString("message"), doc.getString("timestamp"));
            messageList.add(messageInfo);
        }
        return messageList;
    }*/


    public void updateServerLoad(String path, String ip, int load) throws KeeperException, InterruptedException {
        String data = ip + ":" + load;
        Stat stat = zoo.exists(path, false);
        if (stat != null) {
            zoo.setData(path, data.getBytes(), stat.getVersion());
        }
    }


    //Method that will receive the parameters from the ClientChatController
    @PostMapping("/send-login")
    public String sendLogin(@RequestBody String username, @RequestBody String password) {
        try {
            String bestServer = selectBestServer();
            String serverUrl = "https://" + bestServer + ":8080/send-login";
            return restTemplate.postForObject(serverUrl, null, String.class, username, password);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/send-register")
    public String sendRegister(@RequestBody String username, @RequestBody String password) {
        try {
            String bestServer = selectBestServer();
            String serverUrl = "https://" + bestServer + ":8081/send-register";
            return restTemplate.postForObject(serverUrl, null, String.class, username,password);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public void createNode(String path) throws KeeperException, InterruptedException {
        this.zoo.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public boolean nodeExists(String path) throws KeeperException, InterruptedException {
        return this.zoo.exists(path, false) != null;
    }













    public String sendMessage(@RequestParam String from, @RequestParam String to, @RequestParam String content) {
        try {
            String bestServer = selectBestServer();
            String serverUrl = "http://" + bestServer + ":8080/send-message";
            restTemplate.postForObject(serverUrl, null, String.class, from, to, content);
            return "Message sent to " + to + " via server " + bestServer;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
