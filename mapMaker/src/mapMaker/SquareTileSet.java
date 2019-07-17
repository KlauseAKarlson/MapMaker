package mapMaker;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
 
import javax.imageio.ImageIO;

/**
 * 
 * @author KlauseKarlson
 *contains all valid tiles for use in the map, as described by their tile name
 *
 */
public class SquareTileSet extends TileSet{

	public SquareTileSet(int tWidth, int tHeight)
	{
		/**
		 * creates tileset with an empty Hashtable
		 * 
		 */
		super(tWidth,tHeight);
	}
	
	public String toString()
	{
		/**
		 * provides the save data of tileset, not including image data, as two line string
		 */
		String saveString= "tileSet: "+tiles.size()+", "+tileWidth+", "+tileHeight+"\n";//print the size of the tile set, and width then height
		//creates an array containing all of the tile names in the tile set
		String[] tileList=new String[tiles.size()];
		this.getTileList().toArray(tileList);
		//append tile names to string
		for(int i=0;i<tileList.length;i++)
		{
			saveString+=tileList[i];
			if (i< (tileList.length-1) ) {
				saveString+=", ";
			}else {
				//do nothing
			}
		}
		return saveString;
	}//end to string

	
	public BufferedImage resizeImage(BufferedImage i)
	{
		/**
		 * returns a copy of image i resized to the correct size for a tile
		 */
		boolean GoodWidth =  i.getWidth()==this.tileWidth;
		boolean GoodHeight= i.getHeight()==this.tileHeight;
		if(GoodWidth && GoodHeight)
		{
			//if correct width and height, do nothing
			return i;
		}else {
			//resize image and create tile, rsImage=Re-Sized Image
			BufferedImage rsImage=new BufferedImage(tileWidth, tileHeight, i.getType());
			Graphics2D g2d = rsImage.createGraphics();
			g2d.drawImage(i, 0, 0, tileWidth, tileHeight, null);
			g2d.dispose();
			return rsImage;
		}
	}//end resize
	public BufferedImage ResizeAndTransparent(BufferedImage i)
	{
		/**
		 * resizes an image to the correct size and makes its background transparent
		 * this function identifies the background color by sampling the top left pixel of the image
		 */
		//re size input
		BufferedImage rsImage=resizeImage(i);
		//create new image for output
		BufferedImage recoloredImage=new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
		//create space for color data
		int transparent=new Color(0,0,0,0).getRGB();
		int target=rsImage.getRGB(0, 0);
		int pixel;//color of pixel extracted from rsImage
		//iterate through pixels using x for width and y for height
		for (int y=0; y<rsImage.getHeight(); y++)
		{
			for (int x=0; x<rsImage.getWidth();x++)
			{
				pixel=rsImage.getRGB(x, y);
				if (pixel==target)
				{
					recoloredImage.setRGB(x, y, transparent);
				}else {
					recoloredImage.setRGB(x, y, pixel);
				}
			}//end width scan
		}//end height scan
		
		return recoloredImage;
	}//end resize and transparent
	public void createTile(String tileName, BufferedImage i)
	{
		/**
		 * creates a new tile, resizes it if needed, and places it in the hashtable under the provided name.
		 * if this is a duplicated of an already existing tile name, the tile will be replaced
		 * assumes the image is fresh
		 */
		if (tileName.equalsIgnoreCase("Empty"))
		{
			return;//Empty is a reserved name
		}
		Tile newTile=new Tile(tileName, resizeImage(i), true);
		tiles.put(tileName, newTile);
	}
	
	public void createTile(String tileName, String imagePath, boolean freshTile) throws IOException
	{
		/**
		 * creates a new tile, resizes it if needed, and places it in the hashtable under the provided name.
		 * if this is a duplicated of an already existing tile name, the tile will be replaced
		 * freshTile should be true if the tile is being created for the first time, or false if the tile is being loaded from a saved project.
		 * primarily used for loading saved tiles
		 */
		if (!tileName.equalsIgnoreCase("empty"))
		{
			File sourceFile=new File(imagePath);
			BufferedImage inputImage= ImageIO.read(sourceFile);
			Tile newTile;//create tile outside of if/then clause
			//check width and height
			boolean GoodWidth =  inputImage.getWidth()==this.tileWidth;
			boolean GoodHeight= inputImage.getHeight()==this.tileHeight;
			if(GoodWidth && GoodHeight)
			{
				//if correct width and height, go ahead and create tile
				newTile = new Tile(tileName, inputImage, freshTile);
			}else {
				BufferedImage rsImage=resizeImage(inputImage);
				newTile= new Tile(tileName, rsImage, true);
			}
			tiles.put(tileName, newTile);

		}//else do nothing, prevent tile creation for reserved name
			
	}//end createTile

}
