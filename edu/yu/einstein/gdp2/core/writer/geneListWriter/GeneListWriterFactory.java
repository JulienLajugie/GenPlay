/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.geneListWriter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.exception.InvalidFileTypeException;
import yu.einstein.gdp2.gui.fileFilter.BedFilter;
import yu.einstein.gdp2.gui.fileFilter.GdpGeneFilter;


/**
 * Factory that tries to create and to return a subclass of {@link GeneListWriter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneListWriterFactory {


	/**
	 * Tries to create and to return a subclass of {@link GeneListWriter}
	 * @param outputFile output {@link File}
	 * @param data {@link GeneList} to write
	 * @param name a name for the {@link GeneList}
	 * @param ff a subclass of {@link FileFilter}
	 * @return a subclass of {@link GeneListWriter} or null if the type can't be figured out
	 * @throws InvalidFileTypeException
	 */
	public static GeneListWriter getGeneListWriter(File outputFile, GeneList data, String name, FileFilter ff) throws InvalidFileTypeException {
		GeneListWriter writer = null;

		writer = checkFileFilter(outputFile, data, name, ff);
		if (writer != null) {
			return writer;
		}
		// if we can't figure out the type of Extractor
		throw new InvalidFileTypeException();
	}


	/**
	 * Tries to create and to return a subclass of {@link GeneListWriter} depending on the file filter used to save
	 * @param outputFile output {@link File}
	 * @param data {@link GeneList} to write
	 * @param name a name for the {@link GeneList}
	 * @param ff a subclass of {@link FileFilter}
	 * @return a subclass of {@link GeneListWriter} or null if the type can't be figured out
	 */
	public static GeneListWriter checkFileFilter(File outputFile, GeneList data, String name, FileFilter ff) {
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
}
