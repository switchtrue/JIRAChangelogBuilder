/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.switchtrue.jira.changelog;

import java.io.*;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import java.util.LinkedList;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import java.util.Properties;

/**
 * Test case for generating file and module changelog templates
 *
 * @author apigram
 */
public class ChangelogTemplateTest extends TestCase {

  LinkedList<Change> issues;
  List<VersionInfo> versions;
  StringWriter output;

  public ChangelogTemplateTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    File outputDir = new File("test_output");
    if (!outputDir.exists()) {
      try{
        outputDir.mkdir();
      } catch(SecurityException se){
        throw se;
      } 
    }
    
    issues = new LinkedList<Change>();

    issues.add(new Change("TESTPROJ-10", "Test Issue", "Bug"));
    issues.add(new Change("TESTPROJ-20", "Test Issue 2", "Task"));
    issues.add(new Change("TESTPROJ-30", "Test Issue 3", "Support Ticket"));
    issues.add(new Change("TESTPROJ-40", "Test Issue 4", "Task"));

    versions = new LinkedList<VersionInfo>();
    VersionInfo version = new VersionInfo("2.0.1", "Bug fix release for 2.0.0", new Date(), issues);
    versions.add(version);
    version = new VersionInfo("2.0.2", "Bug fix release for 2.0.0", new Date(), issues);
    versions.add(version);

    output = new StringWriter();
  }

  @Override
  protected void tearDown() throws Exception {
    output.close();
    super.tearDown();
  }

  /**
   * White-box unit test of createChangelog method, of class ChangelogTemplate.
   */
  public void testTextChangelog() throws Exception {
    System.out.println("textChangelog");
    try {
      ChangelogTemplate.fileRoot = "examples";
      ChangelogTemplate.createChangelog(versions, output, "plain-text.mustache", LineEnding.NATIVE);
    } catch (Exception e) {
      fail("Exception raised.");
      e.printStackTrace();
    }

    assertNotNull("No file output.", output.toString());
    System.out.println(output.toString());
  }

  /**
   * White-box unit test of createChangelog method, of class ChangelogTemplate.
   */
  public void testHTMLChangelog() throws Exception {
    System.out.println("HTMLChangelog");
    try {
      ChangelogTemplate.fileRoot = "examples";
      ChangelogTemplate.createChangelog(versions, output, "html.mustache", LineEnding.NATIVE);
    } catch (Exception e) {
      fail("Exception raised.");
      e.printStackTrace();
    }

    assertNotNull("No file output.", output.toString());
    System.out.println(output.toString());
  }

  /**
   * White-box unit test of createChangelog method, of class ChangelogTemplate.
   */
  public void testXMLChangelog() throws Exception {
    System.out.println("XMLChangelog");
    try {
      ChangelogTemplate.fileRoot = "examples";
      ChangelogTemplate.createChangelog(versions, output, "xml.mustache", LineEnding.NATIVE);
    } catch (Exception e) {
      fail("Exception raised.");
      e.printStackTrace();
    }

    assertNotNull("No file output.", output.toString());
    System.out.println(output.toString());
  }

  /**
   * Black-box integration test for a complete run of the program.
   *
   * @throws Exception
   */
  public void testFullRun() throws Exception {
    Properties properties = new Properties();
    properties.load(new FileInputStream("testing.properties"));
    System.out.println("fullRun");
    String[] args = new String[12];
    args[0] = properties.getProperty("url");
    args[1] = properties.getProperty("username");
    args[2] = properties.getProperty("password");
    args[3] = properties.getProperty("project");
    args[4] = properties.getProperty("version");
    args[5] = "examples";
    args[6] = "html.mustache,plain-text.mustache";
    args[7] = "--debug";
    args[8] = "--changelog-file-name";
    args[9] = "test_output" + File.separator + "changelog.html,test_output" + File.separator + "changelog.txt";
    args[10] = "--object-cache-path";
    args[11] = "cache";

    // wrapper function has same effect as main, minus the System.exit call.
    Changelog.main(args);

    File f = new File("test_output/changelog.html");
    assertTrue(f.exists());
  }

  public void testLineEndings() throws Exception {
    System.out.println("lineEndings");
    FileWriter out1 = new FileWriter("test_output" + File.separator + "testNative.txt");
    FileWriter out2 = new FileWriter("test_output" + File.separator + "testWindows.txt");
    FileWriter out3 = new FileWriter("test_output" + File.separator + "testNIX.txt");
    try {
      ChangelogTemplate.fileRoot = "examples";
      ChangelogTemplate.createChangelog(versions, out1, "plain-text.mustache", LineEnding.NATIVE);
      ChangelogTemplate.createChangelog(versions, out2, "plain-text.mustache", LineEnding.WINDOWS);
      ChangelogTemplate.createChangelog(versions, out3, "plain-text.mustache", LineEnding.NIX);
    } catch (Exception e) {
      fail("Exception raised.");
      e.printStackTrace();
    }

    // manually check the result for this test case
    out1.close();
    out2.close();
    out3.close();
    assertTrue(true);
  }
  
  /**
   * Black-box integration test for a complete run of the program limiting
   * the changelog to only version that start with a particular value.
   *
   * @throws Exception
   */
  public void testVersionStartsWith() throws Exception {
    Properties properties = new Properties();
    properties.load(new FileInputStream("testing.properties"));
    System.out.println("startsWith");
    
    String starts_with = properties.getProperty("versionstartswith");
    String file_name = "version_starts_with_" + starts_with + ".html";
    
    String[] args = new String[14];
    args[0] = properties.getProperty("url");
    args[1] = properties.getProperty("username");
    args[2] = properties.getProperty("password");
    args[3] = properties.getProperty("project");
    args[4] = properties.getProperty("version");
    args[5] = "examples";
    args[6] = "html.mustache,plain-text.mustache";
    args[7] = "--debug";
    args[8] = "--changelog-file-name";
    args[9] = "test_output" + File.separator + file_name + ",test_output" + File.separator + "changelog.txt";
    args[10] = "--object-cache-path";
    args[11] = "cache";
    args[12] = "--version-starts-with";
    args[13] = starts_with;

    // wrapper function has same effect as main, minus the System.exit call.
    Changelog.main(args);

    File f = new File("test_output/" + file_name);
    assertTrue(f.exists());
  }
  
  /**
   * Black-box integration test for a complete run of the program limiting
   * the changelog to only versions that have a name less than or equal to
   * a certain value.
   *
   * @throws Exception
   */
  public void testVersionLessThanOrEqual() throws Exception {
    Properties properties = new Properties();
    properties.load(new FileInputStream("testing.properties"));
    System.out.println("lessThanOrEqual");
    
    String less_than_equal = properties.getProperty("versionlessthanorequal");
    String file_name = "version_less_than_or_equal_" + less_than_equal + ".html";
    
    String[] args = new String[14];
    args[0] = properties.getProperty("url");
    args[1] = properties.getProperty("username");
    args[2] = properties.getProperty("password");
    args[3] = properties.getProperty("project");
    args[4] = properties.getProperty("version");
    args[5] = "examples";
    args[6] = "html.mustache,plain-text.mustache";
    args[7] = "--debug";
    args[8] = "--changelog-file-name";
    args[9] = "test_output" + File.separator + file_name +",test_output" + File.separator + "changelog.txt";
    args[10] = "--object-cache-path";
    args[11] = "cache";
    args[12] = "--version-less-than-or-equal";
    args[13] = less_than_equal;

    // wrapper function has same effect as main, minus the System.exit call.
    Changelog.main(args);

    File f = new File("test_output/version_less_than_or_equal_" + less_than_equal + ".html");
    assertTrue(f.exists());
  }
}