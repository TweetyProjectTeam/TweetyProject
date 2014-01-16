package net.sf.tweety.agents.gridworldsim.server.statetrans;

import net.sf.tweety.agents.gridworldsim.server.GridWorld;
import net.sf.tweety.agents.gridworldsim.server.ActionRequest;

/**
 * This is the interface for classes implementing state transition rules
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public interface StateTransRule {

    /**
     * Tells if this rule is triggered by a certain {@link ActionRequest}
     * @param request the {@link ActionRequest} to check for if this rule gets triggered
     * @return true if this rule gets triggered, false otherwise
     */
    public boolean isTriggered(ActionRequest request);

    /**
     * apply this rule to the given {@link GridWorld} and {@link ActionRequest}
     * @param gridWorld the {@link GridWorld} whose state should be modified
     * @param request the {@link ActionRequest}
     */
    public void doTransition(GridWorld gridWorld, ActionRequest request);

    /**
     * set a parameter relevant to the execution of this rule (will be passed from the XML document, hence Strings)
     * if no parameters are applicable for a rule, the method can stay empty
     * @param parameter the parameter to set
     * @param value the value to set
     */
    public void setParameter(String parameter, String value);
}
