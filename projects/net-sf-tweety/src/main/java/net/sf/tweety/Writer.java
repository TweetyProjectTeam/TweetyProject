package net.sf.tweety;

import java.io.*;

/**
 * This class represents an abstract writer for
 * writing objects into the file system. 
 * 
 * @author Matthias Thimm
 */
public abstract class Writer {

	/**
	 * 
	 */
	private Object obj;
	
	/**
	 * Creates a new writer for the given object.
	 * @param obj an object.
	 */
	public Writer(Object obj){
		this.obj = obj;
	}
	
	/**
	 * Sets the object of this writer.
	 */
	public void setObject(Object obj){
		this.obj = obj;
	}
	
	/**
	 * Returns the object of this writer.
	 * @return the object of this writer.
	 */
	public Object getObject(){
		return this.obj;
	}
	
	/**
	 * Writes the object into a string.
	 * @return the string representing the object.
	 */
	public abstract String writeToString();
	
	/**
	 * Writes the object into the given file.
	 * @param filename the name of the file.
	 */
	public void writeToFile(String filename) throws IOException{
		String s = this.writeToString();
		// TODO add check for overwriting and error handling
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		out.write(s);
		out.close();
	}
}
