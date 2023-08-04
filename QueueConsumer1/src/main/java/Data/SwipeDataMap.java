package Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Singleton class to keep track of data update
 */
public class SwipeDataMap {

  private static SwipeDataMap instance;
  private ConcurrentMap<Integer, UserSwipeData> dataMap;

  public static SwipeDataMap getInstance() {
    if (instance == null) {
      instance = new SwipeDataMap();
    }
    return instance;
  }

  public SwipeDataMap() {
    this.dataMap = new ConcurrentHashMap<>();
  }

  public void putData(Swipe swipeInfo) {
    //Updating data
    int swiperId = swipeInfo.getSwiper();
    UserSwipeData swipeData = dataMap.getOrDefault(swiperId, new UserSwipeData(swiperId));
    String swipeType = swipeInfo.getSwipeType();
    if (swipeType.equals("left")) {
      swipeData.incLeftSwipe();
    } else {
      //Update potential match and increment right swipe count
      swipeData.incRightSwipe();
      int swipeeId = swipeInfo.getSwipee();
      swipeData.addPotentialMatch(swipeeId);
    }
    dataMap.putIfAbsent(swiperId, swipeData);
  }

  /**
   * Take snapshot and reset the data map
   *
   * @return a ConcurrentMap object represents the snapshot taken
   */
  public synchronized Map<Integer, UserSwipeData> getSnapshot() {
    Map<Integer, UserSwipeData> snapshot = dataMap;
    this.dataMap = new ConcurrentHashMap<>();
    return snapshot;
  }

  public boolean isEmpty() {
    return dataMap.isEmpty();
  }
}
