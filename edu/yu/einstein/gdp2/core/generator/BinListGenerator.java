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
package yu.einstein.gdp2.core.generator;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * The interface BinListGenerator could be implemented by the class able to create a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface BinListGenerator extends Generator {

	/**
	 * Creates and returns a {@link BinList}
	 * @param binSize size of the {@link BinList}
	 * @param precision precision of the data (eg: 1/8/16/32/64-BIT)
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)
	 * @return a {@link BinList}
	 * @throws IllegalArgumentException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public BinList toBinList(int binSize, DataPrecision precision, ScoreCalculationMethod method) throws IllegalArgumentException, InterruptedException, ExecutionException;
	
	
	/**
	 * @return true if the yu.einstein.gdp2.core.generator needs information regarding the {@link ScoreCalculationMethod} criterion
	 */
	public boolean isCriterionNeeded();
	
	
	/**
	 * @return true if the yu.einstein.gdp2.core.generator needs information regarding the size of the bins
	 */
	public boolean isBinSizeNeeded();
	
	
	/**
	 * @return true if the yu.einstein.gdp2.core.generator needs information regarding the precision of the data
	 */
	public boolean isPrecisionNeeded();
}
