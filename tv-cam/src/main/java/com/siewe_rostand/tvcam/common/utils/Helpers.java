package com.siewe_rostand.tvcam.common.utils;

import com.siewe_rostand.tvcam.common.exceptions.GlobalExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rostand
 * @project tvcam
 */
public class Helpers {

  /**
   * log exception with stack traces of the exception
   *
   * @param ex exception to be thrown
   */
  public static void logException(Exception ex) {
    Logger.getLogger(GlobalExceptionHandler.class.getName()).log(Level.SEVERE, null, ex);
  }

  /**
   * Generates a random string of the length passed in parameter
   *
   * @param length Length of the string to generate
   * @return String
   */
  public static String generateRandomString(int length) {
    String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < length; i++) {
      int position = (int) Math.floor(Math.random() * possibleChars.length());
      result.append(possibleChars.charAt(position));
    }

    return result.toString();
  }

  /**
   * Get the extension of the file name provided
   *
   * @param fileName Name of the file we want to get the extension
   * @return a string representing the extension
   */
  public static String getFileExtension(String fileName) {
    if (fileName == null) {
      return null;
    }
    String[] fileNameParts = fileName.split("\\.");

    return fileNameParts[fileNameParts.length - 1];
  }

  /**
   * Transform to uppercase the first character of a string
   *
   * @param str
   * @return the string capitalized
   */
  public static String capitalize(String str) {
    char[] chars = str.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);

    return String.valueOf(chars);
  }

  public static Map<String, List<String>> updateErrorHashMap(
      Map<String, List<String>> errors, String field, String message) {
    List<String> strings;
    if (errors.containsKey(field)) {
      strings = errors.get(field);

    } else {
      strings = new ArrayList<>();
    }
    strings.add(message);
    errors.put(field, strings);

    return errors;
  }
}
