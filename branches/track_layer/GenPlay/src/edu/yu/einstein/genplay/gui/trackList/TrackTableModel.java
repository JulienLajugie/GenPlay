package edu.yu.einstein.genplay.gui.trackList;

import javax.swing.table.AbstractTableModel;

import edu.yu.einstein.genplay.gui.track.layer.LayeredTrack;

public class TrackTableModel extends AbstractTableModel {

	private int rowCount;
	private LayeredTrack[] tracks;
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return row;
		} else {
			return tracks[row];
		}
	}
}
