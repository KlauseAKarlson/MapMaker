package mapMaker;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MapMaker extends JFrame implements ChangeListener, ActionListener{
	/**
	 * main
	 */
	

	private JLabel LChosenTile; //shows current tile selected on the Map
	private JPanel MapHolder;
	private MapViewer ActiveViewer;
	private TileChooser TSetMenu;
	private SpinnerNumberModel LayerChooser;
	private SquareMap ActiveMap;
	private JButton BReplaceTile, BNewMap, BLoadSave, BSaveMap, BImportTile, BAddLayer, BRemoveLayer, 
		BExportToImage, BPrint;
	private JCheckBox BAutoReplace;
	private NewMapDialog DNewMap;
	private CreateTileDialog DCreateTile;
	
	public MapMaker()
	{
		super("MapMaker");
		this.setLayout(new BorderLayout());
		ActiveMap=new SquareMap(10,10);
		//add file interaction buttons
		Box fileBox=Box.createHorizontalBox();
		
		BNewMap=new JButton("New Map");
		BNewMap.addActionListener(this);
		fileBox.add(BNewMap);
		
		BSaveMap=new JButton("Save Map");
		BSaveMap.addActionListener(this);
		fileBox.add(BSaveMap);
		
		BLoadSave=new JButton("Load Saved Map");
		BLoadSave.addActionListener(this);
		fileBox.add(BLoadSave);
		
		BImportTile=new JButton("create new Tile");
		BImportTile.addActionListener(this);
		fileBox.add(BImportTile);
		
		BExportToImage=new JButton("Export Map to Image");
		BExportToImage.addActionListener(this);
		fileBox.add(BExportToImage);
		
		BPrint=new JButton("Print Map");
		BPrint.addActionListener(this);
		fileBox.add(BPrint);
		
		this.add(fileBox, BorderLayout.NORTH);
		
		//create sidepanel wtih map editing tools
		createSidePanel();
		//create map viewer and put it in a scroll panel
		ActiveViewer=ActiveMap.getMapViewer();
		ActiveViewer.addActionListener(this);
		MapHolder=new JPanel();
		MapHolder.add(this.ActiveViewer);
		JScrollPane scrlPn=new JScrollPane(MapHolder);
		scrlPn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrlPn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(scrlPn);
		//finish creating JFrame
		DNewMap=new NewMapDialog(this);
		DCreateTile=new CreateTileDialog(this);
		DCreateTile.addActionListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 600);
		this.setVisible(true);
		
	}//end CTOR
	private void createSidePanel()
	{
		//create and add editing tools
		Box sidePanel= Box.createVerticalBox();
		//show tile currently chosen on map
		sidePanel.add(new JLabel("Current Tile"));
		LChosenTile=new JLabel();
		LChosenTile.setAlignmentX(CENTER_ALIGNMENT);
		sidePanel.add(LChosenTile);

		//allow user to select layer, and add or remove layers
		sidePanel.add(new JLabel("Choose Layer to Edit"));
		
		Box layerBox=Box.createHorizontalBox();
		BAddLayer=new JButton("Add new Layer");
		BAddLayer.addActionListener(this);
		layerBox.add(BAddLayer);
		
		LayerChooser=new SpinnerNumberModel(0,0,0,1);
		JSpinner layerSpinner=new JSpinner(LayerChooser);
		layerSpinner.addChangeListener(this);
		layerBox.add(layerSpinner);
		
		BRemoveLayer=new JButton("Remove Layer");
		BRemoveLayer.addActionListener(this);
		layerBox.add(BRemoveLayer);
		sidePanel.add(layerBox);
		
		//provide user with a tool to replace tiles
		Box replaceBox=Box.createHorizontalBox();
		BAutoReplace=new JCheckBox("<html>Auto Replace<br> on Click</html>");
		//BAuto replace does not need an action listner becuase we will be checking its value when ActiveViewer is clicked instead
		replaceBox.add(BAutoReplace);
		BReplaceTile=new JButton("ReplaceTile");
		BReplaceTile.setIcon(ActiveMap.getTileSet().getEmpty() );//gets empty tile is always available
		BReplaceTile.setVerticalTextPosition(AbstractButton.TOP);
		BReplaceTile.setHorizontalTextPosition(AbstractButton.CENTER);
		BReplaceTile.addActionListener(this);
		replaceBox.add(BReplaceTile);
		sidePanel.add(replaceBox);
		
		
		TSetMenu=new TileChooser(ActiveMap.getTileSet());
		TSetMenu.addActionListener(this);
		sidePanel.add(TSetMenu);
		this.add(sidePanel, BorderLayout.EAST);
	}
	public void replaceMap(SquareMap m)
	{
		//replaces the map being edited with Map m
		ActiveMap=m;
		//replace map viewer
		MapHolder.remove(ActiveViewer);
		ActiveViewer=ActiveMap.getMapViewer();
		ActiveViewer.addActionListener(this);
		MapHolder.add(ActiveViewer);
		//update side panel
		this.TSetMenu.updateTileSet(ActiveMap.getTileSet());
		updateLayers();//update layers first to prevent index out of bound errors
		updateChosenTile();
		updateReplaceTile();
	}
	public void updateLayers()
	{
		//updates all components that interact with layers for the new number of layers
		LayerChooser.setValue(0);
		LayerChooser.setMaximum(ActiveMap.getLayers()-1);
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		updateChosenTile();
	}
	private void updateChosenTile()
	{
		/**
		 * updates label in side panel to represent current tile chosen
		 */
		int layer=(int)LayerChooser.getNumber();
		int col=ActiveViewer.getSelectedColumn();
		int row=ActiveViewer.getSelectedRow();
		LChosenTile.setText(col +","+//current collumn
				row+","+//current row
				layer);//current layer
		LChosenTile.setIcon(ActiveMap.getTile(col, row, layer) );
	}//end update chosen tile
	private void updateReplaceTile()
	{
		/**
		 * updates BReplaceTile to reflect currently chosen tile in Tile Chooser
		 */
		BReplaceTile.setIcon(TSetMenu.getChosenTile() );
	}
	private void replaceTile()
	{
		/**
		 * replaces tile on map with chosen tile
		 */
		int layer=(int)LayerChooser.getNumber();
		int col=ActiveViewer.getSelectedColumn();
		int row=ActiveViewer.getSelectedRow();
		ActiveMap.replaceTile(col, row, layer, 
				TSetMenu.getChosenTileName());
		updateChosenTile();
		ActiveViewer.repaint();
	}//end replace tile
	public SquareMap getMap()
	{
		return ActiveMap;
	}
	public void addLayer()
	{
		/**adds new layer above the currently selected one*/
		this.ActiveMap.newLayer( (int) LayerChooser.getNumber() +1 );
		updateLayers();
	}
	public void removeLayer()
	{
		/**
		 * removes currently selected layer
		 */
		ActiveMap.deleteLayer( (int) LayerChooser.getNumber() );
		updateLayers();
		ActiveViewer.repaint();
		updateChosenTile();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==ActiveViewer)
		{
			updateChosenTile();
			if (BAutoReplace.isSelected())
			{
				replaceTile();
			}
		}else if(e.getSource()==TSetMenu)
		{
			updateReplaceTile();
		}else if(e.getSource()==BReplaceTile)
		{
			replaceTile();
		}else if (e.getSource()==this.BNewMap)
		{
			DNewMap.showUI();
		}else if(e.getSource()==DNewMap.BCreate)
		{
			newMap();
		}else if(e.getSource()==this.BAddLayer)
		{
			addLayer();
		}else if(e.getSource()==this.BRemoveLayer)
		{
			removeLayer();
		}else if(e.getSource()==BLoadSave)
		{
			loadSave();
		}else if(e.getSource()==BSaveMap)
		{
			saveMap();
		}else if(e.getSource()==BImportTile)
		{
			DCreateTile.setVisible(true);
		}else if(e.getSource()==DCreateTile)
		{
			//create tile from opaque image
			addTile(DCreateTile.getTileName(),
					DCreateTile.getTileImage());
		}else if(e.getSource()==BExportToImage)
		{
			exportMapToImage();
		}else if(e.getSource()==BPrint)
		{
			printMap();
		}
	}//end actionPerformed(e)
	public void loadSave()
	{
		/**
		 * give user load save option
		 */
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("plain text",
	            "txt");
	    chooser.setFileFilter(filter);
	    int returnValue=chooser.showOpenDialog(this);
	    if (returnValue==JFileChooser.APPROVE_OPTION)
	    {
	    	File save=chooser.getSelectedFile();
	    	try {
				SquareMap saveMap= SquareMap.createSquareMap(save.getAbsolutePath());
				replaceMap(saveMap);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage(), "File Exception", JOptionPane.ERROR_MESSAGE);
			}
	    }
	}//end load save
	public void saveMap()
	{
		/**
		 * provides user save dialogue and saves file
		 */
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Plain Text",
	            "txt");
	    chooser.setFileFilter(filter);
	    int returnValue=chooser.showSaveDialog(this);
	    if (returnValue==JFileChooser.APPROVE_OPTION)
	    {
	    	File save=chooser.getSelectedFile();
	    	try {
				this.ActiveMap.saveMap(save.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage(), "File Exception", JOptionPane.ERROR_MESSAGE);
			}
	    }
	}//end save map
	public void addTile(String tileName, BufferedImage i)
	{
		this.ActiveMap.getTileSet().createTile(tileName, i);
		this.TSetMenu.updateTiles();
	}
	public void newMap()
	{
		/**
		 * creates a new map based on the current settings of the new map dialog window
		 */
		
		SquareTileSet t=new SquareTileSet(DNewMap.getTileWidth(),DNewMap.getTileHeight());
		SquareMap m=new SquareMap(DNewMap.getMapWidth(), DNewMap.getMapHeight(), t);
		replaceMap(m);
	}
	public void exportMapToImage()
	{
		/**
		 * allows the user to save a copy of the map as an image file
		 */
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("png", 
	    		"png" );//static method provides all image types known by ImageIo
	    chooser.setFileFilter(filter);
	    int returnValue=chooser.showSaveDialog(this);
	    if (returnValue==JFileChooser.APPROVE_OPTION)
	    {
	    	File imageFile=chooser.getSelectedFile();
	    	try {
	    		BufferedImage mapCopy=new BufferedImage(this.ActiveViewer.getWidth(), ActiveViewer.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    		Graphics g = mapCopy.getGraphics();
	    		ActiveViewer.paint(g);
	    		g.dispose();
	    		String filePath=imageFile.getAbsolutePath();
	    		System.out.print(filePath +"\n");
	    		int extensionStart=filePath.lastIndexOf(".") + 1;
	    		String fileExtension= filePath.substring(extensionStart);
	    		boolean iio=ImageIO.write(mapCopy, "png", imageFile);
	    		System.out.print(iio);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage(), "File Exception", JOptionPane.ERROR_MESSAGE);
			}
	    }
	}//end export to image
	public void printMap()
	{
		/**
		 * calls up ui to print map
		 */
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(ActiveViewer);
        boolean ok = job.printDialog();
        if (ok) {// user may cancel
            try {
                 job.print();
            } catch (PrinterException e) {
            	//something went wrong, let the user know
            	e.printStackTrace();
            	JOptionPane.showMessageDialog(this, e.getMessage(), "Printing Error", JOptionPane.ERROR_MESSAGE);
            }//end catch
        }//else do nothing because the user canceled
	}//print 
	
	public static void main(String[] args) {
		new MapMaker();
	}
}//end Map Maker
