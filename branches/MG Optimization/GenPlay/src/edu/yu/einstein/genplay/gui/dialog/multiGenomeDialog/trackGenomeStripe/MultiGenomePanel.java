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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import javax.swing.JDialog;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.MultiGenomeStripe;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe.selectionPanel.GenomeSelectionPanel;


/**
 * 
 * @author Nicolas Fourel
 */
public class MultiGenomePanel extends JDialog {

	private static final long serialVersionUID = 9148947342812016201L;

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private static final int HEADER_HEIGHT = 40;
	private static final int ROW_HEIGHT = 25;
	private static final int HORIZONTAL_INSET = 25;
	private static final int CELL_VCF_TYPE_WIDTH = 60;
	private static final int COLOR_BUTTON_SIDE = 13;

	private static final int THRESHOLD_PANEL_HEIGHT = 50;
	private static final int THRESHOLD_LABEL_WIDTH = 100;
	private static final int THRESHOLD_LINE_HEIGHT = 20;
	private static final int THRESHOLD_INSET = 15;
	
	private static final int DESCRIPTION_PANEL_HEIGHT = 70;
	private static final int DESCRIPTION_LINE_HEIGHT = 20;
	private static final int DESCRIPTION_INSET = 15;
	private static final int DESCRIPTION_LABEL_WIDTH = 80;
	
	private static final int VALIDATION_HEIGHT = 35;
	private static final int VALIDATION_BUTTON_SIDE = 60;

	private static final String DESCRIPTION_TRACK_NAME = "Track name:";
	private static final String DESCRIPTION_TRACK_GROUP = "Group:";

	private static final Color MAIN_COLOR = Color.blue;
	private static final Color SELECTION_COLOR = Color.cyan;
	private static final Color HEADER_COLOR = Color.lightGray;
	private static final Color CONTENT_COLOR = Color.magenta;
	private static final Color DESCRIPTION_COLOR = Color.green;

	private GenomeSelectionPanel 	selectionPanel;
	private TransparencyPanel 		transparencyPanel;
	private QualityPanel			qualityPanel;
	private DescriptionTrackPanel 	descriptionPanel;
	private ValidationPanel 		validationPanel;
	
	private static Object genomeNames[];
	private static int genomeNumber;
	private static int groupLabelWidth;
	private static int dialogWidth;
	private static int dialogHeight;

	private int	approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	
	/**
	 * Constructor of {@link MultiGenomePanel}
	 * @param genomeNames the genome names array
	 */
	public MultiGenomePanel (Object genomeNames[]) {

		//Layout Manager
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(layout);

		MultiGenomePanel.genomeNames = genomeNames;
		initSize();

		//Panels
		selectionPanel = new GenomeSelectionPanel();
		transparencyPanel = new TransparencyPanel(dialogWidth);
		qualityPanel = new QualityPanel(dialogWidth);
		descriptionPanel = new DescriptionTrackPanel(dialogWidth);
		validationPanel = new ValidationPanel(this);
		
		
		//selectionPanel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(selectionPanel, gbc);
		
		//transparencyPanel
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(transparencyPanel, gbc);
		
		//qualityPanel
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(qualityPanel, gbc);
		
		//descriptionPanel
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(descriptionPanel, gbc);
		
		//validationPanel
		gbc.gridx = 0;
		gbc.gridy = 4;
		add(validationPanel, gbc);

		//Dimension
		Dimension panelDim = new Dimension(dialogWidth, dialogHeight);
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);


		//Panel settings
		setTitle("Multi Genome Selection");
		setResizable(false);
		setVisible(false);
		
	}
	
	
	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}


	private void initSize () {
		initGroupNameWidth();
		dialogWidth = groupLabelWidth + (HORIZONTAL_INSET * 2) + (CELL_VCF_TYPE_WIDTH * 4);
		dialogHeight = HEADER_HEIGHT + getSelectionPanelHeight() + (THRESHOLD_PANEL_HEIGHT * 2) + DESCRIPTION_PANEL_HEIGHT + (DESCRIPTION_INSET * 2) + VALIDATION_HEIGHT;
	}
	

	private void initGroupNameWidth () {
		genomeNumber = genomeNames.length;
		int maxLength = 0;
		for (int i = 0; i < genomeNumber; i++) {
			int length = ((String)genomeNames[i]).length();
			if (length > maxLength) {
				maxLength = length;
			}
		}
		groupLabelWidth = maxLength * 7;
	}
	
	
	/**
	 * @param name the new name of the track
	 */
	public void setTrackName (String name) {
		descriptionPanel.setTrackName(name);
	}
	
	
	/**
	 * @param name the new genome group name of the track
	 */
	public void setTrackGenomeGroupName (String name) {
		descriptionPanel.setTrackGenomeGroupName(name);
	}
	
	
	/**
	 * Sets colors for every genomes.
	 * @param colorAssociation the genome names and colors association
	 */
	public void initColors (Map<String, Map<VariantType, Color>> colorAssociation) {
		selectionPanel.initColors(colorAssociation);
	}
	
	
	/**
	 * @param transparency the transparency of the stripes color
	 */
	public void initTransparency (int transparency) {
		transparencyPanel.initTransparency(transparency);
	}
	
	
	/**
	 * @param quality the quality filter
	 */
	public void initQuality (int quality) {
		qualityPanel.initQuality(quality);
	}
	
	
	protected void validChoice () {
		approved = APPROVE_OPTION;
		setVisible(false);
	}
	
	
	protected void cancelChoice () {
		setVisible(false);
	}
	
	
	/**
	 * Creates a track stripe information object:
	 * - genome name, stripe type and color association
	 * @return a new track stripe information object
	 */
	public MultiGenomeStripe getMultiGenomeStripe () {
		MultiGenomeStripe stripeInformation = selectionPanel.getMultiGenomeStripe();
		int alpha = transparencyPanel.getAlphaTransparency();
		stripeInformation.setTransparency(alpha);
		stripeInformation.setQuality(qualityPanel.getQuality());
		return stripeInformation;
	}
	
	
	/**
	 * @param index 			the index in the genome name array
	 * @return the genomeNames	the name of the genome
	 */
	public static String getGenomeNames(int index) {
		return (String) genomeNames[index];
	}

	/**
	 * @return the genomeNumber
	 */
	public static int getGenomeNumber() {
		return genomeNumber;
	}

	/**
	 * @return the groupLabelWidth
	 */
	public static int getGroupLabelWidth() {
		return groupLabelWidth;
	}

	/**
	 * @return the dialogWidth
	 */
	public static int getDialogWidth() {
		return dialogWidth;
	}

	/**
	 * @return the selection panel height
	 */
	public static int getSelectionPanelHeight() {
		return (ROW_HEIGHT * genomeNumber);
	}
	
	/**
	 * @return the transparencyPanelHeight
	 */
	public static int getThresholdPanelHeight() {
		return THRESHOLD_PANEL_HEIGHT;
	}

	/**
	 * @return the transparencyLabelWidth
	 */
	public static int getThresholdLabelWidth() {
		return THRESHOLD_LABEL_WIDTH;
	}

	/**
	 * @return the transparencyLineHeight
	 */
	public static int getThresholdLineHeight() {
		return THRESHOLD_LINE_HEIGHT;
	}

	/**
	 * @return the transparencyInset
	 */
	public static int getThresholdInset() {
		return THRESHOLD_INSET;
	}
	
	/**
	 * @return the descriptionPanelHeight
	 */
	public static int getDescriptionPanelHeight() {
		return DESCRIPTION_PANEL_HEIGHT;
	}

	/**
	 * @return the headerHeight
	 */
	public static int getHeaderHeight() {
		return HEADER_HEIGHT;
	}

	/**
	 * @return the rowHeight
	 */
	public static int getRowHeight() {
		return ROW_HEIGHT;
	}

	/**
	 * @return the cellVcfTypeWidth
	 */
	public static int getCellVcfTypeWidth() {
		return CELL_VCF_TYPE_WIDTH;
	}

	/**
	 * @return the leftInset
	 */
	public static int getHorizontalInset() {
		return HORIZONTAL_INSET;
	}


	/**
	 * @return the colorLabelSide
	 */
	public static int getColorButtonSide() {
		return COLOR_BUTTON_SIDE;
	}

	/**
	 * @return the descriptionLineHeight
	 */
	public static int getDescriptionLineHeight() {
		return DESCRIPTION_LINE_HEIGHT;
	}

	/**
	 * @return the validationHeight
	 */
	public static int getValidationHeight() {
		return VALIDATION_HEIGHT;
	}

	/**
	 * @return the validationButtonSide
	 */
	public static int getValidationButtonSide() {
		return VALIDATION_BUTTON_SIDE;
	}

	/**
	 * @return the descriptionTopInset
	 */
	public static int getDescriptionInset() {
		return DESCRIPTION_INSET;
	}

	/**
	 * @return the descriptionLabelWidth
	 */
	public static int getDescriptionLabelWidth() {
		return DESCRIPTION_LABEL_WIDTH;
	}

	/**
	 * @return the descriptionTrackName
	 */
	public static String getDescriptionTrackName() {
		return DESCRIPTION_TRACK_NAME;
	}

	/**
	 * @return the descriptionTrackGroup
	 */
	public static String getDescriptionTrackGroup() {
		return DESCRIPTION_TRACK_GROUP;
	}

	/**
	 * @return the mainColor
	 */
	public static Color getMainColor() {
		return MAIN_COLOR;
	}

	/**
	 * @return the selectionColor
	 */
	public static Color getSelectionColor() {
		return SELECTION_COLOR;
	}

	/**
	 * @return the headerColor
	 */
	public static Color getHeaderColor() {
		return HEADER_COLOR;
	}

	/**
	 * @return the contentColor
	 */
	public static Color getContentColor() {
		return CONTENT_COLOR;
	}

	/**
	 * @return the descriptionColor
	 */
	public static Color getDescriptionColor() {
		return DESCRIPTION_COLOR;
	}

	/**
	 * @param size a height 
	 * @return the dynamic vertical inset
	 */
	public static int getVerticalInset (int size) {
		return (ROW_HEIGHT - size) / 2;
	}
	
	/**
	 * @return the dynamic vertical inset
	 */
	public static int getVerticalInset () {
		return ROW_HEIGHT / 4;
	}


}
