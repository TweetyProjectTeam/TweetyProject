package org.tweetyproject.web.pyargservices;
import java.util.Objects;

public class Post {
    private String cmd;
    

    public Post() {
    }

    public Post(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Post cmd(String cmd) {
        setCmd(cmd);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Post)) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(cmd, post.cmd);
    }

    @Override
    public String toString() {
        return "{" +
            " cmd='" + getCmd() + "'" +
            "}";
    }
    
}
