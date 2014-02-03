/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.quickViewDialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.yu.einstein.genplay.core.IO.extractor.TransferableTrackExtractor;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genome.Assembly;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.ruler.Ruler;
import edu.yu.einstein.genplay.gui.track.trackTransfer.TrackForTransfer;
import edu.yu.einstein.genplay.util.Images;

/**
 * Jdialog for GenGlay quick view
 * Shows a quick overview of the track inside a jdialog
 * @author Julien Lajugie
 */
public class QuickViewDialog extends JDialog {

	private static final long serialVersionUID = 3137164002203992280L; // generated serial ID
	private static final int DIALOG_WIDTH  	= 800;	// Dialog width


	/**
	 * Init GenPlay Managers
	 * @param assembly assembly to use to initialize the managers
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void initManagers(Assembly assembly) {
		Chromosome chr = assembly.getChromosomeList().get(0);
		ProjectManager.getInstance().setAssembly(assembly);
		ProjectManager.getInstance().updateChromosomeList();
		ProjectManager.getInstance().getProjectWindow().setGenomeWindow(new SimpleGenomeWindow(chr, 0, chr.getLength()));
	}


	/**
	 * Shows the dialog with the track extracted from the specified file
	 * @param genPlayTrackFile
	 * @throws Exception
	 */
	public static void showDialog(File genPlayTrackFile) throws Exception {
		TransferableTrackExtractor extractor = new TransferableTrackExtractor(genPlayTrackFile);
		TrackForTransfer trackForTransfer = extractor.extract();
		initManagers(trackForTransfer.getAssembly());
		new QuickViewDialog(trackForTransfer.getTrackForTransfer());
		System.exit(0);
	}


	/**
	 * Creates an instance of {@link QuickViewDialog}.
	 * Private constructor
	 * @param track track to display in the dialog
	 */
	private QuickViewDialog(Track track) {
		JPanel rulerPanel = new Ruler().getRulerGraphics();
		rulerPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, Ruler.RULER_HEIGHT));

		track.getTrackPanel().setPreferredSize(new Dimension(DIALOG_WIDTH, 100));
		track.setHandleVisible(false);

		// Dialog layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Adds component to the dialog
		add(rulerPanel, BorderLayout.NORTH);
		add(track.getTrackPanel(), BorderLayout.SOUTH);

		// Default close operation
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// Dialog settings
		setTitle("GenPlay Quick View");
		setIconImage(Images.getApplicationImage());
		setModal(true);
		pack();
		setVisible(true);
	}
}
