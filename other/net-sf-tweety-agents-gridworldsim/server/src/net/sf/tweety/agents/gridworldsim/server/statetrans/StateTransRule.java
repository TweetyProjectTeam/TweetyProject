/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
