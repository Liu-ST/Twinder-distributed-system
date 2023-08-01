package SwipeQueue;

import Config.ConsumerConfig;
import Data.UserSwipeData;
import DynamoDB.SwipeDataWriter;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * SwipeConsumer class - used to consume swipe data from queue
 *
 * @author Cody Cao
 */
public class SwipeConsumer {

  private Connection conn;
  private ExecutorService threadPool;
  private SwipeDataWriter writer;
  private BlockingQueue<Integer> data;
  ConcurrentMap<Integer, UserSwipeData> dataMap;

  /**
   * Construct a new SwipeConsumer
   */
  public SwipeConsumer() {

    //Setup connection pool
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(ConsumerConfig.QUEUE_HOST);
    factory.setPort(ConsumerConfig.QUEUE_PORT);
    factory.setVirtualHost(ConsumerConfig.VHOST_NAME);
    factory.setUsername(ConsumerConfig.USER_NAME);
    factory.setPassword(ConsumerConfig.PASSWORD);

    this.dataMap = new ConcurrentHashMap<>();
    try {
      this.conn = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Connects to the queue and starts listening for messages
   */
  public void start() {
    data = new LinkedBlockingQueue<>();
    threadPool = Executors.newFixedThreadPool(ConsumerConfig.NUM_CONNECTIONS);
    for (int i = 0; i < ConsumerConfig.NUM_CONNECTIONS; i++) {
      threadPool.execute(new ConsumerThread(conn, data, dataMap));
    }
    writer = new SwipeDataWriter(data, dataMap);
    writer.run();
  }

  /**
   * Stops listening for message and disconnects from the queue
   */
  public void stop() {
    threadPool.shutdown();
    try {
      threadPool.awaitTermination(30, TimeUnit.SECONDS);
      this.conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
