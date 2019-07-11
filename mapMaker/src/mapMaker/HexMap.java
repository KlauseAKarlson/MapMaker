package mapMaker;

import java.awt.Component;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import mapMaker.Map.Layer;

public class HexMap extends Map {

	private HexTileSet mapTileSet;
	
	public HexMap(int width, int height)
	{
		/**
		 * creates a new blank map with desired height and width
		 */
		super(width,height);
		mapTileSet=new HexTileSet(36);//default size is one inch under default print format
	}//end constructor
	public HexMap(int width, int height, HexTileSet t)
	{
		super(width,height);
		mapTileSet=t;
	}
	public static HexMap loadHexMap(BufferedReader saveReader, File saveFile) throws IOException
	{
		/**
		 * creates a map based on a text based save file
		 * save files are composed of a labeled header followed by each layerNumer saved with the tile name seperated by commas and newlines
		 */

		//read header from save file
		String currentLine=saveReader.readLine();
		String numberString=currentLine.substring(currentLine.indexOf(": ")+2);//get substring containing the width
		int mapWidth=Integer.valueOf(numberString);//convert to int
		currentLine=saveReader.readLine();
		numberString=currentLine.substring(currentLine.indexOf(": ")+2);
		int mapHeight=Integer.valueOf(numberString);//convert to int
		HexMap newMap= new HexMap(mapWidth,mapHeight);
		
		//create tileset
		currentLine=saveReader.readLine();
		//start and end are substring indexes
		int start=currentLine.indexOf(": ")+2;//read multiple substrings from each line from now on
		int end=currentLine.indexOf(",");
		//numberString=currentLine.substring(start, end);//not used
		//int tileCount=Integer.valueOf(numberString);
		start=end+2;//get next substring
		end=currentLine.indexOf(",", start);
		numberString=currentLine.substring(start, end);
		int tileWidth=Integer.valueOf(numberString);
		start=end+2;//get next substring
		numberString=currentLine.substring(start);//last number expected on this line
		int tileHeight=Integer.valueOf(numberString);
		newMap.mapTileSet=new HexTileSet(tileWidth, tileHeight);
		//load tiles
		currentLine=saveReader.readLine();
		start=0;
		end=currentLine.indexOf(",");
		String tileName, tilePath;
		while (end!=-1)//index of returns -1 if there are no matching characters
		{
			tileName=currentLine.substring(start,end);
			tilePath=saveFile.getParent()+File.separator+tileName+".png";
			newMap.mapTileSet.createTile(tileName, tilePath, false);
			start=end+2;
			end=currentLine.indexOf(",", start);
		}//end while loop, will leave one more tile to be created
		tileName=currentLine.substring(start);
		tilePath=saveFile.getParent()+File.separator+tileName+".png";
		newMap.mapTileSet.createTile(tileName, tilePath, false);
		//read layerNumers from save file
		currentLine=saveReader.readLine();//read layer count
		numberString=currentLine.substring(currentLine.indexOf(":")+2);
		int layerCount=Integer.valueOf(numberString);//read number of layers
		newMap.mapLayers=new LinkedList<Layer>();
		int tilesAdded;//width
		for (int i=0;i<layerCount;i++)//create layers
		{

			//add tiles to layer
			for (int line=0;line<newMap.mapHeight;line++)
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
					newMap.replaceTile(tilesAdded, line, i, tileName);
					tilesAdded++;
					start=end+2;
					end=currentLine.indexOf(",", start);
				}
				tileName=currentLine.substring(start);
				newMap.replaceTile(tilesAdded, line, i, tileName);
			}//end iterating through height lines of a layer
			if (i<layerCount-1)
				newMap.newLayer();//add current layer
		}//end iteration through layers
		//close
		saveReader.close();
		return newMap;
	}
	@Override
	public void saveMap(String saveFile) throws IOException {
		/**
		 * saves map as a text document
		 */
		if(!saveFile.endsWith("txt"))//make sure the save file is a text file
			saveFile+=".txt";
		File saveLocation=new File(saveFile);
		FileWriter saveWriter=new FileWriter(saveLocation);
		//write header
		saveWriter.write("HexMap\n");
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
	}

	@Override
	public TileSet getTileSet() {
		return mapTileSet;
	}


	@Override
	public MapViewer getMapViewer() {
		return new HexMapViewer(this);
	}

}
