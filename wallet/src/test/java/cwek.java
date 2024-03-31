import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;

public class cwek {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://admin:FGZtTvywHqQ5MWY9t2KhahqSh8ACVFpRH9TnmVyhSuxSBBTxPH@85.10.196.21:10001")) {
            MongoCollection<Document> collection = mongoClient.getDatabase("global").getCollection("wallet_users");

            collection.updateMany(
                    new Document(),
                    Updates.set("nameLowerCase", new Document("$toLower", "$name"))
            );
        }
    }

}
