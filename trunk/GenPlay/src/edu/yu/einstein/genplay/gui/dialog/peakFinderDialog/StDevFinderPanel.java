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
package edu.yu.einstein.genplay.gui.dialog.peakFinderDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOFindPeaksStDev;
import edu.yu.einstein.genplay.dataStructure.enums.PeakFinderType;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.util.NumberFormats;



/**
 * Panel for the input of a standard deviation peak finder
 * @author Julien Lajugie
 * @version 0.1
 */
class StDevFinderPanel extends JPanel implements PeakFinderPanel {

	private static final long serialVersionUID = -4523301811594013155L;	// generated ID
	private static final int 			INSET = 10;						// inset border to subcomponents
	private static final String 		NAME = PeakFinderType.STDEV.toString(); // Name of the peak finder
	private static int 					defaultRegionWidth = 10;		// default value for the region width
	private static double 				defaultThreshold = 1.0;			// default value for threshold
	private final JTextArea 			jtaDescription;					// description of the peak finder
	private final JLabel 				jlRegionWidth1;					// label region width
	private final JLabel 				jlRegionWidth2;					// label region width
	private final JLabel 				jlThreshold1;					// label threshold
	private final JLabel 				jlThreshold2;					// label threshold
	private final JFormattedTextField 	jftfRegionWidth;				// input box region width
	private final JFormattedTextField 	jftfThreshold;					// input box threshold
	private final BLOFindPeaksStDev 	bloFindPeaks;					// BinList operation to set

	/**
	 * Creates an instance of {@link StDevFinderPanel}
	 */
	StDevFinderPanel(BLOFindPeaksStDev bloFindPeaks) {
		super();

		this.bloFindPeaks = bloFindPeaks;

		setName(NAME);
		setBorder(BorderFactory.createTitledBorder("Input"));
		/*
		 * Compute the standard deviation of each chromosome.
		 * Compute, for each window W of a chromosome, the standard deviation on
		 * a region centered on W with a half size S windows.
		 * The window W is selected if this standard deviation is T time greater than
		 * the chromosome wide standard deviation
		 */
		jtaDescription = new JTextArea("Please refere to the help file for an explanation of the parameters S and T");
		jtaDescription.setEditable(false);
		jtaDescription.setBackground(getBackground());
		jtaDescription.setLineWrap(true);
		jtaDescription.setWrapStyleWord(true);

		jlRegionWidth1 = new JLabel("Enter the half size, S = ");
		jftfRegionWidth = new JFormattedTextField(NumberFormats.getPositionFormat());
		jftfRegionWidth.setValue(defaultRegionWidth);
		jftfRegionWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		jftfRegionWidth.setColumns(4);
		((NumberFormatter) jftfRegionWidth.getFormatter()).setMinimum(1);
		jlRegionWidth2 = new JLabel(" windows");

		jlThreshold1 = new JLabel("Enter the threshold, T = ");
		jftfThreshold = new JFormattedTextField(NumberFormats.getScoreFormat());
		jftfThreshold.setValue(defaultThreshold);
		jftfThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
		jftfThreshold.setColumns(4);
		((InternationalFormatter) jftfThreshold.getFormatter()).setMinimum(0.0);
		jlThreshold2 = new JLabel(" times the chromosome stdev");

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.insets = new Insets(INSET, INSET, 30, INSET);
		add(jtaDescription, c);

		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.insets = new Insets(0, INSET, INSET, 0);
		add(jlRegionWidth1, c);

		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 0, INSET, 0);
		add(jftfRegionWidth, c);

		c.gridx = 2;
		c.weightx = 0;
		c.insets = new Insets(0, 0, INSET, INSET);
		add(jlRegionWidth2, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.insets = new Insets(0, INSET, INSET, 0);
		add(jlThreshold1, c);

		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 0, INSET, 0);
		add(jftfThreshold, c);

		c.gridx = 2;
		c.weightx = 0;
		c.insets = new Insets(0, 0, INSET, INSET);
		add(jlThreshold2, c);
	}


	/**
	 * @return the half window width parameter of the standard deviation peak finder
	 */
	private int getRegionWidth() {
		int windowWidth = ((Number) jftfRegionWidth.getValue()).intValue();
		return windowWidth;
	}


	/**
	 * @return the threshold parameter of the standard deviation peak finder
	 */
	private double getThreshold() {
		double threshold = ((Number) jftfThreshold.getValue()).doubleValue();
		return threshold;
	}


	/**
	 * Save the input in static variables
	 */
	@Override
	public void saveInput() {
		defaultRegionWidth = getRegionWidth();
		defaultThreshold = getThreshold();
	}


	@Override
	public String toString() {
		return getName();
	}


	@Override
	public Operation<BinList[]> validateInput() {
		bloFindPeaks.setHalfWidth(getRegionWidth());
		bloFindPeaks.setThreshold(getThreshold());
		return bloFindPeaks;
	}
}
