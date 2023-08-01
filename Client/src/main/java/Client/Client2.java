package Client;

import Analyzer.Analyzer;
import Analyzer.Writer;
import Config.POSTConfig;
import Request.GETRequest;
import Request.RequestOutput;
import Request.RequestThread;
import Analyzer.OutputGenerator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;

public class Client2 {

  public static void main(String[] args) {
    //Set up variables
    ExecutorService threadPool = Executors.newFixedThreadPool(POSTConfig.NUM_THREADS);

    AtomicInteger successCounter = new AtomicInteger(0);
    AtomicInteger failCounter = new AtomicInteger(0);
    ConcurrentLinkedQueue<RequestOutput> writeQueue = new ConcurrentLinkedQueue<>();

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

    Thread writeThread = new Thread(
        new Writer(writeQueue, Writer.CONCURRENT_FILE_NAME_2, POSTConfig.NUM_THREADS));
    writeThread.start();

    //Start sending requests
    for (int i = 0; i < POSTConfig.REQUEST_NUM; i++) {
      threadPool.execute(new RequestThread(
          successCounter,
          failCounter,
          writeQueue,
          latch,
          httpClient
      ));
    }

    //Start the get thread
    GETRequest getRequest = new GETRequest(httpClient);
    Thread t = new Thread(getRequest);
    t.start();

    //Wait for all threads to finish execution, then terminate the GET thread too
    try {
      latch.await();
      System.out.println("POST done!");
      writeThread.join();
      getRequest.stop();
      t.join(100);
      System.out.println("GET done!");
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

    //Analyze and generate output
    Analyzer analyzer = new Analyzer(Writer.CONCURRENT_FILE_NAME_2);
    try {
      analyzer.analyze();
    } catch (Exception e) {
      e.printStackTrace();
    }

    OutputGenerator output = new OutputGenerator(
        analyzer,
        successCounter.get(),
        failCounter.get(),
        runTime);
    System.out.println(output.getOuput());

    //Print output for GET requests
    String getOutput = new StringBuilder()
        .append("GET Request result:").append(System.lineSeparator())
        .append("Min response time: ")
        .append(getRequest.getMinLatency()).append(" milliseconds").append(System.lineSeparator())
        .append("Max response time: ")
        .append(getRequest.getMaxLatency()).append(" milliseconds").append(System.lineSeparator())
        .append("Average response time: ")
        .append(getRequest.getAverageLatency()).append(" milliseconds").append(System.lineSeparator())
        .toString();
    System.out.println(getOutput);
  }
}
