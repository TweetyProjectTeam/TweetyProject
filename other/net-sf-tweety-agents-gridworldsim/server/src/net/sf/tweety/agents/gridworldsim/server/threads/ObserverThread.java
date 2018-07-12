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
package net.sf.tweety.agents.gridworldsim.server.threads;

import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import net.sf.tweety.agents.gridworldsim.commons.*;
import net.sf.tweety.agents.gridworldsim.server.ObserverLogin;
import net.sf.tweety.agents.gridworldsim.server.items.DisconnectedItem;
import net.sf.tweety.agents.gridworldsim.server.items.NewObserverItem;
import net.sf.tweety.agents.gridworldsim.server.items.QueueItem;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implements a {@code Thread} that handles the incoming messages of observer clients.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ObserverThread extends Thread {

    private static final Logger logger = Logger.getLogger(ObserverThread.class);
    private final LinkedBlockingQueue<QueueItem> inItems;
    private final SocketConnection sConnect;
    private final XPathFactory xPathFactory;

    /**
     * Creates a new {@code ObserverThread}
     * @param sConnect the {@link SocketConnection} of this observer client
     * @param inItems the queue of incoming incidence items
     */
    public ObserverThread(SocketConnection sConnect, LinkedBlockingQueue<QueueItem> inItems) {
        this.sConnect = sConnect;
        this.inItems = inItems;
        xPathFactory = XPathFactory.newInstance();
    }

    /* Creates a QueueItem of type NEWOBSERVER for the login data provided by the observer client*/
    private void loginObserver() throws ConnectionException, XPathExpressionException, SAXException {
        Document doc = HelperFunctions.inputToDocument(sConnect, SchemaLoader.getActionrequest());
        XPath xPath = xPathFactory.newXPath();

        String actionType = xPath.evaluate("/action/@type", doc);
        ActionRequestOp requestOp = new ActionRequestOp(actionType);
        NodeList parameters = (NodeList) xPath.evaluate("/action/parameters/parameter", doc, XPathConstants.NODESET);
        for (int i = 1; i < parameters.getLength() + 1; i++) {
            String currentParaType = xPath.evaluate("/action/parameters/parameter[" + i + "]/name", doc);
            String currentParaValue = xPath.evaluate("/action/parameters/parameter[" + i + "]/value", doc);
            requestOp.addParameter(currentParaType, currentParaValue);
        }


        if (!requestOp.getType().equals("login") || requestOp.getParameterValue("name") == null) {
            throw new ConnectionException("Observer client did not provide login information, closing connection.", true);
        }

        NewObserverItem noItem;
        if (requestOp.getParameterValue("password") != null) {
            noItem = new NewObserverItem(sConnect, new ObserverLogin(requestOp.getParameterValue("name"), requestOp.getParameterValue("password")));
        } else {
            noItem = new NewObserverItem(sConnect, new ObserverLogin(requestOp.getParameterValue("name")));
        }

        QueueItem qItem = new QueueItem(noItem, QueueItem.NEWOBSERVER);
        inItems.add(qItem);
    }

    /**
     * The run method of this thread.
     */
    @Override
    public void run() {
        logger.debug("Thread started");
        try {
            loginObserver();
            while (!isInterrupted()) {
                try {
                    Document doc = HelperFunctions.inputToDocument(sConnect, SchemaLoader.getActionrequest());
                    XPathFactory xPathFact = XPathFactory.newInstance();
                    XPath xPath = xPathFact.newXPath();
                    String actionType = xPath.evaluate("/action/@type", doc);
                    if (actionType.equals("statetrans")) {
                        QueueItem qItem = new QueueItem(null, QueueItem.STATETRANSITEM);
                        inItems.add(qItem);
                    }
                } catch (XPathExpressionException ex) {
                    logger.warn("Illegal XPath expression, that shouldn't happen", ex);
                }
            }
        } catch (SAXException ex) {
            logger.error("Could not get the XML schemas necessary to validate incoming XML documents", ex);
            sConnect.close();
            DisconnectedItem dItem = new DisconnectedItem(sConnect, true);
            QueueItem qdItem = new QueueItem(dItem, QueueItem.DISCONNECTED);
            inItems.add(qdItem);
            interrupt();
        } catch (ConnectionException ex) {
            if (ex.isExpected()) {
                logger.info(ex.getMessage());
            } else {
                logger.warn(ex.getMessage(), ex.getCause());
            }
            DisconnectedItem dItem = new DisconnectedItem(sConnect, false);
            QueueItem qdItem = new QueueItem(dItem, QueueItem.DISCONNECTED);
            inItems.add(qdItem);
            interrupt();
        } catch (XPathExpressionException ex) {
            logger.warn("Illegal XPath expression, that shouldn't happen", ex);
        }

        logger.debug("Thread stopped");
    }
}
