package mapMaker;

import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

@SuppressWarnings("serial")
public class NewMapDialog extends JFrame implements ActionListener {

	//default values are centralized and static to allow values to be reset each time the dialogue is opened
	public static int DefaultMapWidth=10,DefaultMapHeight=10;
	public static double DefaultTileWidth=1,DefaultTileHeight=1;
	private SpinnerNumberModel MapWidth, MapHeight, TileWidth, TileHeight;
	private JRadioButton BSquareMap, BHexMap;
	private String mapStyle=Map.squareMap;
	private List<ActionListener> Listeners;
	
	private JButton BCreate;
	public NewMapDialog()
	{
		super("New Map");
		//initialize spinner number models
		MapWidth=new SpinnerNumberModel(DefaultMapWidth, 5, 500, 1);
		MapHeight=new SpinnerNumberModel(DefaultMapHeight, 5, 500, 1);
		// 1/2 inch pre set, 1/4 inch minimum, 2 inches maximum, 1/4 inch on button press 
		TileWidth=new SpinnerNumberModel( DefaultTileWidth, 0.25, 2, 0.25);
		TileHeight=new SpinnerNumberModel( DefaultTileHeight, 0.25, 2, 0.25);
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
		//java.awt.print restricts pixels per inch to 72
		return (int) (72 *(double)TileWidth.getNumber());//convert from inches to pixels
	}
	public int getTileHeight()
	{
		return (int) (72*(double)TileHeight.getNumber());//convert from inches to pixels
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
		MapWidth.setValue(DefaultMapWidth);
		MapHeight.setValue(DefaultMapHeight);
		TileWidth.setValue(DefaultTileWidth);
		TileHeight.setValue(DefaultTileHeight);
	}
	@Override
	public void setVisible(boolean b)
	{
		if(b)
			clear();
		super.setVisible(b);
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
