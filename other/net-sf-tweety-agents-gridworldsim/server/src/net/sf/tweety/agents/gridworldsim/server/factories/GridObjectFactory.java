package net.sf.tweety.agents.gridworldsim.server.factories;

import java.util.Collection;
import java.util.Iterator;
import net.sf.tweety.agents.gridworldsim.commons.HelperFunctions;
import net.sf.tweety.agents.gridworldsim.server.GridObject;
import net.sf.tweety.agents.gridworldsim.server.GridObjectParameter;
import net.sf.tweety.agents.gridworldsim.server.GridWorld;
import org.apache.log4j.Logger;

/**
 * Objects created from this class are used to produce {@link GridObject} objects. The main idea is that such a factory
 * will be created for every type of {@link GridObject} defined in the configuration XML and then be used to create
 * the actual {@link GridObject} objects.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class GridObjectFactory {

    private final String name;
    private final GridWorld gridWorld;
    private int objectNo;
    private int capNeed;
    private int freeCap;
    private String className;
    private static final Logger logger = Logger.getLogger(GridObjectFactory.class);
    private final Collection<GridObjectParameter> parameters;

    /**
     * Create a new {@code GridObjectFactory}
     * @param name the type name of an object type in the XML
     * @param gridWorld the {@link GridWorld}
     * @param capNeed the default capacity need for the object type
     * @param freeCap the default free capacity for the object type
     * @param parameters the default parameters for the object type
     */
    public GridObjectFactory(String name, GridWorld gridWorld, int capNeed, int freeCap, Collection<GridObjectParameter> parameters) {
        this.name = name;
        this.gridWorld = gridWorld;
        objectNo = 0;
        this.freeCap = freeCap;
        this.capNeed = capNeed;
        this.className = null;
        this.parameters = parameters;
    }

    /**
     * Set that objects for this type should be created from a class that extends {@link GridObject} but is not {@link GridObject}.
     * @param className the name of the class to use for this type of objects
     */
    public void setClass(String className) {
        this.className = className;
    }

    /**
     * Create a new {@link GridObject} based on the configuration of this {@code GridObjectFactory}.
     * @param indiName a {@code String} containing the individual name for this object, if the String is null or empty, a name will be auto-generated from the type name
     * @return a new {@link GridObject} based on the configuration of this {@code GridObjectFactory}
     */
    public GridObject newGridObject(String indiName) {
        GridObject newObject;

        /* try to instantiate the custom class if applicable, else instantiate a normal GridObject */
        if (className != null) {
            try {
                newObject = (GridObject) Class.forName(className).newInstance();
            } catch (InstantiationException ex) {
                logger.warn("ConfigFactory couldn't instantiate an object class, igorning this object.", ex);
                return null;
            } catch (IllegalAccessException ex) {
                logger.warn("ConfigFactory trying to instantiate an object class caused an illegal access exception, ignoring this object.", ex);
                return null;
            } catch (ClassNotFoundException ex) {
                logger.warn("ConfigFactory couldn't find an object class, ignoring this object.", ex);
                return null;
            }
        } else {
            newObject = new GridObject();
        }


        /* set the custom name if available. otherwise auto-generate a name*/
        if (!HelperFunctions.stringSane(indiName)) {
            newObject.configure(name + "." + objectNo++, gridWorld, capNeed, freeCap);
        } else {
            newObject.configure(indiName, gridWorld, capNeed, freeCap);
        }

        /* add all parameters */
        for (Iterator<GridObjectParameter> i = parameters.iterator(); i.hasNext();) {
            GridObjectParameter currentParameter = i.next();
            newObject.addParameter(currentParameter);
        }

        return newObject;
    }
}
