package net.sf.tweety.agents.gridworldsim.serverapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashSet;
import net.sf.tweety.agents.gridworldsim.serverapi.perceptions.*;
import javax.xml.xpath.XPathExpressionException;
import org.apache.log4j.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import net.sf.tweety.agents.gridworldsim.commons.HelperFunctions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.BASE64Decoder;

/**
 * This Factory produces a {@code CridWorldPerception} given a received {@code Document}
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class InputPerceptionFactory {

    private static final Logger logger = Logger.getLogger(InputPerceptionFactory.class);
    private final Document doc;
    private final XPathFactory xPathFactory;

    /**
     * Constructs a new {@code InputPerceptionFactory}
     * @param doc the {@code Document} to parse
     */
    public InputPerceptionFactory(Document doc) {
        this.doc = doc;
        xPathFactory = XPathFactory.newInstance();
    }

    /**
     * processes a cell from the XML document including objects, walls etc.
     * @param gwPercept the {@code GridWorldPerception} to which new cell information should be added from the XML {@code Document}
     * @param currentElement the XML {@code Element} representing the grid cell to process
     */
    private void processCell(GridWorldPerception gwPercept, Element currentElement) {
        /* get the x and y coordinates and free capacity of the cell found in the XML document */
        int xPos = Integer.valueOf(currentElement.getAttribute("xPos"));
        int yPos = Integer.valueOf(currentElement.getAttribute("yPos"));
        int freeCap = HelperFunctions.unbStringToInteger(currentElement.getAttribute("freeCap"));

        /* get the current cell */
        GridCellPerception currentCell = gwPercept.getGridCellPerceptions()[xPos][yPos];

        /* set the current cell as visible */
        currentCell.setVisible(true);

        /* set the free capacity of that cell */
        currentCell.setFreeCap(freeCap);

        /* set the wall status of that cell */
        boolean isWall = Boolean.parseBoolean(currentElement.getAttribute("wall"));
        currentCell.setWall(isWall);

        /* set the trench status of that cell */
        boolean isTrench = Boolean.parseBoolean(currentElement.getAttribute("trench"));
        currentCell.setTrench(isTrench);

        /* set the curtain status of that cell */
        boolean isCurtain = Boolean.parseBoolean(currentElement.getAttribute("curtain"));
        currentCell.setCurtain(isCurtain);

        /* set the fog status of that cell */
        boolean isFog = Boolean.parseBoolean(currentElement.getAttribute("fog"));
        currentCell.setFog(isFog);

        /* set the interference status of that cell */
        boolean isInterference = Boolean.parseBoolean(currentElement.getAttribute("interference"));
        currentCell.setInterference(isInterference);

        /* create GridObjectPerceptions for the GridCellPerception for every object found in the XML document for that cell */
        NodeList childNodes = currentElement.getChildNodes();
        processGridObjects(gwPercept, childNodes, currentCell, false);

        /* create GridObjectPerceptions for the GridCellPerception for every agent found in the XML document for that cell */
        processGridObjects(gwPercept, childNodes, currentCell, true);
    }

    /* creates an int representation of a String translating -1 to "unbounded" */
    private int unbStringToInt(String str) {
        if (str == null || str.equals("unbounded")) {
            return -1;
        } else {
            return Integer.valueOf(str);
        }
    }

    /**
     * Process objects or agents of a grid cell.
     */
    private void processGridObjects(GridWorldPerception gwPercept, NodeList childNodes, GridCellPerception currentCell, boolean isAgent) {
        if (childNodes.getLength() == 0) {
            return;
        }

        Element objectsNode = null;
        if (isAgent) {
            objectsNode = HelperFunctions.findElement(childNodes, "agents");
        } else {
            objectsNode = HelperFunctions.findElement(childNodes, "objects");
        }
        if (objectsNode == null) {
            return;
        }

        NodeList objectNodes = objectsNode.getChildNodes();

        for (int j = 0; j < objectNodes.getLength(); j++) {
            Node currentNode = objectNodes.item(j);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) currentNode;

                String name = currentElement.getAttribute("name");
                String capNeed = currentElement.getAttribute("capNeed");
                int freeCap = unbStringToInt(currentElement.getAttribute("freeCap"));


                Collection<String> properties = getGridObjectProperties(currentElement);


                GridObjectPerception obj;
                if (isAgent) {
                    boolean isSelf = Boolean.valueOf(currentElement.getAttribute(("isYou")));
                    Integer hearing = HelperFunctions.unbStringToInteger(currentElement.getAttribute("hearing"));
                    Integer soundIntensity = HelperFunctions.unbStringToInteger(currentElement.getAttribute("soundIntensity"));
                    Integer priority = HelperFunctions.unbStringToInteger(currentElement.getAttribute("priority"));
                    Integer moveForce = HelperFunctions.unbStringToInteger(currentElement.getAttribute("moveForce"));
                    obj = new AgentPerception(name, Integer.valueOf(capNeed), freeCap, hearing, soundIntensity, properties, isSelf, priority, moveForce);

                    String internalState = getInternalState(currentElement);
                    if (internalState!=null) {
                        ((AgentPerception)obj).setInternalState(internalState);
                    }

                    boolean isMe = Boolean.valueOf(currentElement.getAttribute("isYou"));
                    if (isMe) {
                        gwPercept.setMyAgent((AgentPerception) obj, currentCell.getxPos(), currentCell.getyPos());
                    }
                } else {
                    obj = new GridObjectPerception(name, Integer.valueOf(capNeed), Integer.valueOf(freeCap), properties);
                }
                NodeList newChildNodes = currentElement.getChildNodes();
                processInventory(gwPercept, obj, newChildNodes);
                gwPercept.addToCell(currentCell, obj);
            }
        }
    }


    /* Get the internal state of an agent */
    private String getInternalState(Element objectElement) {
        Element internalStateElement = HelperFunctions.findElement(objectElement.getChildNodes(), "internalstate");
        if (internalStateElement != null) {
            return internalStateElement.getTextContent();
        } else {
            return null;
        }
    }

    /* Get the properties of a GridObject given its XML element */
    private Collection<String> getGridObjectProperties(Element objectElement) {
        Collection<String> properties = new HashSet<String>();
        Element propertiesElement = HelperFunctions.findElement(objectElement.getChildNodes(), "properties");

        if (propertiesElement == null) {
            return properties;
        }

        NodeList propertyNodes = propertiesElement.getChildNodes();
        for (int i = 0; i < propertyNodes.getLength(); i++) {
            Node currentNode = propertyNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) currentNode;
                properties.add(currentElement.getAttribute("value"));
            }
        }

        return properties;
    }

    /**
     * process the inventory of an object or agent
     */
    private void processInventory(GridWorldPerception gwPercept, GridObjectPerception currentObject, NodeList childNodes) {
        NodeList inventoryNodes = null;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node currentNode = childNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) currentNode;
                if (currentElement.getNodeName().equals("objects")) {
                    inventoryNodes = currentElement.getChildNodes();
                    break;
                }
            }
        }

        if (inventoryNodes == null) {
            return;
        }


        for (int i = 0; i < inventoryNodes.getLength(); i++) {
            Node currentNode = inventoryNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) currentNode;
                if (currentNode.getNodeName().equals("object")) {

                    String name = currentElement.getAttribute("name");
                    String capNeed = currentElement.getAttribute("capNeed");
                    String freeCap = currentElement.getAttribute("freeCap");
                    if (freeCap.equals("unbounded")) {
                        freeCap = "-1";
                    }

                    Collection<String> properties = getGridObjectProperties(currentElement);

                    GridObjectPerception child = new GridObjectPerception(name, Integer.valueOf(capNeed), Integer.valueOf(freeCap), properties);
                    gwPercept.addToInventory(currentObject, child);

                    /*  recurse */
                    NodeList childInventoryNodes = currentElement.getChildNodes();
                    processInventory(gwPercept, child, childInventoryNodes);
                }
            }
        }
    }

    /**
     * Process an incoming message given its XML element. First it is tried if a Java object can be created by performing
     * BASE64 decoding and trying to deserialize the decoded message. If this succeeds, the MessagePerception will contain
     * this Java object, if not the content of the message is put verbatim into a String, which becomes the object of the MessagePerception.
     */
    private void processMessage(GridWorldPerception gwPercept, Element currentElement) {
        String sender = currentElement.getAttribute("sender");
        String receiver = currentElement.getAttribute("receiver");
        boolean isPublic = Boolean.valueOf(currentElement.getAttribute("isPublic"));
        String message = currentElement.getTextContent();
        BASE64Decoder decoder = new BASE64Decoder();
        Object messageObject;

        try {
            ByteArrayInputStream bios = new ByteArrayInputStream(decoder.decodeBuffer(message));
            ObjectInputStream ois = new ObjectInputStream(bios);
            messageObject = ois.readObject();
            logger.debug("Message contained a BASE64 encoded serialized Java object of type "+messageObject.getClass().getName());
        } catch (IOException ex) {
            logger.debug("Message content is not a BASE64 encoded serialized Java object. Putting the message content into a String.");
            messageObject=message;
        } catch (ClassNotFoundException ex) {
            logger.debug("Message content is not a BASE64 encoded serialized Java object. Putting the message content into a String.");
            messageObject=message;
        }

        gwPercept.addMessage(new MessagePerception(sender, receiver, messageObject, isPublic));

    }

    /**
     * Get the {@link GridWorldPerception} created from the {@code Document}
     * @return the {@link GridWorldPerception} created from the {@code Document}
     */
    public GridWorldPerception getGridWorldPerception() {
        logger.debug("Starting to parse a Document");
        XPath xPath = xPathFactory.newXPath();

        /* initialize the GridWorldPerception */
        Element currentElement = doc.getDocumentElement();
        int xDimension = Integer.valueOf(currentElement.getAttribute("xDimension"));
        int yDimension = Integer.valueOf(currentElement.getAttribute("yDimension"));
        long time = Long.valueOf(currentElement.getAttribute("time"));
        GridWorldPerception gwPercept = new GridWorldPerception(xDimension, yDimension, time);

        /* iterate over all cells and messages */
        NodeList cellNodes;
        NodeList messageNodes;
        try {
            cellNodes = (NodeList) xPath.evaluate("/gridworld-percept/cells/cell", doc, XPathConstants.NODESET);
            messageNodes = (NodeList) xPath.evaluate("/gridworld-percept/messages/message", doc, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            logger.warn("Problem with the XPath", ex);
            return null;
        }

        for (int i = 0; i < cellNodes.getLength(); i++) {
            Node currentNode = cellNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                currentElement = (Element) currentNode;
                processCell(gwPercept, currentElement);
            }
        }

        for (int i = 0; i < messageNodes.getLength(); i++) {
            Node currentNode = messageNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                currentElement = (Element) currentNode;
                processMessage(gwPercept, currentElement);
            }
        }

        logger.debug("Finished parsing a Document");
        return gwPercept;
    }
}
