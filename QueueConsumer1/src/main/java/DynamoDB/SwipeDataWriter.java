package DynamoDB;

import Config.ConsumerConfig;
import Data.SwipeDataMap;
import Data.UserSwipeData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.InternalServerErrorException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;
import software.amazon.awssdk.services.dynamodb.model.Update;

/**
 * SwipeCounterWriter class - used to update number of left/right swipes in DynamoDB
 *
 * @author Cody Cao
 */
public class SwipeDataWriter implements Runnable {

  private SwipeDataMap dataMap;
  private DynamoDbClient dynamoDbClient;
  private Set<Integer> swiperSet;

  public SwipeDataWriter() {
    SystemPropertyCredentialsProvider cred = SystemPropertyCredentialsProvider.create();
    this.swiperSet = new HashSet<>();
    this.dataMap = SwipeDataMap.getInstance();
    this.dynamoDbClient = DynamoDbClient.builder()
        .credentialsProvider(cred)
        .region(Region.US_WEST_2)
        .httpClient(UrlConnectionHttpClient.builder().build())
        .build();
  }

  @Override
  public void run() {
    while (true) {
      //Sleep thread if data isn't available yet
      if (dataMap.isEmpty()) {
        try {
          Thread.sleep(10);
          continue;
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      sendTransaction();

      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Create and send a transaction request to dynamoDB
   */
  private void sendTransaction() {
    try {
      String updateExpression = "SET LeftSwipe = if_not_exists(LeftSwipe, :startValue) + :leftinc, "
          + "RightSwipe = if_not_exists(RightSwipe, :startValue) + :rightinc, "
          + "Matches = list_append(if_not_exists(Matches, :emptyList), :newMatches)";

      //Take snapshot from dataMap object
      Map<Integer, UserSwipeData> dataMapSnapshot = dataMap.getSnapshot();

      Iterator<Integer> iter = dataMapSnapshot.keySet().iterator();
      int count = 0;
      while (iter.hasNext()) {
        Collection<TransactWriteItem> actions = new ArrayList<>();
        //Making sure only 100 update request max is sent per transaction
        for (int i = 0; i < 100 && iter.hasNext(); i++) {
          int swiperId = iter.next();

          UserSwipeData updateData = dataMapSnapshot.get(swiperId);

          //Stream new matches to a list
          List<AttributeValue> matchesAttrValue = updateData.getMatches().stream()
              .map(number -> AttributeValue.builder().n(String.valueOf(number)).build())
              .collect(Collectors.toList());

          Map<String, AttributeValue> itemKey = new HashMap<>();
          itemKey.put(ConsumerConfig.DYNAMO_PK,
              AttributeValue.builder().n(Integer.toString(swiperId)).build());

          //Set up attributes
          Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
          expressionAttributeValues.put(":startValue", AttributeValue.builder().n("0").build());
          expressionAttributeValues.put(":emptyList",
              AttributeValue.builder().l(new ArrayList<>()).build());
          expressionAttributeValues.put(":leftinc",
              AttributeValue.builder().n(String.valueOf(updateData.getNumLeftSwipe())).build());
          expressionAttributeValues.put(":rightinc",
              AttributeValue.builder().n(String.valueOf(updateData.getNumRightSwipe())).build());
          expressionAttributeValues.put(":newMatches",
              AttributeValue.builder().l(matchesAttrValue).build());

          //Construct the request
          Update updateReq = Update
              .builder()
              .tableName(ConsumerConfig.DYNAMO_TABLE_NAME)
              .key(itemKey)
              .updateExpression(updateExpression)
              .expressionAttributeValues(expressionAttributeValues)
              .build();

          //Add request to transaction list
          actions.add(TransactWriteItem.builder().update(updateReq).build());
        }
        //Construct transaction
        TransactWriteItemsRequest updateCountTransaction = TransactWriteItemsRequest
            .builder()
            .transactItems(actions)
            .build();

        // Run the transaction and process the result.
        try {
          System.out.println("Sending...");
          long start = System.currentTimeMillis();
          dynamoDbClient.transactWriteItems(updateCountTransaction);
          long latency = System.currentTimeMillis() - start;
          System.out.println(
              String.format("Transaction Successful, that took %d milliseconds", latency));
        } catch (ResourceNotFoundException rnf) {
          System.err.println(
              "One of the table involved in the transaction is not found" + rnf.getMessage());
        } catch (InternalServerErrorException ise) {
          System.err.println("Internal Server Error" + ise.getMessage());
        } catch (TransactionCanceledException tce) {
          System.err.println("Transaction Canceled " + tce.getMessage());
        }
      }


    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
