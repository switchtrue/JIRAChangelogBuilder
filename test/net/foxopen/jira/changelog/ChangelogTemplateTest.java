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
 * @version 1.03.00
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
	 * @throws Exception
	 */
	public void testFileChangelog() throws Exception {
		System.out.println("fileChangelog");
		try {
			ChangelogTemplate.createChangelog(true, versions, output, "file.mustache", LineEnding.NATIVE);
		} catch (Exception e) {
			fail("Exception raised.");
			e.printStackTrace();
		}
		
		assertNotNull("No file output.", output.toString());
		System.out.println(output.toString());
	}
	
	/**
	 * Black-box unit test of createChangelog method, of class ChangelogTemplate.
	 * @throws Exception
	 */
	public void testModuleChangelog() throws Exception {
		System.out.println("moduleChangelog");
		try {
			ChangelogTemplate.createChangelog(false, versions, output, "module.mustache", LineEnding.NATIVE);
		} catch (Exception e) {
			fail("Exception raised.");
			e.printStackTrace();
		}
		
		assertNotNull("No file output.", output.toString());
		System.out.println(output.toString());
	}
	
	/**
	 * White-box unit test of newline normalisation within ChangelogTemplate.
	 * @throws Exception 
	 * 
	 */
	public void testChangelogLineEndings() throws Exception {
		System.out.println("changelogLineEndings");
		
		String LF = System.getProperty("line.separator");
		try {
			
			ChangelogTemplate.createChangelog(false, versions, output, "module.mustache", LineEnding.NIX);
			System.out.println("UNIX newlines:");
			System.out.println(output.toString());
			assertFalse("UNIX line ending not converted properly.", (output.toString().contains("\r\n")));
			output.flush();
			
			ChangelogTemplate.createChangelog(false, versions, output, "module.mustache", LineEnding.WINDOWS);
			System.out.println("Windows newlines:");
			System.out.println(output.toString());
			assertTrue("Windows line ending not converted properly.", (output.toString().contains("\r\n")));
			output.flush();
			
			ChangelogTemplate.createChangelog(false, versions, output, "module.mustache", LineEnding.NATIVE);
			System.out.println("System newlines:");
			System.out.println(output.toString());
			assertTrue("System line ending not converted properly.", (output.toString().contains(LF)));
		} catch (Exception e) {
			fail("Exception raised.");
			e.printStackTrace();
		}
		
		assertNotNull("No file output.", output.toString());
		System.out.println(output.toString());
	}
	
	/**
	 * White-box regression test for integrating the new template module with the existing program base.
	 * @throws Exception
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
			clWriter.build(jira.getVersionInfoList(), "changelog", files, LineEnding.NATIVE);
			
			// attempt to open the generated changelog file. If an IOException is thrown, then the file does not exist.
			FileReader reader = new FileReader("changelog1.txt");
			reader.close();
			reader = new FileReader("changelog2.txt");
			reader.close();
		} catch (IOException e) {
			fail("File does not exist!");
			System.err.println(e.getMessage());
		} catch (Exception e) {
			fail("Exception raised.");
			System.err.println(e.getMessage());
		}
	}
}
