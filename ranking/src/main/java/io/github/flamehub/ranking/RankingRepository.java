package io.github.flamehub.ranking;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.ranking.info.RankingInfo;

import java.util.ArrayList;
import java.util.List;

public final class RankingRepository {

    private final DatabaseConnector databaseConnector;

    public RankingRepository(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public List<RankingEntry> loadByInfo(RankingInfo info) {
        MongoDatabase database = this.databaseConnector.getMongoClient().getDatabase(info.getDatabase());
        MongoCollection<Document> collection = database.getCollection(info.getCollection());

        ArrayList<RankingEntry> into = collection.find()
                .sort(Sorts.descending(info.getField()))
                .limit(info.getLimit())
                .map(document -> {

                    String field = info.getField();
                    if (field.contains(".")) {
                        String[] split = field.split("\\.");
                        Document doc = document.get(split[0], Document.class);
                        return new RankingEntry(document.getString(info.getEntryField()), doc.get(split[1]));
                    }

                    return new RankingEntry(document.getString(info.getEntryField()), document.get(field));
                })
                .into(new ArrayList<>());

//        if (info.getField().equalsIgnoreCase("spendTime")) {
//            into.sort(Comparator.comparingLong(entry -> -Long.parseLong(entry.getValue().toString())));
//
//        }

        return into;
    }

//
//    public List<RankingEntry> loadByInfo(RankingInfo info) {
//        List<RankingEntry> rankingEntries = new ArrayList<>();
//        try (
//                Connection connection = this.databaseService.getConnection();
//                PreparedStatement statement = connection.prepareStatement(this.selectStatement
//                        .replace("{FIELD}", info.getField())
//                        .replace("{LIMIT}", String.valueOf(info.getLimit()))
//                        .replace("{TABLE}", info.getTable()));
//                ResultSet resultSet = statement.executeQuery()
//        )
//        {
//
//            while (resultSet.next()) {
//                RankingEntry name = new RankingEntry(
//                        resultSet.getString(info.getEntryField()),
//                        resultSet.getObject(info.getField())
//                );
//                rankingEntries.add(name);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return rankingEntries;
//
//    }
}
