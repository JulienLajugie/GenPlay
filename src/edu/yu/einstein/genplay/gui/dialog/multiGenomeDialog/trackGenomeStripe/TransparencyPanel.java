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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class TransparencyPanel extends JPanel implements ChangeListener {

	private static final long serialVersionUID = -3751033351191803873L;


	private JLabel name;
	private JLabel value;
	private JSlider slider;
	private static final int TRANSPARENCY_MIN = 0;
	private static final int TRANSPARENCY_MAX = 100;
	private static int TRANSPARENCY_INIT = 50;
	private static final int SLIDER_RIGHT_OFFSET = 20;


	protected TransparencyPanel (int width) {
		//Dimension
		Dimension panelDim = new Dimension(width, MultiGenomeStripeSelectionDialog.getThresholdPanelHeight());
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);


		//Name label
		name = new JLabel("Transparency:");
		Dimension labelDim = new Dimension(MultiGenomeStripeSelectionDialog.getThresholdLabelWidth(), MultiGenomeStripeSelectionDialog.getThresholdLineHeight());
		name.setSize(labelDim);
		name.setPreferredSize(labelDim);
		name.setMinimumSize(labelDim);
		name.setMaximumSize(labelDim);

		//Slider
		slider = new JSlider(JSlider.HORIZONTAL, TRANSPARENCY_MIN, TRANSPARENCY_MAX, TRANSPARENCY_INIT);
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		//slider.setPaintTicks(true);
		//slider.setPaintLabels(true);
		Dimension sliderDim = new Dimension(getSliderWidth(width), MultiGenomeStripeSelectionDialog.getThresholdLineHeight());
		slider.setSize(sliderDim);
		slider.setPreferredSize(sliderDim);
		slider.setMinimumSize(sliderDim);
		slider.setMaximumSize(sliderDim);

		//Value label
		value = new JLabel(TRANSPARENCY_INIT + " %");
		Dimension valueDim = new Dimension(40, MultiGenomeStripeSelectionDialog.getThresholdLineHeight());
		value.setSize(valueDim);
		value.setPreferredSize(valueDim);
		value.setMinimumSize(valueDim);
		value.setMaximumSize(valueDim);


		//Layout
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();


		//value
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(MultiGenomeStripeSelectionDialog.getThresholdInset(), getSliderLeftInset(), 0, 0);
		add(value, gbc);

		//name
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, MultiGenomeStripeSelectionDialog.getHorizontalInset(), 0, (SLIDER_RIGHT_OFFSET/2));
		add(name, gbc);

		//slider
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 0, (SLIDER_RIGHT_OFFSET/2));
		add(slider, gbc);

	}


	private int getSliderLeftInset() {
		int left = (slider.getSize().width - value.getSize().width) / 20;
		return left;
	}


	private int getSliderWidth (int width) {
		int sliderWidth = width - (MultiGenomeStripeSelectionDialog.getThresholdLabelWidth() + MultiGenomeStripeSelectionDialog.getHorizontalInset() + SLIDER_RIGHT_OFFSET);
		return sliderWidth;
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		int transparency = (int)source.getValue();
		value.setText(transparency + " %");
	}
	
	
	protected int getAlphaTransparency () {
		int transparency = getTransparency();
		int alpha = transparency * 255 / 100;
		return alpha;
	}
	
	
	private int getTransparency () {
		String s = value.getText().substring(0, value.getText().length()-2);
		return 100 - Integer.parseInt(s);
	}
	
	
	protected void initTransparency (int transparency) {
		slider.setValue(100 - transparency);
	}


}
