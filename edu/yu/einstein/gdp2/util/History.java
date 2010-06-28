/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.util;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The history class provides tools to manage an history of the action performed
 * on a BinList.
 * 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class History implements Serializable {

	private static final long serialVersionUID = -1385318410072807666L; // generated
																		// ID
	/**
	 */
	private List<String> history; // history
	// private List<String> undo; // undo history
	/**
	 */
	private List<String> redo; // redo history

	/**
	 * Public constructor. Initializes the history.
	 */
	public History() {
		super();
		history = new ArrayList<String>();
		// undo = null;
		redo = new ArrayList<String>();
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
	 * 
	 * @param s
	 *            String describing the last action performed.
	 */
	public void add(String s) {
		/*
		 * if (history.size() > 0) { Collections.copy(undo, history); }
		 */
		history.add(s);
		redo.clear();
	}

	/**
	 * Adds an element to the history with the specified color
	 * 
	 * @param s
	 *            string to add
	 * @param color
	 *            color of the text
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
		/*
		 * if (undo != null) { redo = history; history = undo; undo = null; }
		 */
		int lastIndex = history.size() - 1;
		String lastAction = history.get(lastIndex);
		redo.add(lastAction);
		history.remove(lastIndex);
	}

	/**
	 * Redoes the last undone action.
	 */
	public void redo() {
		if ((redo != null) && (!redo.isEmpty())) {
			// undo = history;
			// history = redo;
			// redo = null;
			int lastRedoIndex = redo.size() - 1;
			String lastRedoAction = redo.get(lastRedoIndex);
			history.add(lastRedoAction);
			redo.remove(lastRedoIndex);
		}
	}

	/**
	 * Resets the history.
	 */
	public void reset() {
		add("RESET", Color.red);
	}

	/**
	 * @return A string containing all the history. Each action is separated by
	 *         a new line.
	 */
	@Override
	public String toString() {
		if ((history == null) || (history.size() == 0)) {
			return null;
		}
		String returnString = new String();
		for (String s : history) {
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
			history.set(history.size() - 1, history.get(history.size() - 1)
					+ " : ERROR");
		}
	}

	/**
	 * Save the history in a file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void save(File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(this.toString());
		writer.close();
	}

	/**
	 * Performs a deep clone of the current {@link History}.
	 * 
	 * @return a new History
	 */
	public History deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos
					.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((History) ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
