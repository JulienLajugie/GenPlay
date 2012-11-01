package edu.yu.einstein.genplay.gui.action.repeatFamilyTrack;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.RepeatFamily;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.core.list.repeatFamilyList.operation.RFLOConvertIntoMask;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.checkBoxTableChooser.CheckBoxTableChooserDialog;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * Converts the selected families from the selected repeat track into a Mask
 * @author Julien Lajugie
 * @version 0.1
 */
public class RFTAConvertIntoMask extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -167849335384206182L;  						// generated ID
	private static final String 	ACTION_NAME = "Convert Into Mask";							// action name
	private static final String 	DESCRIPTION = "Convert the specified families into a mask";	// tooltip
	private Track<?> 				selectedTrack;												// The selected track.

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = RFTAConvertIntoMask.class.getName();


	/**
	 * Creates an instance of {@link RFTAConvertIntoMask}
	 */
	public RFTAConvertIntoMask() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() throws Exception {		
		selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			RepeatFamilyList selectedTrackData = (RepeatFamilyList) selectedTrack.getData();
			List<String> families = new ArrayList<String>(); 
			for (List<RepeatFamily> currentRepeatList: selectedTrackData) {
				for (RepeatFamily currentFamily: currentRepeatList) {
					if (!families.contains(currentFamily.getName())) {
						families.add(currentFamily.getName());
					}
				}
			}
			CheckBoxTableChooserDialog<String> familyChooser = new CheckBoxTableChooserDialog<String>();
			familyChooser.setItems(families);
			familyChooser.setSelectedItems(families);
			familyChooser.setOrdering(false);
			if (familyChooser.showDialog(getRootPane()) == CheckBoxTableChooserDialog.APPROVE_OPTION) {
				List<String> selectedFamilies = familyChooser.getSelectedItems();
				if ((selectedFamilies != null) && (!selectedFamilies.isEmpty())) {
					return new RFLOConvertIntoMask(selectedTrackData, selectedFamilies);
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			selectedTrack.setMask(actionResult);
		}
	}
}
