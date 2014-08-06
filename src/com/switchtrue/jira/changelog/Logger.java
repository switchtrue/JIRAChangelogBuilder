package com.switchtrue.jira.changelog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple logger that allows logging to be enabled or disabled. When enabled
 * errors will be logged to standard out rather than error out.
 *
 * @author mleonard87
 *
 */
public class Logger {

  /**
   * Indicates if debug logging is enabled.
   */
  public static boolean loggingEnabled = false;

  /**
   * Logs <param>message</param> to standard out if logging is enabled.
   *
   * @param message the String to log to standard out.
   */
  public static void log(String message) {
    if (loggingEnabled) {
      Date d = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
      System.out.println("DEBUG " + sdf.format(d) + ": " + message);
    }
  }

  /**
   * Logs <param>message</param> to error out if logging is disabled and exits
   * or logs to standard out if logging is enabled.
   *
   * @param message the String to log.
   */
  public static void err(String message) {
    if (loggingEnabled) {
      Date d = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
      System.out.println("ERROR " + sdf.format(d) + ": " + message);
    } else {
      System.err.println(message);
      System.exit(1);
    }
  }

  /**
   * Enable logging.
   */
  public static void enable() {
    loggingEnabled = true;
  }

  /**
   * Disable logging.
   */
  public static void disable() {
    loggingEnabled = false;
  }
}
