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
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFHeader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.IndelVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.ReferenceBlankVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.ReferenceVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.SNPVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.exception.ExceptionManager;
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
	private final VariantInterface variant;
	private final VCFLine variantInformation;			// the variant to display the information of
	private final String genomeFullName;
	private final GridBagConstraints gbc;


	/**
	 * Constructor of {@link GlobalInformationPanel}
	 * Initializes all label and put them on the panel, this is the main method.
	 */
	protected GlobalInformationPanel (VariantInterface variant, VCFLine variantInformation) {
		this.variant = variant;
		this.variantInformation = variantInformation;

		if ((variant instanceof IndelVariant) || (variant instanceof SNPVariant)) {
			genomeFullName = variant.getVariantListForDisplay().getAlleleForDisplay().getGenomeInformation().getName();
		} else if ((variant instanceof ReferenceVariant) || (variant instanceof ReferenceBlankVariant)) {
			genomeFullName = ProjectManager.getInstance().getAssembly().getDisplayName();
		} else {
			genomeFullName = "";
		}

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
		} else if (variant.getType() == VariantType.REFERENCE) {
			addReferencePanel();
		} else if (variant.getType() == VariantType.SNPS) {
			addSNPPanel();
		} else if ((variant.getType() == VariantType.INSERTION) || (variant.getType() == VariantType.DELETION)) {
			addIndelPanel();
		} else {
			addUnknownPanel();
		}
	}


	/**
	 * Insert a description of the mixed variation
	 */
	private void addMixPanel () {
		String text = "<html><i>";
		text += "When variation stripes are too small to be displayed one by one, GenPlay merged them creating a";
		text += " <b>" + VariantType.MIX.toString() + "</b> ";
		text += "type.";
		//String text = "When variation stripes are too small to be displayed one by one, GenPlay merged them creating a " + VariantType.MIX.toString() + " type.";
		addDescriptionRow(text);
		addObjectRow("Position: ", variant.getStart() + " to " + variant.getStop());
		addObjectRow("Length: ", "" + ((variant.getStop() - variant.getStart()) + 1));
		gbc.weighty = 1;
		addObjectRow("Type: ", VariantType.MIX.toString());
	}


	/**
	 * Insert a description of the reference stripe
	 */
	private void addBlankPanel () {
		String text = "<html><i>";
		text += "A blank of synchronization is the display of an insertion that occured in an other allele/genome within the project.";
		text += "</i></html>";
		addDescriptionRow(text);
		addObjectRow("Position: ", variant.getStart() + " to " + variant.getStop());
		addObjectRow("Length: ", "" + ((variant.getStop() - variant.getStart()) + 1));
		gbc.weighty = 1;
		addObjectRow("Type: ", VariantType.BLANK.toString());
	}


	/**
	 * Insert a description of the blank of synchronization
	 */
	private void addReferencePanel () {
		String text = "<html><i>";
		text += "This stripe represents the reference.";
		text += "</i></html>";
		addDescriptionRow(text);
		addObjectRow("Position: ", variant.getStart() + " to " + variant.getStop());
		addObjectRow("Length: ", "" + ((variant.getStop() - variant.getStart()) + 1));
		addObjectRow("Type: ", VariantType.REFERENCE.toString());
		String reference = this.variantInformation.getREF();
		if (reference.length() > 1) {
			reference = reference.substring(1);
		}
		gbc.weighty = 1;
		addObjectRow("REF: ", reference);
	}


	/**
	 * Insert SNP variant information
	 */
	private void addSNPPanel () {
		int startPosition = variant.getStart();
		int stopPosition = startPosition + 1;
		int length = 1;
		addObjectRow("Genome: ", FormattedMultiGenomeName.getUsualName(genomeFullName) + " (" + FormattedMultiGenomeName.getRawName(genomeFullName) + ")");
		addObjectRow("Group: ", FormattedMultiGenomeName.getGroupName(genomeFullName));
		addObjectRow("Position: ", startPosition + " to " + stopPosition);
		addObjectRow("Length: ", "" + length);
		addObjectRow("Type: ", variant.getType().toString());
		if (!this.variantInformation.getID().equals(".")) {
			addLabelRow("ID: ", getIDLabel(this.variantInformation.getID()));
		} else {
			addObjectRow("ID: ", this.variantInformation.getID());
		}
		addObjectRow("REF: ", this.variantInformation.getREF());
		addObjectRow("ALT: ", this.variantInformation.getALT());
		addObjectRow("Quality: ", "" + variant.getScore());
		gbc.weighty = 1;
		addObjectRow("Filter: ", this.variantInformation.getFILTER());
	}


	/**
	 * Insert Indel variant information
	 */
	private void addIndelPanel () {
		int startPosition = variant.getStart();
		int stopPosition = variant.getStop();
		int length = (stopPosition - startPosition) + 1;
		addObjectRow("Genome: ", FormattedMultiGenomeName.getUsualName(genomeFullName) + " (" + FormattedMultiGenomeName.getRawName(genomeFullName) + ")");
		addObjectRow("Group: ", FormattedMultiGenomeName.getGroupName(genomeFullName));
		addObjectRow("Position: ", startPosition + " to " + stopPosition);
		addObjectRow("Length: ", "" + length);
		addObjectRow("Type: ", variant.getType().toString());
		addObjectRow("ID: ", this.variantInformation.getID());
		addObjectRow("REF: ", this.variantInformation.getREF());
		addObjectRow("ALT: ", this.variantInformation.getALT());
		addObjectRow("Quality: ", "" + variant.getScore());
		gbc.weighty = 1;
		addObjectRow("Filter: ", this.variantInformation.getFILTER());
	}


	/**
	 * Unsupported variant
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
		if ((valueObject == null) || valueObject.toString().equals("")) {
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
		VCFHeader header = null;
		if ((variantInformation != null) && (variantInformation.getGenomeIndexer() instanceof VCFHeader)) {
			header = (VCFHeader) variantInformation.getGenomeIndexer();
		}

		Dimension keyDimension = new Dimension(KEY_WIDTH, LABEL_HEIGHT);
		Dimension valueDimension = new Dimension(VALUE_WIDTH, LABEL_HEIGHT);

		JLabel keyLabel = new JLabel(key);
		keyLabel.setSize(keyDimension);
		valueLabel.setSize(valueDimension);
		String toolTip;
		if ((variantInformation != null) && (header != null) && key.equals("ALT: ") && (valueLabel.getText().charAt(0) == '<')) {
			toolTip = valueLabel.getText() + " (";
			String id = valueLabel.getText().substring(1, valueLabel.getText().length() - 1);
			VCFHeaderType headerType = header.getAltHeaderFromID(id);
			toolTip += headerType.getDescription();
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
