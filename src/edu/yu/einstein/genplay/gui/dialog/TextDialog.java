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
import java.awt.Desktop;
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
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.util.Images;


/**
 * A {@link JDialog} with a {@link JEditorPane}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TextDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -4149933399246523843L; // generated ID

	private static final Dimension DIALOG_DIMENSION = new Dimension(600, 500); // dimension of the dialog
	private final JEditorPane 	jepText;	// display the text
	private final JScrollPane 	jspText;	// scroll pane for the text
	private final JButton 		jbOk;		// button ok


	/**
	 * Private constructor.
	 * Creates an instance of {@link TextDialog}
	 * @param fileURL url of the file to display
	 * @param title title of the dialog
	 * @throws IOException
	 */
	private TextDialog(String fileURL, String title) throws IOException {
		jepText = new JEditorPane(fileURL);
		jepText.setEditable(false);
		// add  hyperlink gestion
		jepText.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent evt) {
				if (Desktop.isDesktopSupported()) {
					if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						try {
							Desktop.getDesktop().browse(evt.getURL().toURI());
						} catch (Exception e) {
							ExceptionManager.getInstance().caughtException(e);
						}
					}
				}
			}
		});

		jspText = new JScrollPane(jepText);

		jbOk = new JButton("Ok");
		jbOk.addActionListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		add(jspText, c);

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(jbOk, c);

		setTitle(title);
		setIconImage(Images.getApplicationImage());
		getRootPane().setDefaultButton(jbOk);
		pack();
		setSize(DIALOG_DIMENSION);
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(getRootPane());
	}


	/**
	 * Closes the window
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		dispose();
	}


	/**
	 * Shows the dialog
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param fileURL url of the file to display
	 * @param title title of the dialog
	 * @throws IOException
	 */
	public static void showDialog(Component parent, String fileURL, String title) throws IOException {
		TextDialog textDialog = new TextDialog(fileURL, title);
		textDialog.setLocationRelativeTo(parent);
		textDialog.setVisible(true);
	}
}
