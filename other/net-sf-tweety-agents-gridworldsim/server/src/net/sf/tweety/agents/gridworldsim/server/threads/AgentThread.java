package net.sf.tweety.agents.gridworldsim.server.threads;

import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import net.sf.tweety.agents.gridworldsim.commons.*;
import net.sf.tweety.agents.gridworldsim.server.items.ActionRequestOpItem;
import net.sf.tweety.agents.gridworldsim.server.items.DisconnectedItem;
import net.sf.tweety.agents.gridworldsim.server.items.QueueItem;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implements a {@code Thread} that handles the incoming XML messages of agents and initializes a newly connected agent.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class AgentThread extends Thread {

    private static final Logger logger = Logger.getLogger(AgentThread.class);
    private final SocketConnection sConnect;
    private final LinkedBlockingQueue<QueueItem> inItems;

    /**
     * Initializes a new {@code AgentThread}
     * @param sConnect the {@link SocketConnection} for the agent, whose incoming connections should be handled
     * @param inItems the queue incoming incidents
     */
    public AgentThread(SocketConnection sConnect, LinkedBlockingQueue<QueueItem> inItems) {
        this.sConnect = sConnect;
        this.inItems = inItems;
    }

    /**
     * The run method of this thread.
     */
    @Override
    public void run() {
        logger.debug("Thread started");

        while (!isInterrupted()) {
            try {
                Document doc = HelperFunctions.inputToDocument(sConnect, SchemaLoader.getActionrequest());
                XPathFactory xPathFact = XPathFactory.newInstance();
                XPath xPath = xPathFact.newXPath();
                String actionType = xPath.evaluate("/action/@type", doc);
                QueueItem item;

                ActionRequestOp requestOp = new ActionRequestOp(actionType);
                NodeList parameters = (NodeList) xPath.evaluate("/action/parameters/parameter", doc, XPathConstants.NODESET);
                for (int i = 1; i < parameters.getLength() + 1; i++) {
                    String currentParaType = xPath.evaluate("/action/parameters/parameter[" + i + "]/name", doc);
                    String currentParaValue = xPath.evaluate("/action/parameters/parameter[" + i + "]/value", doc);
                    requestOp.addParameter(currentParaType, currentParaValue);
                }

                String internalState = xPath.evaluate("/action/internalstate", doc);
                if (internalState != null && !internalState.equals("")) {
                    requestOp.setInternalState(internalState);
                }

                ActionRequestOpItem aItem = new ActionRequestOpItem(sConnect, requestOp);
                item = new QueueItem(aItem, QueueItem.ACTIONREQUESTOP);

                inItems.add(item);
            } catch (SAXException ex) {
                logger.warn("Could not get the XML schemas necessary to validate incoming XML documents", ex);
                sConnect.close();
                DisconnectedItem dItem = new DisconnectedItem(sConnect, true);
                QueueItem qdItem = new QueueItem(dItem, QueueItem.DISCONNECTED);
                inItems.add(qdItem);
                break;
            } catch (ConnectionException ex) {
                if (ex.isExpected()) {
                    logger.info(ex.getMessage());
                } else {
                    logger.warn(ex.getMessage(), ex.getCause());
                }
                DisconnectedItem dItem = new DisconnectedItem(sConnect, true);
                QueueItem qdItem = new QueueItem(dItem, QueueItem.DISCONNECTED);
                inItems.add(qdItem);
                break;
            } catch (XPathExpressionException ex) {
                logger.warn("Illegal XPath expression, that shouldn't happen", ex);
            }
        }

        logger.debug("Thread stopped");
    }
}
