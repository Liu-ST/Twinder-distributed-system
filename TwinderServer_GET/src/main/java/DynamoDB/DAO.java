package DynamoDB;

import Config.ServerConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

/**
 * Data access class to retrieve a user's list of potential matches from DynamoDB
 *
 * @author Cody Cao
 */
public class DAO {

  private int swiperId;
  private DynamoDbClient dynamoDbClient;

  private Map<String, AttributeValue> keyMap;

  /**
   * Constructs a new DAO object
   *
   * @param swiperId
   */
  public DAO(int swiperId) {
    this.swiperId = swiperId;
    //Create a keymap
    keyMap = new HashMap<>();
    keyMap.put(ServerConfig.DYNAMO_PK,
        AttributeValue.builder().n(String.valueOf(swiperId)).build());

    SystemPropertyCredentialsProvider cred = SystemPropertyCredentialsProvider.create();
    this.dynamoDbClient = DynamoDbClient.builder()
        .credentialsProvider(cred)
        .region(Region.US_WEST_2)
        .httpClient(UrlConnectionHttpClient.builder().build())
        .build();
  }

  /**
   * @return a List of Integer represents the potential matches
   */
  public List<Integer> getMatches() {
    GetItemRequest request = GetItemRequest.builder()
        .tableName(ServerConfig.DYNAMO_TABLE_NAME)
        .key(keyMap)
        .projectionExpression(
            ServerConfig.MATCHES_COL_NAME) //Only retrieve the list of potential matches
        .build();

    // Send the request to DynamoDB and retrieve the response
    GetItemResponse response = dynamoDbClient.getItem(request);
    List<Integer> matches = null;
    if (response.hasItem()) {
      matches = new ArrayList<>();

      //Extract the list property from the response
      AttributeValue attributeValue = response.item().get(ServerConfig.MATCHES_COL_NAME);
      if (attributeValue != null && attributeValue.l() != null) {
        List<AttributeValue> listAttribute = attributeValue.l();
        for (AttributeValue item : listAttribute) {
          //Add the potential matches to the list
          matches.add(Integer.parseInt(item.n()));
        }
      }
    }
    return matches;
  }

  /**
   * @return a pair of two integers represents the number of left and right swipes the given swiper
   * has performed
   */
  public int[] getStats() {
    GetItemRequest request = GetItemRequest.builder()
        .tableName(ServerConfig.DYNAMO_TABLE_NAME)
        .key(keyMap)
        .projectionExpression(
            ServerConfig.LEFT_SWIPE_COL_NAME + ", "
                + ServerConfig.RIGHT_SWIPE_COL_NAME) //Only retrieve the number of left and right swipes
        .build();

    // Send the request to DynamoDB and retrieve the response
    long startReq = System.currentTimeMillis();
    GetItemResponse response = dynamoDbClient.getItem(request);
    long reqLatency = System.currentTimeMillis() - startReq;
    System.out.println("Request to DynamoDB took: " + reqLatency + "ms");
    int[] result = null;
    if (response.hasItem()) {
      //Extract the list property from the response
      AttributeValue numLeftSwipes = response.item().get(ServerConfig.LEFT_SWIPE_COL_NAME);
      AttributeValue numRightSwipes = response.item().get(ServerConfig.RIGHT_SWIPE_COL_NAME);

      //Construct the result array
      result = new int[]{
          Integer.parseInt(numLeftSwipes.n()),
          Integer.parseInt(numRightSwipes.n())
      };
    }
    //In case the given id's not in the dynamodb, return a null array to signal this
    return result;
  }
}
