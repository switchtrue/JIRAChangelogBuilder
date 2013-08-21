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
	 * Black-box unit test of createChangelog method, of class ChangelogTemplate.
	 */
	public void testFileChangelog() throws Exception {
		System.out.println("fileChangelog");
		try {
			ChangelogTemplate.createChangelog(versions, output, "examples/file.mustache", LineEnding.NATIVE);
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
			ChangelogTemplate.createChangelog(versions, output, "examples/module.mustache", LineEnding.NATIVE);
		} catch (Exception e) {
			fail("Exception raised.");
			e.printStackTrace();
		}
		
		assertNotNull("No file output.", output.toString());
		System.out.println(output.toString());
	}
}
