/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 * Objects made from this class model a {@code Socket} connection and ease creating different kinds of streams
 * @author Stefan Tittel <bugreports@tittel.net>
 */
public class SocketConnection {

    private static final Logger logger = Logger.getLogger(SocketConnection.class);
    private final Socket socket;
    private InputStream inStream;
    private OutputStream outStream;


    /**
     * Create a new {@code SocketConnection}.
     * @param socket the {@code Socket} to use
     */
    public SocketConnection(Socket socket) {
        this.socket = socket;
        inStream = null;
        outStream = null;
    }

    /**
     * Get the {@code InputStream} for the current {@code SocketConnection}.
     * @return the {@code InputStream} belonging to the current SocketConnection
     * @throws IOException thrown if getting the {@code InputStream} failed
     */
    public InputStream getInputStream() throws IOException {
        if (inStream == null) {
            inStream = socket.getInputStream();
        }
        return inStream;
    }

    /**
     * Get the {@code OutputStream} for the current {@code SocketConnection}.
     * @return the {@code OutputStream} belonging to the current SocketConnection
     * @throws IOException thrown if getting the {@code OutputStream} failed
     */
    public OutputStream getOutputStream() throws IOException {
        if (outStream == null) {
            outStream = socket.getOutputStream();
        }
        return outStream;
    }

    /**
     * Disconnect the connection and close all corresponding sockets and streams
     */
    public void close() {

        try {
            if (inStream != null) {
                inStream.close();
            }
        } catch (IOException ex) {
            logger.warn("Something went wrong when trying to close the InStream", ex);
        }

        try {
            if (outStream != null) {
                outStream.close();
            }
        } catch (IOException ex) {
            logger.warn("Something went wrong when trying to close the OutStream", ex);
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            logger.warn("Something went wrong when trying to close the Socket", ex);
        }
        logger.debug("Connection closed");
    }
}
