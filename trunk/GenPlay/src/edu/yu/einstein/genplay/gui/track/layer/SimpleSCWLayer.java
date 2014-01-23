/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.track.layer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * Layer displaying a {@link SCWList}
 * @author Julien Lajugie
 */
public class SimpleSCWLayer extends AbstractSCWLayer<SCWList> implements Layer<SCWList>, VersionedLayer<SCWList>, GraphLayer, ColoredLayer {

	/** Generated ID */
	private static final long serialVersionUID = 3779631846077486596L;

	/**  Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;


	/**
	 * Creates an instance of {@link SimpleSCWLayer} with the same properties as the specified {@link SimpleSCWLayer}.
	 * The copy of the data is shallow.
	 * @param simpleSCWLayer
	 */
	private SimpleSCWLayer(SimpleSCWLayer simpleSCWLayer) {
		super(simpleSCWLayer);
	}


	/**
	 * Creates an instance of a {@link SimpleSCWLayer}
	 * @param track track containing the layer
	 * @param data data of the layer
	 * @param name name of the layer
	 */
	public SimpleSCWLayer(Track track, SCWList data, String name) {
		super(track, data, name);
	}


	@Override
	public SimpleSCWLayer clone() {
		return new SimpleSCWLayer(this);
	}


	@Override
	public LayerType getType() {
		return LayerType.SIMPLE_SCW_LAYER;
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CLASS_VERSION_NUMBER);
	}
}
