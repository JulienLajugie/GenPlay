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
 * Panel for the input of a density peak finder
 * @author Julien Lajugie
 * @version 0.1
 */
public class DensityFinderPanel extends JPanel implements PeakFinderPanel {

	private static final long serialVersionUID = 3770930911273486277L;	// generated ID
	private static final String 		NAME = PeakFinderType.DENSITY.toString(); // name of the peak finder
	private static double 				defaultLowThreshold = 0;		// default low threshold
	private static double 				defaultHighThreshold = 100;		// default high threshold
	private static int 					defaultRegionWidth = 10;		// default region width
	private static double 				defaultPercentage = 1;			// default percentage
	private final JTextArea 			jtaDescription;					// description of the peak finder 
	private final JLabel 				jlLowThreshold;					// label low threshold
	private final JLabel 				jlHighTreshold;					// label high threshold
	private final JLabel 				jlRegionWidth;					// label region width
	private final JLabel				jlPercentage;					// label percentage
	private final JFormattedTextField 	jftfLowThreshold;				// input box low threshold
	private final JFormattedTextField 	jftfHighThreshold;				// input box high threshold
	private final JFormattedTextField 	jftfRegionWidth;				// input box region width
	private final JFormattedTextField 	jftfPercentage;					// input box percentage
	
	
	/**
	 * Creates an instance of {@link DensityFinderPanel}
	 */
	public DensityFinderPanel() {
		super();
		setName(NAME);
		
		jtaDescription = new JTextArea("Please refere to the help file for an explanation of the parameters L, H, S and P");
		jtaDescription.setEditable(false);
		jtaDescription.setBackground(getBackground());
		
		jlLowThreshold = new JLabel("Enter the low threshold L");
		jftfLowThreshold = new JFormattedTextField(new DecimalFormat("0.0"));
		jftfLowThreshold.setValue(defaultLowThreshold);
		jftfLowThreshold.setHorizontalAlignment(JFormattedTextField.RIGHT);
		
		jlHighTreshold = new JLabel("Enter the high threshold H");
		jftfHighThreshold = new JFormattedTextField(new DecimalFormat("0.0"));
		jftfHighThreshold.setValue(defaultHighThreshold);
		jftfHighThreshold.setHorizontalAlignment(JFormattedTextField.RIGHT);
		
		jlRegionWidth = new JLabel("Enter the region half size S (in windows)");
		jftfRegionWidth = new JFormattedTextField(new DecimalFormat("0"));
		jftfRegionWidth.setValue(defaultRegionWidth);
		jftfRegionWidth.setHorizontalAlignment(JFormattedTextField.RIGHT);
		
		jlPercentage = new JLabel("Enter percentage P");
		jftfPercentage = new JFormattedTextField(new DecimalFormat("0%"));
		jftfPercentage.setValue(defaultPercentage);
		jftfPercentage.setHorizontalAlignment(JFormattedTextField.RIGHT);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(0, 0, 30, 0);
		add(jtaDescription, c);
		
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 0, 0);
		add(jlLowThreshold, c);
	
		c.gridx = 1;
		c.weightx = 1;
		add(jftfLowThreshold, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		add(jlHighTreshold, c);
		
		c.gridx = 1;
		c.weightx = 1;
		add(jftfHighThreshold, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		add(jlRegionWidth, c);
		
		c.gridx = 1;
		c.weightx = 1;
		add(jftfRegionWidth, c);
		
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		add(jlPercentage, c);
		
		c.gridx = 1;
		c.weightx = 1;
		add(jftfPercentage, c);
	}
	
	
	/**
	 * @return the high threshold parameter of the density peak finder
	 */
	public double getHighThreshold() {
		double highThreshold = ((Number) jftfHighThreshold.getValue()).doubleValue();
		return highThreshold;
	}
	
	
	/**
	 * @return the low threshold parameter of the density peak finder
	 */
	public double getLowThreshold() {
		double lowThreshold = ((Number) jftfLowThreshold.getValue()).doubleValue();
		return lowThreshold;
	}
	
	
	/**
	 * @return the percentage parameter of the density peak finder
	 */
	public double getPercentage() {
		double percentage = ((Number) jftfPercentage.getValue()).doubleValue();
		return percentage;
	}
	
	
	/**
	 * @return the half window width parameter of the density peak finder
	 */
	public int getRegionWidth() {
		int windowWidth = ((Number) jftfRegionWidth.getValue()).intValue();
		return windowWidth;
	}
	
	
	@Override
	public void saveInput() {
		defaultRegionWidth = getRegionWidth();
		defaultLowThreshold = getLowThreshold();
		defaultHighThreshold = getHighThreshold();
		defaultPercentage = getPercentage();
	}
	
	
	@Override
	public String toString() {
		return getName();
	}
	
	
	@Override
	public boolean isInputValid() {
		boolean isValid = true;
		if (getLowThreshold() >= getHighThreshold()) {
			JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Invalid Input", JOptionPane.ERROR_MESSAGE);
			isValid = false;
		}
		if (getRegionWidth() <= 0) {
			JOptionPane.showMessageDialog(getRootPane(), "The region width must be positive", "Invalid Input", JOptionPane.ERROR_MESSAGE);
			isValid = false;
		}
		if ((getPercentage() > 1) || (getPercentage() < 0)) {
			JOptionPane.showMessageDialog(getRootPane(), "The percentage must be between 0 and 100", "Invalid Input", JOptionPane.ERROR_MESSAGE);
			isValid = false;
		}	
		return isValid;
	}
}
