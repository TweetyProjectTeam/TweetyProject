package net.sf.tweety.agents.gridworldsim.serverapi.perceptions;

import java.util.Collection;

/**
 * Objects made from this class represent perceived agents
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class AgentPerception extends GridObjectPerception implements Comparable {

    private final boolean self;
    private final Integer hearing;
    private final Integer soundIntensity;
    private final Integer priority;
    private final Integer moveForce;
    private String internalState;

    /**
     * Constructs a new {@code AgentPerception}
     * @param name the name of the agent
     * @param capNeed the capacity need of the agent
     * @param freeCap the free capacity of the agent
     * @param hearing the hearing capability of the agent (can be {@code null})
     * @param soundIntensity the sound intensity of the agent  (can be {@code null})
     * @param properties the properties of this agent
     * @param self true if an agent is perceiving himself with this {@code AgentPerception}, false otherwise
     * @param priority the execution order priority of the agent
     * @param moveForce the move force of the agent
     */
    public AgentPerception(String name, int capNeed, int freeCap, Integer hearing, Integer soundIntensity, Collection<String> properties, boolean self, Integer priority, Integer moveForce) {
        super(name, capNeed, freeCap, properties);
        this.self = self;
        this.hearing = hearing;
        this.soundIntensity = soundIntensity;
        this.priority = priority;
        this.moveForce = moveForce;
    }

    /**
     * Tells if an agent is perceiving himself with this {@code AgentPerception}.
     * @return true if an agent is perceiving himself with this {@code AgentPerception}, false otherwise
     */
    public boolean isSelf() {
        return self;
    }

    /**
     * Compare this {@code AgentPerception} to another object. If the other object is an instance of {@link GridObjectPerception}, a
     * lexicographical comparing will be done except if this {@code AgentPerception} is {@code isSelf()}, in which case it will always be
     * sorted on top.
     * @param o the {@code Object} to compare with
     * @return negative value if this {@code AgentPerception} is sorted before another object, 0 if both are equal regarding the order and positive value if this {@code AgentPerception} comes after the other {@code Object} in the ordering.
     */
    @Override
    public int compareTo(Object o) {
        if (self) {
            return -1;
        } else {
            return super.compareTo(o);
        }
    }

    /**
     * Get the hearing capability.
     * @return the hearing capability (can be {@code null})
     */
    public Integer getHearing() {
        return hearing;
    }

    /**
     * Get the sound intensity.
     * @return the sound intensity (can be {@code null})
     */
    public Integer getSoundIntensity() {
        return soundIntensity;
    }

    /**
     * Get the priority regarding execution order.
     * @return the priority regarding execution order
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Get the internal state (if it has been set)
     * @return the internal state
     */
    public String getInternalState() {
        return internalState;
    }

    /**
     * Set the internal state.
     * @param internalState the internal state to set
     */
    public void setInternalState(String internalState) {
        this.internalState = internalState;
    }

    /**
     * Get the move force.
     * @return the move force
     */
    public Integer getMoveForce() {
        return moveForce;
    }
}
