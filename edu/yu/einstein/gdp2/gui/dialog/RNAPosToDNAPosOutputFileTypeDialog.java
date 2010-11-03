/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import yu.einstein.gdp2.core.enums.RNAToDNAResultType;


/**
 * A frame to select the Output File Type for the {@link PARNAPosToDNAPos}
 * @author Chirag Gorasia
 * @author Julien Lajugie
 * @version 0.1
 */
public class RNAPosToDNAPosOutputFileTypeDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 8313046917432891962L;	// generated ID
	private static final int INSET = 15;	// gab between the components and the window 
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private final JComboBox jcb;			// combo box
	private final JLabel jlFileTypeOptions;	// Output File Type Option Label
	private JButton jbOK;					// ok button
	private JButton jbCancel;				// cancel button
	private int	approved = CANCEL_OPTION;	// true if okay has been clicked
		
	
	/**
	 * Public constructor. Creates an instance of {@link RNAPosToDNAPosOutputFileTypeDialog}
	 */
	public RNAPosToDNAPosOutputFileTypeDialog() {
		super();
		jlFileTypeOptions = new JLabel("RNA To DNA Output File Type: ");
		jcb = new JComboBox(RNAToDNAResultType.values());
		
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
		gbc.insets = new Insets(INSET, INSET, INSET, 0);
		add(jlFileTypeOptions, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(INSET, 0, INSET, INSET);
		add(jcb, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(INSET, INSET, INSET, 0);
		add(jbOK, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(INSET, 0, INSET, INSET);
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
	 * @return an {@link RNAToDNAResultType} corresponding to the selected output
	 */
	public RNAToDNAResultType getSelectedOutputFileType() {
		return (RNAToDNAResultType) jcb.getSelectedItem();
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
