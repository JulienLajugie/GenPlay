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
package edu.yu.einstein.genplay.gui.old.track.drawer.multiGenome;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeDensityDrawer implements Serializable {


	/** Generated serial version ID */
	private static final long serialVersionUID = 7812963695931085036L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;				// saved format version
	private static final int BIN_SIZE = 1000;

	@SuppressWarnings("unused")									// IN DEVELOPPMENT
	private MultiGenomeDrawer 	drawer;
	@SuppressWarnings("unused")
	private List<Variant> variantList;
	@SuppressWarnings("unused")
	private List<Integer> densityList;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}


	/**
	 * Constructor of {@link MultiGenomeVariantDrawer}
	 * @param drawer
	 */
	protected MultiGenomeDensityDrawer (MultiGenomeDrawer drawer) {
		setDrawer(drawer);
		densityList = new IntArrayAsIntegerList();
	}


	/**
	 * @param drawer the drawer to set
	 */
	protected void setDrawer(MultiGenomeDrawer drawer) {
		this.drawer = drawer;
	}


	/**
	 * Updates the variant list.
	 * @param variantlist the new variant list
	 */
	protected void updateDensityList (List<Variant> variantlist) {
		this.variantList = variantlist;

		int chromosomeSize = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome().getLength();
		int binNumber = Math.round(chromosomeSize / BIN_SIZE);
		if ((chromosomeSize % BIN_SIZE) != 0) {
			binNumber++;
		}

		densityList = new IntArrayAsIntegerList(binNumber);




	}


	/*private int getIndex (int position) {
		int index = Math.round(chromosomeSize / BIN_SIZE);
	}*/




	protected void drawDensity (Graphics g, GenomeWindow genomeWindow, double xFactor) {

	}
}
