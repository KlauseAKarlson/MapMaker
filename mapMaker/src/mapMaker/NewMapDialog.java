package mapMaker;

import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class NewMapDialog extends JFrame implements ActionListener {

	private MapMaker Parent;
	private SpinnerNumberModel MapWidth, MapHeight, TileWidth, TileHeight;
	public JButton BCreate;
	public NewMapDialog(MapMaker m)
	{
		super("New Map");
		Parent=m;
		//initialize spinner number models
		MapWidth=new SpinnerNumberModel(10, 5,500, 1);
		MapHeight=new SpinnerNumberModel(10, 5,500, 1);
		TileWidth=new SpinnerNumberModel(100, 50, 400, 10);
		TileHeight=new SpinnerNumberModel(100, 50, 400, 10);
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
		
		BCreate=new JButton("Create map");
		BCreate.addActionListener(this);
		BCreate.addActionListener(Parent);
		this.add(BCreate);
		
		this.pack();
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
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
		this.setVisible(false);
	}

}//end class
