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


	private static final int LABEL_HEIGHT = 15;		// height of a label
	private static final int KEY_WIDTH = 40;		// width of a label used to display a key
	private static final int VALUE_WIDTH = 60;		// width of a label used to display a value
	private MGPosition variantInformation;			// the variant to display the information of


	/**
	 * Constructor of {@link GlobalInformationPanel}
	 * Initializes all label and put them on the panel, this is the main method.
	 */
	protected GlobalInformationPanel (MGPosition variantInformation) {
		this.variantInformation = variantInformation;


		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridy = 0;

		if (variantInformation == null) {
			gbc = addObjectRow("Genome: ", null, gbc);
			gbc = addObjectRow("Group: ", null, gbc);
			gbc = addObjectRow("Position: ", " -", gbc);
			gbc = addObjectRow("Length: ", " -", gbc);
			gbc = addObjectRow("Type: ", " -", gbc);
			gbc = addObjectRow("ID: ", null, gbc);
			gbc = addObjectRow("REF: ", null, gbc);
			gbc = addObjectRow("ALT: ", null, gbc);
			gbc = addObjectRow("Quality: ", null, gbc);
			gbc = addObjectRow("Filter: ", null, gbc);
		} else {
			VariantInterface variant = variantInformation.getVariant();
			if (variant.getType() == VariantType.MIX) {
				gbc = addObjectRow("Genome: ", null, gbc);
				gbc = addObjectRow("Group: ", null, gbc);
				gbc = addObjectRow("Position: ", variant.getStart() + " to " + variant.getStop(), gbc);
				gbc = addObjectRow("Length: ", "" + (variant.getStop() - variant.getStart() + 1), gbc);
				gbc = addObjectRow("Type: ", VariantType.MIX.toString(), gbc);
				gbc = addObjectRow("ID: ", null, gbc);
				gbc = addObjectRow("REF: ", null, gbc);
				gbc = addObjectRow("ALT: ", null, gbc);
				gbc = addObjectRow("Quality: ", null, gbc);
				gbc = addObjectRow("Filter: ", null, gbc);
			} else {
				VariantType type = variant.getType();
				int startPosition = variant.getStart();
				int stopPosition;
				if (type == VariantType.SNPS) {
					stopPosition = startPosition + 1;
				} else {
					stopPosition = variant.getStop();
				}
				String genomeFullName = variant.getVariantListForDisplay().getAlleleForDisplay().getGenomeInformation().getName();
				gbc = addObjectRow("Genome: ", FormattedMultiGenomeName.getUsualName(genomeFullName) + " (" + FormattedMultiGenomeName.getRawName(genomeFullName) + ")", gbc);
				gbc = addObjectRow("Group: ", FormattedMultiGenomeName.getGroupName(genomeFullName), gbc);
				gbc = addObjectRow("Position: ", startPosition + " to " + stopPosition, gbc);
				gbc = addObjectRow("Length: ", "" + (stopPosition - startPosition + 1), gbc);
				gbc = addObjectRow("Type: ", variant.getType().toString(), gbc);
				if (type == VariantType.SNPS && !this.variantInformation.getId().equals(".")) {
					gbc = addLabelRow("ID: ", getIDLabel(this.variantInformation.getId()), gbc);
				} else {
					gbc = addObjectRow("ID: ", this.variantInformation.getId(), gbc);
				}
				gbc = addObjectRow("REF: ", this.variantInformation.getReference(), gbc);
				gbc = addObjectRow("ALT: ", this.variantInformation.getAlternative(), gbc);
				gbc = addObjectRow("Quality: ", "" + variant.getScore(), gbc);
				gbc = addObjectRow("Filter: ", this.variantInformation.getFilter(), gbc);
			}
		}
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
		final Color inColor = Color.blue;
		final Color outColor = Color.blue;
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
	 * @return				the constraint
	 */
	private GridBagConstraints addObjectRow (String key, Object valueObject, GridBagConstraints gbc) {
		String value;
		if (valueObject == null || valueObject.toString().equals("")) {
			value = "-";
		} else {
			value = valueObject.toString();
		}
		JLabel valueLabel = new JLabel(value);
		return addLabelRow(key, valueLabel, gbc);
	}


	/**
	 * Adds an association key/value to the panel.
	 * @param key			the key
	 * @param valueObject	the value
	 * @param gbc			the constraint
	 * @return				the constraint
	 */
	private GridBagConstraints addLabelRow (String key, JLabel valueLabel, GridBagConstraints gbc) {
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
		gbc.gridx = 0;
		gbc.weightx = 0.1;
		add(keyLabel, gbc);
		gbc.weightx = 1.9;
		gbc.gridx = 1;
		add(valueLabel, gbc);
		gbc.gridy++;

		return gbc;
	}


	/**
	 * @return the height of the panel
	 */
	protected static int getPanelHeight () {
		return LABEL_HEIGHT * 10;
	}

}
