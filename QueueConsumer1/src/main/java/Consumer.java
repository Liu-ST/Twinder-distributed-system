import SwipeQueue.SwipeConsumer;

public class Consumer {
  public static void main(String[] args){
    //Set aws credentials
    System.setProperty("aws.accessKeyId", "ASIATYHSEPEYWEDIYTGP");
    System.setProperty("aws.secretAccessKey", "uHid05oS3hZZRnMXMGohCu0ctX3N+38GlUtk0kqt");
    System.setProperty("aws.sessionToken", "FwoGZXIvYXdzEJv//////////wEaDC9TVX6ZlLZ8vgnCHyLJAZkzEd/kHIu+l6dphqj7D/akaIjUYDfaEMkRI7xaRf+FIKhASYntCxDi1JcBvxaGpPRKn/igMdC5Wuih8pNkkP64zgYuq6nsmmcUfo/kSDCJcG0N9TUSygL1GSVXjUh+Zn9D43RuU/BUqt8XXBmskEVtgIq42BtmT+wqW5OTNY1eXpYfbL3WCqebwj+FHkDpgOgEnMk/HF2pNWALEX2b+2a03sMid5ZqJaYQUKHnRHMCSiAGOWNiaps4pwrTFB63QcdT+L//iPIUiyjA5rSmBjIt3zJXniOapRtJg53yvFDUAI2jtYJDU5gKo2OMvszkA82QrLS764s8lY5Jherf");

    SwipeConsumer consumer = new SwipeConsumer();
    consumer.start();
  }
}
