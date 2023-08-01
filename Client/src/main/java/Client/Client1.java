package Client;

import Config.POSTConfig;
import Request.RequestThread;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;

public class Client1 {

  public static void main(String[] args) {
    //Set up variables
    ExecutorService threadPool = Executors.newFixedThreadPool(POSTConfig.NUM_THREADS);

    AtomicInteger successCounter = new AtomicInteger(0);
    AtomicInteger failCounter = new AtomicInteger(0);

    CountDownLatch latch = new CountDownLatch(POSTConfig.REQUEST_NUM);

    PoolingAsyncClientConnectionManager cm = new PoolingAsyncClientConnectionManager();
    cm.setMaxTotal(POSTConfig.MAX_TOTAL_CONN);
    cm.setDefaultMaxPerRoute(POSTConfig.MAX_PER_ROUTE);

    RequestConfig requestConfig = RequestConfig.custom()
        //.setResponseTimeout(3000, TimeUnit.MILLISECONDS)
        .build();

    CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom()
        .setDefaultRequestConfig(requestConfig)
        .setConnectionManager(cm)
        .build();
    httpClient.start();

    //Start the counter
    long start = System.currentTimeMillis();

    //Start sending requests
    for (int i = 0; i < POSTConfig.REQUEST_NUM; i++) {
      threadPool.execute(new RequestThread(
          successCounter,
          failCounter,
          latch,
          httpClient
      ));
    }

    //Wait for all threads to finish execution
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    long end = System.currentTimeMillis();

    //Shut down connection pool & thread pool
    try {
      cm.close();
      threadPool.shutdown();
      threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    int runTime = (int) TimeUnit.MILLISECONDS.toSeconds(end - start);
    double throughput = 1.0 * POSTConfig.REQUEST_NUM / runTime;

    StringBuilder sb = new StringBuilder()
        .append(successCounter).append(" requests succeeded").append(System.lineSeparator())
        .append(failCounter).append(" requests failed").append(System.lineSeparator())
        .append("Took ").append(runTime).append(" seconds.").append(System.lineSeparator())
        .append("Total throughput: ").append(throughput).append(" requests/second")
        .append(System.lineSeparator());

    System.out.println(sb.toString());
  }
}
