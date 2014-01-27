/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.exceptionDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import edu.yu.einstein.genplay.core.email.GenPlayEmail;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class ExceptionReportDialog extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = 9215524746622426426L;

	private static final int 	DIALOG_WIDTH  	= 350;	// Dialog width
	private static final int 	REPORT_HEIGHT 	= 200;	// Text area height

	private JTextArea 			textArea;				// Area displaying the text of the exception report
	private JScrollPane 		contentPane;			// Scrollpane containing the text area dispaying the report
	private String 				report			= "";	// Exception report showed in the dialog


	/**
	 * Constructor of {@link ExceptionReportDialog}
	 */
	public ExceptionReportDialog () {
		// Dialog layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Adds component to the dialog
		add(getMessagePane(), BorderLayout.NORTH);
		add(getErrorScrollPane(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);

		// Default close operation
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		// Dialog settings
		setTitle("Exception report");
		setIconImage(Images.getApplicationImage());
		setResizable(true);
		setVisible(false);
		pack();
	}


	/**
	 * Creates the button panel. Two buttons are present:
	 * - Ok: Close the dialog
	 * - Send Report: Send report by email
	 * @return the button panel
	 */
	private JPanel getButtonPanel () {
		// Creates the Clear button
		JButton okButton = new JButton("Ok");
		okButton.setToolTipText("Close the dialog");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});

		// Creates the Hide button
		JButton sendButton = new JButton("Send Report");
		sendButton.setToolTipText("Send report by email");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(okButton);
		panel.add(sendButton);

		// Returns the panel
		return panel;
	}


	/**
	 * Creates the scroll pane that will contain the messages
	 * @return the scroll pane
	 */
	private JScrollPane getErrorScrollPane () {
		// Creates the text area
		textArea = new JTextArea();
		Dimension textDimension = new Dimension(DIALOG_WIDTH, REPORT_HEIGHT);
		textArea.setMinimumSize(textDimension);
		textArea.setMargin(new Insets(0, 0, 0, 0));
		textArea.setEditable(false);
		textArea.setText(report);

		// Creates the scroll pane
		contentPane = new JScrollPane(textArea);
		Dimension scrollDimension = new Dimension(DIALOG_WIDTH, REPORT_HEIGHT);
		contentPane.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		contentPane.setPreferredSize(scrollDimension);
		contentPane.setMinimumSize(scrollDimension);

		// Return the scroll pane
		return contentPane;
	}


	private JPanel getMessagePane () {
		JLabel label1 = new JLabel("An unexepected error occured.");
		JLabel label2 = new JLabel("Please see the report below for further information.");

		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.insets = new Insets(5, 5, 5, 0);

		panel.add(label1, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 5, 10, 0);
		panel.add(label2, gbc);

		return panel;
	}


	/**
	 * @return The exception report showed in this dialog
	 */
	public String getReport() {
		return report;
	}


	/**
	 * Send the report
	 */
	private void send () {
		if (GenPlayEmail.send("[GenPlay] Error report", report)) {
			setReport("");
			setVisible(false);
			JOptionPane.showMessageDialog(this, "The report has been sent.", "Message", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "The report could not been sent.", "Message Error", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Sets the report to display in this dialog
	 * @param report
	 */
	public void setReport(String report) {
		this.report = report;
		textArea.setText(report);
		contentPane.getVerticalScrollBar().setValue(contentPane.getVerticalScrollBar().getMaximum());
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 */
	public void showDialog(Component parent) {
		// Sets dialog display options
		setLocationRelativeTo(parent);
		setVisible(true);
	}
}
