package Data;

import Config.ConsumerConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Placeholder object to store swiper's data temporarily
 *
 * @author Cody Cao
 */
public class UserSwipeData {

  private int swiperId;
  private AtomicInteger numLeftSwipe, numRightSwipe;
  private ArrayBlockingQueue<Integer> matches;

  /**
   * Construct a new data placeholder object for user's swipe
   *
   * @param swiperId
   */
  public UserSwipeData(int swiperId) {
    this.swiperId = swiperId;
    this.numLeftSwipe = new AtomicInteger(0);
    this.numRightSwipe = new AtomicInteger(0);
    this.matches = new ArrayBlockingQueue<>(ConsumerConfig.MAX_POTENTIAL_MATCHES);
  }

  /**
   * Increment number of left swipe
   */
  public void incLeftSwipe() {
    this.numLeftSwipe.getAndIncrement();
  }

  /**
   * Increment number of right swipe
   */
  public void incRightSwipe() {
    this.numRightSwipe.getAndIncrement();
  }

  /**
   * Add a potential match (right-swiped) userId
   *
   * @param swipeeId
   */
  public synchronized void addPotentialMatch(int swipeeId) {
    if (matches.size() == ConsumerConfig.MAX_POTENTIAL_MATCHES) {
      matches.poll();
    }

    matches.offer(swipeeId);
  }

  /**
   * @return an Integer represents the number of left swipe of the user
   */
  public int getNumLeftSwipe() {
    return numLeftSwipe.get();
  }

  /**
   * @return an Integer represents the number of right swipe of the user
   */
  public int getNumRightSwipe() {
    return numRightSwipe.get();
  }

  /**
   * @return an array represents the id of potential matches
   */
  public List<Integer> getMatches() {
    List<Integer> res = new ArrayList<>();
    res.addAll(matches);
    return res;
  }
}
