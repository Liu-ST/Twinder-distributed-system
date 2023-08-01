package Analyzer;

import Config.POSTConfig;
import Request.RequestOutput;
import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Writer implements Runnable {

  /**
   * CSV file name for concurrent run
   */
  public static final String CONCURRENT_FILE_NAME = "output_concurrent";

  /**
   * CSV file name for concurrent run - part 2
   */
  public static final String CONCURRENT_FILE_NAME_2 = "output_concurrent_2.csv";

  /**
   * CSV file name for single threaded run
   */
  public static final String SINGLE_THREAD_FILE_NAME = "output_single_thread";

  /**
   * Header of output csv file
   */
  private static final String[] HEADERS = new String[]{"start", "type", "latency", "code"};
  private ConcurrentLinkedQueue<RequestOutput> writeQueue;
  private CSVWriter output;
  private int numRows;
  private int numThreads;

  /**
   * Constructs a new Writer object
   *
   * @param writeQueue a ConcurrentLinkedQueue object to receive write requests
   * @param fileName   a String represents the output file
   * @param numThreads an Integer represents the number of producer threads
   */
  public Writer(ConcurrentLinkedQueue<RequestOutput> writeQueue, String fileName, int numThreads) {
    this.writeQueue = writeQueue;
    try {
      numRows = 0;
      this.numThreads = numThreads;
      java.io.Writer writer = new BufferedWriter(
          new FileWriter(fileName)
      );

      output = new CSVWriter(writer,
          CSVWriter.DEFAULT_SEPARATOR,
          CSVWriter.NO_QUOTE_CHARACTER,
          CSVWriter.DEFAULT_ESCAPE_CHARACTER,
          CSVWriter.DEFAULT_LINE_END);
      output.writeNext(HEADERS);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    while (numRows < POSTConfig.REQUEST_NUM) {
      RequestOutput row = writeQueue.poll();

      //In case there's no record yet, sleep the thread for a while
      if (row == null) {
        try {
          Thread.sleep(500);
          continue;
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      numRows++;
      output.writeNext(row.toCSVRow());
    }
    try {
      output.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
