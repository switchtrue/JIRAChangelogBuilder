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
		
		output = new StringWriter();
	}
	
	@Override
	protected void tearDown() throws Exception {
		output.close();
		super.tearDown();
	}

	/**
	 * Black-box unit test of createChangelog method, of class ChangelogTemplate.
	 */
	public void testFileChangelog() throws Exception {
		System.out.println("fileChangelog");
		try {
			ChangelogTemplate.createChangelog(true, versions, output, "file.mustache");
		} catch (Exception e) {
			fail("Exception raised.");
			e.printStackTrace();
		}
		
		assertNotNull("No file output.", output.toString());
		System.out.println(output.toString());
	}
	
	/**
	 * Black-box unit test of createChangelog method, of class ChangelogTemplate.
	 */
	public void testModuleChangelog() throws Exception {
		System.out.println("moduleChangelog");
		try {
			ChangelogTemplate.createChangelog(false, versions, output, "module.mustache");
		} catch (Exception e) {
			fail("Exception raised.");
			e.printStackTrace();
		}
		
		assertNotNull("No file output.", output.toString());
		System.out.println(output.toString());
	}
	
	/**
	 * Black-box regression test for integrating the new template module with the existing program base.
	 */
	public void testCreateAll() throws Exception {
		System.out.println("allChangelogs");
		try {
			JiraAPI jira = new JiraAPI("jenkins", "j3nk1ns!", "https://fivium.atlassian.net", "");
			jira.fetchVersionDetails("TESTPROJ", "2.0.1");
			String[] files = new String[2];
			files[0] = "file.mustache";
			files[1] = "module.mustache";
			ChangelogBuilder clWriter = new ChangelogBuilder();
			clWriter.build(jira.getVersionInfoList(), "changelog.txt", files);
			
			// attempt to open the generated changelog file. If an IOException is thrown, then the file does not exist.
			FileReader reader = new FileReader("changelog1");
			reader.close();
			reader = new FileReader("changelog2");
			reader.close();
		} catch (IOException e) {
			fail("File does not exist!");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			fail("Exception raised.");
			System.out.println(e.getMessage());
		}
	}
}
