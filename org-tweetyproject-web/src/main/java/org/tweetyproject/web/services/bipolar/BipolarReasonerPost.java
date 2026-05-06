/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.bipolar;

import java.util.List;

/**
 * The BipolarReasonerPost class represents a data structure for holding information
 * related to a bipolar argumentation reasoner request sent via HTTP POST.
 */
public class BipolarReasonerPost {

  /** The command type for the bipolar reasoner request */
  private String cmd;

  /** The email associated with the bipolar reasoner request */
  private String email;

  /** The number of arguments in the bipolar reasoner request */
  private int nr_of_arguments;

  /** The attacks information in the bipolar reasoner request */
  private List<List<Integer>> attacks;

  /** The supports information in the bipolar reasoner request */
  private List<List<Integer>> supports;

  /** The semantics specified in the bipolar reasoner request */
  private String semantics;

  /** The solver specified in the bipolar reasoner request */
  private String solver;

  /** The timeout value (in seconds) specified in the bipolar reasoner request */
  private int timeout;

  /** The unit timeout value specified in the bipolar reasoner request */
  private String unit_timeout;

  /**
   * Gets the unit timeout value specified in the bipolar reasoner request.
   *
   * @return The unit timeout value
   */
  public String getUnit_timeout() {
      return unit_timeout;
  }

  /**
   * Sets the unit timeout value in the bipolar reasoner request.
   *
   * @param unit_timeout The unit timeout value to be set
   */
  public void setUnit_timeout(String unit_timeout) {
      this.unit_timeout = unit_timeout;
  }

  /**
   * Gets the command type in the bipolar reasoner request.
   *
   * @return The command type
   */
  public String getCmd() {
      return this.cmd;
  }

  /**
   * Sets the command type in the bipolar reasoner request.
   *
   * @param cmd The command type to be set
   */
  public void setCmd(String cmd) {
      this.cmd = cmd;
  }

  /**
   * Gets the email associated with the bipolar reasoner request.
   *
   * @return The email associated with the request
   */
  public String getEmail() {
      return this.email;
  }

  /**
   * Sets the email associated with the bipolar reasoner request.
   *
   * @param email The email to be set
   */
  public void setEmail(String email) {
      this.email = email;
  }

  /**
   * Gets the number of arguments in the bipolar reasoner request.
   *
   * @return The number of arguments
   */
  public int getNr_of_arguments() {
      return this.nr_of_arguments;
  }

  /**
   * Sets the number of arguments in the bipolar reasoner request.
   *
   * @param nr_of_arguments The number of arguments to be set
   */
  public void setNr_of_arguments(int nr_of_arguments) {
      this.nr_of_arguments = nr_of_arguments;
  }

  /**
   * Gets the attacks information in the bipolar reasoner request.
   *
   * @return The attacks information
   */
  public List<List<Integer>> getAttacks() {
      return this.attacks;
  }

  /**
   * Sets the attacks information in the bipolar reasoner request.
   *
   * @param attacks The attacks information to be set
   */
  public void setAttacks(List<List<Integer>> attacks) {
      this.attacks = attacks;
  }

  /**
   * Gets the supports information in the bipolar reasoner request.
   *
   * @return The supports information
   */
  public List<List<Integer>> getSupports() {
      return this.supports;
  }

  /**
   * Sets the supports information in the bipolar reasoner request.
   *
   * @param supports The supports information to be set
   */
  public void setSupports(List<List<Integer>> supports) {
      this.supports = supports;
  }

  /**
   * Gets the semantics specified in the bipolar reasoner request.
   *
   * @return The semantics specified
   */
  public String getSemantics() {
      return this.semantics;
  }

  /**
   * Sets the semantics in the bipolar reasoner request.
   *
   * @param semantics The semantics to be set
   */
  public void setSemantics(String semantics) {
      this.semantics = semantics;
  }

  /**
   * Gets the solver specified in the bipolar reasoner request.
   *
   * @return The solver specified
   */
  public String getSolver() {
      return this.solver;
  }

  /**
   * Sets the solver in the bipolar reasoner request.
   *
   * @param solver The solver to be set
   */
  public void setSolver(String solver) {
      this.solver = solver;
  }

  /**
   * Gets the timeout value (in seconds) specified in the bipolar reasoner request.
   *
   * @return The timeout value
   */
  public int getTimeout() {
      return this.timeout;
  }

  /**
   * Sets the timeout value (in seconds) in the bipolar reasoner request.
   *
   * @param timeout The timeout value to be set
   */
  public void setTimeout(int timeout) {
      this.timeout = timeout;
  }
}