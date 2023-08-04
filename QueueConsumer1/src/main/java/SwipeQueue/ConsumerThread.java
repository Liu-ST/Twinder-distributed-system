package SwipeQueue;

import Config.ConsumerConfig;
import Data.Swipe;
import Data.SwipeDataMap;
import Data.UserSwipeData;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

public class ConsumerThread implements Runnable {

  private Channel chan;

  /**
   * Constructs a new consumer thread to handle messages
   *
   * @param conn a Connection object represents the connection to the queue
   */
  public ConsumerThread(Connection conn) {
    try {
      this.chan = conn.createChannel();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Callback function to receive message
   */
  private DeliverCallback callback = (consumerTag, delivery) -> {
    try {
      String message = new String(delivery.getBody(), "UTF-8");

      //Deserialize message to object
      Swipe swipeInfo = new Gson().fromJson(message, Swipe.class);

      //Put swipe into dataMap
      SwipeDataMap.getInstance().putData(swipeInfo);

      //Acknowledge the message after performing computation
      chan.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    } catch (Exception e) {
      //In case an exception was thrown, reject the message and let RabbitMQ knows that it's failed to be consumed
      chan.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
    }
  };

  @Override
  public void run() {
    try {
      //Bind the queue to exchange
      chan.queueDeclare(ConsumerConfig.QUEUE_NAME,
          ConsumerConfig.QUEUE_DURABILITY,
          false,
          false,
          null);

      //Start listening for messages
      chan.basicConsume(ConsumerConfig.QUEUE_NAME,
          ConsumerConfig.QUEUE_AUTO_ACK, callback,
          (consumerTag -> {
          }));

      System.out.println(String.format("Threads num %d running on channel %d",
          Thread.currentThread().getId(),
          chan.getChannelNumber()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
