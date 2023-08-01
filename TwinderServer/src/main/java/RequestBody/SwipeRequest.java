package RequestBody;

import com.google.gson.annotations.SerializedName;

public class SwipeRequest {

  @SerializedName("swipeType")
  private String swipeType;

  @SerializedName("swiper")
  private int swiper;

  @SerializedName("swipee")
  private int swipee;

  @SerializedName("comment")
  private String comment;

  /**
   * Construct a new SwipeResponse object
   *
   * @param swiper  an Integer represents the swiper's id
   * @param swipee  an Integer represents the swipee's id
   * @param comment a String represents the message
   */
  public SwipeRequest(int swiper, int swipee, String comment) {
    this.swipeType = null;
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
  }

  /**
   * @param swipeType a String represents the swipe type (left/right)
   * @param swiper    an Integer represents the swiper's id
   * @param swipee    an Integer represents the swipee's id
   * @param comment   a String represents the message
   */
  public SwipeRequest(String swipeType, int swiper, int swipee, String comment) {
    this.swipeType = swipeType;
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
  }

  /**
   * @return a String represents the swipe type (left/right)
   */
  public String getSwipeType() {
    return swipeType;
  }

  /**
   * Set swipe type
   *
   * @param swipeType a String represents the swipe type (left/right)
   */
  public void setSwipeType(String swipeType) {
    this.swipeType = swipeType;
  }

  /**
   * @return an Integer represents the swiper's id
   */
  public int getSwiper() {
    return swiper;
  }

  /**
   * Set swiper's ID
   *
   * @param swiper an Integer represents the swiper's id
   */
  public void setSwiper(int swiper) {
    this.swiper = swiper;
  }

  /**
   * @return an Integer represents the swipee's id
   */
  public int getSwipee() {
    return swipee;
  }

  /**
   * Set swipee's id
   *
   * @param swipee an Integer represents the swipee's id
   */
  public void setSwipee(int swipee) {
    this.swipee = swipee;
  }

  /**
   * @return a String represents the message
   */
  public String getComment() {
    return comment;
  }

  /**
   * Set the message
   *
   * @param comment a String represents the message
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   *
   * @return a String contains object's info
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SwipeRequest{");
    sb.append("swipeType='").append(swipeType).append('\'');
    sb.append(", swiper=").append(swiper);
    sb.append(", swipee=").append(swipee);
    sb.append(", comment='").append(comment).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
