package net.sf.tweety.arg.delp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility functions for test classes to access KBs etc.
 *
 * @author Linda.Briesemeister
 */
class Utilities {

    /**
     * Get knowledge base as a String from the named test resource.
     * @param resourceName name of the text file with the KB
     * @return knowledge base as a String
     * @throws IOException if text file cannot be read
     */
    public static String getKB(String resourceName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                Utilities.class.getResourceAsStream(resourceName)));
        StringBuilder bufferKB = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            bufferKB.append(line);
        }
        return bufferKB.toString();
    }

}
