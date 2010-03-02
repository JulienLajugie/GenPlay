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
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.NumberFormatter;

import yu.einstein.gdp2.core.list.geneList.GeneListOperations;
import yu.einstein.gdp2.gui.action.geneListTrack.ExtractIntervalAction;


/**
 * A frame to define the interval for the {@link ExtractIntervalAction}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ExtractGeneIntervalsDialog extends JDialog implements ActionListener {

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private static final long serialVersionUID = 4391029438908582693L;	// generated ID
	private final static String[] OPTIONS = 
	{"before start position", "after start position", 
		"before stop position", "after stop position"};		// available options in the comboBoxes
	private final static int[] OPTION_VALUES = {
		GeneListOperations.BEFORE_START, 
		GeneListOperations.AFTER_START,
		GeneListOperations.BEFORE_STOP, 
		GeneListOperations.AFTER_STOP
	};														// value associated to this operations
	private final JLabel 				jlStart1;			// first label start
	private final JFormattedTextField 	jftfStartDistance;	// text field start distance
	private final JLabel 				jlStart2;			// second label start
	private final JComboBox 			jcbStartFrom;		// comboBox start from
	private final JLabel 				jlStop1;			// first label stop
	private final JFormattedTextField 	jftfStopDistance;	// text field stop distance
	private final JLabel 				jlStop2;			// second label stop
	private final JComboBox 			jcbStopFrom;		// comboBox stop from
	private final JButton 				jbOk;				// button okay
	private final JButton 				jbCancel;			// button cancel
	private int	approved = CANCEL_OPTION;					// approved or canceled


	/**
	 * Creates an instance of {@link ExtractGeneIntervalsDialog}
	 */
	public ExtractGeneIntervalsDialog() {
		super();

		jlStart1 = new JLabel("Extract intervals starting at ");
		jftfStartDistance = new JFormattedTextField(new DecimalFormat("###,###,###"));
		((NumberFormatter)jftfStartDistance.getFormatter()).setMinimum(0);
		jftfStartDistance.setValue(0);
		jftfStartDistance.setColumns(9);
		jlStart2 = new JLabel(" bp ");
		jcbStartFrom = new JComboBox(OPTIONS);		

		jlStop1 = new JLabel("and stop at ");
		jftfStopDistance = new JFormattedTextField(new DecimalFormat("###,###,###"));
		jftfStopDistance.setValue(0);
		jftfStopDistance.setColumns(9);
		jlStop2 = new JLabel(" bp ");
		((NumberFormatter)jftfStopDistance.getFormatter()).setMinimum(0);
		jcbStopFrom = new JComboBox(OPTIONS);

		jbOk = new JButton("Ok");
		jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.setDefaultCapable(true);
		jbOk.addActionListener(this);

		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(jlStart1, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		add(jftfStartDistance, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		add(jlStart2, gbc);

		gbc.gridx = 3;
		gbc.gridy = 0;
		add(jcbStartFrom, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(jlStop1, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		add(jftfStopDistance, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		add(jlStop2, gbc);

		gbc.gridx = 3;
		gbc.gridy = 1;
		add(jcbStopFrom, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(jbOk, gbc);

		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, gbc);

		pack();
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setTitle("Track configuration");
		setVisible(false);
		jbOk.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOk);
	}


	/**
	 * @return the start distance
	 */
	public int getStartDistance() {
		Number res = (Number) jftfStartDistance.getValue();  
		return res.intValue();
	}


	/**
	 * @return an int corresponding to start from 
	 */
	public int getStartFrom() {
		return OPTION_VALUES[jcbStartFrom.getSelectedIndex()];
	}


	/**
	 * @return the stop distance
	 */
	public int getStopDistance() {
		Number res = (Number) jftfStopDistance.getValue();  
		return res.intValue();
	}


	/**
	 * @return an int corresponding to stop from
	 */
	public int getStopFrom() {
		return OPTION_VALUES[jcbStopFrom.getSelectedIndex()];
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == jbOk) {
			approved = APPROVE_OPTION;
		}
		setVisible(false);		
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
