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

import java.util.Random;
import net.sf.tweety.agents.gridworldsim.server.ActionRequest;
import net.sf.tweety.agents.gridworldsim.server.GridCell;
import net.sf.tweety.agents.gridworldsim.server.GridWorld;
import org.apache.log4j.Logger;

/**
 * This rule creates fog.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class FogRule implements StateTransRule {

    private static final Logger logger = Logger.getLogger(FogRule.class);
    private float createProb;
    private float removeProb;
    private String first;

    /**
     * Creates a new {@code FogRule}.
     */
    public FogRule() {
        createProb = 0f;
        removeProb = 0f;
        first = "random";
    }

    /**
     * Checks if this rule is triggered (which is the case for "always" {@link ActionRequest}s).
     * @param request the {@link ActionRequest} to check
     * @return true if this rule gets triggered, false otherwise
     */
    @Override
    public boolean isTriggered(ActionRequest request) {
        if (request.getRequestOp().getType().equals("always")) {
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
        logger.debug("Starting to create and uncreate fog.");

        /* set the order of fog generations/removal according to the parameters*/
        if (first.equals("random")) {
            Random r = new Random();
            float dice = r.nextFloat();
            if (dice < 0.5) {
                createFog(gridWorld);
                removeFog(gridWorld);
            } else {
                removeFog(gridWorld);
                createFog(gridWorld);
            }
        } else if (first.equals("create")) {
            createFog(gridWorld);
            removeFog(gridWorld);
        } else if (first.equals("remove")) {
            removeFog(gridWorld);
            createFog(gridWorld);
        }
    }

    /* remove fog */
    private void removeFog(GridWorld gridWorld) {
        if (removeProb > 0 && createProb > 0) {
            Random r = new Random();
            GridCell[][] cells = gridWorld.getGridCells();
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[0].length; j++) {
                    float dice = r.nextFloat();
                    if (dice < removeProb) {
                        GridCell currentCell = cells[i][j];
                        if (!currentCell.hasExclusiveObstacle()) {
                            currentCell.setFog(false);
                        }
                    }
                }
            }
        }

    }

    /* create fog */
    private void createFog(GridWorld gridWorld) {
        if (createProb > 0) {
            Random r = new Random();
            GridCell[][] cells = gridWorld.getGridCells();
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[0].length; j++) {
                    float dice = r.nextFloat();
                    if (dice < createProb) {
                        GridCell currentCell = cells[i][j];
                        if (!currentCell.hasExclusiveObstacle()) {
                            cells[i][j].setFog(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * set the parameters (creation/removal order and creation/removal probabilities)
     * @param parameter the parameter to set (first={random, create, remove}, createProb, removeProb)
     * @param value the value of the parameter
     */
    @Override
    public void setParameter(String parameter, String value) {
        if (parameter.equals("first") && (value.equals("random") || value.equals("create") || value.equals("remove"))) {
            first = value;
        }

        if (parameter.equals("createProb")) {
            try {
                createProb = Float.valueOf(value);
            } catch (NumberFormatException ex) {
                logger.warn("Illegal value for createProb");
            }
        }

        if (parameter.equals("removeProb")) {
            try {
                removeProb = Float.valueOf(value);
            } catch (NumberFormatException ex) {
                logger.warn("Illegal value for removeProb");
            }
        }

    }
}
