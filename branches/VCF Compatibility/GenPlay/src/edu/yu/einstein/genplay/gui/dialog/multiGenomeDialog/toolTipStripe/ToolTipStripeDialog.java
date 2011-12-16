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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.DisplayableVariant;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * This class shows variant stripe information.
 * It is possible to move forward and backward on the variant list.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ToolTipStripeDialog extends JDialog {

	private static final long serialVersionUID = -4932470485711131874L;


	private static final int WIDTH = 250;	// width of the dialog
	private static final int V_GAP = 5;		// vertical gap between dialog components
	private static final int H_GAP = 5;		// horizontal gap between dialog components

	private List<DisplayableVariant> 	displayableVariantList;		// a list of displayable variant
	private DisplayableVariant 			displayableVariant;			// the current variant object to display

	private JPanel headerPanel;			// panel containing the global information
	private JPanel infoPanel;			// panel containing the INFO field information of the VCF
	private JPanel formatPanel;			// panel containing the FORMAT field information of the VCF
	private JPanel navigationPanel;		// panel to move forward/backward
	private boolean first;


	/**
	 * Constructor of {@link ToolTipStripeDialog}
	 * @param fittedDisplayableVariantList the full list of displayable variants
	 */
	public ToolTipStripeDialog (List<DisplayableVariant> fittedDisplayableVariantList) {
		super(MainFrame.getInstance());
		this.displayableVariantList = fittedDisplayableVariantList;
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setTitle("Variant properties");
		first = true;
	}


	/**
	 * Method for showing the dialog box.
	 * @param displayableVariant	variant to show information
	 * @param X			X position on the screen
	 * @param Y			Y position on the screen
	 */
	public void show (DisplayableVariant displayableVariant, int X, int Y) {
		this.displayableVariant = displayableVariant;
		initContent();
		setLocation(X, Y);
		setVisible(true);
	}


	/**
	 * Initializes the content of the dialog box according to a variant
	 * @param displayableVariant	variant to show information
	 */
	private void initContent () {
		VariantInfo variantInfo;
		VariantFormat variantFormat;

		if (displayableVariant.getType() == VariantType.MIX) {
			variantInfo = new VariantInfo(null);
			variantFormat = new VariantFormat(null);
		} else {
			variantInfo = new VariantInfo(displayableVariant.getNativeVariant());
			variantFormat = new VariantFormat(displayableVariant.getNativeVariant());
		}

		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, H_GAP, V_GAP);
		setLayout(layout);

		if (first) {
			headerPanel = new JPanel();
			infoPanel = new JPanel();
			formatPanel = new JPanel();
			navigationPanel = new JPanel();

			add(headerPanel);
			add(infoPanel);
			add(formatPanel);
			add(navigationPanel);
			first = false;
		}
		updatePanel(headerPanel, new GlobalInformationPanel(displayableVariant));
		updatePanel(infoPanel, variantInfo.getPane());
		updatePanel(formatPanel, variantFormat.getPane());
		updatePanel(navigationPanel, new NavigationPanel(this));

		int height = GlobalInformationPanel.getPanelHeight() +
		PanelInformation.getPanelHeight() +
		PanelInformation.getPanelHeight() +
		NavigationPanel.getPanelHeight() +
		(V_GAP * 11) + 40;
		Dimension dimension = new Dimension(ToolTipStripeDialog.WIDTH, height);
		setSize(dimension);

		validate();
	}


	/**
	 * Updates a panel with another one
	 * @param previousPanel	panel to update
	 * @param newPanel		new panel
	 */
	private void updatePanel (JPanel previousPanel, JPanel newPanel) {
		previousPanel.removeAll();
		previousPanel.add(newPanel);
	}


	/**
	 * @return the variant
	 */
	public DisplayableVariant getDisplayableVariant() {
		return displayableVariant;
	}


	/**
	 * Looks for the next variant and run the dialog initialization.
	 * @return true if it moves to the next variant, false otherwise
	 */
	protected boolean goToNextVariant () {
		DisplayableVariant newDisplayableVariant = getNextDisplayableVariant();
		int referencePosition = newDisplayableVariant.getNativeVariant().getReferenceGenomePosition();
		int startPos = referencePosition - 1;
		int stopPos = referencePosition + 1;
		if (startPos < 0) {
			startPos = 0;
		}
		try {
			long start = System.currentTimeMillis();
			List<Map<String, Object>> map = newDisplayableVariant.getNativeVariant().getPositionInformation().getReader().query(newDisplayableVariant.getNativeVariant().getChromosomeName(), startPos, stopPos);
			System.out.println(System.currentTimeMillis() - start);
			System.out.println(map.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return initVariant(newDisplayableVariant);
	}


	/**
	 * Looks for the previous variant and run the dialog initialization.
	 * @return true if it moves to the previous variant, false otherwise
	 */
	protected boolean goToPreviousVariant () {
		DisplayableVariant newDisplayableVariant = getPreviousDisplayableVariant();
		return initVariant(newDisplayableVariant);
	}


	/**
	 * initializes the dialog content and moves the screen onto the related variant.
	 * @param newDisplayableVariant	the variant to display
	 * @return						if it moves to the previous variant, false otherwise 
	 */
	private boolean initVariant (DisplayableVariant newDisplayableVariant) {
		if (newDisplayableVariant == null) {
			return false;
		}

		this.displayableVariant = newDisplayableVariant;
		initContent();
		int variantStart = displayableVariant.getStart();
		int width = MainFrame.getInstance().getControlPanel().getGenomeWindow().getSize();
		int startWindow = variantStart - (width / 2);
		int stopWindow = startWindow + width;
		Chromosome chromosome = MainFrame.getInstance().getControlPanel().getGenomeWindow().getChromosome();
		GenomeWindow genomeWindow = new GenomeWindow(chromosome, startWindow, stopWindow);
		MainFrame.getInstance().getControlPanel().setGenomeWindow(genomeWindow);
		return true;
	}


	/**
	 * @param regularDisplayableVariant the current variant
	 * @return	the index in the variant list of the variant
	 */
	private int getDisplayableVariantIndex (DisplayableVariant displayableVariant) {
		for (int i = 0; i < displayableVariantList.size(); i++) {
			if (displayableVariantList.get(i).equals(displayableVariant)) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * @param regularDisplayableVariant	the current variant
	 * @return the previous variant compare to the current variant
	 */
	private DisplayableVariant getPreviousDisplayableVariant () {
		DisplayableVariant result;
		int currentIndex = getDisplayableVariantIndex(displayableVariant);
		int previousIndex = currentIndex - 1;
		if (previousIndex >= 0) {
			result = displayableVariantList.get(previousIndex);
		} else {
			result = displayableVariant;
		}
		return result;
	}


	/**
	 * @param regularDisplayableVariant	the current variant
	 * @return the next variant compare to the current variant
	 */
	private DisplayableVariant getNextDisplayableVariant () {
		DisplayableVariant result;
		int currentIndex = getDisplayableVariantIndex(displayableVariant);
		int nextIndex = currentIndex + 1;
		if (nextIndex >= 0 && nextIndex < displayableVariantList.size()) {
			result = displayableVariantList.get(nextIndex);
		} else {
			result = displayableVariant;
		}
		return result;
	}

}
