/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.project;

import java.awt.Component;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import yu.einstein.gdp2.core.RNAPosToDNAPos.GeneRelativeToGenomePosition;
import yu.einstein.gdp2.core.enums.RNAToDNAResultType;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.dialog.RNAPosToDNAPosOutputFileTypeDialog;
import yu.einstein.gdp2.gui.fileFilter.BedFilter;
import yu.einstein.gdp2.gui.fileFilter.BedGraphFilter;
import yu.einstein.gdp2.gui.fileFilter.GdpGeneFilter;
import yu.einstein.gdp2.util.Utils;


/**
 * Replaces positions relative to a reference (RNA) to DNA positions 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PARNAPosToDNAPos extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 8927411528919859767L;		// generated ID
	private static final String 	DESCRIPTION = 
		"Replace positions relative to a reference (RNA) to DNA positions"; // tooltip
	private static final String 	ACTION_NAME = "RNA To DNA Reference";	// action name
	private final 		Component 	parent;									// parent component
	private RNAToDNAResultType outputFileType;								// output file type


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PARNAPosToDNAPos";


	/**
	 * Creates an instance of {@link PARNAPosToDNAPos}
	 * @param parent parent component
	 */
	public PARNAPosToDNAPos(Component parent) {
		super();
		this.parent = parent;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}




	@Override
	protected Void processAction() throws Exception {
		File fileRef;
		File fileData;
		File fileOutput = null;


		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		FileNameExtensionFilter textFileFilter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
		FileFilter[] fileFilters1 = {textFileFilter, new BedGraphFilter()};
		fileData = Utils.chooseFileToLoad(parent, "Select Coverage File", defaultDirectory, fileFilters1);
		if (fileData != null) {
			FileFilter[] fileFilters2 = {textFileFilter, new BedFilter()};
			fileRef = Utils.chooseFileToLoad(parent, "Select Reference File", defaultDirectory, fileFilters2);
			if (fileRef != null) {

				RNAPosToDNAPosOutputFileTypeDialog rnaToDnaDialog = new RNAPosToDNAPosOutputFileTypeDialog();
				int rtddResult = rnaToDnaDialog.showDialog(getRootPane());
				outputFileType = rnaToDnaDialog.getSelectedOutputFileType();

				if (rtddResult == RNAPosToDNAPosOutputFileTypeDialog.APPROVE_OPTION) {
					if (outputFileType == RNAToDNAResultType.GDP) {
						JFileChooser jfc = new JFileChooser(ConfigurationManager.getInstance().getDefaultDirectory());
						jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						jfc.setDialogTitle("Select Output GDP File");
						jfc.addChoosableFileFilter(new GdpGeneFilter());
						jfc.setAcceptAllFileFilterUsed(false);
						int returnVal = jfc.showSaveDialog(parent);
						if(returnVal == JFileChooser.APPROVE_OPTION) {
							fileOutput = Utils.addExtension(jfc.getSelectedFile(), "gdp");
						}
					} else {
						JFileChooser jfc = new JFileChooser(ConfigurationManager.getInstance().getDefaultDirectory());
						jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						jfc.setDialogTitle("Select Output BGR File");
						jfc.addChoosableFileFilter(new BedGraphFilter());
						jfc.setAcceptAllFileFilterUsed(false);
						int returnVal = jfc.showSaveDialog(parent);
						if(returnVal == JFileChooser.APPROVE_OPTION) {
							fileOutput = Utils.addExtension(jfc.getSelectedFile(), "bgr");						
						}
					}
					if (fileOutput != null) {
						final GeneRelativeToGenomePosition grtgp = new GeneRelativeToGenomePosition(fileData, fileRef, fileOutput, outputFileType);
						notifyActionStart("Generating Output Files", 1, false);
						grtgp.rePosition();
					}
				}
			}
		}
		return null;
	}
}
