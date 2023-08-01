import Config.ServerConfig;
import MessageQueue.QueueProducer;
import RequestBody.SwipeRequest;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

import com.rabbitmq.client.MessageProperties;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Swipe class to handle Swipe requests.
 *
 * @author Cody Cao
 */
public class Swipe extends HttpServlet {
    private QueueProducer producer;

    /**
     * Initialize the servlet
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.producer = QueueProducer.getInstance();
    }

    /**
     * Tear down resources
     */
    @Override
    public void destroy() {
        super.destroy();
        producer.destroy();
    }

    //Swipe types supported by the server
    private static final List<String> SWIPE_TYPE = List.of("left", "right");

    /**
     * Handle POST request for Swipe
     *
     * @param request  an HttpServletRequest object represents the client's request
     * @param response an HttpServletResponse object represents the server's response
     * @throws ServletException when there's an exception with ServLet
     * @throws IOException      when there's an IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        String path = request.getPathInfo();

        //Check if there's a URL
        if (path == null || path.isEmpty() || path.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Parameters missing");
            return;
        }

        //Confirm that the URL is valid.
        //If not valid, generate a Bad Request response
        String[] urlParts = path.split("/");
        if (!isUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid parameter");
            return;
        }

        SwipeRequest swipeInfo;
        try {
            Gson gSon = new Gson();
            //Parse request body from JSON to object
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = request.getReader().readLine()) != null) {
                sb.append(s);
            }
            swipeInfo = gSon.fromJson(sb.toString(), SwipeRequest.class);

        } catch (Exception e) {
            System.err.println(e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing body");
            return;
        }

        //If there was no one - return a bad request response
        if (swipeInfo == null || !isBodyValid(swipeInfo)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Body is missing/invalid");
            return;
        }

        //Update swipe type and publish the message to RabbitMQ
        swipeInfo.setSwipeType(urlParts[1]);
        publishMessage(swipeInfo);

        //Send response
        response.setStatus(HttpServletResponse.SC_CREATED);
        //Return a dummy response
        response.getWriter().write("Written");
    }

    /**
     * @param urlPath an Array with parameters of the path
     * @return whether the argument provided is valid for the request
     */
    private boolean isUrlValid(String[] urlPath) {
        if (SWIPE_TYPE.contains(urlPath[1])) {
            return true;
        }
        return false;
    }

    /**
     * @param swipeInfo a SwipeRequest object contains information about the request's body
     * @return true if the swiper id, swipee id, and comment length is within the range specified,
     * false otherwise.
     */
    private boolean isBodyValid(SwipeRequest swipeInfo) {
        int swiperId = swipeInfo.getSwiper();
        int swipeeId = swipeInfo.getSwipee();
        int messageLength = swipeInfo.getComment().length();

        if (swiperId > 0 && swiperId <= ServerConfig.SWIPER_RANGE
                && swipeeId > 0 && swipeeId <= ServerConfig.SWIPEE_RANGE
                && messageLength <= ServerConfig.COMMENT_LENGTH) {
            return true;
        }
        return false;
    }

    /**
     * Publish the swipe information to the 2 queues
     * @param swipeInfo
     * @throws IOException
     */
    private void publishMessage(SwipeRequest swipeInfo) throws IOException {
        byte[] message = new Gson()
                .toJson(swipeInfo)
                .getBytes("UTF-8");
        //Retrieve channel from the connection pool
        Channel channel = producer.getConnection();

        //Publish message to queue 1
        channel.basicPublish("", ServerConfig.QUEUE_1_NAME, MessageProperties.MINIMAL_PERSISTENT_BASIC, message);

        producer.putConnection(channel);
    }

}
