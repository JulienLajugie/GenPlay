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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.toolTipStripe;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * This class shows all global information about a variant.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GlobalInformationPanel extends JPanel {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -120050377469385302L;
	private static final int WIDTH = 250;	// width of the dialog
	private static final int LABEL_HEIGHT = 15;		// height of a label
	private static final int KEY_WIDTH = 40;		// width of a label used to display a key
	private static final int VALUE_WIDTH = 60;		// width of a label used to display a value
	private VariantInterface variant;
	private MGPosition variantInformation;			// the variant to display the information of
	private GridBagConstraints gbc;


	/**
	 * Constructor of {@link GlobalInformationPanel}
	 * Initializes all label and put them on the panel, this is the main method.
	 */
	protected GlobalInformationPanel (VariantInterface variant, MGPosition variantInformation) {
		this.variant = variant;
		this.variantInformation = variantInformation;

		Dimension dimension = new Dimension(WIDTH, getPanelHeight());
		setPreferredSize(dimension);
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridy = 0;

		if (variant.getType() == VariantType.MIX) {
			addMixPanel();
		} else if (variant.getType() == VariantType.BLANK) {
			addBlankPanel();
		} else if (variant.getType() == VariantType.SNPS) {
			addSNPPanel();
		} else if (variant.getType() == VariantType.INSERTION || variant.getType() == VariantType.DELETION) {
			addIndelPanel();
		} else {
			addUnknownPanel();
		}
	}


	/**
	 * Insert a description of the mixed variation
	 * @param gbc the layout constraints
	 */
	private void addMixPanel () {
		String text = "<html><i>";
		text += "When variation stripes are too small to be displayed one by one, GenPlay merged them creating a";
		text += " <b>" + VariantType.MIX.toString() + "</b> ";
		text += "type.";
		//String text = "When variation stripes are too small to be displayed one by one, GenPlay merged them creating a " + VariantType.MIX.toString() + " type.";
		addDescriptionRow(text);
		addObjectRow("Position: ", variant.getStart() + " to " + variant.getStop());
		addObjectRow("Length: ", "" + (variant.getStop() - variant.getStart() + 1));
		gbc.weighty = 1;
		addObjectRow("Type: ", VariantType.MIX.toString());
	}


	/**
	 * Insert a description of the blank of synchronization
	 * @param gbc the layout constraints
	 */
	private void addBlankPanel () {
		String text = "<html><i>";
		text += "A blank of synchronization is the display of an insertion that occured in an other allele/genome within the project.";
		text += "</i></html>";
		//String text = "A blank of synchronization is the display of an insertion that occured in an other allele/genome within the project.";
		addDescriptionRow(text);
		addObjectRow("Position: ", variant.getStart() + " to " + variant.getStop());
		addObjectRow("Length: ", "" + (variant.getStop() - variant.getStart() + 1));
		gbc.weighty = 1;
		addObjectRow("Type: ", VariantType.BLANK.toString());
	}


	/**
	 * Insert SNP variant information
	 * @param gbc the layout constraints
	 */
	private void addSNPPanel () {
		int startPosition = variant.getStart();
		int stopPosition = startPosition + 1;
		int length = 1;
		String genomeFullName = variant.getVariantListForDisplay().getAlleleForDisplay().getGenomeInformation().getName();
		addObjectRow("Genome: ", FormattedMultiGenomeName.getUsualName(genomeFullName) + " (" + FormattedMultiGenomeName.getRawName(genomeFullName) + ")");
		addObjectRow("Group: ", FormattedMultiGenomeName.getGroupName(genomeFullName));
		addObjectRow("Position: ", startPosition + " to " + stopPosition);
		addObjectRow("Length: ", "" + length);
		addObjectRow("Type: ", variant.getType().toString());
		if (!this.variantInformation.getId().equals(".")) {
			addLabelRow("ID: ", getIDLabel(this.variantInformation.getId()));
		} else {
			addObjectRow("ID: ", this.variantInformation.getId());
		}
		addObjectRow("REF: ", this.variantInformation.getReference());
		addObjectRow("ALT: ", this.variantInformation.getAlternative());
		addObjectRow("Quality: ", "" + variant.getScore());
		gbc.weighty = 1;
		addObjectRow("Filter: ", this.variantInformation.getFilter());
	}


	/**
	 * Insert Indel variant information
	 * @param gbc the layout constraints
	 */
	private void addIndelPanel () {
		int startPosition = variant.getStart();
		int stopPosition = variant.getStop();
		int length = stopPosition - startPosition + 1;
		String genomeFullName = variant.getVariantListForDisplay().getAlleleForDisplay().getGenomeInformation().getName();
		addObjectRow("Genome: ", FormattedMultiGenomeName.getUsualName(genomeFullName) + " (" + FormattedMultiGenomeName.getRawName(genomeFullName) + ")");
		addObjectRow("Group: ", FormattedMultiGenomeName.getGroupName(genomeFullName));
		addObjectRow("Position: ", startPosition + " to " + stopPosition);
		addObjectRow("Length: ", "" + length);
		addObjectRow("Type: ", variant.getType().toString());
		addObjectRow("ID: ", this.variantInformation.getId());
		addObjectRow("REF: ", this.variantInformation.getReference());
		addObjectRow("ALT: ", this.variantInformation.getAlternative());
		addObjectRow("Quality: ", "" + variant.getScore());
		gbc.weighty = 1;
		addObjectRow("Filter: ", this.variantInformation.getFilter());
	}


	/**
	 * Unsupported variant
	 * @param gbc the layout constraints
	 */
	private void addUnknownPanel () {
		gbc.weighty = 1;
		addDescriptionRow("Goodbye unknown");
	}


	/**
	 * This method is specific to the ID information of a SNP variant.
	 * It creates a label that contains a hyperlink if the ID is valid to the DBSNP website.
	 * @param id 	the SNP id
	 * @return		the label
	 */
	private JLabel getIDLabel (final String id) {
		final JLabel idLabel = new JLabel();
		final Font inFont = new Font(getFont().getName(), Font.BOLD, getFont().getSize());
		final Font outFont = new Font(getFont().getName(), Font.PLAIN, getFont().getSize());
		final Color inColor = Colors.BLUE;
		final Color outColor = Colors.BLUE;
		idLabel.setText(id);
		idLabel.setFont(outFont);
		idLabel.setForeground(outColor);
		idLabel.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (Desktop.isDesktopSupported()) {
					try {
						// we open a browser showing information on the SNP
						String link = "http://www.ncbi.nlm.nih.gov/snp/?term=" + id + "&SITE=NcbiHome&submit=Go";
						Desktop.getDesktop().browse(new URI(link));
					} catch (Exception e1) {
						ExceptionManager.handleException(getRootPane(), e1, "Error while opening the web browser");
					}
				}
			}

			@Override
			public void mouseEntered (MouseEvent arg0) {
				idLabel.setForeground(inColor);
				idLabel.setFont(inFont);
			}

			@Override
			public void mouseExited (MouseEvent arg0) {
				idLabel.setForeground(outColor);
				idLabel.setFont(outFont);
			}
		});
		return idLabel;
	}


	/**
	 * Adds an association key/value to the panel.
	 * This method prepares the information but uses the addLabelRow to add them on the panel.
	 * @param key			the key
	 * @param valueObject	the value
	 * @param gbc			the constraint
	 */
	private void addObjectRow (String key, Object valueObject) {
		String value;
		if (valueObject == null || valueObject.toString().equals("")) {
			value = "-";
		} else {
			value = valueObject.toString();
		}
		JLabel valueLabel = new JLabel(value);
		addLabelRow(key, valueLabel);
	}


	/**
	 * Adds an association key/value to the panel.
	 * @param key			the key
	 * @param valueObject	the value
	 * @param gbc			the constraint
	 */
	private void addLabelRow (String key, JLabel valueLabel) {
		Dimension keyDimension = new Dimension(KEY_WIDTH, LABEL_HEIGHT);
		Dimension valueDimension = new Dimension(VALUE_WIDTH, LABEL_HEIGHT);

		JLabel keyLabel = new JLabel(key);
		keyLabel.setSize(keyDimension);
		valueLabel.setSize(valueDimension);
		String toolTip;
		if (variantInformation != null && variantInformation.getVariant() != null && key.equals("ALT: ") && valueLabel.getText().charAt(0) == '<') {
			toolTip = valueLabel.getText() + " (";
			toolTip += variantInformation.getAltHeader(valueLabel.getText()).getDescription();
			toolTip += ")";
		} else {
			toolTip = valueLabel.getText();
		}
		valueLabel.setToolTipText(toolTip);
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.weightx = 0.1;
		add(keyLabel, gbc);
		gbc.weightx = 1.9;
		gbc.gridx = 1;
		add(valueLabel, gbc);
		gbc.gridy++;
	}
	
	
	/**
	 * Adds an association key/value to the panel.
	 * @param text			the description
	 * @param gbc			the constraint
	 */
	private void addDescriptionRow (String text) {
		Dimension keyDimension = new Dimension(WIDTH - 20, LABEL_HEIGHT * 4);
		
		JLabel descriptionLabel = new JLabel(text);
		descriptionLabel.setSize(keyDimension);
		descriptionLabel.setPreferredSize(keyDimension);
		descriptionLabel.setMinimumSize(keyDimension);
		descriptionLabel.setMaximumSize(keyDimension);

		gbc.gridx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 2;
		add(descriptionLabel, gbc);
		gbc.gridy++;
	}


	/**
	 * @return the height of the panel
	 */
	protected static int getPanelHeight () {
		return LABEL_HEIGHT * 11;
	}

}
