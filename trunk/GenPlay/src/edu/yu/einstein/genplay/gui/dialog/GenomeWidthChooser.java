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

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.NumberFormats;


/**
 * A dialog box used to choose a genome width value (in bp)
 * @author Julien Lajugie
 */
public final class GenomeWidthChooser extends JDialog {

	private static final long 			serialVersionUID = -1145665933228762636L; 	// Generated serial number
	private static final Dimension		DIALOG_DIMENSION = new Dimension(230, 130);	// size of the dialog
	private static JScrollBar 			jsbGenomeWidth;								// ScrollBar use to choose sigma
	private static JFormattedTextField 	jftfGenomeWidth;							// TextField for the value of sigma
	private static JButton 				jbOk;										// Button Ok
	private static JButton 				jbCancel;									// Button Cancel
	private static int 					windowSize;									// Greatest bin size of the selected curves
	private static Integer 				validGenomeWidth;							// A valid value for sigma
	private static boolean 				validated;									// True if OK has been pressed
	private static boolean 				showSigma;									// show the sigma label if true
	private static JLabel 				jlSigma;									// label showing the value of sigma

	/**
	 * Displays a GenomeWidthChooser dialog, and returns
	 * an integer value for the moving window size.
	 * @param parent The parent {@link Component} from which the dialog is displayed..
	 * @param aWindowSize a size of bins.
	 * @param showSigma set to true to display sigma value
	 * @return An Integer value of the moving window size if OK has been pressed. Null otherwise.
	 */
	public static Integer getMovingWindowSize(Component parent, int aWindowSize, boolean showSigma) {
		windowSize = aWindowSize;
		validGenomeWidth = windowSize * 10;
		GenomeWidthChooser.showSigma = showSigma;
		GenomeWidthChooser FS = new GenomeWidthChooser(parent);
		FS.setVisible(true);
		if(validated) {
			return validGenomeWidth;
		} else {
			return null;
		}
	}


	/**
	 * Updates the text of the sigma label
	 */
	private static void updateSigmaLabeText(int genomeWidth) {
		if (jlSigma.isVisible()) {
			int sigma = genomeWidth / 4;
			jlSigma.setText("Sigma = " + NumberFormats.getPositionFormat().format(sigma));
		}
	}


	/**
	 * Private constructor. Used internally to create a {@link GenomeWidthChooser} dialog.
	 * @param parent The parent {@link Component} from which the dialog is displayed.
	 */
	private GenomeWidthChooser(Component parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		validated = false;
		initComponent();
		setTitle("Moving Window Value :");
		setIconImages(Images.getApplicationImages());
		getRootPane().setDefaultButton(jbOk);
		setLocationRelativeTo(parent);
	}


	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		jsbGenomeWidth = new JScrollBar(Adjustable.HORIZONTAL, validGenomeWidth, 0, windowSize, windowSize * 1000);
		jsbGenomeWidth.setBlockIncrement(windowSize);
		jsbGenomeWidth.setUnitIncrement(windowSize);
		jsbGenomeWidth.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				jsbSigmaAdjustmentValueChanged();
			}
		});

		jftfGenomeWidth = new JFormattedTextField(NumberFormats.getPositionFormat());
		jftfGenomeWidth.setValue(validGenomeWidth);
		jftfGenomeWidth.setColumns(8);
		jftfGenomeWidth.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				jftfSigmaPropertyChange();
			}
		});

		jftfGenomeWidth.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {}
			@Override
			public void insertUpdate(DocumentEvent e) {
				jftfSigmaDocumentChange();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				jftfSigmaDocumentChange();
			}
		});

		jlSigma = new JLabel();
		jlSigma.setVisible(showSigma);
		updateSigmaLabeText(validGenomeWidth);

		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();
			}
		});

		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();
			}
		});

		// we want the size of the two buttons to be equal
		jbOk.setPreferredSize(jbCancel.getPreferredSize());

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		add(jsbGenomeWidth, c);

		c.fill = GridBagConstraints.NONE;
		c.gridy = 1;
		add(jftfGenomeWidth, c);

		c.gridy = 2;
		add(jlSigma, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 3;
		add(jbOk, c);

		c.gridx = 1;
		add(jbCancel, c);

		setSize(DIALOG_DIMENSION);
		setResizable(false);
	}


	/**
	 * Closes the dialog. No action are performed.
	 */
	private void jbCancelActionPerformed() {
		dispose();

	}


	/**
	 * Closes the dialog. Sets validated to true so the main function can return the two selected curves.
	 */
	private void jbOkActionPerformed() {
		validated = true;
		dispose();
	}


	/**
	 * Updates sigma during input in the text field
	 */
	private void jftfSigmaDocumentChange() {
		Integer currentGenomicWidth = null;
		try {
			currentGenomicWidth = Integer.parseInt(jftfGenomeWidth.getText());
		} catch (NumberFormatException e) {
			currentGenomicWidth = null;
		}
		if (currentGenomicWidth == null) {
			try {
				currentGenomicWidth = NumberFormats.getPositionFormat().parse(jftfGenomeWidth.getText()).intValue();
			} catch (ParseException e) {
				currentGenomicWidth = null;
			}
		}
		if (currentGenomicWidth != null) {
			updateSigmaLabeText(currentGenomicWidth);
		}
	}


	/**
	 * Changes the text of textField jftfGenomicWidth when the scrollBar jftfSigma is used.
	 */
	private void jftfSigmaPropertyChange() {
		int currentGenomicWidth = ((Number)(jftfGenomeWidth.getValue())).intValue();
		if((currentGenomicWidth < windowSize) || (currentGenomicWidth > (windowSize * 100000))) {
			JOptionPane.showMessageDialog(getRootPane(), "The moving window value must be between " + windowSize + " and " + (windowSize * 100) + ".", "Incorrect sigma value.", JOptionPane.WARNING_MESSAGE);
			jftfGenomeWidth.setValue(validGenomeWidth);
		}
		else {
			validGenomeWidth = currentGenomicWidth;
			jsbGenomeWidth.setValue(currentGenomicWidth);
			updateSigmaLabeText(validGenomeWidth);
		}
	}


	/**
	 * Changes the position of the scrollBar jftfSigma when the value of the textField jsbSigma changes.
	 */
	private void jsbSigmaAdjustmentValueChanged() {
		validGenomeWidth = jsbGenomeWidth.getValue();
		jftfGenomeWidth.setValue(validGenomeWidth);
		updateSigmaLabeText(validGenomeWidth);
	}
}
