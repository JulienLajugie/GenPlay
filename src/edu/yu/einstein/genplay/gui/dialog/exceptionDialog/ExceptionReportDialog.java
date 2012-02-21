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
package edu.yu.einstein.genplay.gui.dialog.exceptionDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExceptionReportDialog extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = 9215524746622426426L;

	private static final int DIALOG_WIDTH 	= 400;	// Dialog width
	private static final int CONTENT_HEIGHT = 600;	// Text area height

	private static ExceptionReportDialog instance = null;		// unique instance of the singleton
	private	List<String> 	messages = new ArrayList<String>();	// List of messages
	private JTextArea 		textArea;							// Text area where messages are displayed


	/**
	 * @return an instance of a {@link ExceptionReportDialog}. 
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ExceptionReportDialog getInstance() {
		if (instance == null) {
			synchronized(ExceptionReportDialog.class) {
				if (instance == null) {
					instance = new ExceptionReportDialog();
				}
			}
		}
		return instance;
	}


	/**
	 * Constructor of {@link ExceptionReportDialog}
	 */
	private ExceptionReportDialog () {
		// Dialog layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Adds component to the dialog
		add(getErrorScrollPane(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);

		// Dialog settings
		setTitle("Errors report");
		setAlwaysOnTop(true);
		setResizable(true);
		setVisible(false);
		pack();
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 */
	public void showDialog(Component parent) {
		// Update the text area before showing the dialog
		updateTextArea();

		// Sets dialog display options
		setLocationRelativeTo(parent);
		setVisible(true);
	}


	/**
	 * Add a message to the list of message.
	 * The first line will contain the message number, the other lines will be tab indented.
	 * @param message message to add
	 */
	public void addMessage (String message) {
		messages.add(formatText(message));
	}


	/**
	 * Resets the text area, it deletes all the previous messages.
	 */
	private void resetTextArea () {
		messages = new ArrayList<String>();
		updateTextArea();
	}


	/**
	 * Updates the text of the text area adding and formatting all the messages.
	 */
	private void updateTextArea () {
		String text = "";
		for (String message: messages) {
			text += message;
		}
		textArea.setText(text);
	}
	
	
	/**
	 * Formats a message when adding.
	 * A message can contain \n character in order to give multiple message at once.
	 * The first line will contain the message number, the other lines will be tab indented.
	 * @param message 	the message
	 * @return			the formatted message
	 */
	private String formatText (String message) {
		int messageNumber = messages.size() + 1;

		String text = "";
		String prefix = messageNumber + ": ";
		String indent = getIndent(prefix);
		
		String[] array = message.split("\n");
		for (int i = 0; i < array.length; i++) {
			switch (i) {
			case 0:
				text += prefix;
				break;
			default:
				text += indent;
				break;
			}
			text += array[i] + "\n";
		}
		
		return text;
	}

	
	/**
	 * Creates an indent depending on the prefix of the first line of the message.
	 * An indent is only a white space adjusted according to the first line.
	 * @param prefix the prefix of the first line of the message
	 * @return the indent
	 */
	private String getIndent (String prefix) {
		int prefixLength = prefix.length();
		String indent = "";
		for (int i = 0; i < (prefixLength + 10); i++) {
			indent += " ";
		}
		return indent;
	}


	/**
	 * Creates the scroll pane that will contain the messages
	 * @return the scroll pane
	 */
	private JScrollPane getErrorScrollPane () {
		// Creates the text area
		textArea = new JTextArea();
		Dimension textDimension = new Dimension(DIALOG_WIDTH, CONTENT_HEIGHT);
		textArea.setMinimumSize(textDimension);
		textArea.setMargin(new Insets(0, 0, 0, 0));
		textArea.setEditable(false);

		// Creates the scroll pane
		JScrollPane contentPane = new JScrollPane(textArea);
		Dimension scrollDimension = new Dimension(DIALOG_WIDTH, CONTENT_HEIGHT);
		contentPane.setPreferredSize(scrollDimension);
		contentPane.setMinimumSize(scrollDimension);

		// Return the scroll pane
		return contentPane;
	}
	

	/**
	 * Creates the button panel. Two buttons are present:
	 * - Clear: in order to delete the text from the text area
	 * - Hide: in order to close the dialog
	 * @return the button panel
	 */
	private JPanel getButtonPanel () {
		// Creates the Clear button
		JButton clearButton = new JButton("Clear");
		clearButton.setToolTipText("Clear all messages from the text area");
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetTextArea();
			}
		});

		// Creates the Hide button
		JButton hideButton = new JButton("Hide");
		hideButton.setToolTipText("Close the dialog");
		hideButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(clearButton);
		panel.add(hideButton);

		// Returns the panel
		return panel;
	}


	/**
	 * Close the dialog.
	 */
	private void closeDialog () {
		setVisible(false);
	}

}
