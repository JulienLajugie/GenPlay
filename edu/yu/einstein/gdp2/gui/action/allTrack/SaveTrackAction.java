/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.core.writer.binListWriter.BinListWriter;
import yu.einstein.gdp2.core.writer.binListWriter.BinListWriterFactory;
import yu.einstein.gdp2.core.writer.geneListWriter.GeneListWriter;
import yu.einstein.gdp2.core.writer.geneListWriter.GeneListWriterFactory;
import yu.einstein.gdp2.exception.InvalidFileTypeException;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.fileFilter.ExtendedFileFilter;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;

/**
 * Saves the selected {@link Track}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SaveTrackAction extends TrackListAction {

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
	 * Creates an instance of {@link SaveTrackAction}
	 */
	public SaveTrackAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Saves the selected {@link Track}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (getTrackList().getSelectedTrack() != null) {
			Track selectedTrack = getTrackList().getSelectedTrack(); 
			if (selectedTrack instanceof BinListTrack) {
				saveBinList((BinListTrack)selectedTrack);
			} else if (selectedTrack instanceof GeneListTrack) {
				saveGeneList((GeneListTrack)selectedTrack);
			}
		}
	}


	/**
	 * Saves a {@link BinListTrack}
	 * @param selectedTrack selected {@link BinListTrack}
	 */
	private void saveBinList(BinListTrack selectedTrack) {
		final String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		final JFileChooser jfc = new JFileChooser(defaultDirectory);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save Track");
		for (FileFilter currentFilter: Utils.getWritableBinListFileFilters()) {
			jfc.addChoosableFileFilter(currentFilter);
		}
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileFilter(jfc.getChoosableFileFilters()[0]);
		final int returnVal = jfc.showSaveDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			final ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			final File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
				final BinList data = selectedTrack.getBinList();
				final String name = selectedTrack.getName();
				try {
					final BinListWriter blw = BinListWriterFactory.getBinListWriter(selectedFile, data, name, selectedFilter);
					// thread for the action
					new ActionWorker<Void>(getTrackList(), "Saving Track #" + selectedTrack.getTrackNumber()) {
						@Override
						protected Void doAction() {
							try {
								blw.write();
								return null;
							} catch (IOException e) {
								ExceptionManager.handleException(getRootPane(), e, "Error while saving the track");
								return null;
							} 
						}
						@Override
						protected void doAtTheEnd(Void actionResult) {}
					}.execute();
				} catch (InvalidFileTypeException e) {
					ExceptionManager.handleException(getRootPane(), e, "Error while saving the track");
				}
			}
		}
	}


	/**
	 * Saves a {@link GeneListTrack}
	 * @param selectedTrack selected {@link GeneListTrack}
	 */
	private void saveGeneList(GeneListTrack selectedTrack) {
		final String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		final JFileChooser jfc = new JFileChooser(defaultDirectory);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save Track");
		for (FileFilter currentFilter: Utils.getWritableGeneFileFilters()) {
			jfc.addChoosableFileFilter(currentFilter);
		}
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileFilter(jfc.getChoosableFileFilters()[0]);
		final int returnVal = jfc.showSaveDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			final ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			final File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
				final GeneList data = selectedTrack.getData();
				final String name = selectedTrack.getName();
				try {
					final GeneListWriter glw = GeneListWriterFactory.getGeneListWriter(selectedFile, data, name, selectedFilter);
					// thread for the action
					new ActionWorker<Void>(getTrackList(), "Saving Track #" + selectedTrack.getTrackNumber()) {
						@Override
						protected Void doAction() {
							try {
								glw.write();
								return null;
							} catch (IOException e) {
								ExceptionManager.handleException(getRootPane(), e, "Error while saving the track");
								return null;
							} 
						}
						@Override
						protected void doAtTheEnd(Void actionResult) {}
					}.execute();
				} catch (InvalidFileTypeException e) {
					ExceptionManager.handleException(getRootPane(), e, "Error while saving the track");
				}
			}
		}
	}
}
