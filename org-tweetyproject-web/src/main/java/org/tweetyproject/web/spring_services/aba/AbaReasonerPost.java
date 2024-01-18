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
package org.tweetyproject.web.spring_services.aba;

import java.util.Objects;

/**
 * *description missing*
 */
public class AbaReasonerPost
 {

    private String cmd;
    private String email;
    private String kb;
    private String kb_format;
    private String fol_signature;
    /**
     * *description missing*
     * @return *description missing*
     */
    public String getFol_signature() {
      return fol_signature;
    }

    /**
     * *description missing*
     * @param fol_signature *description missing*
     */
    public void setFol_signature(String fol_signature) {
      this.fol_signature = fol_signature;
    }

    private String query_assumption;
    private String semantics;
    private int timeout;
    private String unit_timeout;


  /**
 * *description missing*
 */
public AbaReasonerPost() {
  }

  /**
   * *description missing*
 * @param cmd *description missing*
 * @param email *description missing*
 * @param kb *description missing*
 * @param kb_format *description missing*
 * @param fol_signature *description missing*
 * @param query_assumption *description missing*
 * @param semantics *description missing*
 * @param timeout *description missing*
 * @param unit_timeout *description missing*
 */
public AbaReasonerPost(String cmd, String email, String kb, String kb_format, String fol_signature, String query_assumption, String semantics, int timeout, String unit_timeout) {
    this.cmd = cmd;
    this.email = email;
    this.kb = kb;
    this.kb_format = kb_format;
    this.query_assumption = query_assumption;
    this.semantics = semantics;
    this.timeout = timeout;
    this.unit_timeout = unit_timeout;
    this.fol_signature = fol_signature;
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
public String getKb() {
    return this.kb;
  }

  /**
   * *description missing*
 * @param kb *description missing*
 */
public void setKb(String kb) {
    this.kb = kb;
  }

  /**
   * *description missing*
 * @return *description missing*
 */
public String getKb_format() {
    return this.kb_format;
  }

  /**
   * *description missing*
 * @param kb_format *description missing*
 */
public void setKb_format(String kb_format) {
    this.kb_format = kb_format;
  }

  /**
   * *description missing*
 * @return *description missing*
 */
public String getQuery_assumption() {
    return this.query_assumption;
  }

  /**
   * *description missing*
 * @param query_assumption *description missing*
 */
public void setQuery_assumption(String query_assumption) {
    this.query_assumption = query_assumption;
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

  /**
   * *description missing*
 * @return *description missing*
 */
public String getUnit_timeout() {
    return this.unit_timeout;
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
 * @param cmd *description missing*
 * @return *description missing*
 */
public AbaReasonerPost cmd(String cmd) {
    setCmd(cmd);
    return this;
  }

  /**
   * *description missing*
 * @param email *description missing*
 * @return *description missing*
 */
public AbaReasonerPost email(String email) {
    setEmail(email);
    return this;
  }

  /**
   * *description missing*
 * @param kb *description missing*
 * @return *description missing*
 */
public AbaReasonerPost kb(String kb) {
    setKb(kb);
    return this;
  }

  /**
   * *description missing*
 * @param kb_format *description missing*
 * @return *description missing*
 */
public AbaReasonerPost kb_format(String kb_format) {
    setKb_format(kb_format);
    return this;
  }

  /**
   * *description missing*
 * @param query_assumption *description missing*
 * @return *description missing*
 */
public AbaReasonerPost query_assumption(String query_assumption) {
    setQuery_assumption(query_assumption);
    return this;
  }

  /**
   * *description missing*
 * @param semantics *description missing*
 * @return *description missing*
 */
public AbaReasonerPost semantics(String semantics) {
    setSemantics(semantics);
    return this;
  }

  /**
   * *description missing*
 * @param timeout *description missing*
 * @return *description missing*
 */
public AbaReasonerPost timeout(int timeout) {
    setTimeout(timeout);
    return this;
  }

  /**
   * *description missing*
 * @param unit_timeout *description missing*
 * @return *description missing*
 */
public AbaReasonerPost unit_timeout(String unit_timeout) {
    setUnit_timeout(unit_timeout);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AbaReasonerPost)) {
            return false;
        }
        AbaReasonerPost abaReasonerPost = (AbaReasonerPost) o;
        return Objects.equals(cmd, abaReasonerPost.cmd) && Objects.equals(email, abaReasonerPost.email) && Objects.equals(kb, abaReasonerPost.kb) && Objects.equals(kb_format, abaReasonerPost.kb_format) && Objects.equals(query_assumption, abaReasonerPost.query_assumption) && Objects.equals(semantics, abaReasonerPost.semantics) && timeout == abaReasonerPost.timeout && Objects.equals(unit_timeout, abaReasonerPost.unit_timeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cmd, email, kb, kb_format, query_assumption, semantics, timeout, unit_timeout);
  }

  @Override
  public String toString() {
    return "{" +
      " cmd='" + getCmd() + "'" +
      ", email='" + getEmail() + "'" +
      ", kb='" + getKb() + "'" +
      ", kb_format='" + getKb_format() + "'" +
      ", query_assumption='" + getQuery_assumption() + "'" +
      ", semantics='" + getSemantics() + "'" +
      ", timeout='" + getTimeout() + "'" +
      ", unit_timeout='" + getUnit_timeout() + "'" +
      "}";
  }


}