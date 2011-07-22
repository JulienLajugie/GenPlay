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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.track;

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
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.Variant;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * This class shows variant stripe information.
 * @author Nicolas Fourel
 */
public class ToolTipStripe extends JDialog {

	private static final long serialVersionUID = -4932470485711131874L;

	private final String fields[] = {"Genome:", "Chromosome:", "Type:", "Position:", "First allele:", "Second allele:", "Quality:"};
	private final int LABEL_WIDTH = 110;
	private final int VALUE_WIDTH = 180;
	private final int LINE_HEIGHT = 20;
	private final int LINE_NUMBER = fields.length;
	private final int OFFSET_HEIGHT = 7 * LINE_NUMBER;
	private final int OFFSET_WIDTH = 40;

	private TrackGraphics<?> origin;
	private JLabel label[];
	private JLabel value[];
	private JPanel pane;
	private Variant variant;
	private JButton nextVariant;
	private JButton previousVariant;
	private Variant[] variants;


	/**
	 * Constructor of {@link ToolTipStripe}
	 */
	protected ToolTipStripe (TrackGraphics<?> origin) {
		super(MainFrame.getInstance());
		this.origin = origin;
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);

		int height = LINE_NUMBER * LINE_HEIGHT + OFFSET_HEIGHT;
		int width = LABEL_WIDTH + VALUE_WIDTH + OFFSET_WIDTH;
		Dimension dialogDim = new Dimension(width, height);
		setComponentSize(this, dialogDim);
	}


	/**
	 * Method for showing the dialog box.
	 * @param variant	variant to show information
	 * @param X			X position on the screen
	 * @param Y			Y position on the screen
	 */
	protected void show (Variant variant, int X, int Y) {
		this.variant = variant;
		variants = origin.getShortVariantList(this.variant);
		initContent(variant);
		setLocation(X, Y);
		setVisible(true);
	}


	/**
	 * Initializes the content of the dialog box according to a variant
	 * @param variant	variant to show information
	 */
	private void initContent (Variant variant) {
		// Panel
		pane = new JPanel();

		int height = LINE_NUMBER * LINE_HEIGHT;
		int width = LABEL_WIDTH + VALUE_WIDTH;
		Dimension paneDim = new Dimension(width, height);
		setComponentSize(pane, paneDim);

		// Layout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		pane.setLayout(layout);

		// Labels
		label = new JLabel[fields.length];
		value = new JLabel[fields.length];
		Dimension labelDim = new Dimension(LABEL_WIDTH, LINE_HEIGHT);
		Dimension valueDim = new Dimension(VALUE_WIDTH, LINE_HEIGHT);
		for (int i = 0; i < fields.length; i++) {
			// initialization
			label[i] = new JLabel(fields[i]);
			value[i] = new JLabel();

			// value text
			value[i].setText(getText(i));

			// size
			setComponentSize(label[i], labelDim);
			setComponentSize(value[i], valueDim);

			// label position
			constraints.gridx = 0;
			constraints.gridy = i;
			constraints.insets = new Insets(0, 0, 0, 0);
			pane.add(label[i], constraints);

			// value position
			constraints.gridx = 1;
			constraints.gridy = i;
			constraints.insets = new Insets(0, 0, 0, 0);
			pane.add(value[i], constraints);
		}

		// Buttons
		Dimension buttonDim = new Dimension(20 * 3, 20);
		Insets inset = new Insets(0, 0, 0, 0);

		// Next variant
		nextVariant = new JButton("next");
		nextVariant.setSize(buttonDim);
		nextVariant.setMinimumSize(buttonDim);
		nextVariant.setMaximumSize(buttonDim);
		nextVariant.setPreferredSize(buttonDim);
		nextVariant.setToolTipText("next");
		nextVariant.setMargin(inset);
		nextVariant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				initVariant(nextVariant);
			}
		});


		// Previous variant
		previousVariant = new JButton("previous");
		previousVariant.setSize(buttonDim);
		previousVariant.setMinimumSize(buttonDim);
		previousVariant.setMaximumSize(buttonDim);
		previousVariant.setPreferredSize(buttonDim);
		previousVariant.setToolTipText("previous");
		previousVariant.setMargin(inset);
		previousVariant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				initVariant(previousVariant);
			}
		});

		// add buttons
		constraints.gridx = 0;
		constraints.gridy++;
		pane.add(previousVariant, constraints);

		constraints.gridx = 1;
		pane.add(nextVariant, constraints);

		add(pane);
	}


	/**
	 * Updates the dialog box content.
	 * All labels (values) are updated
	 */
	private void updateContent () {
		for (int i = 0; i < fields.length; i++) {
			value[i].setText(getText(i));
		}
	}


	/**
	 * Gets the specific text for a label.
	 * The right order is given by the "field" object.
	 * @param index	index of the value (according to the field)
	 * @return the related value
	 */
	private String getText (int index) {
		String text = ""; 
		switch (index) {
		case 0:
			text += "NC";
			break;
		case 1:
			text += "NC";
			break;
		case 2:
			text += variant.getType().toString();
			break;
		case 3:
			text += variant.getStart() + " to " + variant.getStop();
			break;
		case 4:
			text += getAllelePresence(variant.isOnFirstAllele());
			break;
		case 5:
			text += getAllelePresence(variant.isOnSecondAllele());
			break;
		case 6:
			text += variant.getQualityScore();
			break;
		default:
			break;
		}
		return text;
	}


	/**
	 * @return the variant
	 */
	public Variant getVariant() {
		return variant;
	}



	private void initVariant (JButton button) {
		Variant newVariant;
		if (button.equals(nextVariant)) {
			newVariant = variants[1]; 
		} else {
			newVariant = variants[0];
		}

		if (newVariant == null) {
			button.setEnabled(false);
		} else {
			variants = origin.getShortVariantList(newVariant);
			this.variant = newVariant;
			updateContent();
			//int variantStart = variant.getStart() + ((variant.getStop() - variant.getStart()) / 2);
			int variantStart = variant.getStart();
			int width = MainFrame.getInstance().getControlPanel().getGenomeWindow().getSize();
			int startWindow = variantStart - (width / 2);
			int stopWindow = startWindow + width;
			Chromosome chromosome = MainFrame.getInstance().getControlPanel().getGenomeWindow().getChromosome();
			GenomeWindow genomeWindow = new GenomeWindow(chromosome, startWindow, stopWindow);
			MainFrame.getInstance().getControlPanel().setGenomeWindow(genomeWindow);
		}
	}


	private void setComponentSize (Component component, Dimension dim) {
		component.setMinimumSize(dim);
		component.setMaximumSize(dim);
		component.setPreferredSize(dim);
		component.setSize(dim);
	}


	private String getAllelePresence (boolean b) {
		String result;
		if (b) {
			result = "present";
		} else {
			result = "absent";
		}
		return result;
	}

}