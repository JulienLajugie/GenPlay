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
package yu.einstein.gdp2.core.writer;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.writer.SCWListWriter.SCWListAsBedGraphWriter;
import yu.einstein.gdp2.core.writer.SCWListWriter.SCWListAsBedWriter;
import yu.einstein.gdp2.core.writer.SCWListWriter.SCWListAsGFFWriter;
import yu.einstein.gdp2.core.writer.SCWListWriter.SCWListWriter;
import yu.einstein.gdp2.core.writer.binListWriter.BinListAsBedGraphWith0Writer;
import yu.einstein.gdp2.core.writer.binListWriter.BinListAsBedGraphWriter;
import yu.einstein.gdp2.core.writer.binListWriter.BinListAsBedWriter;
import yu.einstein.gdp2.core.writer.binListWriter.BinListAsGFFWriter;
import yu.einstein.gdp2.core.writer.binListWriter.BinListAsWiggleWriter;
import yu.einstein.gdp2.core.writer.binListWriter.BinListWriter;
import yu.einstein.gdp2.core.writer.binListWriter.SerializedBinListWriter;
import yu.einstein.gdp2.core.writer.geneListWriter.GeneListAsBedWriter;
import yu.einstein.gdp2.core.writer.geneListWriter.GeneListAsGdpGeneWriter;
import yu.einstein.gdp2.core.writer.geneListWriter.GeneListWriter;
import yu.einstein.gdp2.exception.InvalidFileTypeException;
import yu.einstein.gdp2.gui.fileFilter.BedFilter;
import yu.einstein.gdp2.gui.fileFilter.BedGraphFilter;
import yu.einstein.gdp2.gui.fileFilter.BedGraphWith0Filter;
import yu.einstein.gdp2.gui.fileFilter.GFFFilter;
import yu.einstein.gdp2.gui.fileFilter.GdpGeneFilter;
import yu.einstein.gdp2.gui.fileFilter.SerializedBinListFilter;
import yu.einstein.gdp2.gui.fileFilter.WiggleFilter;


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
