package mapMaker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HexTileSet extends TileSet {
	public HexTileSet(int tWidth, int tHeight) {
		super(tWidth, tHeight);
	}
	public HexTileSet(int radius)
	{
		super( (int) ( radius*Math.sqrt(3) ), 
				(radius*2) );
		/*
		 * creates a tielset made of regular hexigons based on the outer radius
		 * the inner radius is sqrt(3)/2 * radius, which is doubled to create the width
		 */
	}

	/**
	 * defines a tileset made of hexagonal tiles
	 * The left and right sides of the hexagon are the middle half of the left and right sides of the rectangle bounding the hexagon
	 * the other four sides form slopes from the left and right sides to the center of the top and bottom of the bounding rectangle
	 */
	public boolean inHex(int x, int y)
	{
		/**
		 * returns true if the coordinates are inside the hexagon bounded by the width and height of the tile
		 */
		double slope = (tileHeight/2.0)/(tileWidth);
		boolean inside=false;
		//check in acordance to sides
		if (x<(tileWidth/2 +1) )
		{
			inside= 
					( y < (0.75*tileHeight+slope*x) ) &&
					( y > (0.25*tileHeight-slope*x));
		}else {
			int x2=x-tileWidth/2;
			inside =
					( y < (tileHeight -slope*x2) )&&
					(y > (0+slope*x2));
		}
		return inside;
	}
	
	@Override
	public String toString() {
		/**
		 * provides the save data of tileset, not including image data, as two line string
		 */
		String saveString= "tileSet: "+tileWidth+", "+tileHeight+"\n";//print width and height
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
	}

	@Override
	public void createTile(String tileName, BufferedImage i) {
		/**
		 * creates a new tile, resizes it if needed, and places it in the hashtable under the provided name.
		 * if this is a duplicated of an already existing tile name, the tile will be replaced
		 * assumes the image is fresh
		 */
		if (tileName.equalsIgnoreCase("Empty"))
		{
			throw new IllegalArgumentException("\"Empty\" is a reserved name");
		}
		Tile newTile=new Tile(tileName, resizeImage(i), true);
		tiles.put(tileName, newTile);
	}

	@Override
	public void createTile(String tileName, String imagePath, boolean freshTile) throws IOException {
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
				//resize image and create tile, rsImage=Re-Sized Image
				BufferedImage rsImage=resizeImage(inputImage);
				newTile= new Tile(tileName, rsImage, true);
			}
			tiles.put(tileName, newTile);

		}//else do nothing, prevent tile creation for reserved name

	}//end create tile

	@Override
	public BufferedImage resizeImage(BufferedImage i) {
		/**
		 * resizes the image into a hexagon tile of the correct size
		 */
		
		BufferedImage rsImage=new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
		//create space for color data
		int transparent=new Color(0,0,0,0).getRGB();
		//draw a copy of the source image
		Graphics2D g2d = rsImage.createGraphics();
		g2d.drawImage(i, 0, 0, tileWidth, tileHeight, null);
		g2d.dispose();
		//make areas outside the hex transparent
		for (int y=0;y<=tileHeight/4;y++)
		{
			for (int x=0;x<tileWidth;x++)
			{
				if (! inHex(x, y) )
					rsImage.setRGB(x, y, transparent);
			}//end row loop
		}//end height loop
		for (int y=(tileHeight*3)/4;y<tileHeight;y++)
		{
			for (int x=0;x<tileWidth;x++)
			{
				if (! inHex(x, y) )
					rsImage.setRGB(x, y, transparent);
			}//end row loop
		}//end height loop
		return rsImage;
	}//end resizeImage

	@Override
	public BufferedImage ResizeAndTransparent(BufferedImage i) {
		/**
		 * creates a version of the desired tile with a transparent background
		 * the background is sampled at the middle of the left side
		 */
		//re size input
		BufferedImage rsImage=resizeImage(i);
		BufferedImage recoloredImage=new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
		//create space for color data
		int transparent=new Color(0,0,0,0).getRGB();
		int target=rsImage.getRGB(0, tileHeight/2);
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
	}

}
