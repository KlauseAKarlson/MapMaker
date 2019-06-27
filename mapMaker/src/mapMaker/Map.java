package mapMaker;
import java.util.*;
import java.awt.Component;
import java.awt.Graphics;
import java.io.*;

public class Map {

	private int mapWidth;
	private int mapHeight;
	private LinkedList<Layer> mapLayers;
	private TileSet mapTileSet;
	
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
		mapTileSet=new TileSet(72,72);//default size is one inch under default print format
	}//end constructor
	public Map(int width, int height, TileSet t)
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
		mapTileSet=t;//use default size for now
	}//end constructor
	public Map(String saveLocation) throws IOException
	{
		/**
		 * creates a map based on a text based save file
		 * save files are composed of a labeled header followed by each layerNumer saved with the tile name seperated by commas and newlines
		 */
		//evaluate and open file
		if(!saveLocation.endsWith("txt"))//make sure the save file is a text file
			throw new IOException("Wrong file format");
		File saveFile=new File(saveLocation);
		BufferedReader saveReader=new BufferedReader(new FileReader(saveFile));
		//read header from save file
		String currentLine=saveReader.readLine();
		String numberString=currentLine.substring(currentLine.indexOf(": ")+2);//get substring containing the width
		this.mapWidth=Integer.valueOf(numberString);//convert to int
		currentLine=saveReader.readLine();
		numberString=currentLine.substring(currentLine.indexOf(": ")+2);
		this.mapHeight=Integer.valueOf(numberString);//convert to int
		//create tileset
		currentLine=saveReader.readLine();
		//start and end are substring indexes
		//TODO move into TileSet for ecapsulation
		int start=currentLine.indexOf(": ")+2;//read multiple substrings from each line from now on
		int end=currentLine.indexOf(",");
		numberString=currentLine.substring(start, end);
		int tileCount=Integer.valueOf(numberString);
		start=end+2;//get next substring
		end=currentLine.indexOf(",", start);
		numberString=currentLine.substring(start, end);
		int tileWidth=Integer.valueOf(numberString);
		start=end+2;//get next substring
		numberString=currentLine.substring(start);//last number expected on this line
		int tileHeight=Integer.valueOf(numberString);
		this.mapTileSet=new TileSet(tileWidth, tileHeight);
		//load tiles
		currentLine=saveReader.readLine();
		start=0;
		end=currentLine.indexOf(",");
		String tileName, tilePath;
		while (end!=-1)//index of returns -1 if there are no matching characters
		{
			tileName=currentLine.substring(start,end);
			tilePath=saveFile.getParent()+File.separator+tileName+".png";
			this.mapTileSet.createTile(tileName, tilePath, false);
			start=end+2;
			end=currentLine.indexOf(",", start);
		}//end while loop, will leave one more tile to be created
		tileName=currentLine.substring(start);
		tilePath=saveFile.getParent()+File.separator+tileName+".png";
		this.mapTileSet.createTile(tileName, tilePath, false);
		//read layerNumers from save file
		currentLine=saveReader.readLine();//read layer count
		numberString=currentLine.substring(currentLine.indexOf(":")+2);
		int layerCount=Integer.valueOf(numberString);//read number of layers
		this.mapLayers=new LinkedList<Layer>();
		int tilesAdded;//width
		for (int i=0;i<layerCount;i++)//create layers
		{
			this.mapLayers.add(new Layer() );//add current layer
			//add tiles to layer
			for (int line=0;line<this.mapHeight;line++)
			{
				//read line and add tiles one by one
				currentLine=saveReader.readLine();
				//ready substring indexes
				start=0;
				end=currentLine.indexOf(",");
				tilesAdded=0;
				while (end!=-1)//index of returns -1 if there are no matching characters
				{
					tileName=currentLine.substring(start, end);
					this.replaceTile(tilesAdded, line, i, tileName);
					tilesAdded++;
					start=end+2;
					end=currentLine.indexOf(",", start);
				}
				tileName=currentLine.substring(start);
				this.replaceTile(tilesAdded, line, i, tileName);
			}//end iterating through height lines of a layer
			
		}//end iteration through layers
		//close
		saveReader.close();
	}//end constructor from save file
	
	public void saveMap(String saveFile) throws IOException
	{
		/**
		 * saves map as a text document
		 */
		if(!saveFile.endsWith("txt"))//make sure the save file is a text file
			saveFile+=".txt";
		File saveLocation=new File(saveFile);
		FileWriter saveWriter=new FileWriter(saveLocation);
		//write header
		saveWriter.write("mapWidth: "+this.mapWidth+"\n");
		saveWriter.write("mapHeight: "+this.mapHeight+"\n");
		//write tileset
		saveWriter.write(this.mapTileSet.toString()+"\n");
		//save any fresh (newly created) tiles
		String saveDirectory=saveLocation.getParent();
		this.mapTileSet.saveFresh(saveDirectory);//seperator will be added by saveFresh
		//write layers
		saveWriter.write("layers: "+this.mapLayers.size()+"\n");
		Layer currentLayer;
		String tileText;
		Tile currentTile;
		for (int i=0; i<mapLayers.size();i++)
		{
			//write layers from bottom to top
			currentLayer=mapLayers.get(i);
			for (int y=0; y<mapHeight;y++)
			{
				//write rows naturally
				for (int x=0; x<mapWidth;x++)
				{
					currentTile =currentLayer.tiles[x][y];
					if (currentTile == null)//take care of uninitialized empty tiles
					{
						tileText="Empty";
					}else {

						tileText=currentTile.getName();
					}
					if(x<mapWidth-1)
					{
						tileText+=", ";
					}else {
						tileText+="\n";
					}
					saveWriter.write(tileText);
				}//end X (width) loop
			}//end y (height) loop
		}//end layers loop
		saveWriter.close();
	}//end save map
	
	public TileSet getTileSet()
	{
		return mapTileSet;
	}//end get tiel set
	
	public int getWidth()
	{
		return this.mapWidth;
	}
	
	public int getHeight()
	{
		return this.mapHeight;
	}
	
	public int getLayers()
	{
		return mapLayers.size();
	}
	
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
			t=mapTileSet.getTile("Empty");
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
				t.getIcon().paintIcon(c, g, pixelX, pixelY);
			//nulls are treated as blank spaces
		}
	}//end paint tiles at 
	
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
		if (!mapTileSet.validTileName(tileName))
		{
			throw new IllegalArgumentException("Tile name not in tile set");
		}
		Layer selectedLayer= mapLayers.get(layerNumber);
		Tile replacementTile = mapTileSet.getTile(tileName);
		selectedLayer.tiles[x][y]=replacementTile;
	}//end replace tile
	
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
