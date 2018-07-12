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
package net.sf.tweety.agents.gridworldsim.server;

import net.sf.tweety.agents.gridworldsim.commons.ConnectionException;
import net.sf.tweety.agents.gridworldsim.server.items.QueueItem;
import net.sf.tweety.agents.gridworldsim.server.factories.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import net.sf.tweety.agents.gridworldsim.commons.ActionRequestOp;
import net.sf.tweety.agents.gridworldsim.commons.HelperFunctions;
import net.sf.tweety.agents.gridworldsim.commons.SocketConnection;
import net.sf.tweety.agents.gridworldsim.server.items.ActionRequestOpItem;
import net.sf.tweety.agents.gridworldsim.server.items.DisconnectedItem;
import net.sf.tweety.agents.gridworldsim.server.items.NewObserverItem;
import net.sf.tweety.agents.gridworldsim.server.statetrans.StateTransRule;
import net.sf.tweety.agents.gridworldsim.server.threads.AgentConnectorThread;
import net.sf.tweety.agents.gridworldsim.server.threads.ObserverConnectorThread;
import org.apache.log4j.Appender;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This is the main server class that handles the incoming incidents received from the threads which handle client connections.
 * Such an incident  can be a newly received action request, a newly connected client or the disconnection of a client. If
 * (depending on the execution mode) the conditions for a state transition are met, it will kickstart the state transition,
 * for instance by calling all active {@link StateTransRule}s or by registering or unregistering an agent or observer client.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class Server {

    private static final Logger logger = Logger.getLogger(Server.class);
    private final GridWorld gridWorld;
    private final LinkedBlockingQueue<QueueItem> inItems;
    private final Map<Agent, SocketConnection> agentToSocket;
    private final Map<SocketConnection, Agent> socketToAgent;
    private final AgentFactory agentFactory;
    private final Collection<SocketConnection> observers;
    private final Collection<Agent> waitForAgents;
    private final Map<Agent, ActionRequest> unprocessedRequests;
    private final Collection<LoginRequest> unprocessedLogins;
    private final Collection<ObserverLogin> observerLoginData;
    private Timer timer;
    private final short execMode;
    private final int timeout;
    private long time;
    private final Map<Agent, String> internalStates;
    private final Collection<Agent> unprocessedDisconnects;

    /**
     * Constructs a new {@code Server} and fetches basic information and GridWorld data structures from the {@link ConfigFactory}.
     * @param confFactory the {@code ConfigFactory} that creates all sorts of data structures from the XML config file
     * @param inItems the queue of incoming incidents from the threads handling client connections
     * @param configLocation the location of the XML configuration file
     * @throws XPathExpressionException thrown if an invalid XPath expression has been encountered
     * @throws FileNotFoundException thrown if the XML configuration file couldn't not have been found
     * @throws ParserConfigurationException thrown if the XML parser could not be configured
     * @throws SAXException thrown if there was a problem parsing an XML document (usually because it does not validate against the schema)
     * @throws IOException thrown when an I/O error occurred (usually because the XML configuration file could not be accessed)
     */
    public Server(ConfigFactory confFactory, LinkedBlockingQueue<QueueItem> inItems, String configLocation) throws XPathExpressionException, FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        this.inItems = inItems;
        gridWorld = confFactory.getGridWorld();
        observerLoginData = confFactory.getObserverLoginData();
        agentFactory = new AgentFactory(configLocation, gridWorld);
        gridWorld.setAgentFactory(agentFactory);
        agentToSocket = new HashMap<Agent, SocketConnection>();
        socketToAgent = new HashMap<SocketConnection, Agent>();
        observers = new HashSet<SocketConnection>();
        waitForAgents = new HashSet<Agent>();
        unprocessedRequests = new HashMap<Agent, ActionRequest>();
        execMode = gridWorld.getExecMode().getMode();
        timeout = gridWorld.getExecMode().getTimeout();
        internalStates = new HashMap<Agent, String>();
        time = 0;
        unprocessedLogins = new HashSet<LoginRequest>();
        unprocessedDisconnects = new HashSet<Agent>();
    }

    /**
     * This method is the main method of the server that waits for new QueueItems to appear in the queue of incoming incidents.
     * Depending on the type of QueueItem, the handling of them will be kickstarted accordingly.
     */
    private void processQueueItems() {

        /**
         * In case an execution mode is selected that supports time-based state transitions and a timeout has been
         * specified, a timer is set.
         */
        if ((execMode == ExecutionMode.WAIT_FOR_ALL || execMode == ExecutionMode.FIXED_TIME) && timeout > 0) {
            setTimer();
        }

        /* main loop */
        while (true) {
            try {
                /* wait for new items to appear on the queue */
                QueueItem qItem = inItems.take();

                /* handle the case that the incoming QueueItem is an ActionRequestOp (this includes logins of agents) */
                if (qItem.getType() == QueueItem.ACTIONREQUESTOP) {
                    ActionRequestOpItem requestItem = (ActionRequestOpItem) qItem.getQueueObject();
                    ActionRequestOp requestOp = requestItem.getRequestOp();

                    /* handle the case that the incoming ActioNRequestOp is a login attempt of an agent */
                    if (requestOp.getType().equals("login")) {
                        LoginRequest loginRequest = new LoginRequest(requestItem.getsConnect(), requestOp);
                        unprocessedLogins.add(loginRequest);

                        /**
                         * The login of an agent will trigger a state transition immediately if the execution mode is either "on demand"
                         * or if the execution mode is "wait for all" but not agent is part of the GridWorld yet.
                         */
                        if (execMode == ExecutionMode.ON_DEMAND || (agentToSocket.keySet().isEmpty() && execMode == ExecutionMode.WAIT_FOR_ALL)) {
                            stateTransition();
                        }
                    } else {
                        /* if the ActionRequestOp is not a login, it will be fed to method handling state transitions */
                        Agent agent = socketToAgent.get(requestItem.getsConnect());
                        if (agent != null) {
                            ActionRequest request = new ActionRequest(agent, requestOp);
                            checkStateTrans(request);
                        }
                    }
                }

                /**
                 * Handles the case a new observer client connected by adding its SockerConnection to the set of
                 * connected observers if the login data given by the client was correct. Since these lines of codes are
                 * only reached when the GridWorld data structures are consistent (i.e. not resembling an intermediate state),
                 * the observer client is straightly told about the current state.
                 */
                if (qItem.getType() == QueueItem.NEWOBSERVER) {
                    NewObserverItem newObsItem = (NewObserverItem) qItem.getQueueObject();
                    if (observerLoginData.contains(newObsItem.getObserverLogin())) {
                        observers.add(newObsItem.getsConnect());
                        logger.info("Successfully registered new observer client.");
                        Collection<SocketConnection> currentObserver = new HashSet<SocketConnection>();
                        currentObserver.add(newObsItem.getsConnect());
                        tellObservers(currentObserver);
                    } else {
                        newObsItem.getsConnect().close();
                        logger.info("Observer client specified invalid login data.");
                    }
                }

                /* Handles the case a client (either agent or observer) has been disconnected. */
                if (qItem.getType() == QueueItem.DISCONNECTED) {
                    DisconnectedItem dItem = (DisconnectedItem) qItem.getQueueObject();


                    /* Handle the case an agent connected and disconnected again without a state transition in between. */
                    SocketConnection sConnect = dItem.getsConnect();
                    removeDisconnectedFromUnprocessedLogins(sConnect);

                    /**
                     * If the disconnected client is a registered agent, it must be removed from the grid. If the execution mode
                     * is "on demand" this will be processed immediately. If the execution mode is "wait for all" it is
                     * made sure that the state transition does not wait for the just removed agent.
                     */
                    if (dItem.isAgent()) {
                        Agent currentAgent = socketToAgent.get(sConnect);
                        if (currentAgent != null) {
                            unprocessedDisconnects.add(currentAgent);

                            if (execMode == ExecutionMode.ON_DEMAND) {
                                stateTransition();
                            } else if (execMode == ExecutionMode.WAIT_FOR_ALL) {
                                waitForAgents.remove(currentAgent);
                                waitForAll(null);
                            }
                        }
                        /* In case the disconnected client is an observer, it is enough just to remove it from the observer set. */
                    } else {
                        observers.remove(dItem.getsConnect());
                        dItem.getsConnect().close();
                    }
                }

                /**
                 * In case a timer requests state transition or a "force state transition" action request from an
                 * observer client is received, state transition will commence immediately.
                 */
                if (qItem.getType() == QueueItem.STATETRANSITEM) {
                    stateTransition();
                }
            } catch (NullPointerException ex) {
                logger.warn("A null pointer exception happened while processing an input, continuing with the next input.", ex);
            } catch (InterruptedException ex) {
                logger.warn("The waiting for new incidents main processing method was interrupted.", ex);
            }
        }
    }

    /* Creates and sets a timer, that puts a STATETRANSITEM to the incident queue when it fires. */
    private void setTimer() {
        timer = new Timer();
        TimeoutInserter toInserter = new TimeoutInserter(inItems);
        timer.schedule(toInserter, timeout, timeout);
    }

    /* This method makes state transition happen. */
    private void stateTransition() {
        logger.debug("Beginning with a state transition");

        /* Process all disconnections of agents since the last state transition. */
        for (Iterator<Agent> i = unprocessedDisconnects.iterator(); i.hasNext();) {
            removeAgent(i.next());
        }
        unprocessedDisconnects.clear();

        /* Process all new connections of agents (that didn't already disconnect again) since the last state transition */
        for (Iterator<LoginRequest> i = unprocessedLogins.iterator(); i.hasNext();) {
            loginAgent(i.next());
        }
        unprocessedLogins.clear();

        /**
         * Generate and process the "always true" action request that always gets execute once and first
         * at every state transition.
         */
        ActionRequestOp alwaysRequestOp = new ActionRequestOp("always");
        ActionRequest alwaysRequest = new ActionRequest(null, alwaysRequestOp);
        processActionRequest(alwaysRequest);

        /* Sort (by agent priority) and process all action requests since the last state transition. */
        if (!unprocessedRequests.isEmpty()) {
            ActionRequest[] sortedUnprocessedRequests = unprocessedRequests.values().toArray((new ActionRequest[unprocessedRequests.values().size()]));
            Arrays.sort(sortedUnprocessedRequests);

            for (int i = 0; i < sortedUnprocessedRequests.length; i++) {
                processActionRequest(sortedUnprocessedRequests[i]);
            }
            unprocessedRequests.clear();
        }

        logger.debug("State transition finished.");

        /* If the execution mode is "wait for all", add all agents of the current state to the set of agents to wait for */
        if (execMode == ExecutionMode.WAIT_FOR_ALL) {
            waitForAgents.addAll(agentToSocket.keySet());
        }

        /* state transition finished at this point, tell all connected clients about the new state*/

        tell();
    }

    /**
     * Add the new action request to the set of unprocessed requests. If the execution mode is "on demand" start the
     * state transition immediately. If the execution mode is "wait for all", do additional processing.
     */
    private void checkStateTrans(ActionRequest request) {
        if (!request.getRequestOp().getType().equals("always")) {
            unprocessedRequests.put(request.getAgent(), request);
        }

        if (execMode == ExecutionMode.ON_DEMAND) {
            stateTransition();
        } else if (execMode == ExecutionMode.WAIT_FOR_ALL) {
            waitForAll(request);
        }
    }

    /**
     * In case of execution mode "wait for all": Stop waiting for the agent from whom a request has been received.
     * If this was the last agent to wait for, start the state transition. Reset the timer if applicable.
     */
    private void waitForAll(ActionRequest request) {
        if (request != null) {
            waitForAgents.remove(request.getAgent());
        }

        if (waitForAgents.isEmpty()) {
            if (timer != null) {
                timer.cancel();
            }

            stateTransition();

            if (timeout > 0) {
                setTimer();
            }
        }
    }

    /* Increase the time counter, tell all agents and observers about the new state and clear the messages from the GridWorld. */
    private void tell() {
        logger.debug("Starting to send perceptions of the new state to all connected clients");
        time++;
        tellObservers(observers);
        tellAgents();
        gridWorld.clearPublicMessages();
        gridWorld.clearPrivMessages();
        logger.debug("Finished sending perceptions of the new state to all connected clients");
    }

    /* Tell all connected observer clients about the new state. */
    private void tellObservers(Collection<SocketConnection> observersToTell) {
        OutputPerceptionFactory gwdFactory = new OutputPerceptionFactory(gridWorld, internalStates);

        /* All observers receive the same perception. It will be generated in case there is at least one observer client. */
        Document doc;
        if (!observersToTell.isEmpty()) {
            try {
                doc = gwdFactory.getDocument(time); // this creates the perception
            } catch (ParserConfigurationException ex) {
                logger.warn("Coulnd't configure the parser to create an XML document for the observers.", ex);
                return;
            }
        } else {
            return;
        }

        /* Now tell all observers. If during this it is noticed that an observer is not connected anymore, remove it. */
        Collection<SocketConnection> removeList = new LinkedList<SocketConnection>();
        for (Iterator<SocketConnection> i = observersToTell.iterator(); i.hasNext();) {
            SocketConnection sConnect = i.next();
            try {
                HelperFunctions.sendDocument(doc, sConnect);
            } catch (ConnectionException ex) {
                logger.info("Observer client disconnected");
                removeList.add(sConnect);
            }
        }
        observers.removeAll(removeList);
    }

    /* Create a perception for every agent and send it.*/
    private void tellAgents() {
        Collection<Agent> removeList = new LinkedList<Agent>();

        for (Iterator<Agent> i = agentToSocket.keySet().iterator(); i.hasNext();) {
            Agent currentAgent = i.next();
            try {
                OutputPerceptionFactory gwdFactory = new OutputPerceptionFactory(gridWorld, currentAgent);
                Document doc = gwdFactory.getDocument(time); // this creates the perception
                SocketConnection sConnect = agentToSocket.get(currentAgent);
                HelperFunctions.sendDocument(doc, sConnect);
                logger.debug("Created an XML output message for agent " + currentAgent.getName());
            } catch (ConnectionException ex) {
                if (ex.isExpected()) {
                    logger.info(ex.getMessage());
                } else {
                    logger.warn(ex.getMessage(), ex);
                }
                removeList.add(currentAgent); // if an agent is found out to be disconnected, add it to the remove list
            } catch (NullPointerException ex) {
                logger.warn("A null pointer exception happened while trying to send out a document to an agent. Continuing with the next document.", ex);
            } catch (ParserConfigurationException ex) {
                logger.warn("There was a problem configuring the parser while trying to send out a document to an agent. Continuing with the next document.", ex);
            }
        }


        /* Remove all agents that got noticed as disconnecting while sending out perceptions.*/
        for (Iterator<Agent> i = removeList.iterator(); i.hasNext();) {
            removeAgent(i.next());
        }
    }

    /* Apply a single action request to the GridWorld. */
    private void processActionRequest(ActionRequest request) {
        logger.debug("Starting to process an action request.");

        List<StateTransRule> rules = gridWorld.getRules();

        if (request.getRequestOp().hasInternalState()) {
            internalStates.put(request.getAgent(), "("+time+") "+request.getRequestOp().getInternalState());
        }

        String requestType = request.getRequestOp().getType();
        if (requestType.equals("privateMsg") || requestType.equals("publicMsg")) {
            processMessage(request);
        } else {
            for (Iterator<StateTransRule> i = rules.iterator(); i.hasNext();) {
                StateTransRule currentRule = i.next();
                if (currentRule.isTriggered(request)) {
                    currentRule.doTransition(gridWorld, request);
                }
            }
        }
        logger.debug("Finished processing an action request.");
    }

    /**
     * Process a message action request (the method assumes the given action request is a message). In case of
     * a private message, conditions for delivery are immediately checked. In case of a public message the
     * decision who will be able to receive the method is done during perception generation.
     */
    private void processMessage(ActionRequest request) {
        ActionRequestOp op = request.getRequestOp();
        String receiverString = op.getParameterValue("receiver");
        String messageString = op.getParameterValue("message");

        if (op.getType().equals("privateMsg") && receiverString != null && messageString != null) {
            GridObject receiver = gridWorld.getGridObjectByName(receiverString);
            if (receiver != null && receiver instanceof Agent && receiver != request.getAgent()) {
                if (gridWorld.getGridObjectLocation(receiver).equals(gridWorld.getGridObjectLocation(request.getAgent()))) {
                    PrivateMessage newMsg = new PrivateMessage(request.getAgent(), ((Agent) receiver), messageString);
                    ((Agent) receiver).addMessage(newMsg);
                    gridWorld.addPrivateMessage(newMsg);
                    logger.debug("Successful added a private message from " + request.getAgent().getName() + " to " + receiverString + " to the GridWorld");
                } else {
                    logger.debug("Cannot process private message from " + request.getAgent().getName() + " to " + receiverString + " because both agents are not in the same cell.");
                }
            } else {
                logger.debug("Cannot process private message from " + request.getAgent().getName() + " to " + receiverString + " because the receiver is not valid.");
            }
        } else if (op.getType().equals("publicMsg") && messageString != null) {
            gridWorld.addPublicMessage(new PublicMessage(request.getAgent(), messageString));
            logger.debug("Processed a public message from " + request.getAgent().getName() + ".");
        }

    }

    /* Removes an agent. */
    private void removeAgent(Agent currentAgent) {
        if (agentToSocket.keySet().contains(currentAgent)) {
            agentToSocket.get(currentAgent).close();
            socketToAgent.remove(agentToSocket.get(currentAgent));
            agentToSocket.remove(currentAgent);
            waitForAgents.remove(currentAgent);
            unprocessedRequests.remove(currentAgent);
            gridWorld.removeAgent(currentAgent);
        }
    }

    /* Handles the logging in of an agent. */
    private boolean loginAgent(LoginRequest lRequest) {
        SocketConnection agentConnect = lRequest.getsConnect();
        ActionRequestOp requestOp = lRequest.getRequestOp();

        /* New agents are created by the AgentFactory. */
        Agent newAgent = agentFactory.getAgent(requestOp);

        if (newAgent == null) {
            logger.info("Couldn't create the agent.");
            agentConnect.close();
            return false;
        }

        boolean success = gridWorld.addAgent(newAgent);
        if (success) {
            agentToSocket.put(newAgent, agentConnect);
            socketToAgent.put(agentConnect, newAgent);
            logger.info("Successfully registered agent " + newAgent.getName() + " to the GridWorld.");
            return true;
        } else {
            agentConnect.close();
            logger.info("Couldn't add agent " + newAgent.getName() + " to the GridWorld.");
            return false;
        }

    }

    /**
     * If an agent got disconnected with no state transition happening between his login and the disconnection,
     * his login request would still be in the set of unprocessed logins. This method prevents that.
     */
    private void removeDisconnectedFromUnprocessedLogins(SocketConnection disconnectedAgentSocket) {
        Collection<LoginRequest> toRemove = new LinkedList<LoginRequest>();
        for (Iterator<LoginRequest> i = unprocessedLogins.iterator(); i.hasNext();) {
            LoginRequest currentRequest = i.next();
            if (currentRequest.getsConnect() == disconnectedAgentSocket) {
                toRemove.add(currentRequest);
            }
        }
        unprocessedLogins.removeAll(toRemove);
    }

    /**
     * The main method to start the server.
     * @param args the filename of the XML file containing the server configuration. If not specified, the server tries
     * to load {@code server.xml} from the current directiory.
     */
    public static void main(String[] args) {

        /* set up the Logger */
        BasicConfigurator.configure();

        logger.info("Server starting up");

        /* get the location of the config file from the parameters or default to "server.xml" if no parameter is given */
        String configLocation;
        if (args.length > 0) {
            configLocation = args[0];
        } else {
            configLocation = "server.xml";
        }

        try {
            /* instantiate the ConfigFactory and retrieve the ports for agent client and observation client connections */
            ConfigFactory confFactory = new ConfigFactory(configLocation);
            int agentPort = confFactory.getAgentPort();
            int observerPort = confFactory.getObserverPort();

            /* setup debugging level  */
            String debugPath = confFactory.getDebugPath();
            boolean debug = confFactory.isDebug();
            Logger root = Logger.getRootLogger();
            if (debug) {
                root.setLevel(org.apache.log4j.Level.DEBUG);
            } else {
                root.setLevel(org.apache.log4j.Level.INFO);
            }

            /* setup log output to file */
            if (HelperFunctions.stringSane(debugPath) && confFactory.logFile()) {
                try {
                    FileOutputStream fout = new FileOutputStream(HelperFunctions.cleanPath(debugPath) + "gridworldsim-server.log");
                    TTCCLayout ttcclayout = new TTCCLayout();
                    ttcclayout.setDateFormat(AbsoluteTimeDateFormat.ABS_TIME_DATE_FORMAT);
                    Appender fileAppender = new WriterAppender(ttcclayout, fout);
                    root.addAppender(fileAppender);
                } catch (Exception ex) {
                    logger.warn("Could not create server log file. Invalid path?", ex);
                }

            }

            /* setup file output of all received and sent XML data */
            if (HelperFunctions.stringSane(debugPath) && confFactory.logData()) {
                HelperFunctions.setDebugPath(debugPath);
            }

            /* set up the queue for the incoming incidents from the threads handling agent and observer clients */
            LinkedBlockingQueue<QueueItem> inItems = new LinkedBlockingQueue<QueueItem>();

            /* configure and start the thread that handles new connections from observer clients */
            ObserverConnectorThread observerThread = new ObserverConnectorThread(inItems, observerPort);
            observerThread.start();

            /* configure and start the thread that handles new connections from agents */
            AgentConnectorThread agentThread = new AgentConnectorThread(inItems, agentPort);
            agentThread.start();

            /* create the server and make it start processing incoming incidents from the queue */
            Server server = new Server(confFactory, inItems, configLocation);
            server.processQueueItems();
        } catch (FileNotFoundException ex) {
            logger.fatal("Couldn't find XML config file, exiting.");
            System.exit(1);
        } catch (ParserConfigurationException ex) {
            logger.fatal("Couldn't configure the parser that should parse the XML config file, exiting.", ex);
            System.exit(1);
        } catch (SAXException ex) {
            logger.fatal("A problem occured parsing the XML config file, exiting.", ex);
            System.exit(1);
        } catch (IOException ex) {
            logger.fatal("An I/O exception occured when trying to create one of the factories, exiting.", ex);
            System.exit(1);
        } catch (XPathExpressionException ex) {
            logger.fatal("An illegal XPathExpression was used when trying to parse stuff from the XML configuration file.", ex);
            System.exit(1);
        }

        logger.debug("Server main method terminating.");
    }
}
