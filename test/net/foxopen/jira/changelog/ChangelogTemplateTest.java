/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.foxopen.jira.changelog;

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
			ChangelogTemplate.createChangelog(versions, output, "examples/plain-text.mustache");
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
			ChangelogTemplate.createChangelog(versions, output, "examples/html.mustache");
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
			ChangelogTemplate.createChangelog(versions, output, "examples/xml.mustache");
		} catch (Exception e) {
			fail("Exception raised.");
			e.printStackTrace();
		}
		
		assertNotNull("No file output.", output.toString());
		System.out.println(output.toString());
	}
	
	/**
	 * Black-box integration test for a complete run of the program.
	 * @throws Exception 
	 */
	public void testFullRun() throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream("credentials.properties"));
		System.out.println("fullRun");
		String[] args = new String[9];
		args[0] = "https://fivium.atlassian.net";
		args[1] = properties.getProperty("username");
		args[2] = properties.getProperty("password");
		args[3] = "TESTPROJ";
		args[4] = "2.0.2";
		args[5] = "examples/html.mustache";
		args[6] = "--debug";
		args[7] = "--changelog-file-name";
		args[8] = "changelog.html";
		
		// wrapper function has same effect as main, minus the System.exit call.
		Changelog.main(args);
		
		File f = new File("changelog.html");
		assertTrue(f.exists());
	}
}
