package org.tweetyproject.web.services.spring.src.main.java.com.pyargservices;

import java.util.List;

public class PyArgPost {
//   {
//     "cmd": "get_models",
//     "email": "pyarg@mail.com",
//     "nr_of_arguments": 3,
//     "attacks": [[1, 2], [2, 1], [3, 3], [3, 1]],
//     "semantics": "wad",
// "solver": "simple",
// "timeout": <maximal ms allowed for computation>
// }



    private String cmd;
    private String email;
    private int nr_of_arguments;
    private List<List<Integer>> attacks;
    private String semantics;
    private String solver;
    private int timeout; // Timeout in seconds

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