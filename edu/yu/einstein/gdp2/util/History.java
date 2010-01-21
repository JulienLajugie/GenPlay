/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.util;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * The history class provides tools to manage an history of the action performed on a BinList.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class History {
	private ArrayList<String> history;
	private ArrayList<String> undo;
	private ArrayList<String> redo;


	/**
	 * Public constructor. Initializes the history.
	 */
	public History() {
		super();
		history = new ArrayList<String>();
		undo = null;
		redo = null;
	}


	/**
	 * @return The history as a String[].
	 */
	public String[] get() {
		String[] a = new String[history.size()];
		return history.toArray(a);
	}	


	/**
	 * @return The number of element in the history.
	 */
	public int size() {
		return history.size();
	}


	/**
	 * Adds an element to the history.
	 * @param s String describing the last action performed.
	 */
	public void add(String s) {
		if (undo == null) {
			undo = new ArrayList<String>();
		} 
		if (history.size() > 0) {
			undo.add(history.get(history.size() - 1));
		}
		history.add(s);
		redo = null;	
	}
	
	
	/**
	 * Adds an element to the history with the specified color
	 * @param s string to add
	 * @param color color of the text
	 */
	public void add(String s, Color color) {
		String rgb = Integer.toHexString(color.getRGB());
		rgb = rgb.substring(2, rgb.length());
		s = "<html><p style=\"color:#" + rgb + "\">" + s + "</p></html>";
		add(s);
	}
	

	/**
	 * Undoes the last entry in the history.
	 */
	public void undo() {
		if (undo != null) {
			redo = history;
			history = undo;
			undo = null;
		}
	}


	/**
	 * Redoes the last undone action. 
	 */
	public void redo() {
		if (redo != null) {
			undo = history;
			history = redo;
			redo = null;
		}
	}


	/**
	 * Resets the history.
	 */
	public void reset() {
		/*undo = (ArrayList<String>) history.clone();
		history.clear();
		redo = null;*/
		add("RESET", Color.red);
	}


	/**
	 * @return A string containing all the history. Each action is separated by a new line.
	 */
	@Override
	public String toString() {
		if ((history == null) || (history.size() == 0)) {
			return null;
		}
		String returnString = new String();
		for(String s : history) {
			returnString += s;
			returnString += "\n";
		}
		return returnString;
	}


	/**
	 * Sets the last entry in the history as an error.
	 */
	public void setLastAsError() {
		if (history.size() > 0) {			
			history.set(history.size() - 1, history.get(history.size() - 1) + " : ERROR");
		}
	}


	/**
	 * Save the history in a file.
	 * @param file
	 * @throws IOException
	 */
	public void save(File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(this.toString());
		writer.close();
	}
}
