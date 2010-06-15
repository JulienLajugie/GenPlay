/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.writer.binListWriter.BinListWriter;
import yu.einstein.gdp2.core.writer.binListWriter.BinListWriterFactory;
import yu.einstein.gdp2.core.writer.geneListWriter.GeneListWriter;
import yu.einstein.gdp2.core.writer.geneListWriter.GeneListWriterFactory;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.fileFilter.ExtendedFileFilter;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;

/**
 * Saves the selected {@link Track}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATASave extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 212223991804272305L;	// generated ID
	private static final String 	ACTION_NAME = "Save As";			// action name
	private static final String 	DESCRIPTION = 
		"Save the selected track";								 		// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "saveTrack";


	/**
	 * Creates an instance of {@link ATASave}
	 */
	public ATASave() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	protected Void processAction() throws Exception {
		if (getTrackList().getSelectedTrack() != null) {
			Track selectedTrack = getTrackList().getSelectedTrack(); 
			if (selectedTrack instanceof BinListTrack) {
				saveBinList((BinListTrack)selectedTrack);
			} else if (selectedTrack instanceof GeneListTrack) {
				saveGeneList((GeneListTrack)selectedTrack);
			}
		}
		return null;
	}


	/**
	 * Saves a {@link BinListTrack}
	 * @param selectedTrack selected {@link BinListTrack}
	 * @throws Exception 
	 */
	private void saveBinList(BinListTrack selectedTrack) throws Exception {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		JFileChooser jfc = new JFileChooser(defaultDirectory);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save Track");
		for (FileFilter currentFilter: Utils.getWritableBinListFileFilters()) {
			jfc.addChoosableFileFilter(currentFilter);
		}
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileFilter(jfc.getChoosableFileFilters()[0]);
		int returnVal = jfc.showSaveDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
				BinList data = selectedTrack.getBinList();
				String name = selectedTrack.getName();
				BinListWriter blw = BinListWriterFactory.getBinListWriter(selectedFile, data, name, selectedFilter);
				notifyActionStart("Saving Track #" + selectedTrack.getTrackNumber(), 1);
				blw.write();
			}
		}
	}


	/**
	 * Saves a {@link GeneListTrack}
	 * @param selectedTrack selected {@link GeneListTrack}
	 * @throws Exception 
	 */
	private void saveGeneList(GeneListTrack selectedTrack) throws Exception {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		JFileChooser jfc = new JFileChooser(defaultDirectory);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save Track");
		for (FileFilter currentFilter: Utils.getWritableGeneFileFilters()) {
			jfc.addChoosableFileFilter(currentFilter);
		}
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileFilter(jfc.getChoosableFileFilters()[0]);
		int returnVal = jfc.showSaveDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
				GeneList data = selectedTrack.getData();
				String name = selectedTrack.getName();
				final GeneListWriter glw = GeneListWriterFactory.getGeneListWriter(selectedFile, data, name, selectedFilter);
				notifyActionStart("Saving Track #" + selectedTrack.getTrackNumber(), 1);
				glw.write();
			}
		}
	}
}
