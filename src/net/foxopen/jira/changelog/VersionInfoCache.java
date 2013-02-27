package net.foxopen.jira.changelog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class VersionInfoCache
{  
  private final static String SERIALIZED_OBJECT_EXT = ".ser";
  
  private final String projectKey_;
  private final String cachePath_;
  
  public VersionInfoCache(String projectKey, String cachePath)
  {
    projectKey_ = projectKey;
    cachePath_ = cachePath + "\\";
  }
  
  public void cache(VersionInfo vi)
  {
    try {
      FileOutputStream fileOut = new FileOutputStream(cachePath_ + projectKey_ + "_"+ vi.getName() + SERIALIZED_OBJECT_EXT);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(vi);
      out.close();
      fileOut.close();
     } catch(IOException i) {
       i.printStackTrace();
     }
  }
  
  public VersionInfo getCached(String versionLabel)
  {
    VersionInfo vi = null;
    try {
      FileInputStream fileIn = new FileInputStream(cachePath_ + projectKey_ + "_" + versionLabel + SERIALIZED_OBJECT_EXT);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      vi = (VersionInfo) in.readObject();
      in.close();
      fileIn.close();
      return vi;
    } catch (FileNotFoundException fnf) {
      return null; 
    } catch(IOException i) {
      i.printStackTrace();
      return null;
    } catch(ClassNotFoundException c) {
      System.out.println("VersionInfo class could not be found during deserialization.");
      c.printStackTrace();
      return null;
    }
  }
}



// 8517
