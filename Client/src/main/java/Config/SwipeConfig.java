package Config;

/**
 * Configuration class for a Swipe data
 *
 * @author Cody Cao
 */
public class SwipeConfig {

  /**
   * Swiper's id range
   */
  public static final int SWIPER_RANGE = 50000;

  /**
   * Swipee's id range
   */
  public static final int SWIPEE_RANGE = SWIPER_RANGE;

  /**
   * Length of a randomly generated comment
   */
  public static final int COMMENT_LENGTH = 256;

  /**
   * Number of time the client thread will retry sending a thread until it deem the request as
   * failed
   */
  public static final int NUM_RETRY = 5;

  /**
   * private constructor, this class shouldn't be used to construct an object
   */
  public SwipeConfig() {
  }
}
