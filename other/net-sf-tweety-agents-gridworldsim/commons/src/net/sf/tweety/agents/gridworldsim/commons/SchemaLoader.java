package net.sf.tweety.agents.gridworldsim.commons;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * This class provides {@code Schema}s for validation.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class SchemaLoader {

    private static boolean initialized = false;
    private static Schema server;
    private static Schema perception;
    private static Schema actionrequest;
    private static final Logger logger = Logger.getLogger(SchemaLoader.class);

    /* setup the Schemas if they haven't been set up before */
    private static void setupSchemas() throws SAXException {
        if (!initialized) {
            initialized = true;
            SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            server = sFact.newSchema(SchemaLoader.class.getResource("schemas/server.xsd"));
            perception = sFact.newSchema(SchemaLoader.class.getResource("schemas/perception.xsd"));
            actionrequest = sFact.newSchema(SchemaLoader.class.getResource("schemas/actionrequest.xsd"));
        }
    }

    /**
     * Get the {@code Schema} for validation of action requests
     * @return the {@code Schema} for validation of action requests
     * @throws SAXException thrown if the {@code Schema} could not be generated
     */
    public static Schema getActionrequest() throws SAXException {
        setupSchemas();
        return actionrequest;
    }

    /**
     * Get the {@code Schema} for validation of perceptions
     * @return the {@code Schema} for validation of perceptions
     * @throws SAXException thrown if the {@code Schema} could not be generated
     */
    public static Schema getPerception() throws SAXException {
        setupSchemas();
        return perception;
    }

    /**
     * Get the {@code Schema} for validation of the server config file
     * @return the {@code Schema} for validation of the server config file
     * @throws SAXException thrown if the {@code Schema} could not be generated
     */
    public static Schema getServer() throws SAXException {
        setupSchemas();
        return server;
    }
}
