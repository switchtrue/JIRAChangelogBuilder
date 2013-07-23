package net.foxopen.jira.changelog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A disk-based cache for VersionInfo instances. VersionInfo objects are serialized to disk
 * with a file name of projectkey_versionname.ser. These can be deserialized and used upon request. 
 * @author leonmi
 * 
 */
public class VersionInfoCache
{  
  private final static String SERIALIZED_OBJECT_EXT = ".ser";
  
  private final String projectKey_;
  private final String cachePath_;
  
  /**
   * Constructor to create a cache for this JIRA project.
   * The cache will be located at the cachePath set, if this directory does not exist, it will be created.
   * 
   * @param projectKey key used for this project in JIRA.
   * @param cachePath path on disk to the cache.
   */
  public VersionInfoCache(String projectKey, String cachePath)
  {
    Logger.log("Creating version info cache.");
    projectKey_ = projectKey;
    
    String fileSeparator = System.getProperty("file.separator");
    Logger.log("Using system file path separator of '" + fileSeparator + "'");
    cachePath_ = cachePath.endsWith(fileSeparator) ? cachePath : cachePath + fileSeparator;    
    (new File(cachePath_)).mkdirs();
  }
  
  /**
   * Cache a given VersionInfo object by serialising it to disk with a file name of projectkey_versionname.ser.
   * 
   * @param versionInfo the VersionInfo instance to be cached.
   */
  public void cache(VersionInfo versionInfo)
  {
    String filename = cachePath_ + projectKey_ + "_"+ versionInfo.getName() + SERIALIZED_OBJECT_EXT;
    Logger.log("Caching version changelog for version '" + versionInfo.getName() + "' in file: " + filename);
    try {
      FileOutputStream fileOut = new FileOutputStream(filename);
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
      Logger.log("Cache hit for '" + versionName + "'");
      return vi;
    } catch (FileNotFoundException fnf) {
      Logger.log("Cache miss for '" + versionName + "'");
      return null; // OK, don't use cache.
    } catch(IOException i) {
      Logger.log("Cache miss for '" + versionName + "'");
      return null; // OK, don't use cache.
    } catch(ClassNotFoundException c) {
      System.out.println("VersionInfo class could not be found during deserialization.");
      c.printStackTrace();
      return null;
    }
  }
}



// 8517
