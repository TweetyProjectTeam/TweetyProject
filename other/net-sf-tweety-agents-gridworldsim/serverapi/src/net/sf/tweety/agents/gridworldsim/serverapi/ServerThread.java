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

import net.sf.tweety.agents.gridworldsim.commons.*;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import java.util.concurrent.LinkedBlockingQueue;
import org.xml.sax.SAXException;

/**
 * Implements a {@code Thread} that handles incoming messages to the server and constructs a {@code GridWorldPerception} from it.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ServerThread extends Thread {

    private static final Logger logger = Logger.getLogger(ServerThread.class);
    private final SocketConnection sConnect;
    private final LinkedBlockingQueue<ClientQueueItem> inItems;

    /**
     * Constructs a new {@code ServerThread}.
     * @param sConnect the {@link SocketConnection} of the connection to the server
     * @param inItems the queue of incoming incidents, into which this {@code ServerThread} will put incidents
     */
    public ServerThread(SocketConnection sConnect, LinkedBlockingQueue<ClientQueueItem> inItems) {
        this.sConnect = sConnect;
        this.inItems = inItems;
    }

    /**
     * The run method of this Thread.
     */
    @Override
    public void run() {
        logger.debug("Thread started");
        while (!isInterrupted()) {
            try {
                try {
                    Document doc = HelperFunctions.inputToDocument(sConnect, SchemaLoader.getPerception());
                    InputPerceptionFactory factory = new InputPerceptionFactory(doc);
                    inItems.add(new ClientQueueItem(factory.getGridWorldPerception()));
                } catch (SAXException ex) {
                    logger.error("Cannot get the schema to validate the input from the server, closing connection.", ex);
                    throw new ConnectionException("Disconnecting because schema could not be found", false);
                }
            } catch (ConnectionException ex) {
                if (ex.isExpected()) {
                    logger.info(ex.getMessage());
                } else {
                    logger.warn(ex.getMessage(), ex.getCause());
                }
                inItems.add(new ClientQueueItem(ex));
                break;
            }
        }

        logger.debug("Thread stopped");
    }
}
