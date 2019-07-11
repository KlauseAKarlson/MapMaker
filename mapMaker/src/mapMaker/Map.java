package mapMaker;

import java.awt.Component;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import mapMaker.Map.Layer;


public abstract class Map {

	
	protected int mapWidth;
	protected int mapHeight;
	protected LinkedList<Layer> mapLayers;
	public static final String hexMap="HexMap",squareMap="SquareMap";
	
	public static Map loadMap(String saveLocation) throws IOException
	{
		/**
		 * functions as a factory method for loading maps from saved files
		 * 
		 */
		//evaluate and open file
		if(!saveLocation.endsWith("txt"))//make sure the save file is a text file
			throw new IOException("Wrong file format");
		File saveFile=new File(saveLocation);
		BufferedReader saveReader=new BufferedReader(new FileReader(saveFile));
		String mapType=saveReader.readLine();
		switch (mapType)
		{
			case hexMap:
				return HexMap.loadHexMap(saveReader, saveFile);
			case squareMap:
				return SquareMap.loadSquareMap(saveReader, saveFile);
			
			default:
				throw new IOException("Map Type not recognized");
		}
	}//end load map
	
	public Map(int width, int height)
	{
		/**
		 * creates a new blank map with desired height and width
		 */
		mapWidth=width;
		mapHeight=height;
		mapLayers=new LinkedList<Layer>();
		Layer background=new Layer();
		//unknown or null tiles are treated as empty spaces as a fail safe design
		mapLayers.add(background);
	}//end constructor
	
	public abstract void saveMap(String saveFile) throws IOException;
	
	public abstract TileSet getTileSet();

	public Tile getTile(int x, int y, int layerNumber)
	{
		/**
		 * returns the tile at selected position and layerNumer
		 */
		if (x<0 || x>=mapWidth 
				|| y<0 || y>=mapHeight
				|| layerNumber<0 || layerNumber>=mapLayers.size())
		{
			throw new IndexOutOfBoundsException("Tile outside fo map"+x+","+y+","+layerNumber);
		}
		Layer selectedLayer=this.mapLayers.get(layerNumber);
		Tile t=selectedLayer.tiles[x][y];
		if (t==null)//handle uninitialized tiles
		{
			t=getTileSet().getEmpty();
		}//else do nothing
		return t;
	}//end get tile
	
	public Tile[] getTiles(int x,int y)
	{
		/**
		 * returns the tiles from bottom to top at the selected position
		 */
		if (x<0 || x>=mapWidth 
				|| y<0 || y>=mapHeight)
		{
			throw new IndexOutOfBoundsException("Tile outside fo map");
		}
		Tile[] tileStack=new Tile[getLayers()];
		for (int i=0;i<getLayers();i++)
		{
			tileStack[i]=getTile(x,y,i);
		}
		return tileStack;
	}//end getTiles
	
	public void replaceTile(int x, int y, int layerNumber, String tileName)
	{
		/**
		 * replaces tile at selected location with the one designated by tile name
		 * 
		 */
		if (x<0 || x>=mapWidth 
				|| y<0 || y>=mapHeight
				|| layerNumber<0 || layerNumber>=mapLayers.size())
		{
			throw new IndexOutOfBoundsException("Tile outside fo map");
		}
		if (!getTileSet().validTileName(tileName))
		{
			throw new IllegalArgumentException("Tile name not in tile set");
		}
		Layer selectedLayer= mapLayers.get(layerNumber);
		Tile replacementTile = getTileSet().getTile(tileName);
		selectedLayer.tiles[x][y]=replacementTile;
	}//end replace tile
	
	public int getLayers()
	{
		return mapLayers.size();
	}
	
	public int getWidth()
	{
		return this.mapWidth;
	}
	
	public int getHeight()
	{
		return this.mapHeight;
	}
	public abstract MapViewer getMapViewer()
	/**
	 * produces a map viewer that shows this map.
	 */;
	
	public void paintTileAt(int tileX, int tileY, int pixelX, int pixelY, Component c, Graphics g)
	{
		/**
		 * paints tiles from bottom to top
		 * the first two values are the location of the desired tiles on the map
		 * the second two values are the locations of where the tiles will be painted on the desired component
		 */
		if (tileX<0 || tileX>=mapWidth 
				|| tileY<0 || tileY>=mapHeight)
		{
			throw new IndexOutOfBoundsException("Tile outside fo map, x"+tileX+"y"+tileY);
		}
		Tile[] tiles=getTiles(tileX,tileY);
		for (Tile t:tiles)
		{
			if (t != null)
				t.paintIcon(c, g, pixelX, pixelY);
			//nulls are treated as blank spaces
		}
	}//end paint tiles at 
	
	public void newLayer()
	{
		/**
		 * adds a new blank layer on top
		 * 
		 */
		this.mapLayers.add(new Layer());
	}//end new layer
	
	public boolean newLayer(int layerNumber)
	{
		/**
		 * adds a new layer in selected placement, pushing the other layers up
		 * returns false if a forbidden layerNumber is chosen
		 */
		if (layerNumber>=0 || layerNumber<=this.getLayers())
		{
			this.mapLayers.add(layerNumber, new Layer());
			return true;
		}else {
			return false;
		}
	}//end newLayer(int layerNumber)
	
	public boolean deleteLayer(int layerNumber)
	{
		/**
		 * deletes a layer unless it is the bottom, background layer
		 * returns true if successful or false if forbidden
		 */
		if (layerNumber>0 && layerNumber<this.getLayers())
		{
			mapLayers.remove(layerNumber);
			return true;
		}else {
			return false;
		}
	}//end deleteLayer
	
	protected class Layer{
		/**container for a tile array, */
		Tile[][] tiles;
		
		protected Layer()
		{
			tiles=new Tile[mapWidth][mapHeight];
		}
	}//end Layer class
}//end map class
