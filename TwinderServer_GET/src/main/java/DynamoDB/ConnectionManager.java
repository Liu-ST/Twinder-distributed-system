package DynamoDB;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Singleton class to hold DynamoDB connections
 */
public class ConnectionManager {
  private static final int NUM_CONN = 20;
  private static ConnectionManager instance;
  private BlockingQueue<DynamoDbClient> connectionPool;
  public static ConnectionManager getInstance() {
    if(instance == null) {
      instance = new ConnectionManager();
    }
    return instance;
  }

  public ConnectionManager() {
    this.connectionPool = new ArrayBlockingQueue<>(NUM_CONN);
    SystemPropertyCredentialsProvider cred = SystemPropertyCredentialsProvider.create();
    for(int i = 0; i < NUM_CONN; i++) {
      connectionPool.offer(DynamoDbClient.builder()
          .credentialsProvider(cred)
          .region(Region.US_WEST_2)
          .httpClient(UrlConnectionHttpClient.builder().build())
          .build());
    }
  }

  public DynamoDbClient getConnection() {
    try {
      return connectionPool.poll(100, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void putConnection(DynamoDbClient client) {
    connectionPool.offer(client);
  }
}
