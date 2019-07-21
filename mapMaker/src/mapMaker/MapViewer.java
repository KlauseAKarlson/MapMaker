package mapMaker;

import java.awt.event.ActionListener;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class MapViewer extends JPanel implements Printable, Pageable{
	protected List<ActionListener> Listeners;
	protected int selectedColumn=0, selectedRow=0;
	/**
	 *This class represents a way for the users to select tiles on the map 		
	 * inheriting classes should 
	 * 	1: display the map when printed
	 * 	2: tell the action listeners when a tile has been selected
	 */
	
	public MapViewer()
	{
		super();
		Listeners=new LinkedList<ActionListener> ();
	}
	
	public void addActionListener(ActionListener a)
	{
		Listeners.add(a);
	}
	public void removeActionListener(ActionListener a)
	{
		Listeners.remove(a);
	}
	
	public int getSelectedColumn()
	{
		return selectedColumn;
	}
	public int getSelectedRow()
	{
		return selectedRow;
	}

}//end class