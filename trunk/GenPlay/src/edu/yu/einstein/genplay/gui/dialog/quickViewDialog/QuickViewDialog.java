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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.WindowConstants;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.yu.einstein.genplay.core.IO.extractor.TransferableTrackExtractor;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosome.SimpleChromosome;
import edu.yu.einstein.genplay.dataStructure.genome.Assembly;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.ruler.Ruler;
import edu.yu.einstein.genplay.util.Images;

/**
 * Jdialog for GenGlay quick view
 * Shows a quick overview of the track inside a jdialog
 * @author Julien Lajugie
 */
public class QuickViewDialog extends JDialog {

	private static final long serialVersionUID = 3137164002203992280L; // generated serial ID
	private static final int 	DIALOG_WIDTH  	= 800;	// Dialog width
	private static final int 	CHROMOSOME_LENGTH = 250000000;


	/**
	 * Init GenPlay Managers
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void initManagers() {
		// keep only the "basic" chromosomes (we don't want the _random)
		Chromosome chr = new SimpleChromosome("Chr1", CHROMOSOME_LENGTH);
		List<Chromosome> chrList = new ArrayList<Chromosome>();
		chrList.add(chr);
		Assembly assembly = new Assembly("Quick View", "0001 01");
		assembly.setChromosomeList(chrList);
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
		Track track = extractor.extract();
		initManagers();
		new QuickViewDialog(track);
		//System.exit(0);
	}


	/**
	 * Creates an instance of {@link QuickViewDialog}.
	 * Private constructor
	 * @param track track to display in the dialog
	 */
	private QuickViewDialog(final Track track) {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println("fdsaf");
				ProjectManager.getInstance().getProjectWindow().setTrackWidth(track.getGraphicsPanel().getWidth());
			}
		});

		// Dialog layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		//Track displayedTrack = new Track(1);
		//displayedTrack.setContentAs(track);

		Ruler ruler = new Ruler();
		add(ruler.getRulerPanel(), BorderLayout.NORTH);
		ruler.getOptionButton().setEnabled(false);

		// Adds component to the dialog
		add(track.getTrackPanel(), BorderLayout.SOUTH);

		// Default close operation
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// Dialog settings
		setTitle("GenPlay Quick View");
		setIconImage(Images.getApplicationImage());
		pack();
		setSize(new Dimension(DIALOG_WIDTH, getHeight()));
		//setModal(true);
		setResizable(true);
		//System.out.println(">>>" + track.getGraphicsPanel().getWidth());
		setVisible(true);

		//System.out.println(">>>" + track.getGraphicsPanel().getWidth());
		//validate();
		//displayedTrack.getGraphicsPanel().componentResized(null);

		//ProjectManager.getInstance().getProjectWindow().setTrackWidth(displayedTrack.getGraphicsPanel().getWidth());
	}
}
