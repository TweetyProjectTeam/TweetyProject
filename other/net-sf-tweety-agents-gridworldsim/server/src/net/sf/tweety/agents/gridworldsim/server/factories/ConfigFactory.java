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
package net.sf.tweety.agents.gridworldsim.server.factories;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import net.sf.tweety.agents.gridworldsim.commons.*;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.xpath.XPathConstants;
import java.util.Map;
import net.sf.tweety.agents.gridworldsim.server.*;
import net.sf.tweety.agents.gridworldsim.server.statetrans.StateTransRule;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Collection;

/**
 * Objects made from this class parse the server configuration file and create the initial GridWorld and
 * configuration settings according to it.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ConfigFactory {

    private static final Logger logger = Logger.getLogger(ConfigFactory.class);
    private final Document doc;
    private final XPathFactory xPathFactory;

    /**
     * Constructs a new {@code ConfigFactory}
     * @param file the location of the configuration file to parse
     * @throws FileNotFoundException thrown if the XML file was not found
     * @throws IOException thrown in case of an I/O error
     * @throws ParserConfigurationException  thrown if there was a problem configuring the parser
     * @throws SAXException thrown if there was a problem while parsing
     */
    public ConfigFactory(String file) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        xPathFactory = XPathFactory.newInstance();
        doc = HelperFunctions.fileToDocument(file, SchemaLoader.getServer());
    }

    /**
     * Get all login data for observer clients.
     * @return all login data for observer client
     * @throws XPathExpressionException thrown if there was an invalid XPath expression
     */
    public Collection<ObserverLogin> getObserverLoginData() throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        NodeList observers = (NodeList) xPath.evaluate("/gridworld/observers/observer", doc, XPathConstants.NODESET);
        Collection<ObserverLogin> observerLoginData = new HashSet<ObserverLogin>();

        for (int i = 1; i < observers.getLength() + 1; i++) {
            String currentName = xPath.evaluate("/gridworld/observers/observer[" + i + "]/@name", doc);
            String currentPassword = xPath.evaluate("/gridworld/observers/observer[" + i + "]/@password", doc);

            if (currentPassword == null || currentPassword.equals("")) {
                observerLoginData.add(new ObserverLogin(currentName));
            } else {
                observerLoginData.add(new ObserverLogin(currentName, currentPassword));
            }
        }
        return observerLoginData;
    }

    /**
     * Get the path where to put debug output.
     * @return the path where to put debug output
     * @throws XPathExpressionException thrown if there was an invalid XPath expression
     */
    public String getDebugPath() throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        String debug = xPath.evaluate("/gridworld/server/debug/@path", doc);
        return debug;
    }

    /**
     * Tells if debug log level is enabled.
     * @return true if debug log level is enable, false if info log level is enabled
     * @throws XPathExpressionException thrown if there was an invalid XPath expression
     */
    public boolean isDebug() throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        if (xPath.evaluate("/gridworld/server/debug/@level", doc).equals("debug")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tells if all incoming and outgoing XML data should be logged to file.
     * @return true if all incoming and outgoing XML data should be logged to file
     * @throws XPathExpressionException thrown if there was an invalid XPath expression
     */
    public boolean logData() throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        return Boolean.valueOf(xPath.evaluate("/gridworld/server/debug/@data", doc));
    }

     /**
     * Tells if all server messages should be logged to file ({@code gridworldsim.log} in the debug path).
     * @return true if all server messages should be logged to file, false otherwise
     * @throws XPathExpressionException thrown if there was an invalid XPath expression
     */
    public boolean logFile() throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        return Boolean.valueOf(xPath.evaluate("/gridworld/server/debug/@filelog", doc));
    }

    /**
     * Get the initial {@link GridWorld} according to the configuration file.
     * @return the initial {@link GridWorld} according to the configuration file
     * @throws XPathExpressionException thrown when an error occurred with an XPath expression
     */
    public GridWorld getGridWorld() throws XPathExpressionException {
        logger.debug("Starting to create initial GridWorld");
        XPath xPath = xPathFactory.newXPath();

        /* get dimensions */
        int xDimension = Integer.valueOf(xPath.evaluate("/gridworld/@xDimension", doc));
        int yDimension = Integer.valueOf(xPath.evaluate("/gridworld/@yDimension", doc));

        /* get necessity for an agent to see the sender of a public message to know its origin*/
        String msgSightReqStr = xPath.evaluate("/gridworld/@msgsightreq", doc);
        boolean msgSightReq;
        if (msgSightReqStr == null || msgSightReqStr.equals("")) {
            msgSightReq = true;
        } else {
            msgSightReq = Boolean.valueOf(msgSightReqStr);
        }

        /* get the execution mode*/
        Element execModeString = (Element) xPath.evaluate("/gridworld/server/execmode/*[1]", doc, XPathConstants.NODE);
        String selectedExecModeString = execModeString.getNodeName();

        int execTimeOut = 0;
        if (selectedExecModeString.equals("fixed")) {
            execTimeOut = Integer.valueOf(execModeString.getAttribute("interval"));
        } else if (selectedExecModeString.equals("waitforall") && !execModeString.getAttribute("timeout").equals("")) {
            execTimeOut = Integer.valueOf(execModeString.getAttribute("timeout"));
        }

        ExecutionMode execMode = new ExecutionMode(selectedExecModeString, execTimeOut);

        /* get the default free cap for cells */
        int gridCellFreeCapDefault = -1;
        try {
            gridCellFreeCapDefault = Integer.valueOf(xPath.evaluate("/gridworld/cellcapacities/@default", doc));
        } catch (NumberFormatException ex) {
            // really nothing to do here :)
        }

        /* create the GridWorld and setup obstacles, cell capacities and contained objects */
        GridWorld gridWorld = new GridWorld(xDimension, yDimension, gridCellFreeCapDefault, getRules(), msgSightReq, execMode);
        setupObstacles(gridWorld);
        setupCellCapacities(gridWorld);
        setupObjectFactories(gridWorld);
        logger.debug("Finished creating the initial GridWorld");
        return gridWorld;
    }

    /* setup the free capacities of cells for which the free capacity differs from the default free capacity */
    private void setupCellCapacities(GridWorld gridWorld) throws XPathExpressionException {
        logger.debug("Setting up cell capacities");
        XPath xPath = xPathFactory.newXPath();
        NodeList cellNodes = (NodeList) xPath.evaluate("/gridworld/cellcapacities/cell", doc, XPathConstants.NODESET);

        for (int i = 1; i < cellNodes.getLength() + 1; i++) {
            int xPos = Integer.valueOf(xPath.evaluate("/gridworld/cellcapacities/cell[" + i + "]/@xPos", doc));
            int yPos = Integer.valueOf(xPath.evaluate("/gridworld/cellcapacities/cell[" + i + "]/@yPos", doc));
            int freeCap = HelperFunctions.unbStringToInteger(xPath.evaluate("/gridworld/cellcapacities/cell[" + i + "]/@capacity", doc));

            if (xPos < gridWorld.getXDimension() && yPos < gridWorld.getYDimension()) {
                gridWorld.getGridCells()[xPos][yPos].setInitCap(freeCap);
            } else {
                logger.warn("You tried to specify the free cell capacity of a cell that doesn't exist, ignoring");
            }
        }
        logger.debug("Finished setting up cell capacities");
    }


    /* get the state transition rules from the XML document */
    private List<StateTransRule> getRules() throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        NodeList ruleNodes = (NodeList) xPath.evaluate("/gridworld/rules/rule", doc, XPathConstants.NODESET);
        List<StateTransRule> rules = new LinkedList<StateTransRule>();

        for (int i = 1; i < ruleNodes.getLength() + 1; i++) {
            String className = xPath.evaluate("/gridworld/rules/rule[" + i + "]/@class", doc);
            try {
                StateTransRule currentRule = (StateTransRule) Class.forName(className).newInstance();
                NodeList parameterRules = (NodeList) xPath.evaluate("/gridworld/rules/rule[" + i + "]/parameter", doc, XPathConstants.NODESET);

                for (int j = 1; j < parameterRules.getLength() + 1; j++) {
                    String currentParameterName = xPath.evaluate("/gridworld/rules/rule[" + i + "]/parameter[" + j + "]/@paraName", doc);
                    String currentParameterValue = xPath.evaluate("/gridworld/rules/rule[" + i + "]/parameter[" + j + "]/@value", doc);
                    currentRule.setParameter(currentParameterName, currentParameterValue);
                }
                rules.add(currentRule);
            } catch (InstantiationException ex) {
                logger.warn("ConfigFactory couldn't instantiate a rule class", ex);
            } catch (IllegalAccessException ex) {
                logger.warn("ConfigFactory trying to instantiate a rule class caused an illegal access exception", ex);
            } catch (ClassNotFoundException ex) {
                logger.warn("ConfigFactory couldn't find a rule class", ex);
            } catch (XPathExpressionException ex) {
                logger.warn("ConfigFactory had problems with an XPath expression", ex);
            }
        }
        return rules;
    }

    /**
     * Get the port number this server should use for listening to agent connections.
     * @return the port number this server should use for listening to agent connections
     * @throws XPathExpressionException thrown when an error occurred with the XPath expression
     */
    public int getAgentPort() throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        return Integer.valueOf(xPath.evaluate("/gridworld/server/@agentPort", doc));
    }

    /**
     * Get the port number this server should use for listening to observer client connections.
     * @return the port number this server should use for listening to observer client connections
     * @throws XPathExpressionException thrown when an error occurred with the XPath expression
     */
    public int getObserverPort() throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        return Integer.valueOf(xPath.evaluate("/gridworld/server/@observerPort", doc));
    }


    /* do the actual work of setting up the objects */
    private void performSetupObjects(GridWorld gridWorld, NodeList objectNodes, Map<String, GridObjectFactory> goFactories) {
        Queue<InitContainsContainer> objectQueue = new LinkedList<InitContainsContainer>();

        /* set up top-level objects (those with a location on the Grid) */
        for (int i = 0; i < objectNodes.getLength(); i++) {
            Element currentElement = (Element) objectNodes.item(i);
            int xPos = Integer.valueOf(currentElement.getAttribute("xPos"));
            int yPos = Integer.valueOf(currentElement.getAttribute("yPos"));
            GridObjectLocation newLocation = new GridObjectLocation(xPos, yPos);

            GridObject newObject = FactoryHelper.setupSingleObject(currentElement, goFactories);
            boolean success = gridWorld.addGridObject(newObject, newLocation);

            if (success) {
                NodeList children = currentElement.getChildNodes();
                Element containsElement = HelperFunctions.findElement(children, "contains");

                if (containsElement == null) {
                    continue;
                }

                NodeList instances = containsElement.getChildNodes();
                for (int j = 0; j < instances.getLength(); j++) {
                    if (instances.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) instances.item(j);
                        if (childElement.getNodeName().equals("instance")) {
                            InitContainsContainer childContainer = new InitContainsContainer(childElement, newObject, newObject);
                            objectQueue.add(childContainer);
                        }
                    }
                }
            } else {
                logger.warn("Problem for cell (" + xPos + "," + yPos + ") while adding object " + newObject.getName() + ". Not enough free cell capacity for the capacity need of the object? Ignoring this object.");
            }

        }

        /* setup contained objects */
        FactoryHelper.addInventory(objectQueue, goFactories, gridWorld);

    }

    /* set up the GridObjectFactories according to the specification from the XML */
    private void setupObjectFactories(GridWorld gridWorld) throws XPathExpressionException {
        logger.debug("Starting to setup objects");
        XPath xPath = xPathFactory.newXPath();

        /* this Map will map GridObjects (identified by their names) to the factory providing GridObjects of that type */
        Map<String, GridObjectFactory> goFactories = new HashMap<String, GridObjectFactory>();

        /* create a GridObjectFactory for every GridObjectType type and put it to the Map */
        NodeList typeNodes = (NodeList) xPath.evaluate("/gridworld/objects/types/type", doc, XPathConstants.NODESET);

        /* set the general default */
        int freeCap = -1;
        int capNeed = 0;

        /* starting parsing */
        capNeed = HelperFunctions.updateIntFromString(capNeed, xPath.evaluate("/gridworld/objects/types/@capNeed", doc));
        freeCap = HelperFunctions.updateIntFromString(freeCap, xPath.evaluate("/gridworld/objects/types/@freeCap", doc));

        for (int i = 1; i < typeNodes.getLength() + 1; i++) {
            int currentFreeCap = freeCap;
            int currentCapNeed = capNeed;

            String name = xPath.evaluate("/gridworld/objects/types/type[" + i + "]/@typeName", doc);
            String className = xPath.evaluate("/gridworld/objects/types/type[" + i + "]/@class", doc);

            currentCapNeed = HelperFunctions.updateIntFromString(currentCapNeed, xPath.evaluate("/gridworld/objects/types/type[" + i + "]/@capNeed", doc));
            currentFreeCap = HelperFunctions.updateIntFromString(currentFreeCap, xPath.evaluate("/gridworld/objects/types/type[" + i + "]/@freeCap", doc));

            Collection<GridObjectParameter> parameters = new HashSet<GridObjectParameter>();
            NodeList parameterNodes = (NodeList) xPath.evaluate("/gridworld/objects/types/type[" + i + "]/parameters/parameter", doc, XPathConstants.NODESET);
            for (int j = 1; j < parameterNodes.getLength() + 1; j++) {
                String parameterName = xPath.evaluate("/gridworld/objects/types/type[" + i + "]/parameters/parameter[" + j + "]/@paraName", doc);
                String parameterValue = xPath.evaluate("/gridworld/objects/types/type[" + i + "]/parameters/parameter[" + j + "]/@value", doc);
                parameters.add(new GridObjectParameter(parameterName, parameterValue));
            }

            GridObjectFactory newFactory = new GridObjectFactory(name, gridWorld, currentCapNeed, currentFreeCap, parameters);
            if (className != null && !className.equals("")) {
                newFactory.setClass(className);
            }

            goFactories.put(name, newFactory);
        }

        /* tell the AgentFactory about the Map of GridObjectFactories */
        AgentFactory.setGoFactories(goFactories);

        /* now we deal with the actual object instances (the <instances> section of the XML) */
        NodeList instanceNodes = (NodeList) xPath.evaluate("/gridworld/objects/instances/instance", doc, XPathConstants.NODESET);

        /* set up the actual objects */
        performSetupObjects(gridWorld, instanceNodes, goFactories);

        logger.debug("Finished setting up objects");
    }

    /**
     * this method parses the configuration for obstacle declarations and sets the GridCells' obstacle status accordingly
     */
    private void setupObstacles(GridWorld gridWorld) throws XPathExpressionException {
        logger.debug("Starting to setup obstacles");
        XPath xPath = xPathFactory.newXPath();
        NodeList obstacleNodes = (NodeList) xPath.evaluate("/gridworld/obstacles/*", doc, XPathConstants.NODESET);

        for (int i = 1; i < obstacleNodes.getLength() + 1; i++) {
            String obstacleType = ((Node) xPath.evaluate("/gridworld/obstacles/*[" + i + "]", doc, XPathConstants.NODE)).getNodeName();

            boolean interfering = Boolean.valueOf(xPath.evaluate("/gridworld/obstacles/" + obstacleType + "/@interfering", doc));
            NodeList obstacles = (NodeList) xPath.evaluate("/gridworld/obstacles/" + obstacleType + "/*", doc, XPathConstants.NODESET);

            for (int j = 1; j < obstacles.getLength() + 1; j++) {
                int startx = Integer.valueOf(xPath.evaluate("/gridworld/obstacles/" + obstacleType + "/*[" + j + "]/@xPos", doc));
                int starty = Integer.valueOf(xPath.evaluate("/gridworld/obstacles/" + obstacleType + "/*[" + j + "]/@yPos", doc));

                // these defaults will be used for <single> obstacles, in which case length and direction will not be overwritten
                int length = 1;
                String direction = Constants.NORTH;

                // if we have a <multi> obstacle declaration, overwrite length and direction
                Node currentObstacleNode = (Node) xPath.evaluate("/gridworld/obstacles/" + obstacleType + "/*[" + j + "]", doc, XPathConstants.NODE);
                if (currentObstacleNode.getNodeName().equals("multi")) {
                    length = Integer.valueOf(xPath.evaluate("/gridworld/obstacles/" + obstacleType + "/multi[" + j + "]/@length", doc));
                    direction = xPath.evaluate("/gridworld/obstacles/" + obstacleType + "/multi[" + j + "]/@direction", doc);
                }

                if (checkObstacleSanity(gridWorld, startx, starty, length, direction, obstacleType)) {
                    performObstacleUpdate(gridWorld, startx, starty, length, direction, obstacleType, interfering);
                }
            }
        }

        logger.debug("Finished setting up obstacles");
    }

    /**
     * update the obstacle status of the affected {@code GridCell}s for an obstacle
     */
    private void performObstacleUpdate(GridWorld gridWorld, int startx, int starty, int length, String direction, String obstacleType, boolean interfering) {
        GridCell[][] cells = gridWorld.getGridCells();

        if (direction.equals(Constants.NORTH)) {
            for (int i = starty; i < starty + length; i++) {
                setGridCellObstacle(cells[startx][i], obstacleType, interfering);
            }
        }

        if (direction.equals(Constants.SOUTH)) {
            for (int i = starty; i > starty - length; i--) {
                setGridCellObstacle(cells[startx][i], obstacleType, interfering);
            }
        }

        if (direction.equals(Constants.EAST)) {
            for (int i = startx; i < startx + length; i++) {
                setGridCellObstacle(cells[i][starty], obstacleType, interfering);
            }
        }

        if (direction.equals(Constants.WEST)) {
            for (int i = startx; i > startx - length; i--) {
                setGridCellObstacle(cells[i][starty], obstacleType, interfering);
            }
        }
    }

    /**
     * sets up a GridCell to contain the specified obstacle type
     */
    private void setGridCellObstacle(GridCell cell, String obstacleType, boolean interfering) {
        if (obstacleType.equals("walls")) {
            cell.setWall(true);
        }
        if (obstacleType.equals("trenches")) {
            cell.setTrench(true);
        }
        if (obstacleType.equals("curtains")) {
            cell.setCurtain(true);
        }
        if (obstacleType.equals("interferences") || interfering) {
            cell.setInterference(true);
        }
    }

    /**
     * Check if a given obstacle is sane, i.e. not falling off the grid
     */
    private boolean checkObstacleSanity(GridWorld gridWorld, int startx, int starty, int length, String direction, String obstacleType) {

        if (startx < 0 || startx > gridWorld.getXDimension() - 1) {
            logger.warn(obstacleType + " starting x value " + startx + " outside the grid - ignoring this obstacle");
            return false;
        }
        if (starty < 0 || starty > gridWorld.getYDimension() - 1) {
            logger.warn(obstacleType + " starting y value " + starty + " outside the grid - ignoring this obstsacle");
            return false;
        }

        if (direction.equals(Constants.NORTH) && starty + length > gridWorld.getYDimension()) {
            logger.warn("North facing " + obstacleType + " (x=" + startx + ", y=" + starty + ", length=" + length + ") would leave the grid, ignoring this obstacle");
            return false;
        }


        if (direction.equals(Constants.SOUTH) && starty - length <= 0) {
            logger.warn("South facing " + obstacleType + " (x=" + startx + ", y=" + starty + ", length=" + length + ") would leave the grid, ignoring this obstacle");
            return false;
        }


        if (direction.equals(Constants.WEST) && startx - length <= 0) {
            logger.warn("West facing" + obstacleType + " obstacle (x=" + startx + ", y=" + starty + ", length=" + length + ") would leave the grid, ignoring this obstacle");
            return false;
        }

        if (direction.equals(Constants.EAST) && startx + length > gridWorld.getXDimension()) {
            logger.warn("East facing " + obstacleType + " (x=" + startx + ", y=" + starty + ", length=" + length + ") would leave the grid, ignoring this obstacle");
            return false;
        }
        return true;
    }
}
