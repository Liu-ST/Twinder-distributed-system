package Request;

import Body.SwipeDetails;
import Config.POSTConfig;
import Config.SwipeConfig;
import com.google.gson.Gson;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityProducer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;

public class RequestThread implements Runnable {

  private AtomicInteger successCounter;
  private AtomicInteger failCounter;
  private ConcurrentLinkedQueue<RequestOutput> writeQueue;
  private CountDownLatch latch;
  private CloseableHttpAsyncClient httpClient;
  private long start, end;

  /**
   * Constructor for Client1 usage
   * @param successCounter
   * @param failCounter
   * @param latch
   * @param httpClient
   */
  public RequestThread(AtomicInteger successCounter, AtomicInteger failCounter,
      CountDownLatch latch,
      CloseableHttpAsyncClient httpClient) {
    this.successCounter = successCounter;
    this.failCounter = failCounter;
    this.latch = latch;
    this.httpClient = httpClient;
  }

  /**
   * Constructor for Client2 usage - with a WriteQueue that writes to a csv file
   * @param successCounter
   * @param failCounter
   * @param writeQueue
   * @param latch
   * @param httpClient
   */
  public RequestThread(
      AtomicInteger successCounter,
      AtomicInteger failCounter,
      ConcurrentLinkedQueue<RequestOutput> writeQueue,
      CountDownLatch latch,
      CloseableHttpAsyncClient httpClient) {
    this.successCounter = successCounter;
    this.failCounter = failCounter;
    this.writeQueue = writeQueue;
    this.latch = latch;
    this.httpClient = httpClient;
  }

  /**
   * Starts a thread
   */
  @Override
  public void run() {
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    String leftorright = rand.nextInt(2) == 0
        ? "left"
        : "right";

    //Set up request
    AsyncRequestProducer postReq = AsyncRequestBuilder.post()
        .setUri(String.format("%s/swipe/%s",
            POSTConfig.URL,
            leftorright))
        .addHeader("Accept", "application/json")
        .addHeader("Content-type", "application/json")
        .setEntity(new StringAsyncEntityProducer(getRandomSwipeBody()))
        .build();

    //Send post request
    post(postReq);
  }

  /**
   * Sends post request over to server and consumer response
   * @param postReq
   */
  private void post(AsyncRequestProducer postReq ){
    for (int i = 0; i <= SwipeConfig.NUM_RETRY; i++) {
      start = System.currentTimeMillis();
      //Starts sending request
      AsyncResponseConsumer<SimpleHttpResponse> consumer = SimpleResponseConsumer.create();
      Future<SimpleHttpResponse> future = httpClient.execute(
          postReq,
          consumer,
          null);

      //Consume response
      try {
        SimpleHttpResponse response = future.get();

        //Get a timestamp when the response is returned
        end = System.currentTimeMillis();

        //If the response is already successful, we don't need to try again
        if (response.getCode() == 201) {
          if (writeQueue != null) {
            writeQueue.offer(new RequestOutput(
                start,
                "POST",
                end - start,
                response.getCode()
            ));
          }

          //Mark this request as success
          successCounter.getAndIncrement();

          //Countdown latch to signal completion of execution
          latch.countDown();
          return;
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    //After 5 retries, if the request still fail, increment the fail counter
    failCounter.getAndIncrement();
  }

  /**
   * @return a Random request body for swipe POST request
   */
  private String getRandomSwipeBody() {
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    int swiper = rand.nextInt(SwipeConfig.SWIPER_RANGE) + 1;
    int swipee = rand.nextInt(SwipeConfig.SWIPEE_RANGE) + 1;

    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'

    //Generate a random comment
    String comment = rand.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(SwipeConfig.COMMENT_LENGTH)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();

    SwipeDetails swipe = new SwipeDetails();
    swipe.setSwiper(String.valueOf(swiper));
    swipe.setSwipee(String.valueOf(swipee));
    swipe.setComment(comment);
    return new Gson().toJson(swipe);
  }
}