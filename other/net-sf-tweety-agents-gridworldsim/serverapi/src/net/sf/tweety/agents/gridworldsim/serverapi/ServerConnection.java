package net.sf.tweety.agents.gridworldsim.serverapi;

import java.io.IOException;
import java.net.UnknownHostException;
import net.sf.tweety.agents.gridworldsim.commons.*;
import org.apache.log4j.Logger;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Configures and creates a new server connection and a {@code Thread} to handle that connection. Real
 * action (connecting, starting the {@code ServerThread}) only happens after {@code connect()}
 * is called).
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ServerConnection {

    private static final Logger logger = Logger.getLogger(ServerConnection.class);
    private final String hostname;
    private final int port;
    private final LinkedBlockingQueue<ClientQueueItem> inItems;
    private ServerThread serverThread;
    private SocketConnection sConnect;

    /**
     * Constructs a new {@code ServerConnection}.
     * @param hostname the name of the host to connect to
     * @param port the port to connect to
     */
    public ServerConnection(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.inItems = new LinkedBlockingQueue<ClientQueueItem>();
    }

    /**
     * Connect to the server.
     * @return the {@link ActionRequestSender} for sending requests over this connection
     */
    public ActionRequestSender connect() throws ConnectionException {
        logger.debug("Trying to connect");
        try {
            sConnect = new SocketConnection(new Socket(hostname, port));
            ActionRequestSender agentActions = new ActionRequestSender(sConnect);
            serverThread = new ServerThread(sConnect, inItems);
            serverThread.start();
            logger.info("Connection successful");
            return agentActions;
        } catch (UnknownHostException ex) {
            throw new ConnectionException("Cannot find host.", true, ex);
        } catch (IOException ex) {
            throw new ConnectionException("Cannot connect to host.", true, ex);
        }
    }

    /**
     * Disconnect.
     */
    public void disconnect() {
        if (serverThread != null) {
            serverThread.interrupt();
        }
        if (sConnect != null) {
            sConnect.close();
        }
    }

    /**
     * Get the {@code LinkedBlockingQueue} of incoming queue incidents. This queue will be filled by the server with {@link ClientQueueItem}s
     * containing either information about a {@code GridWorldPerception} or about a {@link ConnectionException}.
     * @return the {@code LinkedBlockingQueue} of incoming queue incidents
     */
    public LinkedBlockingQueue<ClientQueueItem> getInItems() {
        return inItems;
    }
}
