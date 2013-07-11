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
 *
 * @author developer1
 */
public class ChangelogTemplateTest extends TestCase {
	
	public ChangelogTemplateTest(String testName) {
		super(testName);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test of createChangelog method, of class ChangelogTemplate.
	 */
	public void testFileChangelog() throws Exception {
		System.out.println("createChangelog");
		LinkedList<Change> issues = new LinkedList<Change>();
		issues.add(new Change("TESTPROJ-10", "Test Issue", "Bug"));
		issues.add(new Change("TESTPROJ-20", "Test Issue 2", "Task"));
		issues.add(new Change("TESTPROJ-30", "Test Issue 3", "Support Ticket"));
		issues.add(new Change("TESTPROJ-40", "Test Issue 4", "Task"));
		
		List<VersionInfo> versions = new LinkedList<VersionInfo>();
		VersionInfo version = new VersionInfo("2.0.1", null, new Date(), issues);
		versions.add(version);

		StringWriter output = new StringWriter();
		
		try {
			ChangelogTemplate.createChangelog(true, versions, output);
		} catch (Exception e) {
			fail("Exception raised.");
			e.printStackTrace();
		}
		
		assertNotNull("No file output.", output.toString());
		System.out.println(output.toString());
	}
	
	/**
	 * Test of createChangelog method, of class ChangelogTemplate.
	 */
	public void testModuleChangelog() throws Exception {
		System.out.println("createChangelog");
		LinkedList<Change> issues = new LinkedList<Change>();
		issues.add(new Change("TESTPROJ-10", "Test Issue", "Bug"));
		issues.add(new Change("TESTPROJ-20", "Test Issue 2", "Task"));
		issues.add(new Change("TESTPROJ-30", "Test Issue 3", "Support Ticket"));
		issues.add(new Change("TESTPROJ-40", "Test Issue 4", "Task"));
		
		List<VersionInfo> versions = new LinkedList<VersionInfo>();
		VersionInfo version = new VersionInfo("2.0.1", null, new Date(), issues);
		versions.add(version);

		StringWriter output = new StringWriter();
		
		try {
			ChangelogTemplate.createChangelog(false, versions, output);
		} catch (Exception e) {
			fail("Exception raised.");
			e.printStackTrace();
		}
		
		assertNotNull("No file output.", output.toString());
		System.out.println(output.toString());
	}
}
