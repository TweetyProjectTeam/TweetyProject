/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.sat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Loads the native libraries of the solver bindings, which are bundled as
 * resources for Linux (x86-64) and Windows. A library inside a jar is first
 * extracted to a temporary file.
 *
 * @author Lars Bengel
 */
public final class NativeLibrary {

	private NativeLibrary() {}

	/**
	 * Loads the bundled native library with the given name.
	 *
	 * @param name the name of the library, e.g. "minisat"
	 * @throws UnsatisfiedLinkError if there is no library for this platform or it cannot be loaded
	 */
	public static void load(String name) {
		String os = System.getProperty("os.name").toLowerCase();
		String suffix = os.contains("win") ? ".dll" : os.contains("nux") ? ".so" : null;
		URL url = suffix == null ? null : NativeLibrary.class.getResource("/" + name + suffix);
		if (url == null) {
			throw new UnsatisfiedLinkError("No native " + name + " library for " + System.getProperty("os.name"));
		}
		try {
			if (url.getProtocol().equals("file")) {
				System.load(Path.of(url.toURI()).toString());
			} else {
				Path file = Files.createTempFile(name, suffix);
				file.toFile().deleteOnExit();
				try (InputStream in = url.openStream()) {
					Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
				}
				System.load(file.toString());
			}
		} catch (IOException | URISyntaxException e) {
			UnsatisfiedLinkError error = new UnsatisfiedLinkError("Cannot load native " + name + " library");
			error.initCause(e);
			throw error;
		}
	}

}
