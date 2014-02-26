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
package edu.yu.einstein.genplay.core.IO.writer;

import java.io.File;

import edu.yu.einstein.genplay.core.IO.writer.SCWListWriter.SCWListAsBedGraphWith0Writer;
import edu.yu.einstein.genplay.core.IO.writer.SCWListWriter.SCWListAsBedGraphWriter;
import edu.yu.einstein.genplay.core.IO.writer.SCWListWriter.SCWListAsBedWriter;
import edu.yu.einstein.genplay.core.IO.writer.SCWListWriter.SCWListAsGFFWriter;
import edu.yu.einstein.genplay.core.IO.writer.SCWListWriter.SCWListWriter;
import edu.yu.einstein.genplay.core.IO.writer.binListWriter.BinListAsWiggleWriter;
import edu.yu.einstein.genplay.core.IO.writer.binListWriter.BinListWriter;
import edu.yu.einstein.genplay.core.IO.writer.geneListWriter.GeneListAsBedWriter;
import edu.yu.einstein.genplay.core.IO.writer.geneListWriter.GeneListWriter;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.fileFilter.BedFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphWith0Filter;
import edu.yu.einstein.genplay.gui.fileFilter.GFFFilter;
import edu.yu.einstein.genplay.gui.fileFilter.WiggleFilter;


/**
 * Factory that tries to create and to return a class implementing the {@link Writer} interface
 * @author Julien Lajugie
 */
public final class WriterFactory {

	/**
	 * Tries to create and to return a subclass of {@link BinListWriter} depending on the file filter used to save
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 * @return a subclass of {@link BinListWriter} or null if the type can't be figured out
	 */
	private static Writer getBinListWriter(File outputFile, BinList data, String name) {
		if (new BedFilter().accept(outputFile)) {
			return new SCWListAsBedWriter(outputFile, data, name);
		} else if (new BedGraphFilter().accept(outputFile)) {
			return new SCWListAsBedGraphWriter(outputFile, data, name);
		} else if (new GFFFilter().accept(outputFile)) {
			return new SCWListAsGFFWriter(outputFile, data, name);
		} else if (new WiggleFilter().accept(outputFile)) {
			return new BinListAsWiggleWriter(outputFile, data, name);
		} else {
			return null;
		}
	}


	/**
	 * Tries to create and to return a subclass of {@link GeneListWriter} depending on the file filter used to save
	 * @param outputFile output {@link File}
	 * @param data {@link GeneList} to write
	 * @param name a name for the {@link GeneList}
	 * @return a subclass of {@link GeneListWriter} or null if the type can't be figured out
	 */
	private static GeneListWriter getGeneListWriter(File outputFile, GeneList data, String name) {
		if (new BedFilter().accept(outputFile)) {
			return new GeneListAsBedWriter(outputFile, data, name);
		} else {
			return null;
		}
	}


	/**
	 * Tries to create and to return a subclass of {@link SCWListWriter} depending on the file filter used to save
	 * @param outputFile output {@link File}
	 * @param data {@link SCWList} to write
	 * @param name a name for the data
	 * @return a subclass of {@link SCWListWriter} or null if the type can't be figured out
	 */
	private static SCWListWriter getSCWListWriter(File outputFile, SCWList data, String name) {
		if (new BedFilter().accept(outputFile)) {
			return new SCWListAsBedWriter(outputFile, data, name);
		} else if (new BedGraphFilter().accept(outputFile)) {
			return new SCWListAsBedGraphWriter(outputFile, data, name);
		} else if (new BedGraphWith0Filter().accept(outputFile)) {
			return new SCWListAsBedGraphWith0Writer(outputFile, data, name);
		} else if (new GFFFilter().accept(outputFile)) {
			return new SCWListAsGFFWriter(outputFile, data, name);
		} else {
			return null;
		}
	}


	/**
	 * Tries to create and to return a class implementing the {@link Writer} interface
	 * @param outputFile output {@link File}
	 * @param data data to write
	 * @param name a name for the data
	 * @return a class implementing the Writer interface.
	 * @throws InvalidFileTypeException
	 */
	public static Writer getWriter(File outputFile, GenomicListView<?> data, String name) throws InvalidFileTypeException {
		Writer writer = null;
		if (data instanceof GeneList) {
			GeneList geneList = (GeneList) data;
			writer = getGeneListWriter(outputFile, geneList, name);
		} else if (data instanceof BinList) {
			BinList scwList = (BinList) data;
			writer = getBinListWriter(outputFile, scwList, name);
		} else if (data instanceof SCWList) {
			SCWList scwList = (SCWList) data;
			writer = getSCWListWriter(outputFile, scwList, name);
		}
		if (writer != null) {
			return writer;
		}
		// if we can't figure out the type of Extractor
		throw new InvalidFileTypeException();
	}
}
