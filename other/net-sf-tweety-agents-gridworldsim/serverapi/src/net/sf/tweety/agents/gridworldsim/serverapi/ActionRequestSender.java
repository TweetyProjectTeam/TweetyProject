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
package net.sf.tweety.agents.gridworldsim.serverapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import net.sf.tweety.agents.gridworldsim.commons.*;
import org.w3c.dom.Document;
import org.apache.log4j.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import java.util.Iterator;
import sun.misc.BASE64Encoder;

/**
 * Objects made from this class are used to send out action requests to the server.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ActionRequestSender {

    private static final Logger logger = Logger.getLogger(ActionRequestSender.class);
    private final SocketConnection sConnect;
    private String internalState;

    /**
     * Constructs a new {@code ActionRequestSender}
     * @param sConnect the {@link SocketConnection} of the server connection
     */
    public ActionRequestSender(SocketConnection sConnect) {
        this.sConnect = sConnect;
    }

    /**
     * Send out a request to move an object in a certain direction.
     * @param objectName the name of the object to move
     * @param direction the direction to move (see {@link Constants} for possible values)
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void moveObject(String objectName, String direction) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("moveObject");
        request.addParameter("object", objectName);
        request.addParameter("direction", direction);
        sendActionDocument(request);
        logger.debug("created a document to move object " + objectName + " in direction " + direction);
    }

    /**
     * Send out a request to move the agent in a certain direction.
     * @param direction the direction to moveAgent (see {@link Constants} for possible values)
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void moveAgent(String direction) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("move");
        request.addParameter("direction", direction);
        sendActionDocument(request);
        logger.debug("created a document to move in direction " + direction);
    }

    /**
     * Creates an XML document for a given action request and sends it out. <br/>
     * <b>Attention:</b> Public use of this method is only intended for custom users action requests, if the user does not want to adjust the server API
     * for this. For standard action requests like "moveAgent" or "takeObject" usage of this method is deprecated and you should use
     * the specialized method for it.
     * @param request the action request to send out via XML
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void sendActionDocument(ActionRequestOp request) throws ConnectionException {
        Element rootElement = setupDocument();
        Document doc = rootElement.getOwnerDocument();
        rootElement.setAttribute("type", request.getType());

        Collection<String> paraStrings = request.getParameters().keySet();
        Element parametersElement = doc.createElement("parameters");
        boolean added = false;
        for (Iterator<String> i = paraStrings.iterator(); i.hasNext();) {
            String currentParameter = i.next();
            Element parameterElement = doc.createElement("parameter");
            Element parameterNameElement = doc.createElement("name");
            Element parameterValueElement = doc.createElement("value");
            parameterNameElement.setTextContent(currentParameter);
            parameterValueElement.setTextContent(request.getParameterValue(currentParameter));
            parameterElement.appendChild(parameterNameElement);
            parameterElement.appendChild(parameterValueElement);
            parametersElement.appendChild(parameterElement);
            added = true;
        }
        if (added) {
            rootElement.appendChild(parametersElement);
        }
        doc.appendChild(rootElement);
        HelperFunctions.sendDocument(doc, sConnect);
    }

    /* set up the XML document as much as all action requests have it in common */
    private Element setupDocument() {
        DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Element rootElement = null;
        try {
            builder = factory1.newDocumentBuilder();
            Document doc = builder.newDocument();
            rootElement = doc.createElement("action");
            rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            rootElement.setAttribute("xsi:noNamespaceSchemaLocation", Constants.ACTIONS_SCHEMA_REMOTE_LOCATION);

            if (HelperFunctions.stringSane(internalState)) {
                Element internalStateElement = doc.createElement("internalstate");
                internalStateElement.setTextContent(internalState);
                rootElement.appendChild(internalStateElement);
                internalState = null;
            }
        } catch (ParserConfigurationException ex) {
            logger.warn("There was a problem configuring the XML parser", ex);
        }
        return rootElement;
    }

    /**
     * Send out an action request to take an object to the inventory.
     * @param takeObjectName the name of the object to take
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void takeObject(String takeObjectName) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("take");
        request.addParameter("object", takeObjectName);
        sendActionDocument(request);
        logger.debug("created a document to take the object " + takeObjectName);
    }

    /**
     * Send out an action request to release an object from the inventory.
     * @param releaseObjectName the name of the object to release
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void releaseObject(String releaseObjectName) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("release");
        request.addParameter("object", releaseObjectName);
        sendActionDocument(request);
        logger.debug("created a document to release the object " + releaseObjectName);
    }

    /**
     * Send out an action request to declare the possible acceptance of an object from an agent.
     * @param agent the agent from which the object should be accepted
     * @param object the object which should be accepted from the agent
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void declareAccept(String agent, String object) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("declareAccept");
        request.addParameter("agent", agent);
        request.addParameter("object", object);
        sendActionDocument(request);
        logger.debug("created a document to declare an acceptance of object " + object + " by agent " + agent + ".");
    }

    /**
     * Send out an action request to recall the possible acceptance of an object from an agent.
     * @param agent the agent from which the object should not be accepted anymore
     * @param object the object which should not be accepted from the agent anymore
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void recallAccept(String agent, String object) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("retractAccept");
        request.addParameter("agent", agent);
        request.addParameter("object", object);
        sendActionDocument(request);
        logger.debug("created a document to recall an acceptance of object " + object + " by agent " + agent + ".");
    }

    /**
     * Send out an action request to declare the acceptance of all objects from an agent.
     * @param agent the agent from which to accept all objects
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void declareAllAcceptAgent(String agent) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("declareAccept");
        request.addParameter("agent", agent);
        sendActionDocument(request);
        logger.debug("created a document to declare the acceptance of all objects from agent " + agent + ".");
    }

    /**
     * Send out an action request to declare the acceptance of an object from all agents.
     * @param object the object which to accept from all agents
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void declareAllAcceptObject(String object) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("declareAccept");
        request.addParameter("object", object);
        sendActionDocument(request);
        logger.debug("created a document to declare the acceptance of object " + object + " from all agents");
    }

    /**
     * Send out an action request to recall the acceptance of all objects from an agent.
     * @param agent the agent from which to recall accepting all objects
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void recallAllAcceptAgent(String agent) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("retractAccept");
        request.addParameter("agent", agent);
        sendActionDocument(request);
        logger.debug("created a document to recall the acceptance of all objects from agent " + agent + ".");
    }

    /**
     * Send out an action request to recall the acceptance of an object from all agents.
     * @param object the object for which to recall the acceptance from all agents
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void recallAllAcceptObject(String object) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("recallAccept");
        request.addParameter("object", object);
        sendActionDocument(request);
        logger.debug("created a document to recall the acceptance of object " + object + " from all agents");
    }

    /**
     * Hand over an object from the inventory to another agent.
     * @param agent the agent which should receive the object
     * @param object the object to hand over
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void handOver(String agent, String object) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("handOver");
        request.addParameter("agent", agent);
        request.addParameter("object", object);
        sendActionDocument(request);
        logger.debug("created a document to hand over " + object + " to agent " + agent + ".");
    }

    /**
     * Load an object into another object.
     * @param object the object that should be loaded into another object
     * @param receiver the object that receives the object
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void load(String object, String receiver) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("load");
        request.addParameter("object", object);
        request.addParameter("receiver", receiver);
        sendActionDocument(request);
        logger.debug("created a document to load " + object + " to " + receiver);
    }

    /**
     * Unload an object from another object.
     * @param object the object that should be unloaded
     * @param destination the destination ({@code INVENTORY} or {@code GRID} from {@link Constants})
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void unLoad(String object, String destination) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("unload");
        request.addParameter("object", object);

        if (destination.equals(Constants.INVENTORY)) {
            request.addParameter("destination", "inventory");
        } else if (destination.equals(Constants.GRID)) {
            request.addParameter("destination", "grid");
        } else {
            logger.warn("Invalid desination for unload operation");
            return;
        }

        sendActionDocument(request);
        logger.debug("created a document to unload " + object + " to the" + destination);
    }

    /**
     * Lock an object with a password.
     * @param object the object to lock
     * @param password the password to use
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void lock(String object, String password) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("lock");
        request.addParameter("object", object);
        request.addParameter("password", password);
        sendActionDocument(request);
    }

    /**
     * Unlock an object using a password.
     * @param object the object to unlock
     * @param password the password to use
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void unlock(String object, String password) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("unlock");
        request.addParameter("object", object);
        request.addParameter("password", password);
        sendActionDocument(request);
    }

    /**
     * Login to a server ({@code login} has to be the first action request after connection).
     * @param username the username to use for login
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void login(String username) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("login");
        request.addParameter("name", username);
        sendActionDocument(request);
    }

    /**
     * Force the transition into the next state
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void forceStateTrans() throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("statetrans");
        sendActionDocument(request);
    }

    /**
     * Send out a no-op action request that does nothing.
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void noOp() throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("noop");
        sendActionDocument(request);
    }

    /**
     * Login to a server using a password ({@code login} has to be the first action request after connection).
     * @param username the username to use for login
     * @param password the password to use for login
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void login(String username, String password) throws ConnectionException {
        ActionRequestOp request = new ActionRequestOp("login");
        request.addParameter("name", username);
        if (password != null && !password.equals("")) {
            request.addParameter("password", password);
        }
        sendActionDocument(request);
    }

    /**
     * Send a message. If the {@code Object} inside the {@link MessageRequest} is not an instance of {@code String},
     * the {@code Object} will be serialized and BASE64 encoded. In any case the message content will be put in a CDATA section of
     * the XML. <br/><br/>
     * The rationale behind this behavior is this: In an environment only populated by Java agents using the GridWorldSim serverapi,
     * arbitrary objects can be easily send and received. In a mixed environment where other agents are not capable of understanding serialized
     * Java objects, the agent can create a custom encoding himself and put it into a {@code String} which will be send out unmodified.
     * @param msg the {@link MessageRequest} to send
     * @throws ConnectionException thrown if disconnection was noticed when trying to send out the action request
     */
    public void sendMessage(MessageRequest msg) throws ConnectionException {
        Element rootElement = setupDocument();
        Document doc = rootElement.getOwnerDocument();
        boolean isPublic = msg.isPublic();


        if (isPublic) {
            rootElement.setAttribute("type", "publicMsg");
        } else {
            rootElement.setAttribute("type", "privateMsg");
        }

        Object msgObj = msg.getMessage();

        String encodedMsg = null;
        if (msgObj instanceof String) {
            encodedMsg = (String) msgObj;
        } else {
            BASE64Encoder encoder = new BASE64Encoder();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(msgObj);
            } catch (IOException ex) {
                logger.warn("An I/O exeception occured when trying to setup or use an object output stream", ex);
                return;
            }
            encodedMsg = encoder.encode(baos.toByteArray());
        }

        Element parametersElement = doc.createElement("parameters");
        Element parameterElement = doc.createElement("parameter");
        Element parameterNameElement = doc.createElement("name");
        Element parameterValueElement = doc.createElement("value");
        parameterNameElement.setTextContent("message");
        parameterElement.appendChild(parameterNameElement);
        parameterValueElement.appendChild(doc.createCDATASection(encodedMsg));
        parameterElement.appendChild(parameterValueElement);
        rootElement.appendChild(parametersElement);
        parametersElement.appendChild(parameterElement);

        if (!isPublic) {
            Element receiverElement = doc.createElement("parameter");
            Element receiverNameElement = doc.createElement("name");
            Element receiverValueElement = doc.createElement("value");
            receiverNameElement.setTextContent("receiver");
            receiverValueElement.setTextContent(msg.getReceiver());
            receiverElement.appendChild(receiverNameElement);
            receiverElement.appendChild(receiverValueElement);
            parametersElement.appendChild(receiverElement);
        }

        doc.appendChild(rootElement);
        HelperFunctions.sendDocument(doc, sConnect);
    }

    /**
     * Set the internal state that should be sent out with the next action request. It will be cleared after the next
     * action request is sent out.
     * @param internalState the internal state to send out with the next action request
     */
    public void setInternalState(String internalState) {
        this.internalState = internalState;
    }
}
