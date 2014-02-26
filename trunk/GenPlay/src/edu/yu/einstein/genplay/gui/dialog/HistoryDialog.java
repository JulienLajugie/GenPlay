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
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.util.FileChooser;
import edu.yu.einstein.genplay.util.History;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;


/**
 * A history frame.
 * @author Julien Lajugie
 */
public final class HistoryDialog extends JDialog {

	private static final long serialVersionUID = 9059804292908454294L; // generated ID
	private static final Dimension HISTORY_FRAME_DIMENSION =
			new Dimension(500, 300);							// dimension of the frame
	/**
	 * Show the history dialog box
	 * @param parent parent component
	 * @param trackName name of a curve
	 * @param history history of a curve
	 */
	public static void showHistoryDialog(Component parent, String trackName, History history) {
		new HistoryDialog(parent, trackName, history).setVisible(true);
	}
	private final JList 		jlHistory;					// list containing the history
	private final JScrollPane   jspHistory;					// scroll pane containing the history list
	private final JButton 		jbSave;						// save button
	private final JButton 		jbClose;					// close button
	private final String 		layerName;					// name of a layer
	private final History 		history;					// history of a layer


	/**
	 * Public constructor.
	 * @param parent parent component
	 * @param layerName name of a layer
	 * @param history history of a layer
	 */
	private HistoryDialog(Component parent, String layerName, History history) {
		super();
		setTitle(layerName);
		setIconImages(Images.getApplicationImages());
		setModalityType(ModalityType.APPLICATION_MODAL);
		this.layerName = layerName;
		this.history = history;

		jlHistory = new JList(history.get());
		//jlHistory.get()
		jspHistory = new JScrollPane(jlHistory);
		jspHistory.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		jbClose = new JButton("Close");
		jbSave = new JButton("Save");

		initComponent();
		setSize(HISTORY_FRAME_DIMENSION);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
		getRootPane().setDefaultButton(jbClose);
	}


	/**
	 * Initializes the component and sub components.
	 */
	private void initComponent() {
		jlHistory.setLayoutOrientation(JList.VERTICAL);
		jlHistory.setVisibleRowCount(history.size());

		jbClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		jbSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				saveHistory();
			}
		});

		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 0.99;
		add(jspHistory, c);

		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weighty = 0.01;
		add(jbSave, c);

		c.gridx = 1;
		c.gridy = 1;
		add(jbClose, c);
	}


	/**
	 * Saves the history in a file.
	 */
	public void saveHistory() {
		FileNameExtensionFilter webPageFilter = new FileNameExtensionFilter("Web Pages", "html", "htm");
		File selectedFile = FileChooser.chooseFile(rootPane, FileChooser.SAVE_FILE_MODE, "Save Layer History", new FileFilter[] {webPageFilter}, false, new File(layerName + ".htm"));
		if(selectedFile != null) {
			try {
				history.save(selectedFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(getRootPane(), "Error while saving the history", "Error", JOptionPane.ERROR_MESSAGE);
				ExceptionManager.getInstance().caughtException(e);
			}
		}
	}
}
