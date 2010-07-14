/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.peakFinderDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import yu.einstein.gdp2.core.enums.PeakFinderType;


/**
 * Panel for the input of a standard deviation peak finder
 * @author Julien Lajugie
 * @version 0.1
 */
public class StDevFinderPanel extends JPanel implements PeakFinderPanel {

	private static final long serialVersionUID = -4523301811594013155L;	// generated ID
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

		
	/**
	 * Creates an instance of {@link StDevFinderPanel}
	 */
	public StDevFinderPanel() {
		super();
		setName(NAME);
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
		
		jlRegionWidth1 = new JLabel("Enter the half size, S = ");
		jftfRegionWidth = new JFormattedTextField(new DecimalFormat("0"));
		jftfRegionWidth.setValue(defaultRegionWidth);
		jftfRegionWidth.setHorizontalAlignment(JFormattedTextField.RIGHT);
		jlRegionWidth2 = new JLabel(" windows");
		
		jlThreshold1 = new JLabel("Enter the threshold, T = ");
		jftfThreshold = new JFormattedTextField(new DecimalFormat("0.0"));
		jftfThreshold.setValue(defaultThreshold);
		jftfThreshold.setHorizontalAlignment(JFormattedTextField.RIGHT);
		jlThreshold2 = new JLabel(" times the chromosome stdev");
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.insets = new Insets(0, 0, 30, 0);
		add(jtaDescription, c);
		
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 0, 0);
		add(jlRegionWidth1, c);
	
		c.gridx = 1;
		c.weightx = 1;
		add(jftfRegionWidth, c);
		
		c.gridx = 2;
		c.weightx = 0;
		add(jlRegionWidth2, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		add(jlThreshold1, c);
		
		c.gridx = 1;
		c.weightx = 1;
		add(jftfThreshold, c);
	
		c.gridx = 2;
		c.weightx = 0;
		add(jlThreshold2, c);
	}
	
	
	/**
	 * @return the half window width parameter of the standard deviation peak finder
	 */
	public int getRegionWidth() {
		int windowWidth = ((Number) jftfRegionWidth.getValue()).intValue();
		return windowWidth;
	}
	
	
	/**
	 * @return the threshold parameter of the standard deviation peak finder
	 */
	public double getThreshold() {
		double threshold = ((Number) jftfThreshold.getValue()).doubleValue();
		return threshold;
	}
	
	
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
	public boolean isInputValid() {
		boolean isValid = true;
		int windowWidth = getRegionWidth();
		if (windowWidth <= 0) {
			JOptionPane.showMessageDialog(getRootPane(), "The region width must be positive", "Invalid Input", JOptionPane.ERROR_MESSAGE);
			isValid = false;
		}
		double threshold = getThreshold();
		if (threshold <= 0) {
			JOptionPane.showMessageDialog(getRootPane(), "The threshold must be positive", "Invalid Input", JOptionPane.ERROR_MESSAGE);
			isValid = false;
		}
		return isValid;
	}
}
