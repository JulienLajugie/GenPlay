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
package edu.yu.einstein.genplay.gui.trackList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import edu.yu.einstein.genplay.gui.track.layer.LayeredTrack;
import edu.yu.einstein.genplay.gui.track.layer.LayeredTrackConstants;

public class TrackTable extends JTable {

	private static final int 	HANDLE_WIDTH = 50;									// width of the handle
	
	public TrackTable(TableModel trackTableModel) {
		super(trackTableModel);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setTableHeader(null);
		setRowHeight(LayeredTrackConstants.TRACK_HEIGHT);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getColumnModel().getColumn(0).setPreferredWidth(HANDLE_WIDTH);
	    addMouseMotionListener(new MouseAdapter() {
	    	@Override
	    	public void mouseMoved(MouseEvent e) {
	    		getComponentAt(e.getPoint()).setBackground(Color.black);
	    		int column = columnAtPoint(e.getPoint());
	    		if (column == 0) {
	    			int row = rowAtPoint(e.getPoint());
		    		//System.out.println(row);
	    			if (row != -1) {
	    				//setRowSelectionInterval(row, row);
	    				((JLabel) getCellRenderer(row, column).getTableCellRendererComponent(TrackTable.this, getValueAt(row, column), false, false, row, column)).setBackground(Color.black);
	    			}
	    		}
	    		super.mouseMoved(e);
	    	}
		});
	}
	
	

	
}
