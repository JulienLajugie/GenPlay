/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import yu.einstein.gdp2.exception.InvalidFileTypeException;
import yu.einstein.gdp2.util.Utils;


/**
 * Factory that tries to create and to return a subclass of {@link Extractor} depending on a file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ExtractorFactory {


	/**
	 * @param fileToExtract file to extract
	 * @param logFile file for the log of the extraction
	 * @return an instance of a subclass of {@link Extractor} if the type has been found.
	 * Otherwise throw a {@link InvalidFileTypeException} 
	 * @throws IOException 
	 * @throws InvalidFileTypeException 
	 */
	public static Extractor getExtractor(File fileToExtract, File logFile) throws IOException, InvalidFileTypeException {
		Extractor extractor = null;
		extractor = checkHeader(fileToExtract, logFile);
		if (extractor != null) {
			return extractor;
		}
		extractor = checkFileExtension(fileToExtract, logFile);
		if (extractor != null) {
			return extractor;
		}
		// if we can't figure out the type of Extractor
		throw new InvalidFileTypeException();
	}


	/**
	 * @param fileToExtract
	 * @param logFile
	 * @return an {@link Extractor} if the extension of the file is known. null otherwise
	 * @throws IOException
	 */
	public static Extractor checkFileExtension(File fileToExtract, File logFile) {
		String fileExtension = Utils.getExtension(fileToExtract);
		if (fileExtension == null) {
			return null;
		}
		if (fileExtension.equalsIgnoreCase("gff")) {
			return new GFFExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("gtf")) {
			// TODO return gtf extractor
			return null;
		} else if (fileExtension.equalsIgnoreCase("gff3")) {
			// TODO return gff3 extractor
			return null;
		} else if (fileExtension.equalsIgnoreCase("gr")) {
			return new BedGraphExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("bed")) {
			return new BedExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("wig")) {
			return new WiggleExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("bgr")) {
			return new BedGraphExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("pair")) {
			return new PairExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("gdp")) {
			return new GdpGeneExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("bin")) {
			return new SerializedBinListExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("elx")) {
			return new ElandExtendedExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("psl")) {
			return new PSLExtractor(fileToExtract, logFile);
		} else if (fileExtension.equalsIgnoreCase("sam")) {
			return new SAMExtractor(fileToExtract, logFile);			
		} else {
			return null;
		}		
	}


	/** 
	 * @param fileToExtract
	 * @param logFile
	 * @return an {@link Extractor} if there is some information about 
	 * the type of {@link Extractor} in the header. Null otherwise  
	 * @throws IOException
	 */
	public static Extractor checkHeader(File fileToExtract, File logFile) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileToExtract));
			boolean isHeader = true;
			String line = null;

			while (((line = reader.readLine()) != null) && isHeader) {
				isHeader = false;
				line = line.trim();
				if (line.length() == 0) {
					isHeader = true;
				}
				// comment line
				if (line.charAt(0) == '#') {
					isHeader = true;
				} 
				// browser line
				if (line.substring(0, 7).equalsIgnoreCase("browser")) {
					isHeader = true;
				}
				// track line
				if (line.substring(0, 5).equalsIgnoreCase("track")) {
					isHeader = true;
				}
				if (isHeader) {
					if (line.substring(0, 5).equalsIgnoreCase("##GFF")) {
						return new GFFExtractor(fileToExtract, logFile);
					} else if (line.substring(0, 5).equalsIgnoreCase("track")) {
						String lineTmp = line.toLowerCase();
						if (lineTmp.contains("type")) {
							String type = null;
							int indexStart = lineTmp.indexOf("type") + 4;
							line = line.substring(indexStart);
							line = line.trim();
							if (line.charAt(0) == '=') {
								// remove the '=' from the line
								line = line.substring(1);
								line = line.trim();
								if (line.charAt(0) == '\"') {
									// remove the first "
									line = line.substring(1);
									type = line.split("\"")[0];
								} else {
									line = line.trim();
									type = line.split(" ")[0].trim();
								}
								reader.close();
								if (type.equalsIgnoreCase("bedgraph")) {
									return new BedGraphExtractor(fileToExtract, logFile);
								} else if (type.equalsIgnoreCase("bed")) {
									return new BedExtractor(fileToExtract, logFile);
								} else if (type.equalsIgnoreCase("gdpGene")) {
									return new GdpGeneExtractor(fileToExtract, logFile);
								} else if (type.equalsIgnoreCase("wiggle")) {
									return new WiggleExtractor(fileToExtract, logFile);
								} else if (type.equalsIgnoreCase("eland_extended")) {	
									return new ElandExtendedExtractor(fileToExtract, logFile);
								} else if (type.equalsIgnoreCase("psl")) {	
									return new PSLExtractor(fileToExtract, logFile);
								} else if (type.equalsIgnoreCase("sam")) {	
									return new SAMExtractor(fileToExtract, logFile);
								} else {
									return null;
								}
							}
						}
					}
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return null;
	}
}
