/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.foxopen.jira.changelog;

import java.util.LinkedList;

/**
 * Defines an issue category (bug, task, support ticket, etc.)
 *
 * @author apigram
 */
public class Type {

  String name;
  LinkedList<Change> issues;

  /**
   * Default constructor
   *
   * @param name The name of the type
   */
  public Type(String name) {
    this.name = name;
    issues = new LinkedList<Change>();
  }
}
