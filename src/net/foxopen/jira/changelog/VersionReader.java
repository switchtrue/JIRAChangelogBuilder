package net.foxopen.jira.changelog;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class VersionReader
{
  /**
   * Scans a text file line by line looking for the first non-empty, non-comment line.
   * When such a line is found it is trimmed and returned as a String.
   * A comment is any line starting with a hash.
   * An empty line is any line that contains only whitespace characters.
   * 
   * @param releaseFileName String containing the path to the release file that contains the current release label.
   * @return                The current release label found in the release file.
   */
  public static String getRelease(String releaseFileName)
  {
    try {
      FileInputStream fis = new FileInputStream(releaseFileName);
      DataInputStream dis = new DataInputStream(fis);
      BufferedReader br = new BufferedReader(new InputStreamReader(dis));
      
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("#") || line.trim().equals("")) {
          continue;
        } else {
          br.close();
          dis.close();
          return line.trim();
        }
      }
      
      br.close();
      dis.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return null;
  }
}