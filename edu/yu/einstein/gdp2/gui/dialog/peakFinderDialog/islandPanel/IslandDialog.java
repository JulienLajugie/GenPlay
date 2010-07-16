/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.islandDialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import yu.einstein.gdp2.core.enums.IslandResultType;
import yu.einstein.gdp2.core.filter.IslandFinder;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.exception.IFSettingsException;
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
	private final IslandDialogInformation	idfInformation;					// panel to manage additional information
	private final IslandDialogInput			idfInput;						// panel to manage input parameters
	private final IslandDialogOutput		idfOutput;						// panel to manage output data
	private final JPanel					jpValidation;					// panel to manage ok & cancel buttons
	private final JButton 					jbOk;							// button OK
	private final JButton 					jbCancel;						// button cancel
	private int								approved = CANCEL_OPTION;		// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	private IslandFinder 					island;							// Island Finder object needs to set parameters (ReadCountLimit, p-value, gap, cut-off)
	private BLAFindIslands 					blaIsland;						// BLAFindIslands object needs to set mutlitrack parameters
	
	/**
	 * Constructor, create an instance of IslandDialog.
	 * @param island 	IslandFinder object, needs to define p-value/readCountLimit parameters.
	 * @param blaIsland	uses to manage multitrack
	 */
	public IslandDialog(IslandFinder island, BLAFindIslands blaIsland) {
		super();
		this.island = island;
		this.blaIsland = blaIsland;
		
		//Fieldset initialization
		this.idfInformation = new IslandDialogInformation("Information", this.island);
		this.idfInput = new IslandDialogInput("Input", this.island, (IslandDialogInformation)this.idfInformation);
		this.idfOutput = new IslandDialogOutput("Output", this.island);
		
		//Validation panel
		this.jpValidation = new JPanel();
		this.jpValidation.setPreferredSize(new Dimension (IslandDialogFieldset.FIELDSET_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jpValidation.setMinimumSize(new Dimension (IslandDialogFieldset.FIELDSET_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jbOk = new JButton("OK");
		this.jbCancel = new JButton("Cancel");
		Dimension dButton = new Dimension ((int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH/4), IslandDialogFieldset.LINE_HEIGHT);
		this.jbOk.setPreferredSize(dButton);
		this.jbCancel.setPreferredSize(dButton);
		this.jbOk.setMinimumSize(dButton);
		this.jbCancel.setMinimumSize(dButton);
		this.jpValidation.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
		this.jpValidation.add(this.jbOk);
		this.jpValidation.add(this.jbCancel);
		
		//Listeners
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					jbOkActionPerformed();
				} catch (IFSettingsException e1) {
					ExceptionManager.handleException(rootPane, e1, e1.getMessage());
				}
			}
		});
		
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();				
			}
		});
		
		//Layout Manager
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets gbcInsets = new Insets (10, 0, 10, 0);
		
		// idfInformation
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = gbcInsets;
		this.add(this.idfInformation, gbc);
		
		// idfInput
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = gbcInsets;
		this.add(this.idfInput, gbc);
		
		// idfOuptut
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = gbcInsets;
		this.add(this.idfOutput, gbc);
		
		// jpValidation
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets (15, 0, 10, 0);
		add(this.jpValidation, gbc);
		
		//JDialog parameters
		Dimension dFrame = new Dimension (IslandDialogFieldset.FIELDSET_WIDTH + 20,
											this.idfInformation.getFieldsetHeight() + 
											this.idfInput.getFieldsetHeight() + 
											this.idfOutput.getFieldsetHeight() +
											6 * IslandDialogFieldset.LINE_HEIGHT);
		setSize(dFrame);
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setTitle("Island Finder Settings");
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
	 * @throws IFSettingsException 
	 */
	private void jbOkActionPerformed() throws IFSettingsException {
		if (this.idfOutput.filteredSelected() | this.idfOutput.IFScoreSelected()) {	// requirements to approved
			//All islands finder parameters must be set
			this.island.setWindowMinValue(this.idfInput.getWindowLimitValue());
			this.island.setGap(this.idfInput.getGap());
			this.island.setIslandMinScore(this.idfInput.getIslandLimitScore());
			this.island.setIslandMinLength(this.idfInput.getMinIslandLength());
			IslandDialogInput.windowMinValueStore = this.idfInput.getWindowLimitValue();
			IslandDialogInput.gapStore = this.idfInput.getGap();
			IslandDialogInput.IslandMinScoreStore = this.idfInput.getIslandLimitScore();
			IslandDialogInput.IslandMinLengthStore = this.idfInput.getMinIslandLength();
			//IslandResultType array to manage the right number of track for the BLAIslands object
			IslandResultType[] list = new IslandResultType[2];
			if (this.idfOutput.filteredSelected()) {
				list[0] = IslandResultType.FILTERED;
				IslandDialogOutput.FilteredStore = true;
			} else {
				IslandDialogOutput.FilteredStore = false;
			}
			if (this.idfOutput.IFScoreSelected()) {
				list[1] = IslandResultType.IFSCORE;
				IslandDialogOutput.IFScoreStore = true;
			} else {
				IslandDialogOutput.IFScoreStore = false;
			}
			this.blaIsland.setResultType(list);
			approved = APPROVE_OPTION;
			this.setVisible(false);
		} else {
			throw new IFSettingsException();
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
}