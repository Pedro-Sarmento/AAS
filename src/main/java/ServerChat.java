import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.atomic.AtomicInteger;
import Zookeeper.ZookeeperClient;

@SpringBootApplication
@EnableWebSocketMessageBroker
public class ServerChat implements WebSocketMessageBrokerConfigurer {

    private ZookeeperClient zooKeeperClient;
    private AtomicInteger currentLoad = new AtomicInteger(0);
    private String serverNodePath;
    private final SimpMessagingTemplate messagingTemplate;

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
            this.zooKeeperClient = new ZookeeperClient();
            serverNodePath = "/servers/server-" + System.currentTimeMillis();
            zooKeeperClient.registerNewServer(serverNodePath, String.valueOf(currentLoad.get()));
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
        Message message = new Message(from, to, content);
        updateLoad(true);
        messagingTemplate.convertAndSend(destination, message);
    }

    //AQUI
    @PostMapping("/send-login")
    public void sendMessage(@RequestParam String username, @RequestParam String password) {
        String destination = "/topic/messages/" + to;
        Message message = new Message(from, to, content);
        updateLoad(true);
        messagingTemplate.convertAndSend(destination, message);
    }

    public void updateLoad(boolean increase) {
        int newLoad = increase ? currentLoad.incrementAndGet() : currentLoad.decrementAndGet();
        try {
            zooKeeperClient.updateServerLoad(serverNodePath, String.valueOf(newLoad).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void measureSystemLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoad = osBean.getSystemLoadAverage();
        int adjustedLoad = Math.min(100, (int) (systemLoad * 100.0));
        updateLoad(adjustedLoad);
        s
    }
}