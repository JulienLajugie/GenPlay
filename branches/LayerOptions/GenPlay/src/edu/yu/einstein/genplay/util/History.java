/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.util;

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

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * The history class provides tools to manage an history of the action performed
 * on a BinList.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class History implements Serializable {

	private static final long serialVersionUID = -1385318410072807666L; // generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private List<String> history; 	// history
	private List<String> redo; 		// redo history


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(history);
		out.writeObject(redo);
	}


	/**
	 * Method used of unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		history = (List<String>) in.readObject();
		redo = (List<String>) in.readObject();
	}


	/**
	 * Public constructor. Initializes the history.
	 */
	public History() {
		super();
		history = new ArrayList<String>();
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
	 * @param s String describing the last action performed.
	 */
	public void add(String s) {
		history.add(s);
		redo.clear();
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
		add("RESET", Colors.RED);
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


	/**
	 * Performs a deep clone of the current {@link History}.
	 * @return a new History
	 */
	public History deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((History) ois.readObject());
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
			return null;
		}
	}
}
