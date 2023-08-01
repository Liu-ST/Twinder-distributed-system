import SwipeQueue.SwipeConsumer;

public class Consumer {
  public static void main(String[] args){
    //Set aws credentials
    System.setProperty("aws.accessKeyId", "ASIATYHSEPEY7WANMBPK");
    System.setProperty("aws.secretAccessKey", "OVMiFrAb2jN/DVUfVg81Pn1PkRaVkpJPpagucv5a");
    System.setProperty("aws.sessionToken", "FwoGZXIvYXdzEMz//////////wEaDHgynDcyCnhsoyB5FyLJAUiExpu6Sv4/QLWTW0YdGpx0akNtxtgySJRVBqGlUgCRysHPckpeLtdMyyOCQEg3PSQ04BrVwbgnXll46SA7ZkbAx4HCXkTe0dv+FWE0sEOjyR5B2oh9lNM/L4gKmMhzHDYA5tBzt2qANtS10TLV+z5buWlH4HE7paXbKYWzSJzjeusU73APF8q3QjOdYRna+75Y8NUBwSOCK2Z0cHsGVcsZM6Fjj1hp4wwACtoOt1R/obIJ/5+c+hQuluskNJhijLa0OezxQkvzcijirIemBjItCRTrPGtrck+91I7DJhdeRyVQIaNveWZ3p4qT9hqPkNO8FsLWlMnddtQ54aGg");

    SwipeConsumer consumer = new SwipeConsumer();
    consumer.start();
  }
}
