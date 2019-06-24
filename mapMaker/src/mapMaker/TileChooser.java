package mapMaker;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.*;
public class TileChooser extends JScrollPane implements ActionListener{

	/**
	 * this is a UI for choosing tiles from a TileSet
	 * it can be assigned action listeners which will be called each time a new tile is chosen
	 */
	private TileSet KnownTiles;
	private JPanel optionBox;
	private String chostenTile="Empty";
	private List<ActionListener> Listeners;
	
	public TileChooser(TileSet t)
	{
		super();
		Listeners=new LinkedList<ActionListener> ();
		KnownTiles=t;
		updateTiles();
		this.setPreferredSize(new Dimension(optionBox.getWidth(),500));
	}
	
		
	public void updateTileSet(TileSet t)
	{
		KnownTiles=t;
		updateTiles();
	}
	public void updateTiles()
	{
		Set<String> tileNames=KnownTiles.getTileList();
		optionBox =  new JPanel(new GridLayout(0,2));
		JButton currentButton;
		for (String tileName:tileNames)
		{
			//create a button with tile name and tile icon
			currentButton=new JButton(tileName,
					KnownTiles.getTile(tileName).getIcon());
			currentButton.setVerticalTextPosition(AbstractButton.BOTTOM);
			currentButton.setHorizontalTextPosition(AbstractButton.CENTER);
			currentButton.setActionCommand(tileName);
			currentButton.addActionListener(this);
			optionBox.add(currentButton);
		}
		this.setViewportView(optionBox);
	}
	
	public String getChosenTileName()
	{
		return this.chostenTile;
	}
	public Tile getChosenTile()
	{
		return KnownTiles.getTile(chostenTile);
	}

	public void addActionListener(ActionListener a)
	{
		Listeners.add(a);
	}
	public void removeActionListener(ActionListener a)
	{
		Listeners.remove(a);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		this.chostenTile=e.getActionCommand();
		if (!Listeners.isEmpty())
		{
			ActionEvent e2=new ActionEvent(this,ActionEvent.ACTION_FIRST ,chostenTile);
			for (ActionListener l:Listeners)
			{
				l.actionPerformed(e2);
			}
		}
	}//end action performed
}
