package net.sf.tweety.agents.gridworldsim.server;

/**
 * Objects made from this class represent the parameter of a {@link GridObject}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridObjectParameter {

    private final String name;
    private final String value;

    /**
     * Constructs a new {@code GridObjectParameter}.
     * @param name the name of the {@code GridObjectParameter}.
     * @param value the value of the {@code GridObjectParameter}
     */
    public GridObjectParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Get the name of this {@code GridObjectParameter}.
     * @return the name of this {@code GridObjectParameter}
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of this {@code GridObjectParameter}.
     * @return the value of this {@code GridObjectParameter}
     */
    public String getValue() {
        return value;
    }
}
