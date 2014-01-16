package net.sf.tweety.agents.gridworldsim.server.factories;

import net.sf.tweety.agents.gridworldsim.server.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import net.sf.tweety.agents.gridworldsim.commons.*;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Objects made from this class create {@link Agent} objects according to the
 * XML server configuration file
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class AgentFactory {

    private static final Logger logger = Logger.getLogger(AgentFactory.class);
    private Document doc;
    private final XPathFactory xPathFactory;
    private final GridWorld gridWorld;
    private int openAgentNo;
    private static Map<String, GridObjectFactory> goFactories;
    private final Map<Agent, Element> inventoryElements;

    /**
     * Set the {@code Map} of {@link GridObjectFactory}s. For every object type in the configuration XML a {@link GridObjectFactory} is created once.
     * These {@link GridObjectFactory}s are then used (also in this class) to easily create {@link GridObject}s of the wanted type.
     * @param goFactories the {@code Map} of {@link GridObjectFactory}s
     */
    public static void setGoFactories(Map<String, GridObjectFactory> goFactories) {
        AgentFactory.goFactories = goFactories;
    }

    /**
     * Constructs a new {@code AgentFactory}.
     * @param file the location of the config file to parse
     * @param gridWorld the {@link GridWorld} of this server
     * @throws FileNotFoundException thrown if the XML file was not found
     * @throws IOException thrown in case of an I/O error
     * @throws ParserConfigurationException  thrown if there was a problem configuring the parser
     * @throws SAXException thrown if there was a problem while parsing
     */
    public AgentFactory(String file, GridWorld gridWorld) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        this.gridWorld = gridWorld;
        xPathFactory = XPathFactory.newInstance();
        doc = HelperFunctions.fileToDocument(file, SchemaLoader.getServer());
        openAgentNo = 0;
        inventoryElements = new HashMap<Agent, Element>();
    }

    /* set up the parameters */
    private void setupParameters(Element parent, Collection<GridObjectParameter> parameters) {
        NodeList children = parent.getChildNodes();
        Element parametersElement = HelperFunctions.findElement(children, "parameters");

        if (parametersElement == null) {
            return;
        }

        NodeList parameterNodes = parametersElement.getChildNodes();

        for (int i = 0; i < parameterNodes.getLength(); i++) {
            if (parameterNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) parameterNodes.item(i);
                if (currentElement.getNodeName().equals("parameter")) {
                    parameters.add(new GridObjectParameter(currentElement.getAttribute("paraName"), currentElement.getAttribute("value")));
                }
            }
        }
    }

    /**
     * Get the {@link Agent} for given login data from the XML configuration file.
     * @param requestOp the {@link ActionRequestOp} containing the login data
     * @return the {@link Agent} for the given login data or {@code null} if no {@link Agent} could be created
     */
    public Agent getAgent(ActionRequestOp requestOp) {
        XPath xPath = xPathFactory.newXPath();

        try {
            /* Get username and password */
            String loginName = requestOp.getParameterValue("name");
            String password = requestOp.getParameterValue("password");


            if (!HelperFunctions.stringSane(loginName)) {
                logger.info("The agent did not provide the login name after connection, terminating agent init");
                return null;
            }


            /* get the element matching the supplied login name */
            Element agentElement = (Element) xPath.evaluate("/gridworld/agents//*[@name='" + loginName + "' or @typeName='" + loginName + "']", doc, XPathConstants.NODE);

            if (agentElement == null) {
                logger.info("The agent specified a login name that could not be found in the server configuration, terminating agent init.");
                return null;
            }

            if (agentElement.getParentNode().getNodeName().equals("closed")) {
                logger.info("The agent tried to login as the name of a closed agent type instead of an instance of this closed agent types, terminating agent init.");
                return null;
            }

            /* check password if necessary */
            String neededPassword = agentElement.getAttribute("password");
            if (!neededPassword.equals("")) {
                if (!neededPassword.equals(password)) {
                    logger.info("The agent supplied the wrong password! Terminating agent init");
                    return null;
                }
            }

            /* check if we deal with an open agent or with an instance of a closed agent */
            boolean isOpen;
            if (agentElement.getNodeName().equals("type")) {
                isOpen = true;
            } else {
                isOpen = false;
            }

            /* general defaults */
            int xPos = 0;
            int yPos = 0;
            int freeCap = -1;
            int capNeed = 0;
            int moveForce = -1;
            int viewRange = -1;
            int soundIntensity = -1;
            int hearing = -1;
            int priority = Integer.MAX_VALUE;

            /* defaults from <types> */
            Element types = (Element) xPath.evaluate("/gridworld/agents/types", doc, XPathConstants.NODE);
            freeCap = HelperFunctions.updateIntFromString(freeCap, types.getAttribute("freeCap"));
            capNeed = HelperFunctions.updateIntFromString(capNeed, types.getAttribute("capNeed"));
            moveForce = HelperFunctions.updateIntFromString(moveForce, types.getAttribute("moveForce"));
            viewRange = HelperFunctions.updateIntFromString(viewRange, types.getAttribute("viewRange"));
            soundIntensity = HelperFunctions.updateIntFromString(soundIntensity, types.getAttribute("soundIntensity"));
            hearing = HelperFunctions.updateIntFromString(hearing, types.getAttribute("hearing"));

            Collection<GridObjectParameter> parameters = new HashSet<GridObjectParameter>();

            /* if this is an instance of a closed type, we first get the defaults from the closed type definition */
            String className;
            if (!isOpen) {
                String nameRef = agentElement.getAttribute("agentTypeRef");
                Element closedType = (Element) xPath.evaluate("/gridworld/agents/types/closed/type[@typeName='" + nameRef + "']", doc, XPathConstants.NODE);
                setupParameters(closedType, parameters);
                freeCap = HelperFunctions.updateIntFromString(freeCap, closedType.getAttribute("freeCap"));
                capNeed = HelperFunctions.updateIntFromString(capNeed, closedType.getAttribute("capNeed"));
                moveForce = HelperFunctions.updateIntFromString(moveForce, closedType.getAttribute("moveForce"));
                viewRange = HelperFunctions.updateIntFromString(viewRange, closedType.getAttribute("viewRange"));
                soundIntensity = HelperFunctions.updateIntFromString(soundIntensity, closedType.getAttribute("soundIntensity"));
                hearing = HelperFunctions.updateIntFromString(hearing, closedType.getAttribute("hearing"));
                priority = HelperFunctions.updateIntFromString(priority, closedType.getAttribute("priority"));
                className = closedType.getAttribute("class");
            } else {
                // if we deal with an open type, we need to make the agent name unique by adding something
                loginName = loginName.concat("." + openAgentNo++);
                className = agentElement.getAttribute("class");
            }


            /* this is in common for both types */
            setupParameters(agentElement, parameters);
            freeCap = HelperFunctions.updateIntFromString(freeCap, agentElement.getAttribute("freeCap"));
            capNeed = HelperFunctions.updateIntFromString(capNeed, agentElement.getAttribute("capNeed"));
            moveForce = HelperFunctions.updateIntFromString(moveForce, agentElement.getAttribute("moveForce"));
            viewRange = HelperFunctions.updateIntFromString(viewRange, agentElement.getAttribute("viewRange"));
            soundIntensity = HelperFunctions.updateIntFromString(soundIntensity, agentElement.getAttribute("soundIntensity"));
            hearing = HelperFunctions.updateIntFromString(hearing, agentElement.getAttribute("hearing"));
            xPos = Integer.valueOf(agentElement.getAttribute("xPos"));
            yPos = Integer.valueOf(agentElement.getAttribute("yPos"));
            priority = HelperFunctions.updateIntFromString(priority, agentElement.getAttribute("priority"));

            /* If a custom Agent class has been specified, try to use it, otherwise instantiate the default Agent class */
            Agent newAgent;
            if (className != null && !className.equals("")) {
                try {
                    newAgent = (Agent) Class.forName(className).newInstance();
                } catch (InstantiationException ex) {
                    logger.warn("AgentFactory couldn't instantiate an object class, ignoring this agent.", ex);
                    return null;
                } catch (IllegalAccessException ex) {
                    logger.warn("AgentFactory trying to instantiate an agent class caused an illegal access exception, ignoring this agent.", ex);
                    return null;
                } catch (ClassNotFoundException ex) {
                    logger.warn("AgentFactory couldn't find an agent class, ignoring this agent.", ex);
                    return null;
                }
            } else {
                newAgent = new Agent();
            }
            newAgent.configure(loginName, gridWorld, capNeed, freeCap, viewRange, moveForce, xPos, yPos, hearing, soundIntensity, priority);
            for (Iterator<GridObjectParameter> i = parameters.iterator(); i.hasNext();) {
                newAgent.addParameter(i.next());
            }
            inventoryElements.put(newAgent, agentElement);
            return newAgent;
        } catch (XPathExpressionException ex) {
            logger.warn("There was a problem with an XPath expression. That shouldn't happen. Ignoring this agent.", ex);
            return null;
        }
    }

    /**
     * Set up the inventory of an {@link Agent} in a "breadth first" manner (if the XML specification violates freeCap/capNeed
     * constraints somewhere in the contains graph, {@link GridObject}s closer to the origin of the graph will be preferred at adding).
     * @param agent the {@link Agent} whose inventory should be created
     */
    public void setupInventory(Agent agent) {
        Element inventoryParent = inventoryElements.get(agent);

        Queue<InitContainsContainer> objectQueue = new LinkedList<InitContainsContainer>();

        NodeList children = inventoryParent.getChildNodes();
        Element containsElement = HelperFunctions.findElement(children, "contains");

        if (containsElement == null) {
            return;
        }

        NodeList instances = containsElement.getChildNodes();
        for (int j = 0; j < instances.getLength(); j++) {
            if (instances.item(j).getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) instances.item(j);
                if (childElement.getNodeName().equals("instance")) {
                    InitContainsContainer childContainer = new InitContainsContainer(childElement, agent, agent);
                    objectQueue.add(childContainer);
                }
            }
        }

        FactoryHelper.addInventory(objectQueue, goFactories, gridWorld);
    }
}
