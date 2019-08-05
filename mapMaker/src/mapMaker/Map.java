package mapMaker;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.*;
import javax.imageio.ImageIO;

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
		 * 
		 */
		//evaluate and open file
		if(!saveLocation.endsWith(".map"))//make sure the save file is a text file
			throw new IOException("Wrong file format");
		ZipFile save=new ZipFile(saveLocation);
		/**
		 * map description contains the map type in the first line,
		 * map width and height on the second line
		 * number of map layers on third line
		 * tile width and height on fourth line
		 * and a list of all tile names on the final line
		 */
		ZipEntry description=save.getEntry("save.txt");
		BufferedReader saveReader=new BufferedReader(
				new InputStreamReader(
						save.getInputStream(description)));
		//read save description
		String currentLine=saveReader.readLine();
		String mapType=currentLine;
		
		//read map size
		currentLine=saveReader.readLine();
		//from now on, start and end will be the indexes of the start of a substring to be extracted from current line
		//numberString will be used to store a substring containing a number to be parsed
		int start=currentLine.indexOf(":", 0)+1;
		int end=currentLine.indexOf(",");
		String numberString=currentLine.substring(start, end);
		int mapWidth=Integer.parseInt(numberString);//parse map width
		start=end+1;
		numberString=currentLine.substring(start);
		int mapHeight=Integer.parseInt(numberString);//parse map height
		
		//read map layers
		currentLine=saveReader.readLine();
		start=currentLine.indexOf(":")+1;
		numberString=currentLine.substring(start);
		int layerCount=Integer.parseInt(numberString);//parse layer count
		
		//read tile size
		currentLine=saveReader.readLine();
		start=currentLine.indexOf(":", 0)+1;
		end=currentLine.indexOf(",");
		numberString=currentLine.substring(start, end);
		int tileWidth=Integer.parseInt(numberString);//parse tile width
		start=end+1;
		numberString=currentLine.substring(start);
		int tileHeight=Integer.parseInt(numberString);//parse tile height
		
		//read tile list
		LinkedList<String> tileNames=new LinkedList<String>();
		currentLine=saveReader.readLine();
		start=0;
		end=currentLine.indexOf(",");
		while (end!=-1)//index of returns -1 if there are no matching characters
		{
			tileNames.add(currentLine.substring(start,end));
			start=end+1;
			end=currentLine.indexOf(",", start);
		}//end while loop, will leave one more tile to be created
		tileNames.add(currentLine.substring(start));
		saveReader.close();
		
		//create map, and load based on description
		Map saveMap=createMap(mapType, mapWidth, mapHeight, tileWidth,  tileHeight);
		saveMap.loadTiles(save, tileNames);//must load tiels before loading layers
		for (int layer=0;layer<layerCount;layer++)
		{
			saveMap.loadLayer(save, layer);
		}//end load layers
		save.close();
		return saveMap;
	}//end load map
	
	private void loadTiles(ZipFile save, LinkedList<String> tileNames) throws IOException
	{
		BufferedImage tileImage;
		ZipEntry tileSource;
		TileSet tSet=this.getTileSet();
		for (String tileName:tileNames)
		{
			tileSource=save.getEntry(tileName+".png");
			tileImage=ImageIO.read(save.getInputStream(tileSource));
			tSet.createTile(tileName, tileImage);
		}//end loop over tiles
	}//end load tiles
	
	private void loadLayer(ZipFile save, int layer) throws IOException
	{
		//check layer and create new one if missing
		while (this.mapLayers.size() <= layer)
			this.newLayer();
		//open file
		ZipEntry layerEntry=save.getEntry("Layer"+layer+".csv");
		BufferedReader saveReader=new BufferedReader(
				new InputStreamReader(
						save.getInputStream(layerEntry)));
		//prepare for loop operations
		String currentLine, tileName;
		int start,end, collumn;
		//add tiles to layer
		for (int line=0;line<mapHeight;line++)
		{
			//read line and add tiles one by one
			currentLine=saveReader.readLine();
			//ready substring indexes
			start=0;
			end=currentLine.indexOf(",");
			collumn=0;
			while (end!=-1)//index of returns -1 if there are no matching characters
			{
				tileName=currentLine.substring(start, end);
				replaceTile(collumn, line, layer, tileName);
				collumn++;
				start=end+2;
				end=currentLine.indexOf(",", start);
			}//at the end there will be one remaining tile
			tileName=currentLine.substring(start);
			replaceTile(collumn, line, layer, tileName);
		}//end iterating through height lines of a layer
	}//end load layer
	
	public static Map createMap(String mapType, int mapWidth, int mapHeight, int tileWidth, int tileHeight)
	{
		if (mapType.equals(squareMap))
		{
			SquareTileSet t=new SquareTileSet(tileWidth,tileHeight);
			return new SquareMap(mapWidth, mapHeight, t);
		}else if(mapType.equals(hexMap))
		{
			HexTileSet t=new HexTileSet(tileWidth, tileHeight);
			return new HexMap(mapWidth, mapHeight, t);
		}else
			return null;
		
	}//end create map
	
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
	
	public void saveMap(String saveFile) throws IOException
	{
		/**
		 * creates a save file with the .map file extension
		 * 
		 * the save file is a zip file with the map description saved as save.txt
		 * and each layer saved as layer(N).csv where N is the layer number
		 * the tiles will be saved as (tile name).png 
		 */
		if(!saveFile.endsWith(".map"))//make sure the save file is a text file
			saveFile+=".map";
		//create zip file
		File saveLocation=new File(saveFile);
		ZipOutputStream zipStream= new ZipOutputStream(new FileOutputStream(saveLocation));
		/**
		 * map description contains the map type in the first line,
		 * map width and height on the second line
		 * number of map layers on third line
		 * tile width and height on fourth line
		 * and a list of all tile names on the final line
		 */
		ZipEntry saveEntry=new ZipEntry("save.txt");
		zipStream.putNextEntry(saveEntry);
		Writer saveWriter=new BufferedWriter(new OutputStreamWriter(zipStream));
		String mapType=this.getMapType();
		//write type
		saveWriter.write(mapType+"\n");
		//write width and height
		saveWriter.write("Map Size:"+
				this.getWidth()+","+
				this.getHeight()+"\n");
		//write layers
		saveWriter.write("layers:"+this.mapLayers.size()+"\n");
		//write tile size
		saveWriter.write("tileSize:"+
				this.getTileSet().getWidth()+","+
				this.getTileSet().getHeight()+"\n");
		//write tile names
		Enumeration<Tile> tiles=this.getTileSet().getTiles();
		StringBuffer tileNames=new StringBuffer();
		String name;
		while (tiles.hasMoreElements())
		{
			name=tiles.nextElement().getName();
			tileNames.append(name);
			if(tiles.hasMoreElements())
				tileNames.append(",");
		}//end iterate through tiles
		saveWriter.write(tileNames.toString());
		//finish writing save description
		saveWriter.flush();
		zipStream.closeEntry();
		//save layers and tiles
		saveLayers(zipStream);
		saveTiles(zipStream);
		//close
		zipStream.close();
	}
	
	private void saveTiles(ZipOutputStream zipStream) throws IOException
	{
		/**
		 * saves fresh tiles to save
		 */
		Enumeration<Tile> tiles=this.getTileSet().getTiles();
		Tile currentTile;
		int tileWidth=this.getTileSet().getWidth();
		int tileHeight=this.getTileSet().getHeight();
		while(tiles.hasMoreElements())
		{
			currentTile=tiles.nextElement();
			//get the java.awt.image from that imageicon being used as the image for the tile
			Image tileImage=currentTile.getImage();
			//create buffered image for  ImageIO
			BufferedImage bi = new BufferedImage(tileWidth,tileHeight,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = bi.createGraphics();
			g2.drawImage(tileImage, 0, 0, null);
			g2.dispose();
			//create zip file and write
			ZipEntry zipEntry = new ZipEntry(currentTile.getName()+".png");
			zipStream.putNextEntry(zipEntry);							
			ImageIO.write(bi, "png", zipStream);//save using image name+
			zipStream.closeEntry();

		}//end while loop
	}//end save fresh tiles
	
	private void saveLayers(ZipOutputStream zipStream) throws IOException
	{
		/**
		 * saves layers to zip file
		 */
		ZipEntry layerEntry;
		Writer saveWriter;
		Layer currentLayer;
		Tile currentTile;
		String tileText;
		for (int i=0;i<mapLayers.size();i++)
		{
			//create zip entry and prepare writer
			layerEntry=new ZipEntry("Layer"+i+".csv");
			zipStream.putNextEntry(layerEntry);
			currentLayer=mapLayers.get(i);
			saveWriter=new BufferedWriter(new OutputStreamWriter(zipStream));
			//write file
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
			//close entry
			saveWriter.flush();
			zipStream.closeEntry();
			
		}//end iterate loop
	}//end save layers
	
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
			return getTileSet().getEmpty();
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
		//check bounds
		if (x<0 || x>=mapWidth 
				|| y<0 || y>=mapHeight
				|| layerNumber<0 || layerNumber>=mapLayers.size())
		{
			if (x!=-1 && y!=-1)//ignore missing tiles in hex maps
				throw new IndexOutOfBoundsException("Tile outside fo map");
			else
				return;//do nothing, common with hex style, 
		}

		//censure valid tile
		if (!getTileSet().validTileName(tileName))
		{
			throw new IllegalArgumentException("Tile name not in tile set");
		}
		Layer selectedLayer= mapLayers.get(layerNumber);
		Tile replacementTile = getTileSet().getTile(tileName);
		//make sure replacement tile is not a background tile being put in the foreground
		if (layerNumber>0 && ! replacementTile.foreground() )
			return; //do nothing rather than replacing tile, don't throw an error.
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
	
	public abstract String getMapType();
	
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
