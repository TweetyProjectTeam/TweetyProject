package net.sf.tweety.agents.gridworldsim.commons;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This error handler gets called if something bad happened during the validation of an XML document.
 * It logs the error and throws an appropriate SAXException.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class ValidationErrorHandler extends DefaultHandler {

    private static final Logger logger = Logger.getLogger(ValidationErrorHandler.class);

    /**
     * Gets called when an error occurred during validation.
     * @param exception the error that occurred
     * @throws SAXException the error that occurred
     */
    @Override
    public void error(SAXParseException exception)
            throws SAXException {
        logger.warn("An error occurred validating an XML document: Check your XML!", exception);
        throw (exception);
    }

    /**
     * Gets called when a fatal error occurred during validation.
     * @param exception the fatal error that occurred
     * @throws SAXException the fatal error that occurred
     */
    @Override
    public void fatalError(SAXParseException exception)
            throws SAXException {
        logger.warn("a fatal error occurred validating an XML document: Check your XML!", exception);
        throw (exception);
    }

    /**
     * Gets called when a warning occurred during validation.
     * @param exception the warning that occurred
     */
    @Override
    public void warning(SAXParseException exception) {
        logger.info("a warning occurred validating an XML document", exception);
    }
}
