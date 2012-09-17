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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.util.Images;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class MultiGenomeTrackActionDialog extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -2219314710448039324L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private int				approved 			= CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	protected static final int MIN_DIALOG_WIDTH = 400;
	protected final ExportSettings settings;


	protected JPanel contentPanel;
	private final LegendPane lengendPanel;
	private final ValidationPane validationPanel;



	/**
	 * Constructor of {@link MultiGenomeTrackActionDialog}
	 * @param settings the export settings
	 * @param title title of the dialog
	 */
	public MultiGenomeTrackActionDialog (ExportSettings settings, String title) {
		this.settings = settings;

		// Layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		initializeContentPanel();

		lengendPanel = new LegendPane(settings.getVariationMap(), settings.getFileList());
		validationPanel = new ValidationPane(this);

		add(lengendPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
		add(validationPanel, BorderLayout.SOUTH);

		// Dialog settings
		setIconImage(Images.getApplicationImage());
		setTitle(title);
		//setAlwaysOnTop(true);
		setResizable(false);
		setVisible(false);
		pack();
	}


	/**
	 * Shows the component.
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		setLocationRelativeTo(parent);
		setModal(true);
		setVisible(true);
		return approved;
	}


	/**
	 * Initialize the content panel, here are the export/convert options.
	 */
	protected abstract void initializeContentPanel ();


	/////////////////////////////////////////////////////////// Validation panel methods
	/**
	 * Called when the dialog has been approved
	 */
	protected void approveDialog () {
		String errors = getErrors();

		if (errors.isEmpty()) {
			approved = APPROVE_OPTION;
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(getRootPane(), errors, "Error", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	/**
	 * Called when the dialog has been canceled
	 */
	protected void cancelDialog () {
		approved = CANCEL_OPTION;
		setVisible(false);
	}


	/**
	 * Sets the text of the "OK" button
	 * @param text text to set
	 */
	protected void setValidationButtonText (String text) {
		validationPanel.setValidationButtonText(text);
	}


	protected abstract String getErrors ();
	///////////////////////////////////////////////////////////
}
