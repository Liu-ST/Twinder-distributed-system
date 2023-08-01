package Request;

import Config.GETConfig;
import Config.SwipeConfig;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;

public class GETRequest implements Runnable {

  private boolean processing;
  private long maxLatency;
  private long minLatency;
  private int numReq;
  private long totalRespTime;
  private CloseableHttpAsyncClient httpClient;

  /**
   * Construct a new GET thread
   */
  public GETRequest(CloseableHttpAsyncClient httpClient) {
    this.processing = true;
    this.maxLatency = 0;
    this.minLatency = Integer.MAX_VALUE;
    this.numReq = 0;
    this.totalRespTime = 0;
    this.httpClient = httpClient;
  }

  /**
   * Stop the thread's processing
   */
  public void stop() {
    this.processing = false;
  }

  /**
   * @return maximum latency occurred during execution
   */
  public long getMaxLatency() {
    return maxLatency;
  }

  /**
   * @return min latency occurred during execution
   */
  public long getMinLatency() {
    return minLatency;
  }

  /**
   * @return average latency occurred during execution
   */
  public double getAverageLatency() {
    return 1.0 * totalRespTime / numReq;
  }

  @Override
  public void run() {
    Random rand = new Random();
    while (processing) {
      for (int i = 0; i < GETConfig.NUM_GET_REQ && processing; i++) {

        //Send Request
        int userId = rand.nextInt(SwipeConfig.SWIPER_RANGE) + 1;
        String type = rand.nextInt(2) == 0
            ? GETConfig.MATCHES_PATH
            : GETConfig.STATS_PATH;

        String path = type + String.valueOf(userId);
        sendRequest(path);
      }
      //Sleep the thread for 1 second
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * Send a GET request to the server
   */
  private void sendRequest(String path) {
    // HttpGet getReq = new HttpGet(path);

    AsyncRequestProducer getReq = AsyncRequestBuilder.get()
        .setUri(path)
        .build();

    long start = System.currentTimeMillis();
    AsyncResponseConsumer<SimpleHttpResponse> consumer = SimpleResponseConsumer.create();
    Future<SimpleHttpResponse> future = httpClient.execute(
        getReq,
        consumer,
        null
    );
    try {
      //HttpResponse response = httpClient.execute(getReq);
      SimpleHttpResponse response = future.get();
      long latency = System.currentTimeMillis() - start;

      //If the response is successful, update the stats
      if (response.getCode() == HttpStatus.SC_OK || response.getCode() == HttpStatus.SC_NOT_FOUND) {
        //Update the stats
        minLatency = Math.min(minLatency, latency);
        maxLatency = Math.max(maxLatency, latency);
        totalRespTime += latency;
        numReq++;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
