package io.github.flamehub.ranking;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.ranking.info.RankingInfo;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public final class RankingRepository {

  private final DatabaseConnector databaseConnector;

  public RankingRepository(final DatabaseConnector databaseConnector) {
    this.databaseConnector = databaseConnector;
  }

  public List<RankingEntry> loadByInfo(final RankingInfo info) {
    final MongoDatabase database = databaseConnector.getMongoClient()
        .getDatabase(info.getDatabase());
    final MongoCollection<Document> collection = database.getCollection(info.getCollection());

    final ArrayList<RankingEntry> results = collection.find()
        .sort(Sorts.descending(info.getField()))
        .limit(info.getLimit())
        .map(document -> {
          final RankingEntry rankingEntry = new RankingEntry(
              document.getString(info.getEntryField()));

          for (final String field : info.getField()) {
            Object value = document;

            final String[] splitFields = field.split("\\.");
            for (final String splitField : splitFields) {
              if (value instanceof Document) {
                value = ((Document) value).get(splitField);
              } else {
                value = null;
                break;
              }
            }

            if (value != null) {
              rankingEntry.getValue().add(value);
            }
          }

          return rankingEntry;
        })
        .into(new ArrayList<>());

    return results;
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
