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
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A {@link JDialog} with a {@link JEditorPane}
 * @author Julien Lajugie
 */
public final class TextDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -4149933399246523843L; // generated ID
	private static final Dimension DIALOG_DIMENSION = new Dimension(600, 500); // dimension of the dialog

	/**
	 * Shows the dialog
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param title title of the dialog
	 * @param text text to display
	 */
	public static void showDialog(Component parent, String title, String text) {
		TextDialog textDialog = new TextDialog(title, text);
		textDialog.setLocationRelativeTo(parent);
		textDialog.setVisible(true);
	}

	private final JTextArea 	jtaText;	// display the text
	private final JScrollPane 	jspText;	// scroll pane for the text
	private final JButton 		jbOk;		// button ok


	/**
	 * Private constructor.
	 * Creates an instance of {@link TextDialog}
	 * @param title title of the dialog
	 * @param text text to display
	 * @throws IOException
	 */
	private TextDialog(String title, String text) {
		jtaText = new JTextArea();
		jtaText.setText(text);
		jtaText.setEditable(false);

		jspText = new JScrollPane(jtaText);
		jspText.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);

		jbOk = new JButton("Ok");
		jbOk.addActionListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		add(jspText, c);

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(jbOk, c);

		setTitle(title);
		setIconImage(Images.getApplicationImage());
		getRootPane().setDefaultButton(jbOk);
		pack();
		setSize(DIALOG_DIMENSION);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(getRootPane());
	}


	/**
	 * Closes the window
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		dispose();
	}
}
