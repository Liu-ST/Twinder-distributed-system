package Config;

public class ConsumerConfig {

  /**
   * Maximum number of potential matches being stored
   */
  public static final int MAX_POTENTIAL_MATCHES = 100;

  /**
   * Number of connections opened towards the message queue host - also used to allocate threads
   */
  public static final int NUM_CONNECTIONS = 300;

  /**
   * RabbitMQ Host
   *
   * AWS Host: "50.112.70.179"
   */
  public static final String QUEUE_HOST = "50.112.70.179";

  /**
   * RabbitMQ Port
   */
  public static final int QUEUE_PORT = 5672;

  /**
   * Virtual Host Name
   */
  public static final String VHOST_NAME = "cs6650";

  /**
   * Username for vhost authentication
   */
  public static final String USER_NAME = "Cody";

  /**
   * Password for vhost authentication
   */
  public static final String PASSWORD = "CS6650-Lab";

  /**
   * Queue Name
   */
  public static final String QUEUE_NAME = "SwipeData1";

  /**
   * Queue durability
   */
  public static final boolean QUEUE_DURABILITY = true;

  /**
   * Queue auto acknowledgement - currently not auto
   */
  public static final boolean QUEUE_AUTO_ACK = false;

  /**
   * DynamoDB table name
   */
  public static final String DYNAMO_TABLE_NAME = "SwipeData";

  /**
   * Primary Key field name
   */
  public static final String DYNAMO_PK = "SwiperId";

  /**
   * a String represents the count left swipe column
   */
  public static final String LEFT_SWIPE_COL_NAME = "LeftSwipe";

  /**
   * a String represents the count right swipe column
   */
  public static final String RIGHT_SWIPE_COL_NAME = "RightSwipe";

  /**
   * a String represents the potential matches
   */
  public static final String MATCHES_COL_NAME = "Matches";

  /**
   * Number of elements being sent per dynamodb update transaction
   */
  public static final int NUM_REQ_PER_TRANSACTION = 100;

  /**
   * Private constructor since no object should be constructed for this class
   */
  private ConsumerConfig(){}

}
