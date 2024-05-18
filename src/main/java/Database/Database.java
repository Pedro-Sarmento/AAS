package Database;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.Date;

public class Database {
        private static final String MongoDBURI = "mongodb://localhost:27017";
        private static volatile MongoClient mongoclient = null;

        public static MongoClient getClient() {
            if (mongoclient == null) {
                mongoclient = MongoClients.create(MongoDBURI);
            }
            return mongoclient;
        }



        public static MongoCollection<Document> GetCollection( String Database, String Collection) {
                MongoClient mongoClient = getClient();
                MongoDatabase database = mongoClient.getDatabase(Database);
                return database.getCollection(Collection);
        }

        public static void AddUser(String username, String password, int userID){
            MongoCollection<Document> collection = GetCollection("Users", "UserInfo");

            Document user_info = new Document();

            user_info.append("username", username)
                     .append("password", password)
                     .append("userID", userID);

            collection.insertOne(user_info);
        }

        public static void DeleteUser(int userID){
            MongoCollection<Document> collection = GetCollection("Users", "UserInfo");
            Bson deleteFilter = eq("userID", userID);

            collection.deleteOne(deleteFilter);
        }

        public static void SendMessage(String sender, String message_content){
            MongoCollection<Document> collection = GetCollection("Chats", "messages");

            Document message_document = new Document();

            message_document.append("sender", sender)
                    .append("timestamp", new Date())
                    .append("message", message_content);
            collection.insertOne(message_document);

        }

        public static String ReadMessages(){
            MongoCollection<Document> collection = GetCollection("Chats", "messages");
            Document document1 = collection.find(new Document("sender", "teste")).first();
            return document1.getString("sender");
        }

        public static long getChatLenght(String collectionName){
            MongoCollection<Document> collection = GetCollection("Chats", collectionName);
            return collection.countDocuments();
        }

        public static MongoCollection<Document> GetDocuments(String collection){
            String[] asd = null;
            MongoCollection<Document>  asds = null;
            return asds;
        }
        public static void main(String[] args){
//            AddUser("teste", "1234", 123);
//            DeleteUser(123);
//            SendMessage("teste", "Hello World!");
            System.out.println(ReadMessages());
        }
    }

