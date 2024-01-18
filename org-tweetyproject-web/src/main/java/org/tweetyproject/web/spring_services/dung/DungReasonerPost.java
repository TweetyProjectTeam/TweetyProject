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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.spring_services.dung;

import java.util.List;

/**
 * *description missing*
 */
public class DungReasonerPost
 {

    private String cmd;
    private String email;
    private int nr_of_arguments;
    private List<List<Integer>> attacks;
    private String semantics;
    private String solver;
    private int timeout; // Timeout in seconds
    private String unit_timeout;

  /**
   * *description missing*
 * @return *description missing*
 */
public String getUnit_timeout() {
      return unit_timeout;
    }

    /**
     * *description missing*
     * @param unit_timeout *description missing*
     */
    public void setUnit_timeout(String unit_timeout) {
      this.unit_timeout = unit_timeout;
    }

  /**
   * *description missing*
 * @return *description missing*
 */
public String getCmd() {
    return this.cmd;
  }

  /**
   * *description missing*
 * @param cmd *description missing*
 */
public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  /**
   * *description missing*
 * @return *description missing*
 */
public String getEmail() {
    return this.email;
  }

  /**
   * *description missing*
 * @param email *description missing*
 */
public void setEmail(String email) {
    this.email = email;
  }

  /**
   * *description missing*
 * @return *description missing*
 */
public int getNr_of_arguments() {
    return this.nr_of_arguments;
  }

  /**
   * *description missing*
 * @param nr_of_arguments *description missing*
 */
public void setNr_of_arguments(int nr_of_arguments) {
    this.nr_of_arguments = nr_of_arguments;
  }

  /**
   * *description missing*
 * @return *description missing*
 */
public List<List<Integer>> getAttacks() {
    return this.attacks;
  }

  /**
   * *description missing*
 * @param attacks *description missing*
 */
public void setAttacks(List<List<Integer>> attacks) {
    this.attacks = attacks;
  }

  /**
   * *description missing*
 * @return *description missing*
 */
public String getSemantics() {
    return this.semantics;
  }

  /**
   * *description missing*
 * @param semantics *description missing*
 */
public void setSemantics(String semantics) {
    this.semantics = semantics;
  }

  /**
   * *description missing*
 * @return *description missing*
 */
public String getSolver() {
    return this.solver;
  }

  /**
   * *description missing*
 * @param solver *description missing*
 */
public void setSolver(String solver) {
    this.solver = solver;
  }

  /**
   * *description missing*
 * @return *description missing*
 */
public int getTimeout() {
    return this.timeout;
  }

  /**
   * *description missing*
 * @param timeout *description missing*
 */
public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

}