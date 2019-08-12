package mapMaker;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
			//draw border
			g2d.setColor(Color.BLACK);
			g2d.drawRect(0, 0,tileWidth-1, tileHeight-1);
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
		int target=rsImage.getRGB(1, 1);
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
		 * @param t should be true if the tile background is transparent, and allows the tiel to be used on foreground layers
		 */
		if (tileName.equalsIgnoreCase("Empty"))
		{
			return;//Empty is a reserved name
		}
		int transparent=new Color(0,0,0,0).getRGB();
		boolean foreground=(i.getRGB(1, tileHeight/2) == transparent);
		Tile newTile=new Tile(tileName, resizeImage(i), foreground);
		tiles.put(tileName, newTile);
	}
	
	
}
