package DynamoDB;

import Config.ConsumerConfig;
import Data.UserSwipeData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.InternalServerErrorException;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

/**
 * SwipeCounterWriter class - used to update number of left/right swipes in DynamoDB
 *
 * @author Cody Cao
 */
public class SwipeDataWriter implements Runnable {
  private ConcurrentMap<Integer, UserSwipeData> dataMap;
  private BlockingQueue<Integer> dataQueue;
  private DynamoDbClient dynamoDbClient;
  private Set<Integer> swiperSet;

  /**
   *
   * @param dataQueue
   * @param dataMap
   */
  public SwipeDataWriter(BlockingQueue<Integer> dataQueue, ConcurrentMap<Integer, UserSwipeData> dataMap) {
    SystemPropertyCredentialsProvider cred = SystemPropertyCredentialsProvider.create();
    this.dataQueue = dataQueue;
    this.swiperSet = new HashSet<>();
    this.dataMap = dataMap;
    this.dynamoDbClient = DynamoDbClient.builder()
        .credentialsProvider(cred)
        .region(Region.US_WEST_2)
        .httpClient(UrlConnectionHttpClient.builder().build())
        .build();
  }

  @Override
  public void run() {
    int counter = 0;
    while (true) {
      Integer swiperId = dataQueue.poll();

      //Sleep thread if data isn't available yet
      if(swiperId == null) {
        try {
          Thread.sleep(10);
          continue;
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      swiperSet.add(swiperId);
      if(swiperSet.size() == ConsumerConfig.NUM_REQ_PER_TRANSACTION) {
        sendTransaction();
      }
    }
  }

  /**
   * Create and send a transaction request to dynamoDB
   */
  private void sendTransaction() {
    try {
      Collection<TransactWriteItem> actions = new ArrayList<>();
      for (int swiperId : swiperSet) {
        UserSwipeData swipeData = dataMap.getOrDefault(swiperId, new UserSwipeData(swiperId));
        List<AttributeValue> matchesAttrValue = swipeData.getMatches().stream()
            .map(number -> AttributeValue.builder().n(String.valueOf(number)).build())
            .collect(Collectors.toList());

        HashMap<String, AttributeValue> swiperRecord = new HashMap<>();
        swiperRecord.put(ConsumerConfig.DYNAMO_PK, AttributeValue.builder().n(Integer.toString(swiperId)).build());
        swiperRecord.put(ConsumerConfig.LEFT_SWIPE_COL_NAME, AttributeValue.builder().n(String.valueOf(swipeData.getNumLeftSwipe())).build());
        swiperRecord.put(ConsumerConfig.RIGHT_SWIPE_COL_NAME, AttributeValue.builder().n(String.valueOf(swipeData.getNumRightSwipe())).build());
        swiperRecord.put(ConsumerConfig.MATCHES_COL_NAME, AttributeValue.builder().l(matchesAttrValue).build());

        Put writeReq = Put
            .builder()
            .tableName(ConsumerConfig.DYNAMO_TABLE_NAME)
            .item(swiperRecord)
            .build();

        //Add request to transaction list
        actions.add(TransactWriteItem.builder().put(writeReq).build());
      }

      //Construct transaction
      TransactWriteItemsRequest updateCountTransaction = TransactWriteItemsRequest
          .builder()
          .transactItems(actions)
          .build();

    // Run the transaction and process the result.
    try {
      dynamoDbClient.transactWriteItems(updateCountTransaction);
    } catch (ResourceNotFoundException rnf) {
      System.err.println("One of the table involved in the transaction is not found" + rnf.getMessage());
    } catch (InternalServerErrorException ise) {
      System.err.println("Internal Server Error" + ise.getMessage());
    } catch (TransactionCanceledException tce) {
      System.err.println("Transaction Canceled " + tce.getMessage());
    } finally {
      //Clear the request set
      this.swiperSet = new HashSet<>();
    }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
