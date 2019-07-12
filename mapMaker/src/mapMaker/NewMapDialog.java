package mapMaker;

import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

public class NewMapDialog extends JFrame implements ActionListener {

	private MapMaker Parent;
	private SpinnerNumberModel MapWidth, MapHeight, TileWidth, TileHeight;
	private JRadioButton BSquareMap, BHexMap;
	private String mapStyle=Map.squareMap;
	private List<ActionListener> Listeners;
	
	private JButton BCreate;
	public NewMapDialog(MapMaker m)
	{
		super("New Map");
		Parent=m;
		//initialize spinner number models
		MapWidth=new SpinnerNumberModel(10, 5, 500, 1);
		MapHeight=new SpinnerNumberModel(10, 5, 500, 1);
		TileWidth=new SpinnerNumberModel(72, 15, 400, 10);
		TileHeight=new SpinnerNumberModel(72, 15, 400, 10);
		//initialize ui
		getContentPane().setLayout(
			    new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		Box mapLabels=Box.createHorizontalBox();
		mapLabels.add(new Label("Map Width"));
		mapLabels.add(new Label("Map Height"));
		this.add(mapLabels);
		
		Box mapSpinners=Box.createHorizontalBox();
		mapSpinners.add(new JSpinner(MapWidth));
		mapSpinners.add(new JSpinner(MapHeight));
		this.add(mapSpinners);
		
		Box tileLabels=Box.createHorizontalBox();
		tileLabels.add(new Label("Tile Width"));
		tileLabels.add(new Label("Tile Height"));
		this.add(tileLabels);
		
		Box tileSpinners=new Box(BoxLayout.X_AXIS);
		tileSpinners.add(new JSpinner(TileWidth));
		tileSpinners.add(new JSpinner(TileHeight));
		this.add(tileSpinners);
		
		Box styles=new Box(BoxLayout.X_AXIS);
		ButtonGroup styleGroup=new ButtonGroup();
		BSquareMap=new JRadioButton("Square Map");
		BSquareMap.setSelected(true);
		BSquareMap.addActionListener(this);
		styles.add(BSquareMap);
		styleGroup.add(BSquareMap);
		BHexMap=new JRadioButton("Hex Map");
		BHexMap.addActionListener(this);
		styles.add(BHexMap);
		styleGroup.add(BHexMap);
		this.add(styles);
		
		BCreate=new JButton("Create map");
		BCreate.addActionListener(this);
		this.add(BCreate);
		
		Listeners=new LinkedList<ActionListener>();
		this.pack();
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
	}
	
	public void addActionListener(ActionListener a)
	{
		Listeners.add(a);
	}
	public void removeActionListener(ActionListener a)
	{
		Listeners.remove(a);
	}
	
	public int getMapWidth()
	{
		return (int) MapWidth.getNumber();
	}
	public int getMapHeight()
	{
		return (int) MapHeight.getNumber();
	}
	public int getTileWidth()
	{
		return (int) TileWidth.getNumber();
	}
	public int getTileHeight()
	{
		return (int) TileHeight.getNumber();
	}
	public String getMapStyle()
	{
		return mapStyle;
	}
	
	public void clear()
	{
		/**
		 * resets all spinner number models to default
		 */
		MapWidth.setValue(10);
		MapHeight.setValue(10);
		TileWidth.setValue(100);
		TileHeight.setValue(100);
	}
	public void showUI()
	{
		/**
		 * resets spinners and makes window visible
		 */
		clear();
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		if (source==this.BSquareMap)
		{
			this.mapStyle=Map.squareMap;
		}else if (source==this.BHexMap)
		{
			this.mapStyle=Map.hexMap;
		}else if(source==this.BCreate)
		{
			this.setVisible(false);
			if (!Listeners.isEmpty())
			{
				ActionEvent e2=new ActionEvent(this,ActionEvent.ACTION_FIRST ,"new map");
				for (ActionListener l:Listeners)
				{
					l.actionPerformed(e2);
				}//end listener loop
			}//end if there are listeners
		}//else do nothing

	}//end action performed

}//end class
