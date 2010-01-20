/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * A dialog box used to choose a {@link ScoreCalculation}. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListPrecisionChooser extends JDialog {

	private static final long serialVersionUID = -5654898399773009796L; // generated ID

	private static JComboBox 	jcbPrecisionOption;			// JComboBox with the different options
	private static JLabel 		jlText;						// label of the box
	private static JButton 		jbOk;						// button OK
	private static JButton 		jbCancel;					// button Cancel
	private static boolean 		validated = false;			// true if OK has been pressed
	
	
	/**
	 * Private constructor. Creates an instance of {@link BinListPrecisionChooser}
	 * @param parent The Component from which the dialog is displayed
	 */
	private BinListPrecisionChooser(Component parent) {
		super();
		setModal(true);
		setTitle("Data Precision");
		initComponent();
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);		
		setLocationRelativeTo(parent);		
	}

	
	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		jlText = new JLabel("Choose a precision for the data of the fixed window list");		
		
		jcbPrecisionOption = new JComboBox(DataPrecision.values());
		// change the default selected item
		jcbPrecisionOption.setSelectedItem(DataPrecision.PRECISION_32BIT);
		
		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				validated = true;
				dispose();
			}
		});
		
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		add(jlText, c);
		
		c.gridy = 1;
		add(jcbPrecisionOption, c);
		
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.gridy = 2;
		c.gridwidth = 1;
		add(jbOk, c);
		
		c.gridx = 1;
		c.gridy = 2;
		add(jbCancel, c);		
	}
	
	
	/**
	 * Shows a dialog box asking the user to choose a precision for the data of a {@link BinList}
	 * @param parent the Component from which the dialog is displayed
	 * @return an Integer representing the precision of the data of a {@link BinList}
	 */
	public static DataPrecision getPrecision(Component parent) {
		new BinListPrecisionChooser(parent).setVisible(true);
		if (validated) {
			return (DataPrecision) jcbPrecisionOption.getSelectedItem();
		} else {
			return null;
		}
	}
}
