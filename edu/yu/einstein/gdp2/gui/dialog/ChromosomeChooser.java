/**
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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import yu.einstein.gdp2.core.manager.ChromosomeManager;


/**
 * A dialog box used to choose a list of chromosomes. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ChromosomeChooser extends JDialog {

	private static final long serialVersionUID = 8456898359925801785L; 	// Generated serial number
	private static JButton 					jbOk;						// Button OK
	private static JButton					jbCancel;					// Button Cancel
	private static JLabel 					jlChoose;					// Label on top of the component
	private static JCheckBox[] 				jcbChromos;					// Array of checkboxes, 1 per chromosome
	private static JCheckBox 				jcbSelectAll;				// Checkbox used to check / unchecked all chromo
	private static boolean 					validated;					// True if the button OK has been pressed
	private static ChromosomeManager 		chromosomeManager;			// ChromosomeManager


	/**
	 * Private constructor. Used internally to create a ChromosomeChooser dialog. 
	 * @param parent The Component from which the dialog is displayed
	 */
	private ChromosomeChooser(Component parent) {
		super();
		setModal(true);
		validated = false;
		chromosomeManager = ChromosomeManager.getInstance();
		initComponent();	
		setTitle("Choose chromosomes");
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}


	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		jlChoose = new JLabel("Choose the chromosomes you want to study:");
		jcbChromos = new JCheckBox[chromosomeManager.size()];

		jcbSelectAll = new JCheckBox("Select All");
		jcbSelectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jcbSelectAllItemStateChanged();				
			}
		});

		jbOk = new JButton("OK");
		jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.setDefaultCapable(true);
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

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER ;
		c.weightx = 0.5;
		c.weighty = 0.20;
		c.anchor = GridBagConstraints.CENTER;
		add(jlChoose, c);

		c.gridwidth = 1;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.anchor = GridBagConstraints.LINE_START;
		int y = 1;
		short k = 0;
		while(k < chromosomeManager.size()) {
			int x = 0;
			c.gridy = y;
			while((k < chromosomeManager.size()) && (x < 4)) {
				jcbChromos[k] = new JCheckBox(chromosomeManager.get(k).getName());
				jcbChromos[k].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						checkIfAllCBChecked();
					}
				});
				c.gridx = x;
				add(jcbChromos[k], c);				
				x++;
				k++;
			}
			y++;
		}

		c.gridx = 0;
		c.gridy = y;
		c.gridwidth = GridBagConstraints.REMAINDER ;
		c.anchor = GridBagConstraints.CENTER;
		add(jcbSelectAll, c);

		c.gridy = y + 1;
		c.gridwidth = 2;
		add(jbOk, c);

		c.gridx = 2;
		add(jbCancel, c);
	}


	/**
	 * Checks / unchecks every chromosome.
	 */
	private void jcbSelectAllItemStateChanged() {
		boolean selectAll = jcbSelectAll.isSelected();
		for(int i = 0; i < chromosomeManager.size(); i++) {
			jcbChromos[i].setSelected(selectAll); 
		}
		jcbSelectAll.setEnabled(true);
	}
	
	
	/**
	 * Checks the "select all" box if all the chromosome are checked.
	 * Unchecks it if not.
	 */
	private static void checkIfAllCBChecked() {
		boolean allChecked = true;
		for (JCheckBox currentCB: jcbChromos) {
			if (!currentCB.isSelected()) {
				allChecked = false;
			}
		}
		if (allChecked) {
			jcbSelectAll.setSelected(true);
		} else {
			jcbSelectAll.setSelected(false);
		}
	}


	/**
	 * Closes the dialog. No action are performed.
	 */
	private void jbCancelActionPerformed() {
		this.dispose();
	}


	/**
	 * Closes the dialog. Sets validated to true so the main function can return a list of booleans 
	 * corresponding to the check / uncheck states of the chromosomes.
	 */
	private void jbOkActionPerformed() {
		validated = true;
		this.dispose();		
	}

	

	/**
	 * Only public function. Displays a ChromosomeChooser dialog, and returns a list of booleans 
	 * corresponding to the check / uncheck states of the chromosomes if the OK has been pressed.
	 * @param parent The Component from which the dialog is displayed.
	 * @return a list of booleans corresponding to the check / uncheck states
	 * of the chromosomes if the OK has been pressed. Else returns null. 
	 */
	public static boolean[] getSelectedChromo(Component parent) {
		ChromosomeChooser CCOP = new ChromosomeChooser(parent);
		CCOP.setVisible(true);	

		if(validated) {
			boolean[] returnArray = new boolean[chromosomeManager.size()];
			for(int i = 0; i < chromosomeManager.size(); i++) {
				returnArray[i] = false;

			}
			for(int i = 0; i < chromosomeManager.size(); i++) {
				returnArray[i] = jcbChromos[i].isSelected();
			}			
			return returnArray;
		}
		else
			return null;
	}
		
	
	/**
	 * Only public function. Displays a ChromosomeChooser dialog, and returns a list of booleans 
	 * corresponding to the check / uncheck states of the chromosomes if the OK has been pressed.
	 * @param parent The Component from which the dialog is displayed.
	 * @param selectedChromo list of the chromosomes that should appear as selected when the dialog is shown
	 * @return a list of booleans corresponding to the check / uncheck states
	 * of the chromosomes if the OK has been pressed. Else returns null. 
	 */
	public static boolean[] getSelectedChromo(Component parent, boolean[] selectedChromo) {
		ChromosomeChooser CCOP = new ChromosomeChooser(parent);
		
		// check the box of the chromosomes that need to be selected
		for (int i = 0; i < selectedChromo.length; i++) {
			jcbChromos[i].setSelected(selectedChromo[i]);
		}
		checkIfAllCBChecked();
		
		CCOP.setVisible(true);	

		if(validated) {
			boolean[] returnArray = new boolean[chromosomeManager.size()];
			for(int i = 0; i < chromosomeManager.size(); i++) {
				returnArray[i] = false;

			}
			for(int i = 0; i < chromosomeManager.size(); i++) {
				returnArray[i] = jcbChromos[i].isSelected();
			}			
			return returnArray;
		}
		else
			return null;
	}
}
