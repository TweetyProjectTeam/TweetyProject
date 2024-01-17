package org.tweetyproject.web.spring_services;
import java.util.Objects;

public class Ping {

    private final long id;
	private final String content;

    public Ping() {
		this.id = -1;
		this.content = "";
	}

    public Ping(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return this.id;
    }


    public String getContent() {
        return this.content;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Ping)) {
            return false;
        }
        Ping ping = (Ping) o;
        return id == ping.id && Objects.equals(content, ping.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }

}