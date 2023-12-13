package org.tweetyproject.web.pyargservices.aba;

import java.util.List;

public class AbaReasonerPost
 {

    private String cmd;
    private String email;
    private int nr_of_arguments;
    private List<List<Integer>> attacks;
    private String semantics;
    private String solver;
    private int timeout; // Timeout in seconds
    private String unit_timeout;

  public String getUnit_timeout() {
      return unit_timeout;
    }

    public void setUnit_timeout(String unit_timeout) {
      this.unit_timeout = unit_timeout;
    }

  public String getCmd() {
    return this.cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getNr_of_arguments() {
    return this.nr_of_arguments;
  }

  public void setNr_of_arguments(int nr_of_arguments) {
    this.nr_of_arguments = nr_of_arguments;
  }

  public List<List<Integer>> getAttacks() {
    return this.attacks;
  }

  public void setAttacks(List<List<Integer>> attacks) {
    this.attacks = attacks;
  }

  public String getSemantics() {
    return this.semantics;
  }

  public void setSemantics(String semantics) {
    this.semantics = semantics;
  }

  public String getSolver() {
    return this.solver;
  }

  public void setSolver(String solver) {
    this.solver = solver;
  }

  public int getTimeout() {
    return this.timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

}