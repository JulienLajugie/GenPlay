/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A dialog box for Distance Calculation between Base Pairs
 * @author Chirag Gorasia
 * @version 0.1
 */
public class DistanceCalculatorDialog extends JDialog {

	private static final long serialVersionUID = 6243891166900722503L;
	private final JPanel jpstrandSelection;
	private final JPanel jprelAbsSelection;
	private final JPanel jptrack1Panel;
	private final JPanel jptrack2Panel;
	
	private final JRadioButton positiveStrand;
	private final JRadioButton negativeStrand;
	private final JRadioButton bothStrand;
	
	private static final int POSITIVE_STRAND = 1;
	private static final int NEGATIVE_STRAND = 2;
	private static final int BOTH_STRAND = 3;
	
	private final JRadioButton relative;
	private final JRadioButton absolute;
	
	private static final int RELATIVE = 1;
	private static final int ABSOLUTE = 2;
	
	private final JRadioButton track1Start;
	private final JRadioButton track1Middle;
	private final JRadioButton track1Stop;
	
	private static final int START_1 = 1;
	private static final int MIDDLE_1 = 2;
	private static final int STOP_1 = 3;
	
	private final JRadioButton track2Start;
	private final JRadioButton track2Middle;
	private final JRadioButton track2Stop;
	
	private static final int START_2 = 1;
	private static final int MIDDLE_2 = 2;
	private static final int STOP_2 = 3;
	
	private int strand;
	private int relabs;
	private int t1pos;
	private int t2pos;
	
	public DistanceCalculatorDialog() {
		jpstrandSelection = new JPanel();
		jprelAbsSelection = new JPanel();
		jptrack1Panel = new JPanel();
		jptrack2Panel = new JPanel();
		
		positiveStrand = new JRadioButton("Positive Strand");
		negativeStrand = new JRadioButton("Negative Strand");
		bothStrand = new JRadioButton("Both Strands");
		
		relative = new JRadioButton("Relative");
		absolute = new JRadioButton("Absolute");
		
		track1Start = new JRadioButton("Start");
		track1Middle = new JRadioButton("Middle");
		track1Stop = new JRadioButton("Stop");
		
		track2Start = new JRadioButton("Start");
		track2Middle = new JRadioButton("Middle");
		track2Stop = new JRadioButton("Stop");
		
		positiveStrand.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		negativeStrand.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		bothStrand.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(positiveStrand);
		bg.add(negativeStrand);
		bg.add(bothStrand);
		bg.setSelected(positiveStrand.getModel(), true);
		
		relative.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		absolute.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		bg = new ButtonGroup();
		bg.add(relative);
		bg.add(absolute);
		bg.setSelected(relative.getModel(), true);
		
		track1Start.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		track1Middle.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		track1Stop.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		bg = new ButtonGroup();
		bg.add(track1Start);
		bg.add(track1Middle);
		bg.add(track1Stop);
		bg.setSelected(track1Start.getModel(), true);
		
		track2Start.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		track2Middle.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		track2Stop.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
			}
		});
		
		bg = new ButtonGroup();
		bg.add(track2Start);
		bg.add(track2Middle);
		bg.add(track2Stop);
		bg.setSelected(track2Start.getModel(), true);
		
		jpstrandSelection.setLayout(new GridBagLayout());
		jpstrandSelection.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		//strandSelection.setBorder(BorderFactory.createLineBorder(Color.black));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		jpstrandSelection.add(positiveStrand,c);
		c.gridy = 1;
		jpstrandSelection.add(negativeStrand,c);
		c.gridy = 2;
		jpstrandSelection.add(bothStrand,c);
		jpstrandSelection.setVisible(true);
		
		jprelAbsSelection.setLayout(new GridBagLayout());
		jprelAbsSelection.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		jprelAbsSelection.add(relative,c);
		c.gridy = 1;
		jprelAbsSelection.add(absolute,c);
		jprelAbsSelection.setEnabled(false);
		jprelAbsSelection.setVisible(true);
		
		jptrack1Panel.setLayout(new GridBagLayout());
		jptrack1Panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		jptrack1Panel.add(track1Start,c);
		c.gridy = 1;
		jptrack1Panel.add(track1Middle,c);
		c.gridy = 2;
		jptrack1Panel.add(track1Stop,c);
		jptrack1Panel.setEnabled(false);
		jptrack1Panel.setVisible(true);
		
		jptrack2Panel.setLayout(new GridBagLayout());
		jptrack2Panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		jptrack2Panel.add(track2Start,c);
		c.gridy = 1;
		jptrack2Panel.add(track2Middle,c);
		c.gridy = 2;
		jptrack2Panel.add(track2Stop,c);
		jptrack2Panel.setEnabled(false);
		jptrack2Panel.setVisible(true);
		
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		add(jpstrandSelection, c);
		
		c.gridx = 1;
		c.gridy = 0;
		add(jprelAbsSelection, c);
		
		c.gridx = 0;
		c.gridy = 1;
		add(jptrack1Panel, c);
		
		c.gridx = 1;
		c.gridy = 1;
		add(jptrack2Panel, c);
	}
	
	public static void main(String args[]) {
		DistanceCalculatorDialog dcd = new DistanceCalculatorDialog();
		dcd.setVisible(true);
	}
}
