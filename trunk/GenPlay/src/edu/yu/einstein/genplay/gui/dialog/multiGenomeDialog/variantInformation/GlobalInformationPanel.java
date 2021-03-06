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

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFHeader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.ReferenceVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.SNPVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
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

	/**
	 * Maximum number of nucleotides displayed in the REF and ALT fields
	 */
	private static final int MAX_SEQUENCE_LENGTH = 20;

	/**
	 * Dimensions of the description label
	 */
	private static final Dimension DESC_LABEL_DIMENSION = new Dimension(VariantInformationDialog.WIDTH - 20, 80);

	private final Variant variant;
	private final VCFLine variantInformation;			// the variant to display the information of
	private final String genomeName;
	private final GridBagConstraints gbc;


	/**
	 * Constructor of {@link GlobalInformationPanel}
	 * Initializes all label and put them on the panel, this is the main method.
	 */
	protected GlobalInformationPanel (Variant variantDisplay, VCFLine variantInformation, String genomeName) {
		variant = variantDisplay;
		this.variantInformation = variantInformation;
		this.genomeName = genomeName;

		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridy = 0;

		addPanel();
	}


	/**
	 * Adds an association key/value to the panel.
	 * @param text			the description
	 * @param gbc			the constraint
	 */
	private void addDescriptionRow (String text) {
		if ((text != null) && !text.isEmpty()) {
			JLabel descriptionLabel = new JLabel(text);
			descriptionLabel.setPreferredSize(DESC_LABEL_DIMENSION);
			gbc.gridx = 0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.weightx = 1;
			add(descriptionLabel, gbc);
			gbc.gridy++;
		}
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

		JLabel keyLabel = new JLabel(key);
		String toolTip = valueLabel.getText();
		if ((variantInformation != null) && (header != null) && key.equals("ALT: ") && (valueLabel.getText().charAt(0) == '<')) {
			String id = valueLabel.getText().substring(1, valueLabel.getText().length() - 1);
			VCFHeaderType headerType = header.getAltHeaderFromID(id);
			if (headerType != null) {
				toolTip += " (" + headerType.getDescription() + ")";
			} else {
				System.err.println("The header for the ID field: '" + id + "' does not seem to be valid.");
			}
		}
		valueLabel.setToolTipText(toolTip);
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.weightx = 0;
		add(keyLabel, gbc);
		gbc.weightx = 1;
		gbc.gridx = 1;
		add(valueLabel, gbc);
		gbc.gridy++;
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


	private void addPanel () {
		//Variant variant = this.variant.getSource();

		// Define all input parameters and set the one we can
		String description = null;
		String genome = null;
		String group = null;
		int startPosition = variant.getStart();
		int stopPosition = -1;
		int length = -1;
		String type = variant.getType().toString();
		String idString = null;
		JLabel idLabel = null;
		String reference = null;
		String alternative = null;
		String quality = null;
		String filter = null;

		// Define the variant type
		//boolean isIndel = variant instanceof IndelVariant;
		boolean isSNP = variant instanceof SNPVariant;
		boolean isReference = variant instanceof ReferenceVariant;
		boolean isMix = variant instanceof MixVariant;

		// Stop position
		if (isSNP) {
			stopPosition = startPosition + 1;
		} else {
			stopPosition = variant.getStop();
		}

		// Length
		length = (stopPosition - startPosition) + 1;


		if (isMix) {
			description = "<html><i>When variation stripes are too small to be displayed one by one, GenPlay merges them creating a <b>" + VariantType.MIX.toString() + "</b> type.";
		} else {
			idString = variantInformation.getID();
			// id field
			if (!idString.equals(".")) {
				idLabel = getIDLabel(idString);
			}

			filter = variantInformation.getFILTER();
			if (isReference) {
				// Description
				description = "<html><i>This stripe represents the reference.\n";

				// Genome names
				genome = ProjectManager.getInstance().getAssembly().getDisplayName();

				// Type
				if (variant.getType() == VariantType.REFERENCE_INSERTION) {
					description += "A blank of synchronization is the display of an insertion that occured in an other allele/genome within the project.";
				}

				description += "</i></html>";

				// Quality
				quality = variantInformation.getQUAL();
			} else {
				// Description
				if (isSNP) {
					description = "<html><i>This stripe represents a SNP.\n";
				} else if (variant.getType() == VariantType.INSERTION) {
					description = "<html><i>This stripe represents a small insertion.\n";
				} else if (variant.getType() == VariantType.DELETION) {
					description = "<html><i>This stripe represents a small deletion.\n";
				}
				// Genome names
				if (genomeName != null) {
					genome = FormattedMultiGenomeName.getUsualName(genomeName) + " (" + FormattedMultiGenomeName.getRawName(genomeName) + ")";
					group = FormattedMultiGenomeName.getGroupName(genomeName);
				}

				// Reference
				reference = variantInformation.getREF();
				if (reference.length() > MAX_SEQUENCE_LENGTH) {
					reference = reference.substring(0, 20) + "...";
				}

				// Alternative
				alternative = variantInformation.getALT();
				if (alternative.length() > MAX_SEQUENCE_LENGTH) {
					alternative = alternative.substring(0, 20) + "...";
				}

				// Quality
				quality = "" + variant.getScore();
			}
		}
		addPanel(description, genome, group, startPosition, stopPosition, length, type, idString, idLabel, reference, alternative, quality, filter);
	}


	private void addPanel (String description, String genome, String group, int startPosition, int stopPosition, int length, String type, String idString, JLabel idLabel, String reference, String alternative, String quality, String filter) {
		addDescriptionRow(description);
		addObjectRow("Genome: ", genome);
		addObjectRow("Group: ",group);
		addObjectRow("Position: ", startPosition + " to " + stopPosition);
		addObjectRow("Length: ", "" + (length - 1));
		addObjectRow("Type: ", type);
		if (idLabel != null) {
			addLabelRow("ID: ", idLabel);
		} else {
			addObjectRow("ID: ", idString);
		}
		addObjectRow("REF: ", reference);
		addObjectRow("ALT: ", alternative);
		addObjectRow("Quality: ", "" + quality);
		gbc.weighty = 1;
		addObjectRow("Filter: ", filter);
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
			public void mouseEntered (MouseEvent arg0) {
				idLabel.setForeground(inColor);
				idLabel.setFont(inFont);
			}

			@Override
			public void mouseExited (MouseEvent arg0) {
				idLabel.setForeground(outColor);
				idLabel.setFont(outFont);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (Desktop.isDesktopSupported()) {
					try {
						// we open a browser showing information on the SNP
						String link = "http://www.ncbi.nlm.nih.gov/snp/?term=" + id + "&SITE=NcbiHome&submit=Go";
						Desktop.getDesktop().browse(new URI(link));
					} catch (Exception e1) {
						ExceptionManager.getInstance().caughtException(Thread.currentThread(), e1, "Error while opening the web browser");
					}
				}
			}
		});
		return idLabel;
	}
}
