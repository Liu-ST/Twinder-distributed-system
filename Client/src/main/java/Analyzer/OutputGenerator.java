package Analyzer;

import Config.POSTConfig;

public class OutputGenerator {

  private int successCounter;
  private int failCounter;
  private int runTime;
  private Analyzer analyzer;

  /**
   * Create an output object used to generate output
   *
   * @param analyzer       an Analyzer object represents the analyses being done
   * @param successCounter an Integer represents number of successful requests
   * @param failCounter    an Integer represents number of fail requests
   * @param runTime        an Integer represents number seconds taken to run the client requests
   */
  public OutputGenerator(Analyzer analyzer, int successCounter, int failCounter, int runTime) {
    this.analyzer = analyzer;
    this.successCounter = successCounter;
    this.failCounter = failCounter;
    this.runTime = runTime;
  }

  /**
   * @return a String represents the output to be displayed
   */
  public String getOuput() {
    double throughput = 1.0 * POSTConfig.REQUEST_NUM / runTime;

    StringBuilder sb = new StringBuilder()
        .append(successCounter).append(" requests succeeded").append(System.lineSeparator())
        .append(failCounter).append(" requests failed").append(System.lineSeparator())
        .append("Took ").append(runTime).append(" seconds.").append(System.lineSeparator())
        .append("Min latency: ").append(analyzer.getMin()).append(" milliseconds")
        .append(System.lineSeparator())
        .append("Max latency: ").append(analyzer.getMax()).append(" milliseconds")
        .append(System.lineSeparator())
        .append("Mean latency: ").append(analyzer.getMean()).append(" milliseconds")
        .append(System.lineSeparator())
        .append("Median latency: ").append(analyzer.getMedian()).append(" milliseconds")
        .append(System.lineSeparator())
        .append("99th percentile latency: ").append(analyzer.getPercentile99())
        .append(" milliseconds").append(System.lineSeparator())
        .append("Total throughput: ").append(throughput).append(" requests/second")
        .append(System.lineSeparator());

    return sb.toString();
  }
}
