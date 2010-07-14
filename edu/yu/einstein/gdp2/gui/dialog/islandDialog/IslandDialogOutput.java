/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.islandDialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import yu.einstein.gdp2.core.filter.IslandFinder;

/**
 * This panel shows output options for the island finder frame settings
 * 
 * @author Nicolas
 * @version 0.1
 */
final class IslandDialogOutput extends IslandDialogFieldset{
	
	private static final long serialVersionUID = -1616307602412859645L;
	
	//Constant values
	private static final int NAME_WIDTH = (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.35);
	private static final int GARBAGE_WIDTH = (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.40);
	
	//Storage values
	protected static boolean 	FilteredStore;
	protected static boolean 	IFScoreStore;
	
	//Graphics elements
	private final JLabel					jlResultType;					// label for result type 
	private final JCheckBox					jcbIFScore;						// check box to choose Island Finder Score output value
	private final JCheckBox					jcbFiltered;					// check box to choose original date filtered output value
	
	
	/**
	 * Constructor for IslandDialogOutput
	 * @param title		fieldset title
	 * @param island	IslandFinder object to set some information
	 */
	IslandDialogOutput(String title, IslandFinder island) {
		super(title, island);
		
		//Set "window size" information
		this.jlResultType = new JLabel("Result Type");
		this.jcbFiltered = new JCheckBox("Start values");
		this.jcbIFScore = new JCheckBox("Island score");
		
		//Dimension
		this.jlResultType.setPreferredSize(new Dimension(IslandDialogOutput.NAME_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jcbFiltered.setPreferredSize(new Dimension(IslandDialogOutput.GARBAGE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jcbIFScore.setPreferredSize(new Dimension(IslandDialogOutput.GARBAGE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		
		//Set selected boxes
		this.jcbFiltered.setSelected(this.getBoolStoredValue(FilteredStore, false));
		this.jcbIFScore.setSelected(this.getBoolStoredValue(IFScoreStore, false));
		
		//Tool Tip Text
		String sResultType = "Output result type";
		String sFiltered = "Windows values will be the windows value";
		String sIFScore = "Windows values will be the island score";
		this.jlResultType.setToolTipText(sResultType);
		this.jcbFiltered.setToolTipText(sFiltered);
		this.jcbIFScore.setToolTipText(sIFScore);
		
		//Layout
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets gbcInsets = new Insets (IslandDialogFieldset.LINE_TOP_INSET_HEIGHT, 0, IslandDialogFieldset.LINE_BOTTOM_INSET_HEIGHT, 0);
		
		//jlResultType
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		add(jlResultType, gbc);
		
		//jcbFiltered
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		add(jcbFiltered, gbc);
		
		//jcbIFScore
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = new Insets (0, 0, IslandDialogFieldset.LINE_BOTTOM_INSET_HEIGHT, 0);
		add(jcbIFScore, gbc);
		
		// Dimension
		this.setRows(gbc.gridy + 1);
		
		this.setVisible(true);
	}

	/**
	 * This method check an Boolean value to return it if it is not null, or return the initial value
	 * 
	 * @param value		value to return
	 * @param initial	returned value if 'value' is null
	 * @return			Integer
	 */
	private Boolean getBoolStoredValue (Boolean value, Boolean initial) {
		if (value != null) {
			return value;
		} else {
			return initial;
		}
	}
	
	//Getters for selected checkboxes
	protected boolean filteredSelected () {
		return this.jcbFiltered.isSelected();
	}
	
	protected boolean IFScoreSelected () {
		return this.jcbIFScore.isSelected();
	}
}