package io.github.flamehub.commons.database;

import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

public final class DatastoreFactory {

  public static Datastore create(final MongoClient mongoClient, final String database, final Class... objectList) {
    final Datastore datastore = Morphia.createDatastore(mongoClient, database);
    datastore.getMapper().map(objectList);
    datastore.ensureIndexes();
    return datastore;
  }

}
