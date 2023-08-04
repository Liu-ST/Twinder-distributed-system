package Config;

public class ServerConfig {
  /**
   * Swiper's id range
   */
  public static final int SWIPER_RANGE = 50000;

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
   * AWS Credentials
   */

  public static final String AWS_ACCESS_KEY = "ASIATYHSEPEYWEDIYTGP";
  public static final String AWS_SECRET_ACCESS_KEY = "uHid05oS3hZZRnMXMGohCu0ctX3N+38GlUtk0kqt";
  public static final String AWS_SESSION_TOKEN = "FwoGZXIvYXdzEJv//////////wEaDC9TVX6ZlLZ8vgnCHyLJAZkzEd/kHIu+l6dphqj7D/akaIjUYDfaEMkRI7xaRf+FIKhASYntCxDi1JcBvxaGpPRKn/igMdC5Wuih8pNkkP64zgYuq6nsmmcUfo/kSDCJcG0N9TUSygL1GSVXjUh+Zn9D43RuU/BUqt8XXBmskEVtgIq42BtmT+wqW5OTNY1eXpYfbL3WCqebwj+FHkDpgOgEnMk/HF2pNWALEX2b+2a03sMid5ZqJaYQUKHnRHMCSiAGOWNiaps4pwrTFB63QcdT+L//iPIUiyjA5rSmBjIt3zJXniOapRtJg53yvFDUAI2jtYJDU5gKo2OMvszkA82QrLS764s8lY5Jherf";
  private ServerConfig(){}
}
