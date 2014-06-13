/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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

import net.sf.tweety.agents.gridworldsim.server.ActionRequest;
import net.sf.tweety.agents.gridworldsim.server.Agent;
import net.sf.tweety.agents.gridworldsim.server.GridObject;
import net.sf.tweety.agents.gridworldsim.server.GridWorld;
import net.sf.tweety.agents.gridworldsim.server.LockerObject;
import org.apache.log4j.Logger;

/**
 * This rule implements the locking and unlocking of a {@link LockerObject}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class LockUnlockRule implements StateTransRule {

    private static final Logger logger = Logger.getLogger(LockUnlockRule.class);

    /**
     * Checks if this rule is triggered (which is the case for "lock" and "unlock" {@link ActionRequest}s).
     * @param request the {@link ActionRequest} to check
     * @return true if this rule gets triggered, false otherwise
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        if (request.getRequestOp().getType().equals("lock") || request.getRequestOp().getType().equals("unlock")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Do the transition.
     * @param gridWorld the {@link GridWorld} whose state should be change by this rule
     * @param request the {@link ActionRequest} that triggers this change
     */
    @Override
    public void doTransition(GridWorld gridWorld, ActionRequest request) {
        Agent agent = request.getAgent();
        String lockerString = request.getRequestOp().getParameterValue("object");
        String passwordString = request.getRequestOp().getParameterValue("password");
        logger.debug("Starting to process a request by agent " + agent.getName() + " to lock " + lockerString);

        if (lockerString == null || passwordString == null) {
            logger.debug("Missing parameter");
            return;
        }

        GridObject object = gridWorld.getGridObjectByName(lockerString);

        if (object == null) {
            logger.debug("Object couldn't be found.");
            return;
        }

        if (!(object instanceof LockerObject)) {
            logger.debug("Object is not a locker.");
            return;
        }

        if (!gridWorld.getGridObjectLocation(object).equals(gridWorld.getGridObjectLocation(agent))) {
            logger.debug("Locker and agent are not in the same cell.");
            return;
        }

        LockerObject locker = (LockerObject) object;

        boolean success = false;

        if (request.getRequestOp().getType().equals("lock")) {
            success = locker.lock(passwordString);
        } else if (request.getRequestOp().getType().equals("unlock")) {
            success = locker.unlock(passwordString);
        }

        if (success) {
            logger.debug("Operation successful.");
        } else {
            logger.debug("Operation not successful. Locker already locked or wrong password?");
        }
    }

    /**
     * set the parameters (none supported)
     * @param parameter this rule doesn't support parameters
     * @param value this rule doesn't support parameters
     */
    @Override
    public void setParameter(String parameter, String value) {
        //no parameters
    }
}
