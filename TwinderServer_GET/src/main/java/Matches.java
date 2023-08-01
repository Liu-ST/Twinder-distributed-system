import Config.ServerConfig;
import DynamoDB.DAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to serve User's Matches - Up to 100 people
 *
 * @author Cody Cao
 */
@WebServlet("/matches/*")
public class Matches extends HttpServlet {

  @Override
  public void init() throws ServletException {
    super.init();

    //Set aws credentials
    System.setProperty("aws.accessKeyId", ServerConfig.AWS_ACCESS_KEY);
    System.setProperty("aws.secretAccessKey", ServerConfig.AWS_SECRET_ACCESS_KEY);
    System.setProperty("aws.sessionToken", ServerConfig.AWS_SESSION_TOKEN);
  }


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
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

    //Retrieve from DynamoDB
    int userId = Integer.parseInt(urlParts[1]);
    DAO dao = new DAO(userId);

    long funcStart = System.currentTimeMillis();
    List<Integer> matches = dao.getMatches();
    long funcLatency = System.currentTimeMillis() - funcStart;
    System.out.println("Function matches took " + funcLatency + " ms");

    String responseMsg;
    if(matches == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      responseMsg = "User not found";
    }
    else {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json");

      //Build the content of the response
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < matches.size(); i++) {
        sb.append(matches.get(i));
        if(i < matches.size() - 1){
          sb.append(", ");
        }
      }
      String matchList = sb.toString();

      responseMsg = new StringBuilder("{")
          .append("\"matchList\": [")
          .append(matchList)
          .append("]")
          .append("}").toString();
    }
    response.getWriter().write(responseMsg);

  }

  /**
   * Verify that the parameter is correct
   *
   * @param urlParts
   * @return
   */
  private boolean isUrlValid(String[] urlParts) {
    try {
      int userId = Integer.parseInt(urlParts[1]);

      //Check if the parameter is within range
      if (userId <= 0 || userId > ServerConfig.SWIPER_RANGE) {
        return false;
      }
    }
    //In case the provided parameter is not an Integer, return false
    catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
}
