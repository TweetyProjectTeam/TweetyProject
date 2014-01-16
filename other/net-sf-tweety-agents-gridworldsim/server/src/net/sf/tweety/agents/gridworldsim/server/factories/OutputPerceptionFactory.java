package net.sf.tweety.agents.gridworldsim.server.factories;

import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.tweety.agents.gridworldsim.commons.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
import net.sf.tweety.agents.gridworldsim.server.*;

/**
 * This Factory produces a {@code Document} representation of a given {@link GridWorld}.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class OutputPerceptionFactory {

    private static final Logger logger = Logger.getLogger(OutputPerceptionFactory.class);
    private final GridWorld gridWorld;
    private final Agent agent;
    private final Map<Agent, String> internalStates;
    private final boolean observer;

    /**
     * Constructs a new {@code OutputPerceptionFactory} (for observer clients)
     * @param gridWorld the {@link GridWorld} for which a {@code Document} representation should be created
     * @param internalStates the {@code Map} of all internal states that {@link Agent}s told the {@link Server} about
     */
    public OutputPerceptionFactory(GridWorld gridWorld, Map<Agent, String> internalStates) {
        this.gridWorld = gridWorld;
        this.internalStates = internalStates;
        agent = null;
        observer = true;
    }

    /**
     * Constructs a new {@code OutputPerceptionFactory} (for agents)
     * @param gridWorld the {@link GridWorld} for which a {@code Document} representation should be created
     * @param agent the {@link Agent} for whom specific output should be generated (only what he can see)
     */
    public OutputPerceptionFactory(GridWorld gridWorld, Agent agent) {
        this.gridWorld = gridWorld;
        this.agent = agent;
        this.internalStates = null;
        observer = false;
    }

    /* get the (rounded up) Eukledian distance between two coordinates */
    private int euklid(int x0, int y0, int x1, int y1) {
        double distance = Math.sqrt(Math.pow((x0 - x1), 2) + Math.pow((y0 - y1), 2));
        return (int) Math.ceil(distance);
    }

    /**
     * tells if the cell at the specified coordinates is visible to the agent regarding viewrange
     * @param x the x coordinate of the cell in question
     * @param y the y coordinate of the cell in question
     * @return the visibility status
     */
    private boolean isEuklidVisible(int x, int y) {

        /* observer clients always see all cells */
        if (observer) {
            return true;
        }

        /* a view range smaller than 0 means that the agent has unlimited view range */
        if (agent.getViewRange() < 0) {
            return true;
        }

        /* get all numbers needed to calculate the view range as double */
        double viewRange = agent.getViewRange();
        double askX = x;
        double askY = y;
        double currX = agent.getLocation().getX();
        double currY = agent.getLocation().getY();
        double distance = 0d;


        /* an agent can always see the cell in which he is currently located */
        if (currX == askX && currY == askY) {
            return true;
        }

        /* calculate the Eukledian distance between the cell location and the current agent location */
        distance = Math.sqrt(Math.pow((askX - currX), 2) + Math.pow((askY - currY), 2));


        /* if the Eukledian distance is smaller than the view range, the cell is visible, otherwise it is not */
        if (distance <= viewRange) {
            return true;
        } else {
            return false;
        }

    }


    /* calculate the visible cell locations */
    private Collection<GridLocation> getVisibleCellLocations() {
        GridLocation lowerLeft = new GridLocation();
        GridLocation upperRight = new GridLocation();
        Collection<GridLocation> visibleCellLocations = new HashSet<GridLocation>();

        /* get the view range (either from the agent or unlimited view if dealing with an observer client) */
        int viewRange;
        if (observer) {
            viewRange = -1;
        } else {
            viewRange = agent.getViewRange();
        }

        /* if the view range is unlimited, the scope of possibly visible cells is the entire grid world */
        if (viewRange < 0) {
            lowerLeft.setXY(0, 0);
            upperRight.setXY(gridWorld.getXDimension() - 1, gridWorld.getYDimension() - 1);
        } else {
            /**
             * in all other cases calculate the scope of potentially visible cells (so we don't have to do expensive
             * calculations for cells that are not even potentially visible)
             */
            int currentX = agent.getLocation().getX();
            int currentY = agent.getLocation().getY();

            lowerLeft.setXY(currentX - viewRange, currentY - viewRange);
            upperRight.setXY(currentX + viewRange, currentY + viewRange);

            if (lowerLeft.getX() < 0) {
                lowerLeft.setXY(0, lowerLeft.getY());
            }
            if (lowerLeft.getY() < 0) {
                lowerLeft.setXY(lowerLeft.getX(), 0);
            }
            if (upperRight.getX() >= gridWorld.getXDimension()) {
                upperRight.setXY(gridWorld.getXDimension() - 1, upperRight.getY());
            }
            if (upperRight.getY() >= gridWorld.getYDimension()) {
                upperRight.setXY(upperRight.getX(), gridWorld.getYDimension() - 1);
            }
        }

        /* iterate over the scope of potentially visible cells and add the location of cells that are actually visible */
        for (int i = lowerLeft.getX(); i <= upperRight.getX(); i++) {
            for (int j = lowerLeft.getY(); j <= upperRight.getY(); j++) {
                if (isEuklidVisible(i, j)) {
                    visibleCellLocations.add(gridWorld.getGridCells()[i][j].getLocation());
                }
            }
        }

        // reduce the visible cells by the cells being invisible due to obstacles
        if (!observer) {
            obstacleCovering(visibleCellLocations);
        }

        return visibleCellLocations;
    }

    /**
     * Create a new actions XML {@code Document} including the root {@code Element}
     */
    private Element createDocumentWithRootElement(long time) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element gridWorldElement = doc.createElement("gridworld-percept");
        gridWorldElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        gridWorldElement.setAttribute("xsi:noNamespaceSchemaLocation", Constants.PERCEPTION_SCHEMA_REMOTE_LOCATION);
        gridWorldElement.setAttribute("xDimension", Integer.valueOf(gridWorld.getXDimension()).toString());
        gridWorldElement.setAttribute("yDimension", Integer.valueOf(gridWorld.getYDimension()).toString());
        gridWorldElement.setAttribute("time", Long.valueOf(time).toString());
        doc.appendChild(gridWorldElement);
        return gridWorldElement;
    }

    /* add all visible cells with their freeCap to the Document and call the processing of cell contents */
    private void processAllCells(Element gridWorldElement, Collection<GridLocation> visibleCellLocations) {
        Document doc = gridWorldElement.getOwnerDocument();
        Element cellsElement = doc.createElement("cells");
        gridWorldElement.appendChild(cellsElement);


        /* now we iterate over all visible cells */
        for (Iterator<GridLocation> i = visibleCellLocations.iterator(); i.hasNext();) {
            GridLocation loc = i.next();
            Element cell = doc.createElement("cell");
            cell.setAttribute("xPos", Integer.valueOf(loc.getX()).toString());
            cell.setAttribute("yPos", Integer.valueOf(loc.getY()).toString());
            cell.setAttribute("freeCap", HelperFunctions.intToString(gridWorld.getGridCells()[loc.getX()][loc.getY()].getFreeCap()));

            /* process the contents (agents, objects etc.) of the cell */
            processCell(doc, cell, loc);
            cellsElement.appendChild(cell);
        }
    }

    /**
     * create XML output for the content of a call
     */
    private void processCellContents(Element agentsElement, Element objectsElement, Collection<GridObject> currentObjects) {
        Document doc = agentsElement.getOwnerDocument();

        /* iterate over the list of objects (including agents) */
        for (Iterator<GridObject> k = currentObjects.iterator(); k.hasNext();) {
            GridObject currentObject = k.next();

            /* create an <agent> or <object> element depending on the type of the object we are just processing */
            Element currentElement;
            if (currentObject instanceof Agent) {
                currentElement = doc.createElement("agent");
                if (observer) {
                    Agent currentAgent = (Agent) currentObject;
                    currentElement.setAttribute("hearing", HelperFunctions.intToString(currentAgent.getHearing()));
                    currentElement.setAttribute("soundIntensity", HelperFunctions.intToString(currentAgent.getSoundIntensity()));
                    currentElement.setAttribute("priority", HelperFunctions.intToString(currentAgent.getPriority()));
                    currentElement.setAttribute("moveForce", HelperFunctions.intToString(currentAgent.getMoveForce()));
                    processInternalState(currentElement, (Agent) currentObject);
                }
            } else {
                currentElement = doc.createElement("object");
            }

            /* set the name of the object as attribute */
            currentElement.setAttribute("name", currentObject.getName());

            /* set the capacity need of the object as attribute */
            currentElement.setAttribute("capNeed", Integer.valueOf(currentObject.getCapNeed()).toString());

            /* set the free capacity of the object as attribute */
            currentElement.setAttribute("freeCap", HelperFunctions.intToString(currentObject.getFreeCap()));

            if (observer) {
                processInventory(currentElement, currentObject, 0, Integer.MAX_VALUE);
            } else {
                if (currentObject.equals(agent)) {
                    currentElement.setAttribute("isYou", "true");
                    processInventory(currentElement, currentObject, 0, 1);
                }

                /* add inventory of all visible objects which reveal their inventory */
                if (!(currentObject instanceof Agent) && currentObject.isInventoryAccessible()) {
                    processInventory(currentElement, currentObject, 0, 0);
                }
            }

            /* process the properties of GridObjects*/
            processGridObjectProperties(currentElement, currentObject);


            /* append the <agent> or <object> child element to the appropriate <agents> or <objects> parent element */
            if (currentObject instanceof Agent) {
                agentsElement.appendChild(currentElement);
            } else {
                objectsElement.appendChild(currentElement);
            }
        }
    }

    /* add the internal state information for observer clients */
    private void processInternalState(Element currentElement, Agent currentAgent) {
        if (internalStates != null) {
            String internalState = internalStates.get(currentAgent);
            if (internalState != null) {
                Document doc = currentElement.getOwnerDocument();
                Element isElement = doc.createElement("internalstate");
                isElement.setTextContent(internalState);
                currentElement.appendChild(isElement);
            }
        }
    }

    /**
     * create XML output for a given cell
     */
    private void processCell(Document doc, Element cell, GridLocation loc) {
        /* get the GridCell from the given location */
        GridCell currentCell = gridWorld.getGridCells()[loc.getX()][loc.getY()];

        /* get the Collection of objects of this GridCell */
        Collection<GridObject> currentObjects = currentCell.getObjectList();

        /* if the current GridCell has content or an obstacle, we will handle that now */
        if (currentObjects.size() > 0 || currentCell.isWall() || currentCell.isTrench() || currentCell.isCurtain() || currentCell.isFog() || currentCell.isInterference()) {
            if (currentCell.isWall()) {
                cell.setAttribute("wall", "true");
            }
            if (currentCell.isTrench()) {
                cell.setAttribute("trench", "true");
            }
            if (currentCell.isFog()) {
                cell.setAttribute("fog", "true");
            }
            if (currentCell.isCurtain()) {
                cell.setAttribute("curtain", "true");
            }

            if (observer && currentCell.isInterference()) {
                cell.setAttribute("interference", "true");
            }

            Element agentsElement = doc.createElement("agents");
            Element objectsElement = doc.createElement("objects");

            boolean contentVisible = false;

            /* the content of a cell is always visible for an observer client */
            if (observer) {
                contentVisible = true;
            }

            /**
             * if a cell has a curtain or fog and is visible in general (otherwise it would never hit these lines of code)
             * the contents (objects, agents) of the cell should be hidden, if the agent is not currently in that cell
             */
            if (!contentVisible) {
                if ((!currentCell.isFog() && !currentCell.isCurtain())
                        || (agent.getLocation().getX() == loc.getX() && agent.getLocation().getY() == loc.getY())) {
                    contentVisible = true;
                }
            }

            /* create XML for the content of this cell, if the content should be visible */
            if (contentVisible) {
                processCellContents(agentsElement, objectsElement, currentObjects);
            }

            /* add the agents Element only to the document if this cell has at least one Agent */
            if (agentsElement.getChildNodes().getLength() > 0) {
                cell.appendChild(agentsElement);
            }

            /* add the objects Element only to the document if this cell has at least one object */
            if (objectsElement.getChildNodes().getLength() > 0) {
                cell.appendChild(objectsElement);
            }
        }
    }

    /* Process the properties of objects/agents */
    private void processGridObjectProperties(Element objectElement, GridObject currentObject) {
        Collection<String> properties;
        if (!observer && agent!=currentObject) {
            properties = currentObject.getProperties(true);
        } else {
            properties = currentObject.getProperties(false);
        }

        if (properties.isEmpty()) {
            return;
        }

        Document doc = objectElement.getOwnerDocument();
        Element propertiesElement = doc.createElement("properties");
        objectElement.appendChild(propertiesElement);

        for (Iterator<String> i = properties.iterator(); i.hasNext();) {
            Element propertyElement = doc.createElement("property");
            propertyElement.setAttribute("value", i.next());
            propertiesElement.appendChild(propertyElement);
        }
    }

    /**
     * append the inventory information for currentObject to currentElement until a contains graph depth of maxDepth
     */
    private void processInventory(Element currentElement, GridObject currentObject, int currentDepth, int maxDepth) {
        Document doc = currentElement.getOwnerDocument();
        Element currentObjectsSection = doc.createElement("objects");
        if (currentObject.getInventory().size() > 0) {
            currentElement.appendChild(currentObjectsSection);
        }


        for (Iterator<GridObject> i = currentObject.getInventory().iterator(); i.hasNext();) {
            GridObject currentInventoryObject = i.next();

            Element currentInventoryObjectElement = doc.createElement("object");
            currentInventoryObjectElement.setAttribute("name", currentInventoryObject.getName());
            currentInventoryObjectElement.setAttribute("capNeed", Integer.valueOf(currentInventoryObject.getCapNeed()).toString());
            currentInventoryObjectElement.setAttribute("freeCap", HelperFunctions.intToString(currentInventoryObject.getFreeCap()));
            processGridObjectProperties(currentInventoryObjectElement, currentInventoryObject);

            currentObjectsSection.appendChild(currentInventoryObjectElement);

            if (currentDepth < maxDepth && (currentInventoryObject.isInventoryAccessible() || agent == null)) {
                processInventory(currentInventoryObjectElement, currentInventoryObject, currentDepth + 1, maxDepth);
            }
        }
    }

    /**
     * Check if a visibility blocking obstacle (visi==true) or interference (visi==false) is on the discrete straight line
     * between (x0,y0) and (x1,y1). If "omit target" is set, an obstacle at the target point of the line will not make
     * this method return false. This algorithm has been adapted from the lecture notes of Leonard McMillan (University of
     * North Carolina) about line drawing algorithms.
     */
    private boolean bresenham(int x0, int y0, int x1, int y1, boolean visi, boolean omitTarget) {
        int bx1 = x1;
        int by1 = y1;


        int dy = y1 - y0;
        int dx = x1 - x0;
        int stepx, stepy;

        if (dy < 0) {
            dy = -dy;
            stepy = -1;
        } else {
            stepy = 1;
        }
        if (dx < 0) {
            dx = -dx;
            stepx = -1;
        } else {
            stepx = 1;
        }
        dy <<= 1;
        dx <<= 1;

        if (dx > dy) {
            int fraction = dy - (dx >> 1);
            while (x0 != x1) {
                if (fraction >= 0) {
                    y0 += stepy;
                    fraction -= dx;
                }
                x0 += stepx;
                fraction += dy;

                if ((visi && !gridWorld.getGridCells()[x0][y0].isVisible())
                        || (!visi && gridWorld.getGridCells()[x0][y0].isInterference())) {
                    if (omitTarget && x0 == bx1 && y0 == by1) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            int fraction = dx - (dy >> 1);
            while (y0 != y1) {
                if (fraction >= 0) {
                    x0 += stepx;
                    fraction -= dy;
                }
                y0 += stepy;
                fraction += dx;
                if ((visi && !gridWorld.getGridCells()[x0][y0].isVisible())
                        || (!visi && gridWorld.getGridCells()[x0][y0].isInterference())) {
                    if (omitTarget && x0 == bx1 && y0 == by1) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * remove all cell locations from the Collectiom of visible cell locations which are invisible due to being covered by an obstacle
     */
    private void obstacleCovering(Collection<GridLocation> visibleCellLocations) {
        GridLocation agentLoc = agent.getLocation();
        for (Iterator<GridLocation> i = visibleCellLocations.iterator(); i.hasNext();) {
            GridLocation currentLoc = i.next();
            boolean visible = bresenham(agentLoc.getX(), agentLoc.getY(), currentLoc.getX(), currentLoc.getY(), true, true);
            if (!visible) {
                i.remove();
            }
        }
    }

    /* process the messages which this agent can hear (or all messages in case of an observer client) */
    private void processMessages(Element gridWorldElement) {
        Document doc = gridWorldElement.getOwnerDocument();
        Element messagesElement = doc.createElement("messages");

        /* private messages for the agent or all private messages for the observer*/
        Collection<PrivateMessage> privMsgs;
        if (observer) {
            privMsgs = gridWorld.getPrivMessages();
        } else {
            privMsgs = agent.getMessages();
        }

        for (Iterator<PrivateMessage> i = privMsgs.iterator(); i.hasNext();) {
            PrivateMessage currentMessage = i.next();
            Element newElement = doc.createElement("message");
            newElement.setAttribute("sender", currentMessage.getSender().getName());
            newElement.setAttribute("isPublic", "false");
            if (observer) { // observers also see the receiver of the private message (not required for agents)
                newElement.setAttribute("receiver", currentMessage.getReceiver().getName());
            }
            newElement.appendChild(doc.createCDATASection(currentMessage.getMessage()));
            messagesElement.appendChild(newElement);
        }
        if (agent != null) {
            agent.clearMessages();
        }

        /* public messages */
        Collection<PublicMessage> messages = gridWorld.getPublicMessages();

        for (Iterator<PublicMessage> i = messages.iterator(); i.hasNext();) {
            PublicMessage currentMessage = i.next();

            boolean receivable = true;
            boolean senderVisi = true;
            if (agent != null) {
                Agent sender = currentMessage.getSender();
                GridLocation senderLoc = gridWorld.getGridObjectLocation(sender);
                GridLocation receiverLoc = agent.getLocation();

                /* check receivable by Euklid and hearing/sound intensity*/
                if (sender.getSoundIntensity() != -1 && agent.getHearing() != -1) {
                    int distance = euklid(senderLoc.getX(), senderLoc.getY(), receiverLoc.getX(), receiverLoc.getY());
                    if (distance > currentMessage.getSender().getSoundIntensity() + agent.getHearing()) {
                        receivable = false;
                    }
                }

                /* if still receivable so far, check for interferences between receiver and sender */
                if (receivable) {
                    receivable = bresenham(senderLoc.getX(), senderLoc.getY(), receiverLoc.getX(), receiverLoc.getY(), false, false);
                }

                /* if an agent is required to see the sender to know the origin of the message, this will be checked now */
                if (receivable && gridWorld.isMsgSightReq()) {
                    senderVisi = bresenham(receiverLoc.getX(), receiverLoc.getY(), senderLoc.getX(), senderLoc.getY(), true, false);
                }
            }

            /* create XML */
            if (receivable) {
                Element newElement = doc.createElement("message");
                if (senderVisi) {
                    newElement.setAttribute("sender", currentMessage.getSender().getName());
                }
                newElement.setAttribute("isPublic", "true");
                newElement.appendChild(doc.createCDATASection(currentMessage.getMessage()));
                messagesElement.appendChild(newElement);
            }
        }

        if (messagesElement.hasChildNodes()) {
            gridWorldElement.appendChild(messagesElement);
        }

    }

    /**
     * Get the {@code Document} representation of this {@code OutputPerceptionFactory}'s {@link GridWorld}
     * @param time the current time (state number)
     * @return the desired {@code Document} representation
     * @throws ParserConfigurationException thrown when there was a problem configuring the parser
     */
    public Document getDocument(long time) throws ParserConfigurationException {

        /* get the Collection of locations of visible cells */
        Collection<GridLocation> visibleCellLocations = getVisibleCellLocations();

        logger.debug("Staring to create a new perception Document");

        /* create a new Document and get its root element */
        Element gridWorldElement = createDocumentWithRootElement(time);
        Document doc = gridWorldElement.getOwnerDocument();

        /* process the cells */
        processAllCells(gridWorldElement, visibleCellLocations);
        processMessages(gridWorldElement);


        logger.debug("Finished creating a new perception Document");
        return doc;
    }
}
