import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.transformers.v2.DynamodbEventTransformer;

import java.util.logging.Level;
import java.util.logging.Logger;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.OperationType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.List;
import software.amazon.awssdk.services.dynamodb.model.Record;

public class DynamoDBReplicationFunction implements RequestHandler<DynamodbEvent, Void> {

  private static final String TARGET_TABLE_NAME = "SwipeDataRead";
  private static final Logger logger = Logger.getLogger(DynamoDBReplicationFunction.class.getName());
  private DynamoDbClient dynamoDBClient;

  @Override
  public Void handleRequest(DynamodbEvent event, Context context) {
    // Initialize dynamoDB connection
    init();

    List<Record> convertedRecords = DynamodbEventTransformer.toRecordsV2(event);
    // Retrieve stream data
    for (Record record : convertedRecords) {

      if (OperationType.INSERT == record.eventName() || OperationType.MODIFY == record.eventName()) {
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
    System.setProperty("aws.accessKeyId", "ASIATYHSEPEYRBF42JPY");
    System.setProperty("aws.secretAccessKey", "XKfT9e4XhEI9K+NRRTx+f295EBFAJ0BAEylUcfKY");
    System.setProperty("aws.sessionToken",
        "FwoGZXIvYXdzEOz//////////wEaDCHS9Hg5E4QGktWrOSLJAQq2Iv8ROnrsrMPGPmODIkOsPUuufrW6eB/0rUwZLTBq1nrkbR9XGAtyNDo+rB9KN1tCGRcPnXcM77Z9+4s4kqzv6LoDZg3VvvTvhCTLbuINVQIfWwU01O3xs0s2Fne00QRnceaUAJ8KlPIs2dtfFtvvj04W4YUoXppLm6lTr+/OyWqIi/irH+t/e/LIn0y63vmaUxmNVotBU6p1oM7p6iYBuZAX8MuYSFcJu3REU3c+2jy0Rx9UMLOfB9IYzUJebmvAq1MfxqBPByiP3MamBjItum7ztRYXQZHE+Tpj5C1+Gg3HHqBkzW0kh9PmMTD+wimwkuO21MCuw/BfkInT");
    SystemPropertyCredentialsProvider cred = SystemPropertyCredentialsProvider.create();
    this.dynamoDBClient = DynamoDbClient.builder()
        .credentialsProvider(cred)
        .region(Region.US_WEST_2)
        .httpClient(UrlConnectionHttpClient.builder().build())
        .build();
  }

  private void replicateToTargetTable(Map<String, AttributeValue> item) {
    try{
      PutItemRequest putItemRequest = PutItemRequest
          .builder()
          .tableName(TARGET_TABLE_NAME)
          .item(item)
          .build();
      PutItemResponse putItemResult = dynamoDBClient.putItem(putItemRequest);

      logger.log(Level.INFO, "Item updated, result is:" + putItemResult.toString());
    } catch (Exception e){
      logger.severe("An error occurred: " + e.getMessage());
    }
    // You can log the result or handle errors if needed
  }
}