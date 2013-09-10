/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.foxopen.jira.changelog;

/**
 * Represents the available line ending types supported by the changelog builder.
 * @author apigram
 * @version 1.05.0
 */
public enum LineEnding {
	NATIVE,
	WINDOWS,
	NIX;
	
	static LineEnding getEnding(String param) {
		if (param.equals("NATIVE")) {
			return NATIVE;
		} else if (param.equals("CRLF")) {
			return WINDOWS;
		} else if (param.equals("LF")) {
			return NIX;
		} else {
			return null;
		}
	}
}
