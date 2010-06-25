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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import yu.einstein.gdp2.core.enums.IslandResultType;
import yu.einstein.gdp2.core.filter.IslandFinder;
import yu.einstein.gdp2.gui.action.binListTrack.BLAFindIslands;

/**
 * A frame allowing to configure the properties of the island finder.
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class IslandDialog extends JDialog {
	
	private static final long serialVersionUID = 8143320501058939077L;
	
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private static final Dimension			APPEARANCE_FRAME_DIMENSION = 
											new Dimension(400, 370);		// Dimension of this frame
	private final JButton 					jbOk;							// button OK
	private final JButton 					jbCancel;						// button cancel
	private final JLabel					jlAverage;						// label for average (lambda value)
	private final JLabel					jlReadCountLimit;				// label for ReadCountLimit
	private final JFormattedTextField		jftfReadCountLimit;				// text field for ReadCountLimit
	private final JLabel					jlPValue;						// label for p-value
	private final JFormattedTextField		jftfPValue;						// text field for p-value
	private final JButton					jbToReadCountLimit;				// button to convert p-value to ReadCountLimit
	private final JButton					jbToPValue;						// button to convert ReadCountLimit to p-value
	private final JLabel					jlGap;							// label for gap
	private final JFormattedTextField		jftfGap;						// text field for gap
	private final JLabel					jlCutOff;						// label for cut-off
	private final JFormattedTextField		jftfCutOff;						// text field for cut-off
	private final JLabel					jlResultType;					// label for result type 
	private final JCheckBox					jcbIFScore;						// check box to choose Island Finder Score output value
	private final JCheckBox					jcbFiltered;					// check box to choose original date filtered output value
	private int								approved = CANCEL_OPTION;		// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	private IslandFinder 					island;							// Island Finder object needs to set parameters (ReadCountLimit, p-value, gap, cut-off)
	private BLAFindIslands 					blaIsland;						// BLAFindIslands object needs to set mutlitrack parameters
	private double							pvalue;							// p-value storage to not recalculate the value if it has no changed
	private int								read;							// read storage to not recalculate the value if it has no changed
	private boolean 						pvalueChanged;					// necessary to know if value has changed
	
	/**
	 * Constructor, create an instance of IslandDialog.
	 * @param island 	IslandFinder object, needs to define p-value/readCountLimit parameters.
	 * @param blaIsland	uses to manage multitrack
	 */
	public IslandDialog(IslandFinder island, BLAFindIslands blaIsland) {
		super();
		this.island = island;
		this.blaIsland = blaIsland;
		pvalueChanged = true;
		
		//Number Format
		NumberFormat floatFormat = NumberFormat.getNumberInstance();
		floatFormat.setMinimumFractionDigits(2);
		floatFormat.setMaximumFractionDigits(15);
		NumberFormat intFormat = NumberFormat.getNumberInstance();
		intFormat.setMaximumFractionDigits(0);
		
		
		//Initialization
		jbOk = new JButton("OK");
		jbCancel = new JButton("Cancel");
		Long average = Math.round(this.island.getLambda()*100)/100;
		jlAverage = new JLabel ("The average is: " + average.toString() + " reads by window");
		jlReadCountLimit = new JLabel ("Read Count");
		jftfReadCountLimit = new JFormattedTextField(floatFormat);
		jftfReadCountLimit.setHorizontalAlignment(JFormattedTextField.RIGHT);
		jftfReadCountLimit.setValue(new Float(0.0));
		jlPValue = new JLabel ("P-Value");
		jftfPValue = new JFormattedTextField(floatFormat);
		jftfPValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		jbToReadCountLimit = new JButton("<");
		jbToPValue = new JButton(">");
		jlGap = new JLabel("Gap");
		jftfGap = new JFormattedTextField(intFormat);
		jftfGap.setHorizontalAlignment(JFormattedTextField.RIGHT);
		jftfGap.setValue(new Integer(0));
		jlCutOff = new JLabel("Island Score cut-off");
		jftfCutOff = new JFormattedTextField(floatFormat);
		jftfCutOff.setHorizontalAlignment(JFormattedTextField.RIGHT);
		jftfCutOff.setValue(new Float(0.0));
		jlResultType = new JLabel("Result Type");
		jcbFiltered = new JCheckBox("Filtered");
		jcbIFScore = new JCheckBox("IF Score");
		
		
		//Tool Tip Text
		String sAverage = "Mean of reads by window";
		String sReadCount = "All values below the Read Count Limit well be ignored";
		String sPValue = "Probability to get false results";
		String sToReadCOunt = "Convert P-Value to Read Count";
		String sToPValue = "Convert Read Count to P-Value";
		String sGap = "Number of window authorized below the Read Count Limit to select island";
		String sCutOff = "All island score below the cut-off well be ignored";
		String sResultType = "Output result type";
		String sFiltered = "Windows values will be the reads";
		String sIFScore = "Windows values will be the island score";
		jlAverage.setToolTipText(sAverage);
		jlReadCountLimit.setToolTipText(sReadCount);
		jftfReadCountLimit.setToolTipText(sReadCount);
		jlPValue.setToolTipText(sPValue);
		jftfPValue.setToolTipText(sPValue);
		jbToReadCountLimit.setToolTipText(sToReadCOunt);
		jbToPValue.setToolTipText(sToPValue);
		jlGap.setToolTipText(sGap);
		jftfGap.setToolTipText(sGap);
		jlCutOff.setToolTipText(sCutOff);
		jftfCutOff.setToolTipText(sCutOff);
		jlResultType.setToolTipText(sResultType);
		jcbFiltered.setToolTipText(sFiltered);
		jcbIFScore.setToolTipText(sIFScore);
		
		
		//Dimension
		jbOk.setPreferredSize(new Dimension (90, 25));
		jbCancel.setPreferredSize(new Dimension (90, 25));
		jftfReadCountLimit.setPreferredSize(new Dimension (130, 25));
		jftfReadCountLimit.setMinimumSize(new Dimension (130, 25));
		jftfPValue.setPreferredSize(new Dimension (130, 25));
		jftfPValue.setMinimumSize(new Dimension (130, 25));
		jftfGap.setPreferredSize(new Dimension (130, 25));
		jftfGap.setMinimumSize(new Dimension (130, 25));
		jftfCutOff.setPreferredSize(new Dimension (130, 25));
		jftfCutOff.setMinimumSize(new Dimension (130, 25));
		jbToReadCountLimit.setPreferredSize(new Dimension (40, 25));
		jbToPValue.setPreferredSize(new Dimension (40, 25));
		
		
		//Listeners
		jbToPValue.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				toPValue();
			}
		});
		
		jbToReadCountLimit.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				toReadCountLimit();
			}
		});
		
		jftfPValue.addKeyListener(new KeyListener () {
			//value is changed when a keyboard action is made 
			@Override
			public void keyPressed(KeyEvent arg0) {
				pvalueChanged = true;
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				pvalueChanged = true;
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				pvalueChanged = true;
			}
		});
		
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();
			}
		});
		
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();				
			}
		});
		
		
		//Layout Manager
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		//Average
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets (10, 3, 10, 3);
		c.gridwidth = 3;
		add(jlAverage, c);
		
		
		//Read Count Limit
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets (10, 3, 10, 3);
		c.gridwidth = 1;
		add(jlReadCountLimit, c);

		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets (2, 3, 10, 3);
		add(jftfReadCountLimit, c);
		
		
		//To ...
		c.gridx = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.ipadx = 5;
		add(jbToReadCountLimit, c);
		
		c.gridx = 2;
		c.anchor = GridBagConstraints.CENTER;
		add(jbToPValue, c);
		
		
		//P-Value
		c.gridx = 3;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.ipadx = 0;
		c.insets = new Insets (10, 3, 10, 3);
		add(jlPValue, c);
		
		c.gridx = 3;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets (2, 3, 10, 3);
		add(jftfPValue, c);
		
		
		//Gap
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets (10, 3, 10, 3);
		add(jlGap, c);
		
		c.gridx = 2;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 3;
		add(jftfGap, c);	
		
		
		//Island Score Cut-Off
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		add(jlCutOff, c);
		
		c.gridx = 2;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 3;
		add(jftfCutOff, c);
		
		//Result Type
		c.gridx = 0;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 1;
		add(jlResultType, c);
		
		c.gridx = 2;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 3;
		add(jcbFiltered, c);
		
		c.gridx = 2;
		c.gridy = 6;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 3;
		c.insets = new Insets (2, 3, 10, 3);
		add(jcbIFScore, c);
		
		
		// OK & Cancel
		c.gridx = 0;
		c.gridy = 7;
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 2;
		c.ipadx = 5;
		c.insets = new Insets (15, 0, 10, 0);
		add(jbOk, c);

		c.gridx = 2;
		c.anchor = GridBagConstraints.CENTER;
		add(jbCancel, c);
		
		
		//JDialog parameters
		setSize(APPEARANCE_FRAME_DIMENSION);
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setTitle("Island Finder Configuration");
		setVisible(false);
		jbOk.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOk);
	}

	/**
	 * Hides this frame when Cancel is pressed. 
	 */
	private void jbCancelActionPerformed() {
		this.setVisible(false);
	}

	/**
	 * Called when OK is pressed.
	 */
	private void jbOkActionPerformed() {
		if (jftfReadCountLimit.getValue() != null &&
			jftfGap.getValue() != null &&
			jftfCutOff != null && (
			jcbFiltered.isSelected() |
			jcbIFScore.isSelected())
			) {	// requirements to approved
			this.island.setReadCountLimit(Double.parseDouble(jftfReadCountLimit.getValue().toString()));
			this.island.setGap(Integer.parseInt(jftfGap.getValue().toString()));
			this.island.setCutOff(Double.parseDouble(jftfCutOff.getValue().toString()));
			IslandResultType[] list = new IslandResultType[2];
			if (jcbFiltered.isSelected()) {
				list[0] = IslandResultType.FILTERED;
			}
			if (jcbIFScore.isSelected()) {
				list[1] = IslandResultType.IFSCORE;
			}
			this.blaIsland.setResultType(list);
			approved = APPROVE_OPTION;
			this.setVisible(false);
		} else {
			JOptionPane.showMessageDialog(null, "Please fill all settings", "Information", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showTrackConfiguration(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}

	/**
	 * Manage the conversion of read count limit to p-value.
	 */
	private void toPValue () {
		int read;
		Double pvalue;
		if (jftfReadCountLimit.getValue() != null) {
			try {
				jftfReadCountLimit.commitEdit();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			read = Integer.parseInt(jftfReadCountLimit.getValue().toString());
			pvalue = island.findPValue(read);
			this.pvalue = pvalue;
			this.read = read;
			jftfPValue.setValue(pvalue);
		}
	}
	
	/**
	 * Manage the conversion of p-value to read count limit.
	 */
	private void toReadCountLimit () {
		double read;
		Double pvalue;
		if (pvalueChanged) {
			if (jftfPValue.getValue() != null) {
				try {
					jftfPValue.commitEdit();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				pvalue = Double.valueOf((jftfPValue.getValue()).toString());
				read = island.findReadCountLimit(pvalue);
				jftfReadCountLimit.setValue(read);
				pvalueChanged = false;
			}
		}
	}
}
