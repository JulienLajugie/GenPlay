/**
 * @author Chirag Gorasia
 * @version 0.1
 */

package yu.einstein.gdp2.core.list.geneList.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.operation.Operation;

/**
 * Renames Genes
 * @author Chirag Gorasia
 * @version 0.1
 */

public class GLOGeneRenamer implements Operation<GeneList> {
	
	private GeneList geneList;					// input geneList
	private File fileName;						// fileName in which the gene needs to be renamed

	public GLOGeneRenamer(GeneList geneList, File fileName) {
		this.geneList = geneList;
		this.fileName = fileName;
	}

	@Override
	public GeneList compute() throws Exception {
		BufferedReader bufReader = new BufferedReader(new FileReader(fileName));
		GeneList renamedList = geneList.deepClone();
		String lineRead;
		String geneNames[];
		while ((lineRead = bufReader.readLine()) != null) {
			geneNames = lineRead.split("\t");
			for (int i = 0; i < renamedList.size(); i++) {
				for (int j = 0; j < renamedList.size(i); j++) {
					if (geneNames.length > 1) {
						if (geneNames[0].trim().equalsIgnoreCase(renamedList.get(i,j).getName())) {
							renamedList.get(i,j).setName(geneNames[1].trim());			
						}
					}
				}
			}
		}
		return renamedList;
	}

	@Override
	public String getDescription() {
		return "Rename Genes";
	}

	@Override
	public String getProcessingDescription() {
		return "Renaming Genes";
	}

	@Override
	public int getStepCount() {
		return 1;
	}	
}
