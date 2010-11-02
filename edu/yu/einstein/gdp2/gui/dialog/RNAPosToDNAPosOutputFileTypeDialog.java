/**
 * @author Chirag Gorasia
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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * A frame to select the Output File Type for the {@link PARNAPosToDNAPos}
 * @author Chirag Gorasia
 * @version 0.1
 */
public class RNAPosToDNAPosOutputFileTypeDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 119673866368451572L;
	
	private final JComboBox jcb;					
	private final JLabel jlFileTypeOptions;			// Output File Type Option Label
	private JButton jbOK;
	private JButton jbCancel;
	
	public static final int 	APPROVE_OPTION = 0;
	public static final int 	CANCEL_OPTION = 1;
	private int	approved = CANCEL_OPTION;
	
	private final static String[] OPTIONS = 
	{"BGR Output File", "GDP Output File", "BGR Output File With Extra Fields"};		// available options in the comboBoxes
	
	
	public RNAPosToDNAPosOutputFileTypeDialog() {
		super();
		jlFileTypeOptions = new JLabel("RNA To DNA Output File Type: ");
		jcb = new JComboBox(OPTIONS);
		
		jbOK = new JButton("OK");
		jbOK.setPreferredSize(new Dimension(75, 30));
		jbOK.setDefaultCapable(true);
		jbOK.addActionListener(this);

		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(this);
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(jlFileTypeOptions);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(jcb);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(jbOK, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, gbc);
		
		pack();
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setTitle("RNA To DNA Output File Selector");
		setVisible(false);
		jbOK.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOK);
	}

	/**
	 * @return an int corresponding to output file selected 
	 */
	public int getSelectedOutputFileType() {
		return jcb.getSelectedIndex();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbOK) {
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