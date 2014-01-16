package net.sf.tweety.agents.gridworldsim.server;

import java.util.Collection;
import java.util.HashSet;

/**
 * Objects made from this class represent {@link Agent}s in the {@link GridWorld}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class Agent extends GridObject {

    private int viewRange;
    private int moveForce;
    private int initXPos;
    private int initYPos;
    private int priority;
    private Integer hearing;
    private Integer soundIntensity;
    private Collection<Agent> agentAllAccepts;
    private Collection<GridObject> objectAllAccepts;
    private Collection<HandOverAccept> accepts;
    private Collection<PrivateMessage> messages;

    /**
     * Constructs a new {@code Agent}. After construction it needs to be configured.
     */
    public Agent() {
        super();
        agentAllAccepts = new HashSet<Agent>();
        objectAllAccepts = new HashSet<GridObject>();
        accepts = new HashSet<HandOverAccept>();
        messages = new HashSet<PrivateMessage>();
    }

    /**
     * Configure the {@code Agent}.
     * @param name the name
     * @param gridWorld the {@link GridWorld} this {@code Agent} belongs to
     * @param initCapNeed the initial capacity need
     * @param initCap the initial capacity
     * @param viewRange the view range
     * @param moveForce the move force
     * @param initXPos the initial x coordinate on the grid
     * @param initYPos the initial y coordinate on the grid
     * @param hearing the hearing capability
     * @param soundIntensity the sound intensity
     * @param priority the priority
     */
    public void configure(String name, GridWorld gridWorld, int initCapNeed, int initCap, int viewRange, int moveForce, int initXPos, int initYPos, int hearing, int soundIntensity, int priority) {
        super.configure(name, gridWorld, initCapNeed, initCap);
        this.viewRange = viewRange;
        this.moveForce = moveForce;
        this.initXPos = initXPos;
        this.initYPos = initYPos;
        this.hearing = hearing;
        this.priority = priority;
        this.soundIntensity = soundIntensity;
    }

    /**
     * Get the move force of this {@code Agent}.
     * @return the move force of this {@code Agent}.
     */
    public int getMoveForce() {
        return moveForce;
    }

    /**
     * Get the view range of this {@code Agent}.
     * @return the view range of this {@code Agent}
     */
    public int getViewRange() {
        return viewRange;
    }

    /**
     * Get the initial (<b>not</b> current) X position of this {@code Agent} on the grid.
     * @return the initial X position of this {@code Agent}
     */
    public int getInitXPos() {
        return initXPos;
    }

    /**
     * Get the initial (<b>not</b> current) Y position of this {@code Agent} on the grid.
     * @return the initial Y position of this {@code Agent}
     */
    public int getInitYPos() {
        return initYPos;
    }

    /**
     * Add a new {@code Agent} from which all hand over actions should be accepted.
     * @param agent the {@code Agent} from which all hand over actions should be accepted
     */
    public void addAllAcceptAgent(Agent agent) {
        agentAllAccepts.add(agent);
    }

    /**
     * Remove the allowance for an {@code Agent} to accept all hand-over action requests from it.
     * @param agent the {@code Agent} from which all hand-over actions should not be allowed any more
     */
    public void removeAllAcceptAgent(Agent agent) {
        agentAllAccepts.remove(agent);
    }

    /**
     * Add a new {@link GridObject} that should be accepted from any agent in a hand over action request.
     * @param gridObject the {@link GridObject} that should be accepted from any agent in a hand over action request
     */
    public void addAllAcceptObject(GridObject gridObject) {
        objectAllAccepts.add(gridObject);
    }

    /**
     * Do not accept the {@link GridObject} unconditionally from any agent anymore.
     * @param gridObject the {@link GridObject} which should be be accepted in an hand over action request unconditionally anymore
     */
    public void removeAllAcceptObject(GridObject gridObject) {
        objectAllAccepts.remove(gridObject);
    }

    /**
     * Accept a certain combination of {@code Agent} and {@link GridObject} to be accepted in a hand over action request.
     * @param agent the {@code Agent} from which the {@link GridObject} should be accepted
     * @param gridObject the {@link GridObject} which should be accepted from the {@code Agent}
     */
    public void addHandOverAccept(Agent agent, GridObject gridObject) {
        accepts.add(new HandOverAccept(agent, gridObject));
    }

    /**
     * Revoke a combination of {@code Agent} and {@link GridObject} from being accepted in a hand over action request.
     * @param agent the {@code Agent} from which the {@link GridObject} should no longer be specifically accepted
     * @param gridObject the {@link GridObject} which should no longer be specifically accepted  by the {@code Agent}
     */
    public void removeHandOverAccept(Agent agent, GridObject gridObject) {
        accepts.remove(new HandOverAccept(agent, gridObject));
    }

    /**
     * Tell if this {@code Agent} accepts a certain {@link GridObject} from a certain {@code Agent}
     * @param agent the {@code Agent} which offers to hand over a {@link GridObject}
     * @param gridObject the {@link GridObject} which is offered
     * @return true if this {@code Agent} agrees to the hand over, false otherwise
     */
    public boolean isHandOverAccepted(Agent agent, GridObject gridObject) {
        return agentAllAccepts.contains(agent) || objectAllAccepts.contains(gridObject) || accepts.contains(new HandOverAccept(agent, gridObject));
    }

    /**
     * Get all {@link PrivateMessage}s this {@code Agent} should perceive in the next state.
     * @return all {@link PrivateMessage}s this {@code Agent} should perceive in the next state
     */
    public Collection<PrivateMessage> getMessages() {
        return messages;
    }

    /**
     * Add a {@link PrivateMessage} this {@code Agent} should perceive in the next state.
     * @param message the {@link PrivateMessage} this {@code Agent} should perceive in the next state.
     */
    public void addMessage(PrivateMessage message) {
        messages.add(message);
    }

    /**
     * Clear all {@link PrivateMessage}s of this {@code Agent}.
     */
    public void clearMessages() {
        messages.clear();
    }

    /**
     * Get the hearing of this {@code Agent}.
     * @return the hearing of this {@code Agent}
     */
    public Integer getHearing() {
        return hearing;
    }

    /**
     * Get the sound intensity of this {@code Agent}.
     * @return the sound intensity of this {@code Agent}
     */
    public Integer getSoundIntensity() {
        return soundIntensity;
    }

    /**
     * Get the priority of this {@code Agent}.
     * @return the priority of this {@code Agent}
     */
    public int getPriority() {
        return priority;
    }
}
