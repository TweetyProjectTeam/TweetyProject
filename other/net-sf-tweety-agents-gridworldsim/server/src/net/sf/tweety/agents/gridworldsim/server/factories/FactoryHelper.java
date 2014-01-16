package net.sf.tweety.agents.gridworldsim.server.factories;

import java.util.Map;
import java.util.Queue;
import net.sf.tweety.agents.gridworldsim.commons.HelperFunctions;
import net.sf.tweety.agents.gridworldsim.server.GridObject;
import net.sf.tweety.agents.gridworldsim.server.GridObjectLocation;
import net.sf.tweety.agents.gridworldsim.server.GridObjectParameter;
import net.sf.tweety.agents.gridworldsim.server.GridWorld;
import net.sf.tweety.agents.gridworldsim.server.InitContainsContainer;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class bundles functionality that is required by more than one factory of this package.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class FactoryHelper {

    private static final Logger logger = Logger.getLogger(FactoryHelper.class);

    /**
     * Sets up a single {@link GridObject}.
     * @param currentElement the {@code Element} defining that {@link GridObject} in the XML configuration file
     * @param goFactories the {@code Map} of {@link GridObjectFactory}s
     * @return the new {@link GridObject}
     */
    public static GridObject setupSingleObject(Element currentElement, Map<String, GridObjectFactory> goFactories) {
        /* get the right GridObjectFactory for this type of object */
        GridObjectFactory currentFactory = goFactories.get(currentElement.getAttribute("objectTypeRef"));

        /* start parsing through the XML */
        GridObject newObject = currentFactory.newGridObject(currentElement.getAttribute("name"));
        String indiCapNeed = currentElement.getAttribute("capNeed");
        if (indiCapNeed != null && !indiCapNeed.equals("")) {
            newObject.setInitCapNeed(Integer.valueOf(indiCapNeed));
        }

        String indiFreeCap = currentElement.getAttribute("freeCap");
        if (indiFreeCap != null && !indiFreeCap.equals("")) {
            if (indiFreeCap.equals("unbounded")) {
                newObject.setInitCap(-1);
            } else {
                newObject.setInitCap(Integer.valueOf(indiFreeCap));
            }
        }


        NodeList parameters = currentElement.getChildNodes();
        Element parametersElement = HelperFunctions.findElement(parameters, "parameters");

        if (parametersElement == null) {
            return newObject;
        }

        NodeList children = parametersElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) children.item(i);
                if (childElement.getNodeName().equals("parameter")) {
                    GridObjectParameter newParameter = new GridObjectParameter(childElement.getAttribute("paraName"), childElement.getAttribute("value"));
                    newObject.addParameter(newParameter);
                }
            }
        }

        return newObject;
    }

    /**
     * Setup the inventory in a "breadth first" manner (if the XML specification violates freeCap/capNeed
     * constraints somewhere in the contains graph, {@link GridObject}s closer to the origin of the graph will be preferred at adding).
     * @param objectQueue a {@code Queue} containing all inventory {@link GridObject}s of the object located on the grid
     * @param goFactories the {@code Map} of {@link GridObjectFactory}s
     * @param gridWorld the {@link GridWorld}
     */
    public static void addInventory(Queue<InitContainsContainer> objectQueue, Map<String, GridObjectFactory> goFactories, GridWorld gridWorld) {


        while (!objectQueue.isEmpty()) {
            InitContainsContainer currentContainer = objectQueue.poll();
            Element currentElement = currentContainer.getObjectElement();
            GridObject parent = currentContainer.getParent();
            GridObject rootObject = currentContainer.getRootObject();
            GridObject newObject = setupSingleObject(currentElement, goFactories);

            // check if it is legal for all objects in the contains graph to add the object regarding capNeed and freeCap
            if (rootObject.getContainedFreeCap(parent) < newObject.getCapNeed()) {
                logger.info("Adding object " + newObject.getName() + " to " + parent.getName() + " would violate the requirement that capNeed "
                        + "cannot be larger than freeCap for one object in the contains graph. Cannot add object. ");
                continue;
            }

            GridObjectLocation newLocation = new GridObjectLocation(parent);
            boolean success = gridWorld.addGridObject(newObject, newLocation);

            if (success) {
                NodeList children = currentElement.getChildNodes();
                Element containsElement = HelperFunctions.findElement(children, "contains");

                if (containsElement == null) {
                    continue;
                }


                NodeList instances = containsElement.getChildNodes();
                for (int i = 0; i < instances.getLength(); i++) {
                    if (instances.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) instances.item(i);
                        if (childElement.getNodeName().equals("instance")) {
                            InitContainsContainer childContainer = new InitContainsContainer(childElement, newObject, rootObject);
                            objectQueue.add(childContainer);
                        }
                    }
                }
            } else {
                logger.warn("Problem to setup contained object " + newObject.getName());
            }
        }
    }
}
