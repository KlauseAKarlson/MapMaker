package mapMaker;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class MapViewer extends JPanel implements ActionListener{

	private Map ActiveMap;
	private int selectedColumn=0, selectedRow=0;
	private List<ActionListener> Listeners;
	/**
	 * This class provides a JPanel to interact with the map
	 * Each tile is represented by a button containing an icon with shows its part of the map
	 * When given an action listener, it will call ActionPerformed whenever its buttons are pressed, with Col,Row as the action command
	 */
	
	MapViewer(Map m)
	{
		super(new GridLayout(m.getWidth(),m.getHeight()));//initialize with appropriately sized gride layout
		//initiate fields
		ActiveMap=m;
		Listeners=new LinkedList<ActionListener> ();
		//set width and height in pixels by multiplying number of tiles by tile dimension in pixels
		int width = ActiveMap.getWidth() * ActiveMap.getTileSet().getWidth();
		int height = ActiveMap.getHeight() * ActiveMap.getTileSet().getHeight();
		this.setSize(width,height);
		//add buttons, left to right, top to bottom
		JButton tileButton;
		LineBorder thinBorder=new LineBorder(Color.black,1);
		Insets tbi=new Insets(0,0,0,0);
		for (int row=0; row<ActiveMap.getHeight(); row++)
		{
			for (int col=0;col<ActiveMap.getWidth(); col++)
			{
				tileButton=new JButton(new MapIcon(col,row));//create a new JButton that will display the correct tile
				tileButton.setMargin(tbi);
				tileButton.setActionCommand(col+","+row);
				tileButton.setBorder(thinBorder);
				tileButton.addActionListener(this);
				this.add(tileButton);
			}//end column loop
		}//end row loop
		this.setMaximumSize(this.getSize());
		this.setPreferredSize(getSize());
	}//end constructor
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
	@Override
	public void actionPerformed(ActionEvent e) {
		//extract collumn and row
		String command = e.getActionCommand();
		int comma=command.indexOf(",");
		selectedColumn=Integer.parseInt( command.substring(0, comma) ) ;
		selectedRow=Integer.parseInt(command.substring(comma+1));
		if (!Listeners.isEmpty())
		{
			ActionEvent e2=new ActionEvent(this,ActionEvent.ACTION_FIRST ,command);
			for (ActionListener l:Listeners)
			{
				l.actionPerformed(e2);
			}
		}
	}//end action performed

	private class MapIcon implements Icon
	{
		/**
		 * light weight icon to pass through paint commands to ActiveMap
		 */
		private int Col,Row;
		
		private MapIcon(int column, int row)
		{
			Col=column;
			Row=row;
		}
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			ActiveMap.paintTileAt(Col, Row, x, y, c, g);
		}

		@Override
		public int getIconWidth() {
			return ActiveMap.getTileSet().getWidth();
		}

		@Override
		public int getIconHeight() {
			return ActiveMap.getTileSet().getHeight();
		}
		
	}
	
}//end map viewer class
