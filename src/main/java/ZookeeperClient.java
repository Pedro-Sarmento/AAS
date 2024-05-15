import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperClient {
    private ZooKeeper zoo;
    CountDownLatch connectionLatch = new CountDownLatch(1);
    private static final String host = "localhost:2181";


    public ZooKeeper connect() throws IOException, InterruptedException {
         zoo = new ZooKeeper(host, 3000, new Watcher(){
             public void process(WatchedEvent we){
                 if (we.getState() == Event.KeeperState.Expired) {
                 try{
                     reconnect();
                 } catch(IOException e){
                     e.printStackTrace();
                 }
                 }
                 if (we.getState() == Event.KeeperState.SyncConnected){
                     connectionLatch.countDown();
                 }
             }
         });
         connectionLatch.await();
         return zoo;
    }

    private void reconnect() throws IOException{
        this.zoo = new ZooKeeper(host, 3000, null);
    }

    public void close() throws InterruptedException {
        zoo.close();
    }

    public void registerNewServer(String path, byte[] data) throws KeeperException, InterruptedException{
        Stat stat = zoo.exists(path, false);
        if(stat == null){
            zoo.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } else{
            zoo.setData(path, data, stat.getVersion());
        }
    }

    public String selectBestServer() throws KeeperException, InterruptedException{
        List<String> servers= zoo.getChildren()
    }

    public void createEPH(String path, String name) throws KeeperException, InterruptedException{
            byte[] data = name.getBytes();
            zoo.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }

}
