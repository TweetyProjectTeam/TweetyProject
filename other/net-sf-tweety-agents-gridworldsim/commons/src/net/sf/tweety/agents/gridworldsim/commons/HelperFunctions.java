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
package net.sf.tweety.agents.gridworldsim.commons;

import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.FileInputStream;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.validation.Schema;
import java.io.FileOutputStream;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class bundles functionality needed by different parts of the GridWorldSim server, agent client and observer client.
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class HelperFunctions {

    private static String debugPath;
    private static final Logger logger = Logger.getLogger(HelperFunctions.class);
    private static int inCounter = 0;
    private static int outCounter = 0;

    /**
     * Set the debug path where incoming and outgoing XML documents are saved. If such a path is set, no further measure
     * are required to enable this kind of debug output.
     * @param debugPath the path where incoming and outgoing XML documents are saved
     */
    public static void setDebugPath(String debugPath) {
        HelperFunctions.debugPath = cleanPath(debugPath);
    }

    /**
     * Make sure the file separator is at the end of a given path.
     * @param path the path to which the file separator should be added if necessary
     * @return the path ending with a file separator
     */
    public static String cleanPath(String path) {
        if (!path.endsWith(System.getProperty("file.separator"))) {
            path = path.concat(System.getProperty("file.separator"));
        }
        return path;
    }

    /**
     * Tells if a given direction is diagonal
     * @param direction the direction to check for if it is diagonal
     * @return true if the direction is diagonal, false otherwise
     */
    public static boolean isDiagnonal(String direction) {
        if (direction.equals(Constants.NORTHEAST) || direction.equals(Constants.SOUTHEAST)
                || direction.equals(Constants.SOUTHWEST) || direction.equals(Constants.NORTHWEST)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tells if a given {@code String} represents a valid direction
     * @param direction the {@code String} to check
     * @return true if the {@code String} represents a valid direction, false otherwise
     */
    public static boolean isValidDirection(String direction) {
        if (direction == null) {
            return false;
        }
        if (direction.equals(Constants.NORTHEAST) || direction.equals(Constants.SOUTHEAST)
                || direction.equals(Constants.SOUTHWEST) || direction.equals(Constants.NORTHWEST)
                || direction.equals(Constants.NORTH) || direction.equals(Constants.EAST)
                || direction.equals(Constants.WEST) || direction.equals(Constants.SOUTH)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Converts a {@code byte} array of length 4 containing a two's complement encoded integer to {@code int}.
     * @param b the {@code byte} array to convert to {@code int}
     * @return the resulting {@code int}
     */
    private static int byteArrayToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * Takes an XML file, parses it and return its {@code Document} representation.
     * @param file the XML file to parse
     * @param schema the {@code Schema} to use for validation of the XML file
     * @return the {@code Document} representation of the XML file
     * @throws FileNotFoundException thrown if the file was not found
     * @throws ParserConfigurationException thrown if the parser could not be configured
     * @throws SAXException thrown if the document couldn't be parsed (usually that means that it didn't validate against the {@code Schema})
     * @throws IOException thrown if the file could not be opened
     */
    public static Document fileToDocument(String file, Schema schema) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        FileInputStream in = new FileInputStream(file);
        InputSource inStream = new InputSource(in);
        DocumentBuilder builder = getValidatingDocumentBuilder(schema);
        return (builder.parse(inStream));
    }

    /**
     * Takes a {@code Document} from an {@code InputStream} an returns it. The input format on the {@code InputStream} has to be:
     * <br><ol><li>4 bytes encoding an {@code int} in two's complement indicating the length of the following XML document
     * <li>An XML document in UTF-8 encoding</ol>
     * @param sConnect the {@code SocketConnection} to use
     * @param schema the {@code Schema} used to validate the incoming XML document
     * @return the resulting {@code Document}
     * @throws ConnectionException thrown if disconnection was noticed or actively initiated (for instance in case of a non-validating XML document)
     */
    public static Document inputToDocument(SocketConnection sConnect, Schema schema) throws ConnectionException {
        InputStream in;
        try {
            in = sConnect.getInputStream();
            byte[] intBytes = new byte[4];
            in.read(intBytes);
            int intValue = byteArrayToInt(intBytes);

            if (intValue == 0) {
                throw new ConnectionException("Connection terminated", true);
            }

            byte[] xmlBytes = new byte[intValue];

            /* Read the stream until we have enough bytes.*/
            int i = 0;
            while (i < intValue) {
                int readCount = in.read(xmlBytes, i, intValue - i);
                if (readCount == -1) {
                    throw new ConnectionException("Connection terminated", true);
                }
                i = i + readCount;
            }

            writeDebugToFile("in-" + (inCounter++) + ".xml", xmlBytes);

            InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(new String(xmlBytes)));
            DocumentBuilder builder;
            builder = getValidatingDocumentBuilder(schema);
            return builder.parse(inStream);
        } catch (IOException ex) {
            throw new ConnectionException("Connection terminated", true, ex);
        } catch (ParserConfigurationException ex) {
            throw new ConnectionException("Problem configuring the parser", false, ex);
        } catch (SAXException ex) {
            throw new ConnectionException("Invalid XML document, does your XML really validate?", true, ex);
        }

    }

    /**
     * get a {@code DocumentBuilder} that will validating according to the specified schema
     * @param schema the XML schema used for validation
     * @return the document builder
     * @throws ParserConfigurationException indicating failure to construct the {@code DocumentBuilder}
     */
    private static DocumentBuilder getValidatingDocumentBuilder(Schema schema) throws ParserConfigurationException {
        DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
        factory1.setSchema(schema);
        factory1.setNamespaceAware(true);
        DocumentBuilder builder = factory1.newDocumentBuilder();
        builder.setErrorHandler(new ValidationErrorHandler());
        return builder;
    }

    /**
     * Writes out a byte array to a file at the debug path.
     */
    private static void writeDebugToFile(String file, byte[] content) {
        if (HelperFunctions.debugPath != null && !HelperFunctions.debugPath.equals("")) {
            try {
                FileOutputStream fout;
                fout = new FileOutputStream(debugPath + file);
                fout.write(content);
            } catch (FileNotFoundException ex) {
                logger.warn("Specificed debug path does not exist or is not accessible. Cannot write XML output to file.");
            } catch (IOException ex) {
                logger.warn("Can't write XML output to file.");
            }
        }
    }

    /**
     * send a {@code Document} over a {@code SocketConnection}
     * @param doc the {@code Document} to send
     * @param sConnect the {@link SocketConnection} to use for sending
     * @throws ConnectionException thrown if disconnection was noticed or actively initiated
     */
    public static void sendDocument(Document doc, SocketConnection sConnect) throws ConnectionException {
        byte[] stringContent;
        try {
            stringContent = HelperFunctions.documentToByteArray(doc);

            writeDebugToFile("out-" + (outCounter++) + ".xml", stringContent);

            byte[] stringLength = HelperFunctions.intToByteArray(stringContent.length);
            sConnect.getOutputStream().write(stringLength);
            sConnect.getOutputStream().write(stringContent);
        } catch (IOException ex) {
            throw new ConnectionException("Connection terminated", true, ex);
        } catch (TransformerConfigurationException ex) {
            throw new ConnectionException("Transformer configuration error", false, ex);
        } catch (TransformerException ex) {
            throw new ConnectionException("Transformer error", false, ex);
        }
    }

    /**
     * converts a {@code Document} into an UTF-8 encoded {@code byte} array
     * @param doc the {@code Document} to convert
     * @return the resulting {@code byte} array
     * @throws TransformerConfigurationException thrown when the {@code Transformer} couldn't be configured
     * @throws TransformerException thrown when the {@code Transformer} had a problem
     */
    public static byte[] documentToByteArray(Document doc) throws TransformerConfigurationException, TransformerException {
        String docString = documentToString(doc);
        byte[] stringContent = docString.getBytes(Charset.forName("UTF-8"));
        return stringContent;
    }

    /**
     * Converts an {@code int} to a {@code byte} array of length 4 containing the integer encoded in two's complement.
     * @param l the integer to convert
     * @return the resulting {@code byte} array
     */
    private static byte[] intToByteArray(int l) {
        byte[] bArray = new byte[4];
        ByteBuffer bBuffer = ByteBuffer.wrap(bArray);
        IntBuffer iBuffer = bBuffer.asIntBuffer();
        iBuffer.put(0, l);
        return bArray;
    }

    /**
     * Convert from {@code Document} to {@code String}
     * @param doc the {@code Document} to convert
     * @return the resulting {@code String}
     */
    private static String documentToString(Document doc) throws TransformerConfigurationException, TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    /**
     * Find an {@code Element} of a given name in a {@code NodeList}
     * @param elements the {@code NodeList} of elements to search
     * @param elementName the name of the {@code Element} to look for
     * @return the {@code Element} if found, otherwise {@code null}
     */
    public static Element findElement(NodeList elements, String elementName) {
        for (int i = 0; i < elements.getLength(); i++) {
            Node currentNode = elements.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) currentNode;
                if (currentNode.getNodeName().equals(elementName)) {
                    return currentElement;
                }
            }
        }

        return null;
    }

    /**
     * Checks if a {@code String} is non-null and non-empty.
     * @param testString the {@code String} to check
     * @return true if the {@code String} is non-null and non-empty, false otherwise
     */
    public static boolean stringSane(String testString) {
        if (testString != null && !testString.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * If the given {@code String} describes a number, it is returned. If it equals "unbounded" -1 is returned. In all other cases, the old value is returned.
     * @param old the old {@code int} to keep, if the {@code String} does not describe a number or "unbounded"
     * @param s the {@code String} used for updating
     * @return the updated {@code int}
     */
    public static int updateIntFromString(int old, String s) {
        if (s.equals("unbounded")) {
            return -1;
        }

        try {
            old = Integer.valueOf(s);
        } catch (NumberFormatException ex) {
            // really nothing to do here
        }

        return old;
    }

    /**
     * Returns a {@code String} representation of an {@code int}, which is "unbounded"
     * in case the {@code int} equals -1.
     * @param i the {@code int} to get the {@code String} representation for
     * @return the {@code String} representation of the {@code int}
     */
    public static String intToString(int i) {
        if (i == -1) {
            return "unbounded";
        } else {
            return Integer.valueOf(i).toString();
        }
    }

    /**
     * creates an {@code Integer} representation of a {@code String} converting "unbounded" to "-1" and "null" to "null".
     * @param str the {@code String} to convert
     * @return the resulting {@code Integer}
     */
    public static Integer unbStringToInteger(String str) {
        if (!stringSane(str)) {
            return null;
        }

        if (str.equals("unbounded")) {
            return -1;
        } else {
            return Integer.valueOf(str);
        }
    }
}
