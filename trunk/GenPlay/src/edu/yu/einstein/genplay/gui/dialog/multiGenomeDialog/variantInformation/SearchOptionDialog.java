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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.variantInformation;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.util.Images;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SearchOptionDialog extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = 5331086340183980251L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private int				approved 			= CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	private SearchOption options;
	private JCheckBox jcbIncludeInsertion;
	private JCheckBox jcbIncludeDeletion;
	private JCheckBox jcbIncludeSNP;
	private JCheckBox jcbIncludeReference;
	private JCheckBox jcbIncludeHeterozygote;
	private JCheckBox jcbIncludeHomozygote;
	private JCheckBox jcbIncludeNoCall;


	/**
	 * Constructor of {@link SearchOptionDialog}
	 */
	public SearchOptionDialog () {
		// Dialog settings
		setTitle("Advanced Options");
		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Add content
		add(getOptionPanel(), gbc);

		gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 10, 3, 10);
		add(getValidationPanel(), gbc);

		// Set dimension
		pack();
		Dimension dimension = new Dimension(200, this.getPreferredSize().height);
		setPreferredSize(dimension);
		setSize(dimension);
	}


	/**
	 * Method for showing the dialog box.
	 * @param parent the parent component
	 * @param options search options
	 * @return true if the dialog has been approved, false otherwise
	 */
	public int showDialog (Component parent, SearchOption options) {
		this.options = options;
		if (options == null) {
			new SearchOption();
		}
		initializeOptions();
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}


	/**
	 * Initializes the check boxes
	 */
	private void initializeOptions () {
		jcbIncludeInsertion.setSelected(options.includeInsertion);
		jcbIncludeDeletion.setSelected(options.includeDeletion);
		jcbIncludeSNP.setSelected(options.includeSNP);
		jcbIncludeReference.setSelected(options.includeReference);
		jcbIncludeHeterozygote.setSelected(options.includeHeterozygote);
		jcbIncludeHomozygote.setSelected(options.includeHomozygote);
		jcbIncludeNoCall.setSelected(options.includeNoCall);
	}


	/**
	 * @return the option panel
	 */
	private JPanel getOptionPanel () {
		// Create elements
		JLabel jlTitle01 = new JLabel("Include (OR):");
		jlTitle01.setToolTipText("OR operator policy between variant types.");
		jcbIncludeInsertion = new JCheckBox("Insertion(s)");
		jcbIncludeDeletion = new JCheckBox("Deletion(s)");
		jcbIncludeSNP = new JCheckBox("SNP(s)");
		jcbIncludeReference = new JCheckBox("Reference(s)");
		jcbIncludeNoCall = new JCheckBox("No call(s)");
		JLabel jlTitle02 = new JLabel("As (OR):");
		jlTitle02.setToolTipText("The selected variant type(s) must belong at least at one of the following genotype categories.");
		jcbIncludeHeterozygote = new JCheckBox("Heterozygote(s)");
		jcbIncludeHomozygote = new JCheckBox("Homozygote(s)");


		// Create the panel
		JPanel panel = new JPanel();

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Add content
		gbc.insets = new Insets(5, 0, 5, 0);
		panel.add(jlTitle01, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 0, 10);
		panel.add(jcbIncludeInsertion, gbc);

		gbc.gridy++;
		panel.add(jcbIncludeDeletion, gbc);

		gbc.gridy++;
		panel.add(jcbIncludeSNP, gbc);

		gbc.gridy++;
		panel.add(jcbIncludeReference, gbc);

		gbc.gridy++;
		panel.add(jcbIncludeNoCall, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 5, 0);
		panel.add(jlTitle02, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 0, 10);
		panel.add(jcbIncludeHeterozygote, gbc);

		gbc.gridy++;
		panel.add(jcbIncludeHomozygote, gbc);



		return panel;
	}


	/**
	 * Creates the panel that contains OK and CANCEL buttons
	 * @return the panel
	 */
	private JPanel getValidationPanel () {
		// Creates the ok button
		JButton jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = APPROVE_OPTION;
				options.setOptions(jcbIncludeInsertion.isSelected(),
						jcbIncludeDeletion.isSelected(),
						jcbIncludeSNP.isSelected(),
						jcbIncludeReference.isSelected(),
						jcbIncludeHeterozygote.isSelected(),
						jcbIncludeHomozygote.isSelected(),
						jcbIncludeNoCall.isSelected());
				setVisible(false);
			}
		});

		// Creates the cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = CANCEL_OPTION;
				setVisible(false);
			}
		});

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}


	/**
	 * @return the search options
	 */
	public SearchOption getOptions() {
		return options;
	}

}
