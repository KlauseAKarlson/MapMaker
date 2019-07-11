package mapMaker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import javax.imageio.ImageIO;

public abstract class TileSet {

	
	protected Hashtable<String,Tile> tiles;
	protected int tileWidth=72, tileHeight=72;//width and height of tiles in pixels
	
	public TileSet(int tWidth, int tHeight)
	{
		tiles= new Hashtable<String,Tile>();
		tileWidth=tWidth;
		tileHeight=tHeight;
		//create reserved empty tile
		BufferedImage blank=new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = blank.createGraphics();
		g2d.setColor(new Color(0,0,0,0));///transparent
		g2d.drawRect(0, 0, tileWidth-1, tileHeight-1);//fill with transparent
		g2d.dispose();
		Tile empty= new Tile("Empty", blank, true);
		tiles.put("Empty", empty);
	}
	public abstract String toString();
	
	public void saveFresh(String saveDirectory) throws IOException
	{
		/**
		 * saves any fresh (newly created) tiles in provided save directory 
		 */
		Enumeration<Tile> tileEnum=this.tiles.elements();
		Tile currentTile;
		while(tileEnum.hasMoreElements())
		{
			currentTile=tileEnum.nextElement();
			if (currentTile.isFresh())
			{
				//get the java.awt.image from that imageicon being used as the image for the tile
				Image tileImage=currentTile.getImage();

				BufferedImage bi = new BufferedImage(this.tileWidth,this.tileHeight,BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = bi.createGraphics();
				g2.drawImage(tileImage, 0, 0, null);
				g2.dispose();
				ImageIO.write(bi, "png", new File(saveDirectory+File.separator+currentTile.getName()+".png"));//save using image name
			}//else do nothing
		}//end while loop
	}//end saveFresh
	
	public abstract  void createTile(String tileName, BufferedImage i);
	
	public abstract void createTile(String tileName, String imagePath, boolean freshTile) throws IOException;
	
	public abstract  BufferedImage resizeImage(BufferedImage i);
	
	public abstract BufferedImage ResizeAndTransparent(BufferedImage i);

	public int getWidth()
	{
		/**
		 * tile width in pixels
		 */
		return this.tileWidth;
	}
	public int getHeight()
	{
		/**
		 * tile height in pixels
		 */
		return this.tileHeight;
	}
	public boolean validTileName(String name)
	{
		/**
		 * returns true if there exists a tile with the name provided
		 */
		return tiles.containsKey(name);
	}
	public Tile getTile(String tileName)
	{
		return tiles.get(tileName);
	}//end getTile

	public Tile getEmpty()
	{
		/**
		 * returns the special empty tile.
		 */
		return tiles.get("Empty");
	}	
	
	public Set<String> getTileList()
	{
		/**
		 * returns a set containing a list of all tile names
		 */
		return tiles.keySet();
	}//end getTileList
}//end class
