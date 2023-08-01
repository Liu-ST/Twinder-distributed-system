package Config;

/**
 * Configuration class for GET request
 *
 * @author Cody Cao
 */
public class GETConfig {
  /**
   * Number of GET requests to send each time the get thread run
   */
  public static final int NUM_GET_REQ = 5;

  public static final String GET_SERVER_URL = "http://54.184.253.70:8080/TwinderServer_GET";

  public static final String STATS_PATH = GET_SERVER_URL + "/stats/";
  public static final String MATCHES_PATH = GET_SERVER_URL + "/matches/";

  /**
   * private constructor, this class shouldn't be used to construct an object
   */
  private GETConfig() {
  }
}
