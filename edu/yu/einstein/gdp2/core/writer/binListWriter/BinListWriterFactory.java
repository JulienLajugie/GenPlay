/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.binListWriter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.exception.InvalidFileTypeException;
import yu.einstein.gdp2.gui.fileFilter.BedFilter;
import yu.einstein.gdp2.gui.fileFilter.BedGraphFilter;
import yu.einstein.gdp2.gui.fileFilter.GFFFilter;
import yu.einstein.gdp2.gui.fileFilter.WiggleFilter;
import yu.einstein.gdp2.util.ChromosomeManager;


/**
 * Factory that tries to create and to return a subclass of {@link BinListWriter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListWriterFactory {


	/**
	 * Tries to create and to return a subclass of {@link BinListWriter}
	 * @param chromosomeManager a {@link ChromosomeManager}
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 * @param ff a subclass of {@link FileFilter}
	 * @return a subclass of {@link BinListWriter} or null if the type can't be figured out
	 * @throws InvalidFileTypeException
	 */
	public static BinListWriter getBinListWriter(ChromosomeManager chromosomeManager, File outputFile, 
			BinList data, String name, FileFilter ff) throws InvalidFileTypeException {
		BinListWriter writer = null;

		writer = checkFileFilter(chromosomeManager, outputFile, data, name, ff);
		if (writer != null) {
			return writer;
		}
		// if we can't figure out the type of Extractor
		throw new InvalidFileTypeException();
	}


	/**
	 * Tries to create and to return a subclass of {@link BinListWriter} depending on the file filter used to save
	 * @param chromosomeManager a {@link ChromosomeManager}
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 * @param ff a subclass of {@link FileFilter}
	 * @return a subclass of {@link BinListWriter} or null if the type can't be figured out
	 */
	public static BinListWriter checkFileFilter(ChromosomeManager chromosomeManager, File outputFile, BinList data, String name, FileFilter ff) {
		if (ff == null) {
			return null;
		} else if (ff instanceof BedFilter) {
			return new BinListAsBedWriter(chromosomeManager, outputFile, data, name);
		} else if (ff instanceof BedGraphFilter) {
			return new BinListAsBedGraphWriter(chromosomeManager, outputFile, data, name);
		} else if (ff instanceof GFFFilter) {
			return new BinListAsGFFWriter(chromosomeManager, outputFile, data, name);
		} else if (ff instanceof WiggleFilter) {
			return new BinListAsWiggleWriter(chromosomeManager, outputFile, data, name);
		} else {
			return null;
		}
	}
}
