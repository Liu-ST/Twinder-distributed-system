import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.transformers.v2.DynamodbEventTransformer;

import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.List;
import software.amazon.awssdk.services.dynamodb.model.Record;

public class DynamoDBReplicationFunction implements RequestHandler<DynamodbEvent, Void> {

  private static final String TARGET_TABLE_NAME = "SwipeDataRead";
  private DynamoDbClient dynamoDBClient;

  @Override
  public Void handleRequest(DynamodbEvent event, Context context) {
    // Initialize dynamoDB connection
    init();

    List<Record> convertedRecords = DynamodbEventTransformer.toRecordsV2(event);
    // Retrieve stream data
    for (Record record : convertedRecords) {
      if ("INSERT".equals(record.eventName()) || "MODIFY".equals(record.eventName())) {
        Map<String, AttributeValue> newImage = record.dynamodb().newImage();
        if (newImage != null) {
          newImage.get("");
          // Perform replication by putting the item into the target table
          replicateToTargetTable(newImage);
        }
      }
    }
    return null;
  }

  private void init() {
    //Set aws credentials
    System.setProperty("aws.accessKeyId", "ASIATYHSEPEYQ2VWIQOP");
    System.setProperty("aws.secretAccessKey", "giYqacLXKX9PxKv8AvYKegefdhObJiC+KCM/LS2O");
    System.setProperty("aws.sessionToken",
        "FwoGZXIvYXdzEOf//////////wEaDMFDJoyNQZB6LJw5OCLJAWYM4EIVA57ZSE/WHXTWNqhX4VpFhI9m6aLK7xSmTVV+vGwvTVvkNwMa6IkimHOs01wBfEiJOMDqLy+aZDWxhLoNF9/jdrbZeT5r5HTLPT7PVgHtAhTiTeLY+C/qi41e1jQpkRusaYIG0RUn9N80gunF5E9eshWVijYN8OCit+QABj4dlLOOmXGcqRXjZGw54ONZ2t/h+FQgP3WP3Fm6iLcyZbcU69uxDjVz0nBpb/FlXfUNvVcNw3PBgliOMCdEksCiX06irhRHmyjavsWmBjIt/UnLGKFCm2qQV39nG1MXIfGKB3jXJtL+BiPsK3Or0I1n/5zGsV2tVREVLiOL");
    SystemPropertyCredentialsProvider cred = SystemPropertyCredentialsProvider.create();
    this.dynamoDBClient = DynamoDbClient.builder()
        .credentialsProvider(cred)
        .region(Region.US_WEST_2)
        .httpClient(UrlConnectionHttpClient.builder().build())
        .build();
  }

  private void replicateToTargetTable(Map<String, AttributeValue> item) {

    PutItemRequest putItemRequest = PutItemRequest
        .builder()
        .tableName(TARGET_TABLE_NAME)
        .item(item)
        .build();
    PutItemResponse putItemResult = dynamoDBClient.putItem(putItemRequest);
    // You can log the result or handle errors if needed
  }
}