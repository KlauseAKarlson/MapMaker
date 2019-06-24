package mapMaker;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

public class Tile {
	private String tileName;
	private ImageIcon image;
	boolean fresh;//true if the tile is new/replaced since the last time the program was loaded. if true the image will be saved
	
	public Tile(String name, BufferedImage imageBuffer, boolean isFresh)
	{
		tileName=name;
		image=new ImageIcon(imageBuffer);
		fresh=isFresh;
	}
	
	public String getName()
	{
		return tileName;
	}
	public ImageIcon getIcon()
	{
		return image;
	}
	public boolean isFresh()
	{
		return fresh;
	}

}
