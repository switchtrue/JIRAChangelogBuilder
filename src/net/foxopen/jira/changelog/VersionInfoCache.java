package net.foxopen.jira.changelog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author leonmi
 *
 * A disk-based cache for VersionInfo instances. VersionInfo objects are serialized to disk
 * with a file name of projectkey_versionname.ser. These can be deserialized and used upon request. 
 * 
 */
public class VersionInfoCache
{  
  private final static String SERIALIZED_OBJECT_EXT = ".ser";
  
  private final String projectKey_;
  private final String cachePath_;
  
  /**
   * Constructor to create a cache for this JIRA project.
   * 
   * @param projectKey key used for this project in JIRA.
   * @param cachePath path on disk to the cache.
   */
  public VersionInfoCache(String projectKey, String cachePath)
  {
    projectKey_ = projectKey;
    cachePath_ = cachePath + "\\";
  }
  
  /**
   * Cache a given VersionInfo object by serialising it to disk with a file name of projectkey_versionname.ser.
   * 
   * @param versionInfo the VersionInfo isntance to be cached.
   */
  public void cache(VersionInfo versionInfo)
  {
    try {
      FileOutputStream fileOut = new FileOutputStream(cachePath_ + projectKey_ + "_"+ versionInfo.getName() + SERIALIZED_OBJECT_EXT);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(versionInfo);
      out.close();
      fileOut.close();
     } catch(IOException i) {
       i.printStackTrace();
     }
  }
  
  /**
   * Deserialize a VersionInfo instance (if found) and return it, thus returning it from the 
   * on-disk cache.
   * 
   * @param versionName the name of the JIRA project version to retrieve.
   */
  public VersionInfo getCached(String versionName)
  {
    VersionInfo vi = null;
    try {
      FileInputStream fileIn = new FileInputStream(cachePath_ + projectKey_ + "_" + versionName + SERIALIZED_OBJECT_EXT);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      vi = (VersionInfo) in.readObject();
      in.close();
      fileIn.close();
      return vi;
    } catch (FileNotFoundException fnf) {
      return null; // OK, don't use cache.
    } catch(IOException i) {
      return null; // OK, don't use cache.
    } catch(ClassNotFoundException c) {
      System.out.println("VersionInfo class could not be found during deserialization.");
      c.printStackTrace();
      return null;
    }
  }
}



// 8517
