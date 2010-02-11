/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;


/**
 * A dialog box used to choose a value for the sigma parameter of a gaussian filter. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GenomeWidthChooser extends JDialog {

	private static final long 			serialVersionUID = -1145665933228762636L; 	// Generated serial number
	private static JScrollBar 			jsbGenomeWidth;								// ScrollBar use to choose sigma 
	private static JFormattedTextField 	jftfGenomeWidth;							// TextField for the value of sigma 
	private static JButton 				jbOk;										// Button Ok
	private static JButton 				jbCancel;									// Button Cancel
	private static int 					windowSize;									// Greatest bin size of the selected curves
	private static Integer 				validGenomeWidth;							// A valid value for sigma
	private static boolean 				validated;									// True if OK has been pressed
	private static String				type; 										// Describe the data asked.
	
	/**
	 * Private constructor. Used internally to create a {@link GenomeWidthChooser} dialog. 
	 * @param parent The parent {@link Component} from which the dialog is displayed.
	 */
	private GenomeWidthChooser(Component parent) {
		super();
		setModal(true);
		validated = false;
		initComponent();
		setTitle(type + " value :");
		getRootPane().setDefaultButton(jbOk);
		setLocationRelativeTo(parent);
	}

	
	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		jsbGenomeWidth = new JScrollBar(JScrollBar.HORIZONTAL, validGenomeWidth, 0, windowSize, windowSize * 100);
		jsbGenomeWidth.setBlockIncrement(windowSize);
		jsbGenomeWidth.setUnitIncrement(windowSize);
		jsbGenomeWidth.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				jsbSigmaAdjustmentValueChanged();
			}
		});

		jftfGenomeWidth = new JFormattedTextField(new DecimalFormat("###,###,###"));
		jftfGenomeWidth.setValue(validGenomeWidth);
		jftfGenomeWidth.setColumns(8);
		jftfGenomeWidth.addPropertyChangeListener(new PropertyChangeListener() {				
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				jftfSigmaPropertyChange();	
			}
		});

		jbOk = new JButton("OK");
		jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();				
			}
		});

		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();				
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jsbGenomeWidth, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(jftfGenomeWidth, c);	

		c.gridx = 0;
		c.gridy = 2;
		add(jbOk, c);		

		c.gridx = 2;
		c.gridy = 2;
		add(jbCancel, c);

		this.pack();
		this.setResizable(false);		
	}

	
	/**
	 * Closes the dialog. No action are performed.
	 */
	private void jbCancelActionPerformed() {
		this.dispose();

	}

	
	/**
	 * Closes the dialog. Sets validated to true so the main function can return the two selected curves.
	 */
	private void jbOkActionPerformed() {
		validated = true;
		this.dispose();		
	}

	
	/**
	 * Changes the text of textField jftfGenomicWidth when the scrollBar jftfSigma is used.  
	 */
	private void jftfSigmaPropertyChange() {
		int currentGenomicWidth = ((Number)(jftfGenomeWidth.getValue())).intValue();

		if((currentGenomicWidth < windowSize) || (currentGenomicWidth > windowSize * 100000)) {
			JOptionPane.showMessageDialog(getRootPane(), type + " value must be between " + windowSize + " and " + (windowSize * 100) + ".", "Incorrect sigma value.", JOptionPane.WARNING_MESSAGE);
			jftfGenomeWidth.setValue(validGenomeWidth);
		}
		else {
			validGenomeWidth = currentGenomicWidth;
			jsbGenomeWidth.setValue(currentGenomicWidth);
		}
	}

	
	/**
	 * Changes the position of the scrollBar jftfSigma when the value of the textField jsbSigma changes.
	 */
	private void jsbSigmaAdjustmentValueChanged() {
		validGenomeWidth = jsbGenomeWidth.getValue();
		jftfGenomeWidth.setValue(validGenomeWidth);
	}	

	
	/**
	 * Displays a GenomeWidthChooser dialog, and returns  
	 * an integer value for sigma.
	 * @param parent The parent {@link Component} from which the dialog is displayed..
	 * @param aWindowSize a size of bins.
	 * @return An Integer value of sigma if OK has been pressed. Null otherwise. 
	 */
	public static Integer getSigma(Component parent, int aWindowSize) {
		windowSize = aWindowSize;
		validGenomeWidth = windowSize * 10;
		type = "Sigma";
		GenomeWidthChooser FS = new GenomeWidthChooser(parent);
		FS.setVisible(true);
		if(validated)
			return validGenomeWidth;
		else
			return null;
	}
	
	
	/**
	 * Displays a GenomeWidthChooser dialog, and returns  
	 * an integer value for the width of a moving standard deviation.
	 * @param parent The parent {@link Component} from which the dialog is displayed..
	 * @param aWindowSize greatest size of the bins of the curves.
	 * @return An Integer value if OK has been pressed. Null otherwise. 
	 */
	public static Integer getMovingStdDevWidth(Component parent, int aWindowSize) {
		windowSize = aWindowSize;
		validGenomeWidth = windowSize * 2;
		type = "Moving std deviation width";
		GenomeWidthChooser FS = new GenomeWidthChooser(parent);
		FS.setVisible(true);
		if(validated)
			return validGenomeWidth;
		else
			return null;
	}
}
