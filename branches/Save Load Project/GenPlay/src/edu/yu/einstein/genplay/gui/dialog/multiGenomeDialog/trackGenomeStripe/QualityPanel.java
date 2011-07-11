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

class QualityPanel extends JPanel implements ChangeListener {

	private static final long serialVersionUID = -3751033351191803873L;


	private JLabel name;
	private JLabel value;
	private JSlider slider;
	private static final int QUALITY_MIN = 0;
	private static final int QUALITY_MAX = 100;
	private static int QUALITY_INIT = 50;
	private static final int SLIDER_RIGHT_OFFSET = 20;


	protected QualityPanel (int width) {
		//Dimension
		Dimension panelDim = new Dimension(width, MultiGenomePanel.getThresholdPanelHeight());
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);


		//Name label
		name = new JLabel("Quality score >=");
		Dimension labelDim = new Dimension(MultiGenomePanel.getThresholdLabelWidth(), MultiGenomePanel.getThresholdLineHeight());
		name.setSize(labelDim);
		name.setPreferredSize(labelDim);
		name.setMinimumSize(labelDim);
		name.setMaximumSize(labelDim);

		//Slider
		slider = new JSlider(JSlider.HORIZONTAL, QUALITY_MIN, QUALITY_MAX, QUALITY_INIT);
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		//slider.setPaintTicks(true);
		//slider.setPaintLabels(true);
		Dimension sliderDim = new Dimension(getSliderWidth(width), MultiGenomePanel.getThresholdLineHeight());
		slider.setSize(sliderDim);
		slider.setPreferredSize(sliderDim);
		slider.setMinimumSize(sliderDim);
		slider.setMaximumSize(sliderDim);

		//Value label
		value = new JLabel(QUALITY_INIT + "");
		Dimension valueDim = new Dimension(40, MultiGenomePanel.getThresholdLineHeight());
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
		gbc.insets = new Insets(MultiGenomePanel.getThresholdInset(), getSliderLeftInset(), 0, 0);
		add(value, gbc);

		//name
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, MultiGenomePanel.getHorizontalInset(), 0, (SLIDER_RIGHT_OFFSET/2));
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
		int sliderWidth = width - (MultiGenomePanel.getThresholdLabelWidth() + MultiGenomePanel.getHorizontalInset() + SLIDER_RIGHT_OFFSET);
		return sliderWidth;
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		int quality = (int)source.getValue();
		value.setText(quality + "");
	}
	
	
	protected int getQuality () {
		return Integer.parseInt(value.getText());
	}
	
	
	protected void initQuality (int quality) {
		slider.setValue(quality);
	}


}
