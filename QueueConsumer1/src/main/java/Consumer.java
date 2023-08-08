import SwipeQueue.SwipeConsumer;

public class Consumer {
  public static void main(String[] args){
    //Set aws credentials
    System.setProperty("aws.accessKeyId", "ASIATYHSEPEYRBF42JPY");
    System.setProperty("aws.secretAccessKey", "XKfT9e4XhEI9K+NRRTx+f295EBFAJ0BAEylUcfKY");
    System.setProperty("aws.sessionToken", "FwoGZXIvYXdzEOz//////////wEaDCHS9Hg5E4QGktWrOSLJAQq2Iv8ROnrsrMPGPmODIkOsPUuufrW6eB/0rUwZLTBq1nrkbR9XGAtyNDo+rB9KN1tCGRcPnXcM77Z9+4s4kqzv6LoDZg3VvvTvhCTLbuINVQIfWwU01O3xs0s2Fne00QRnceaUAJ8KlPIs2dtfFtvvj04W4YUoXppLm6lTr+/OyWqIi/irH+t/e/LIn0y63vmaUxmNVotBU6p1oM7p6iYBuZAX8MuYSFcJu3REU3c+2jy0Rx9UMLOfB9IYzUJebmvAq1MfxqBPByiP3MamBjItum7ztRYXQZHE+Tpj5C1+Gg3HHqBkzW0kh9PmMTD+wimwkuO21MCuw/BfkInT");

    SwipeConsumer consumer = new SwipeConsumer();
    consumer.start();
  }
}
