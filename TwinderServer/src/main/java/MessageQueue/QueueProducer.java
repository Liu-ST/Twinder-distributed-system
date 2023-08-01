package MessageQueue;

import Config.ServerConfig;
import RequestBody.SwipeRequest;

import java.util.concurrent.BlockingQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;


/**
 * Queue Producer class - used to send messages to message queue
 *
 * @author Cody Cao
 */
public class QueueProducer {

    /**
     * @return a Singleton instance to get connection
     */
    public static QueueProducer getInstance() {
        if (instance == null) {
            instance = new QueueProducer();
        }
        return instance;
    }
    private static QueueProducer instance;
    private BlockingQueue<Channel> connectionPool;
    private Connection conn;

    /**
     * Create a new QueueProducer object. This will also create a new connection pool that stores
     * connections to the message queue host
     */
    private QueueProducer() {
        //Generate channels to the queue host that is stored in a pool for reuse
        this.connectionPool = new LinkedBlockingDeque<>(ServerConfig.NUM_CONNECTIONS);

        //Config connections
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ServerConfig.QUEUE_HOST);
        factory.setPort(ServerConfig.QUEUE_PORT);
        factory.setVirtualHost(ServerConfig.VHOST_NAME);
        factory.setUsername(ServerConfig.USER_NAME);
        factory.setPassword(ServerConfig.PASSWORD);

        try {
            conn = factory.newConnection();
            for (int i = 0; i < ServerConfig.NUM_CONNECTIONS; i++) {
                Channel chan = conn.createChannel();
                chan.queueDeclare(ServerConfig.QUEUE_1_NAME, ServerConfig.MESSAGE_PERSISTENT, false, false, null);
                connectionPool.offer(chan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return a connection channel to the server. Blocks if there's no available connection in the
     * pool
     */
    public Channel getConnection() {
        return connectionPool.poll();
    }

    /**
     * Put the connection back in the pool after it's done
     *
     * @param channel a Channel object represents the connection to the RabbitMQ host
     */
    public void putConnection(Channel channel) {
        connectionPool.offer(channel);
    }

    /**
     * Tear down connection
     */
    public void destroy() {
        try {
            this.connectionPool.clear();
            this.conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
