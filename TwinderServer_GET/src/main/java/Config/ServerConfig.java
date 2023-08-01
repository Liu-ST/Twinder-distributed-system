package Config;

public class ServerConfig {
  /**
   * Swiper's id range
   */
  public static final int SWIPER_RANGE = 50000;

  /**
   * DynamoDB table name
   */
  public static final String DYNAMO_TABLE_NAME = "SwipeData";

  /**
   * Primary Key field name
   */
  public static final String DYNAMO_PK = "SwiperId";

  /**
   * a String represents the count left swipe column
   */
  public static final String LEFT_SWIPE_COL_NAME = "LeftSwipe";

  /**
   * a String represents the count right swipe column
   */
  public static final String RIGHT_SWIPE_COL_NAME = "RightSwipe";

  /**
   * a String represents the potential matches
   */
  public static final String MATCHES_COL_NAME = "Matches";

  /**
   * AWS Credentials
   *
   * [default]
   * aws_access_key_id=ASIATYHSEPEYSJY3324K
   * aws_secret_access_key=gcJy/sfMCBV4ZdGf7isOkjKqjxFSQGFu3SsEfyr7
   * aws_session_token=FwoGZXIvYXdzEMb//////////wEaDG7xnPOAJFRHUKCx2SLJAdZBkinZBNE40DjPOkWj6uuEfmHFMwfGN7JZUuPyxnyEkE/klLGHmkFKOan/eXGyiU5sbYrT3TMTKjPcwtc9RKV2otTKvLNdAtzPI57hm5JjIg0+2ERJPtzaSv2V0hoey5SqYMVd70PBCdfnefbkDkNCaa2CYCgHbyjPR8K798i7o9G7dPmyoDW61IBoy3yN1nDC3sCBnsgdKCB9iVNiQhkuvAQMqbvt1xLTYO2mPTbnfd4CeyY76MM4sEXjxZHb1wE/MeABG4f/fiiOh4amBjIt/0aogxxDKDelzdsN5x3NwMqvDpFgK8q373nWzKFcDCsojyIEuTMDW+aDPMuE
   */
  public static final String AWS_ACCESS_KEY = "ASIATYHSEPEY7WANMBPK";
  public static final String AWS_SECRET_ACCESS_KEY = "OVMiFrAb2jN/DVUfVg81Pn1PkRaVkpJPpagucv5a";
  public static final String AWS_SESSION_TOKEN = "FwoGZXIvYXdzEMz//////////wEaDHgynDcyCnhsoyB5FyLJAUiExpu6Sv4/QLWTW0YdGpx0akNtxtgySJRVBqGlUgCRysHPckpeLtdMyyOCQEg3PSQ04BrVwbgnXll46SA7ZkbAx4HCXkTe0dv+FWE0sEOjyR5B2oh9lNM/L4gKmMhzHDYA5tBzt2qANtS10TLV+z5buWlH4HE7paXbKYWzSJzjeusU73APF8q3QjOdYRna+75Y8NUBwSOCK2Z0cHsGVcsZM6Fjj1hp4wwACtoOt1R/obIJ/5+c+hQuluskNJhijLa0OezxQkvzcijirIemBjItCRTrPGtrck+91I7DJhdeRyVQIaNveWZ3p4qT9hqPkNO8FsLWlMnddtQ54aGg";
  private ServerConfig(){}
}
