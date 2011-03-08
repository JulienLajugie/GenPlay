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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.writer;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.writer.SCWListWriter.SCWListAsBedGraphWriter;
import edu.yu.einstein.genplay.core.writer.SCWListWriter.SCWListAsBedWriter;
import edu.yu.einstein.genplay.core.writer.SCWListWriter.SCWListAsGFFWriter;
import edu.yu.einstein.genplay.core.writer.SCWListWriter.SCWListWriter;
import edu.yu.einstein.genplay.core.writer.binListWriter.BinListAsBedGraphWith0Writer;
import edu.yu.einstein.genplay.core.writer.binListWriter.BinListAsBedGraphWriter;
import edu.yu.einstein.genplay.core.writer.binListWriter.BinListAsBedWriter;
import edu.yu.einstein.genplay.core.writer.binListWriter.BinListAsGFFWriter;
import edu.yu.einstein.genplay.core.writer.binListWriter.BinListAsWiggleWriter;
import edu.yu.einstein.genplay.core.writer.binListWriter.BinListWriter;
import edu.yu.einstein.genplay.core.writer.binListWriter.SerializedBinListWriter;
import edu.yu.einstein.genplay.core.writer.geneListWriter.GeneListAsBedWriter;
import edu.yu.einstein.genplay.core.writer.geneListWriter.GeneListAsGdpGeneWriter;
import edu.yu.einstein.genplay.core.writer.geneListWriter.GeneListWriter;
import edu.yu.einstein.genplay.exception.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.fileFilter.BedFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphWith0Filter;
import edu.yu.einstein.genplay.gui.fileFilter.GFFFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GdpGeneFilter;
import edu.yu.einstein.genplay.gui.fileFilter.SerializedBinListFilter;
import edu.yu.einstein.genplay.gui.fileFilter.WiggleFilter;



/**
 * Factory that tries to create and to return a class implementing the {@link Writer} interface
 * @author Julien Lajugie
 * @version 0.1
 */
public final class WriterFactory {


	/**
	 * Tries to create and to return a subclass of {@link BinListWriter} depending on the file filter used to save
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 * @param ff a subclass of {@link FileFilter}
	 * @return a subclass of {@link BinListWriter} or null if the type can't be figured out
	 */
	private static BinListWriter getBinListWriter(File outputFile, BinList data, String name, FileFilter ff) {
		if (ff == null) {
			return null;
		} else if (ff instanceof BedFilter) {
			return new BinListAsBedWriter(outputFile, data, name);
		} else if (ff instanceof BedGraphFilter) {
			return new BinListAsBedGraphWriter(outputFile, data, name);
		} else if (ff instanceof BedGraphWith0Filter) {
			return new BinListAsBedGraphWith0Writer(outputFile, data, name);
		} else if (ff instanceof GFFFilter) {
			return new BinListAsGFFWriter(outputFile, data, name);
		} else if (ff instanceof WiggleFilter) {
			return new BinListAsWiggleWriter(outputFile, data, name);
		}else if (ff instanceof SerializedBinListFilter) {
			return new SerializedBinListWriter(outputFile, data, name);
		} else {
			return null;
		}
	}


	/**
	 * Tries to create and to return a subclass of {@link GeneListWriter} depending on the file filter used to save
	 * @param outputFile output {@link File}
	 * @param data {@link GeneList} to write
	 * @param name a name for the {@link GeneList}
	 * @param ff a subclass of {@link FileFilter}
	 * @return a subclass of {@link GeneListWriter} or null if the type can't be figured out
	 */
	private static GeneListWriter getGeneListWriter(File outputFile, GeneList data, String name, FileFilter ff) {
		if (ff == null) {
			return null;
		} else if (ff instanceof BedFilter) {
			return new GeneListAsBedWriter(outputFile, data, name);
		} else if (ff instanceof GdpGeneFilter) {
			return new GeneListAsGdpGeneWriter(outputFile, data, name);
		} else {
			return null;
		}
	}


	/**
	 * Tries to create and to return a subclass of {@link SCWListWriter} depending on the file filter used to save
	 * @param outputFile output {@link File}
	 * @param data {@link ScoredChromosomeWindowList} to write
	 * @param name a name for the data
	 * @param ff a subclass of {@link FileFilter}
	 * @return a subclass of {@link SCWListWriter} or null if the type can't be figured out
	 */
	private static SCWListWriter getSCWListWriter(File outputFile, ScoredChromosomeWindowList data, String name, FileFilter ff) {
		if (ff == null) {
			return null;
		} else if (ff instanceof BedFilter) {
			return new SCWListAsBedWriter(outputFile, data, name);
		} else if (ff instanceof BedGraphFilter) {
			return new SCWListAsBedGraphWriter(outputFile, data, name);
		} else if (ff instanceof GFFFilter) {
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
	 * @param ff a subclass of {@link FileFilter}
	 * @return a class implementing the Writer interface.
	 * @throws InvalidFileTypeException
	 */
	public static Writer getWriter(File outputFile, ChromosomeListOfLists<?> data, String name, FileFilter ff) throws InvalidFileTypeException {
		Writer writer = null;
		if (data instanceof BinList) {
			BinList binList = (BinList) data;
			writer = getBinListWriter(outputFile, binList, name, ff);
		} else if (data instanceof GeneList) {
			GeneList geneList = (GeneList) data;
			writer = getGeneListWriter(outputFile, geneList, name, ff);
		} else if (data instanceof ScoredChromosomeWindowList) {
			ScoredChromosomeWindowList scwList = (ScoredChromosomeWindowList) data;
			writer = getSCWListWriter(outputFile, scwList, name, ff);
		}
		if (writer != null) {
			return writer;
		}
		// if we can't figure out the type of Extractor
		throw new InvalidFileTypeException();
	}
}
