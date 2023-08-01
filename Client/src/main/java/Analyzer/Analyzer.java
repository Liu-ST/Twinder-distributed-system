package Analyzer;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Analyzer {

  /**
   * Percentile to add to calculation
   */
  private static final int percentile = 99;
  private double mean;
  private float median;
  private long total;
  private int percentile99;
  private int min;
  private int max;
  private double throughput;
  List<Integer> latencyList;
  private String fileName;

  public Analyzer(String fileName) {
    this.fileName = fileName;
    this.latencyList = new ArrayList<>();
    this.min = Integer.MAX_VALUE;
    this.max = 0;
    this.throughput = 0;
  }

  /**
   * Analyze the created csv file and calculate metrics
   * @throws Exception when there is an exception reading file
   */
  public void analyze() throws Exception {
    CSVReader reader = new CSVReader(
        new BufferedReader(
            new FileReader(fileName)
        )
    );

    Iterator<String[]> iter = reader.iterator();
    //Skip headers
    iter.next();

    String[] row;
    while ((row = iter.next()) != null) {
      int latency = Integer.parseInt(row[2]);
      total += latency;
      min = Math.min(min, latency);
      max = Math.max(max, latency);
      latencyList.add(latency);
    }

    int numRow = latencyList.size();
    mean = 1.0 * total / numRow;

    Collections.sort(latencyList);

    //Calculate median
    int mid = numRow / 2;
    median = (numRow % 2 != 0)
        ? latencyList.get(mid)
        : (float) ((latencyList.get(mid) + latencyList.get(mid + 1)) / 2.0);

    //Calculate 99th percentile
    int index = (int) Math.ceil((1.0 * percentile / 100) * latencyList.size()) - 1;
    if (index < 0) {
      index = 0;
    } else if (index >= latencyList.size()) {
      index = latencyList.size() - 1;
    }
    percentile99 = latencyList.get(index);
  }

  public double getMean() {
    return mean;
  }

  public float getMedian() {
    return median;
  }

  public int getPercentile99() {
    return percentile99;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }

  public long getTotal() {
    return total;
  }

  public double getThroughput() {
    return throughput;
  }
}
