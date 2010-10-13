package yu.einstein.gdp2.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

public class TransfragDialog extends JDialog{

	private static final long serialVersionUID = 4265594104159799587L;

	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 0;
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 1;	
	/**
	 * Generate a Gene List Option
	 */
	public static final int GENERATE_GENE_LIST = 0;
	/**
	 * Generate a Fixed Window Option or scored chromosome window list
	 */
	public static final int GENERATE_SCORED_LIST = 1;
	/**
	 * The dialog is for binlist transfrag
	 */
	public static final int BINLIST_TRANSFRAG = 0;	
	/**
	 * The dialog is for SCWL transfrag
	 */
	public static final int SCWLIST_TRANSFRAG = 1;
	
	private final JLabel				jlGapSize;		// label for Gap size
	private final JFormattedTextField	jftGapSize;		// formatted text field for Gap size
	private final JLabel				jlGenerate;		// label for the word generate
	private final JRadioButton 			jrbGeneList;	// radio button gene list 
	private final JRadioButton 			jrbScoredList;	// radio button binlist or SCW list
	private final JButton 				jbCancel;		// cancel button
	private final JButton 				jbOk;			// ok button
	
	private int	approved = CANCEL_OPTION;			// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not 
	private int generateType;						// the type of list to generate
	private int gapSize;
	
	
	/**
	 * Creates an instance of {@link TransfragDialog}
	 * @param transfragType type of the transfrag dialog
	 */
	public TransfragDialog(int transfragType) {
		super();
		jlGapSize = new JLabel("Enter the Gap: ");
		DecimalFormat numFormat = new DecimalFormat("##.##");
		NumberFormatter num = new NumberFormatter(numFormat);
		num.setAllowsInvalid(false);
		jftGapSize = new JFormattedTextField(num);
		jftGapSize.setValue(0);
		jftGapSize.setColumns(9);
		jlGenerate = new JLabel("Generate");
		
		jrbGeneList = new JRadioButton("Gene List");
		if (transfragType == BINLIST_TRANSFRAG) {
			jrbScoredList = new JRadioButton("Fixed Window List");
		} else {
			jrbScoredList = new JRadioButton("Variable Window List");
		}
		
		jrbGeneList.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				//System.out.println("In gene list selected");
				resultTypeChanged();
			}
		});
		
		jrbScoredList.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				//System.out.println("In bin list selected");
				resultTypeChanged();
			}
		});
		
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(jrbGeneList);
		radioGroup.add(jrbScoredList);
		radioGroup.setSelected(jrbScoredList.getModel(), true);
		
		jbOk = new JButton("OK");
		jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.setDefaultCapable(true);
		jbOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkClicked();
			}
		});
		
		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));		
		jbCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlGapSize, c);
		
		c.gridx = 1;
		c.gridwidth = 2;
		add(jftGapSize, c);
		
		c.gridy = 1;
		c.gridx = 0;
		add(jlGenerate, c);
		
		c.gridx = 1;
		add(jrbScoredList, c);
		
		c.gridy = 2;
		add(jrbGeneList, c);
		
		c.gridy = 3;
		c.gridwidth = 1;
		add(jbOk, c);
		
		c.gridy = 3;
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbCancel, c);		
		
		setTitle("Transfrag");
		pack();
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(jbOk);
		setModal(true);
		resultTypeChanged();
	}
	
	/**
	 * @return the gap size
	 */
	public int getGapSize()	{
		try	{
			if(jftGapSize.isEnabled()) {
				gapSize = Integer.parseInt(jftGapSize.getValue().toString());
				return gapSize;
			}
		}
		catch(NumberFormatException e) {
			System.err.println("Please enter a valid gap");
			e.printStackTrace();
		}
		return 0;		
	}
	
	/**
	 * @return the resultType
	 */
	public int getResultType() {
		return generateType;		
	}
	
	/**
	 * Method called when the selected result type changes
	 */
	protected void resultTypeChanged() {
		if (jrbGeneList.isSelected()) {
			generateType = TransfragDialog.GENERATE_GENE_LIST;
		} else if (jrbScoredList.isSelected()) {
			generateType = TransfragDialog.GENERATE_SCORED_LIST;
		}
	}
	
	/**
	 * Method called when the button okay is clicked
	 */
	protected void jbOkClicked() {
		approved = APPROVE_OPTION;
		setVisible(false);
	}
	
	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return GENERATE_OPTION if Generate is clicked. CANCEL_OPTION otherwise.
	 */
	public int showTransfragDialog(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
