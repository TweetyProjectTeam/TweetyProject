package net.sf.tweety.agents.gridworldsim.server.threads;

import org.apache.log4j.Logger;
import net.sf.tweety.agents.gridworldsim.commons.*;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.xpath.XPathFactory;
import net.sf.tweety.agents.gridworldsim.server.items.QueueItem;



/**
 * This class implements the {@code Thread} that listens for new connections from observer clients and starts a new
 * {@code Thread} for every newly connected observer client.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ObserverConnectorThread extends Thread {

    private static final Logger logger = Logger.getLogger(ObserverConnectorThread.class);
    private final int port;
    private final LinkedBlockingQueue<QueueItem> inItems;

    /**
     * Creates a new {@code ObserverConnectorThread}
     * @param inItems the queue of incoming incidence items
     * @param port the port on which to listen for new connections from observer client
     */
    public ObserverConnectorThread(LinkedBlockingQueue<QueueItem> inItems, int port) {
        this.inItems = inItems;
        this.port = port;
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
            logger.info("Server listening for new observer client connections on port " + port);
        } catch (IOException ex) {
            logger.error("Unable to create the server socket for observer connections. observer client connection will not be possible.");
            interrupt();
        }

        while (!isInterrupted() && !serverSocket.isClosed()) {
            try {
                Socket newConnection = serverSocket.accept();
                logger.info("Received new observer client connection from " + newConnection.getInetAddress());
                SocketConnection sConnect = new SocketConnection(newConnection);
                ObserverThread obsThread = new ObserverThread(sConnect, inItems);
                obsThread.start();
            } catch (IOException ex) {
                logger.warn("Connection attempt of a observer client failed", ex);
            } 
        }

        logger.debug("Thread stopped");
    }
}
