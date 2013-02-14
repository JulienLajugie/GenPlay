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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportUtils;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
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
	protected JTextField 	jtfDotValue;
	private final ValidationPane validationPanel;



	/**
	 * Constructor of {@link MultiGenomeTrackActionDialog}
	 * @param settings the export settings
	 * @param title title of the dialog
	 * @param layer the selected {@link Layer}
	 */
	public MultiGenomeTrackActionDialog (ExportSettings settings, String title, Layer<?> layer) {
		this.settings = settings;

		// Layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		initializeContentPanel();

		lengendPanel = new LegendPane(this, layer);
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
		pack();
		setLocationRelativeTo(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);
		return approved;
	}


	/**
	 * Initialize the content panel, here are the export/convert options.
	 */
	protected abstract void initializeContentPanel ();


	/////////////////////////////////////////////////////////// Optional panel methods
	protected JPanel getOptionPanel () {
		// Create the panel
		JPanel panel = new JPanel();

		// Create the labels
		JLabel optionLabel = new JLabel("Option:");
		JLabel idOptionLabel = new JLabel("\".\" in genotype:");

		// Create the value text field
		jtfDotValue = new JTextField();
		Dimension jtfDim = new Dimension(50, 21);
		ExportUtils.setComponentSize(jtfDotValue, jtfDim);
		jtfDotValue.setEnabled(false);

		// Create the radios
		JRadioButton omitButton = new JRadioButton("Omit");
		JRadioButton constantButton = new JRadioButton("Define value:");
		ButtonGroup group = new ButtonGroup();
		group.add(omitButton);
		group.add(constantButton);
		omitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {jtfDotValue.setEnabled(false);}});
		constantButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {jtfDotValue.setEnabled(true);}});
		omitButton.setSelected(true);

		// Create the layout
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;


		// Insert the option label
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel.add(optionLabel, gbc);

		// Insert the ID option label
		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 0, 0);
		panel.add(idOptionLabel, gbc);

		// Insert the omit choice
		gbc.gridx = 1;
		panel.add(omitButton, gbc);

		// Insert the constant choice
		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 5, 0);
		gbc.weighty = 1;
		panel.add(constantButton, gbc);

		// Insert the constant choice
		gbc.gridx = 2;
		gbc.insets = new Insets(0, 5, 5, 0);
		panel.add(jtfDotValue, gbc);

		return panel;
	}


	/////////////////////////////////////////////////////////// Validation panel methods
	/**
	 * Called when the dialog has been approved
	 */
	protected void approveDialog () {
		String errors = getErrors();

		if (errors.isEmpty()) {
			settings.initialize(lengendPanel.getSelectedLayers());
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


	protected void revalidate () {
		if (lengendPanel != null) {
			lengendPanel.revalidate();
		}
		if (contentPanel != null) {
			contentPanel.revalidate();
		}
		pack();
	}


	/**
	 * @return the value to use for "." in genotype, null if they have to be omitted
	 */
	public Double getDotValue () {
		Double value = null;
		if ((jtfDotValue != null) && jtfDotValue.isEnabled()) {
			try {
				value = Double.parseDouble(jtfDotValue.getText());
			} catch (Exception e) {}
		}
		return value;
	}


	/**
	 * @return the settings
	 */
	public ExportSettings getSettings() {
		return settings;
	}

}
