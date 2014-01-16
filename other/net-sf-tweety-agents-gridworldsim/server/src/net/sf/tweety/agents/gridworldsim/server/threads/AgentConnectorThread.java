package net.sf.tweety.agents.gridworldsim.server.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import net.sf.tweety.agents.gridworldsim.commons.ConnectionException;
import net.sf.tweety.agents.gridworldsim.commons.Constants;
import net.sf.tweety.agents.gridworldsim.commons.HelperFunctions;
import net.sf.tweety.agents.gridworldsim.commons.SocketConnection;
import net.sf.tweety.agents.gridworldsim.server.items.QueueItem;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;


/**
 * This class implements a {@code Thread} that listens for new connections from agents and starts a separate {@code Thread}
 * to handle such connections.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class AgentConnectorThread extends Thread {

    private static final Logger logger = Logger.getLogger(AgentConnectorThread.class);
    private final int port;
    private final LinkedBlockingQueue<QueueItem> inItems;

    /**
     * Create a new {@code AgentConnectionThread}
     * @param inItems the queue of incoming incidents
     * @param agentPort the port on which to listen for new connections from agents
     */
    public AgentConnectorThread(LinkedBlockingQueue<QueueItem> inItems, int agentPort) {
        this.port = agentPort;
        this.inItems = inItems;
    }

    /**
     * The run method of this thread.
     */
    @Override
    public void run() {
        logger.debug("Thread started");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server listening for new agent connections on port " + port);
        } catch (IOException ex) {
            logger.fatal("Unable to create the server socket for agent connections, exiting.");
            System.exit(1);
        }

        while (!isInterrupted() && !serverSocket.isClosed()) {
            try {
                Socket newConnection = serverSocket.accept();
                logger.info("Received new agent connection from " + newConnection.getInetAddress());
                SocketConnection sConnect = new SocketConnection(newConnection);
                AgentThread inThread = new AgentThread(sConnect, inItems);
                inThread.start();

            } catch (IOException ex) {
                logger.info("Connection attempt from agent failed.", ex);
            }
        }
        logger.debug("Thread stopped");
    }
}
