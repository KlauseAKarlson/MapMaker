package mapMaker;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

public class Tile extends ImageIcon {
	boolean fresh;//true if the tile is new/replaced since the last time the program was loaded. if true the image will be saved
	
	public Tile(String name, BufferedImage imageBuffer, boolean isFresh)
	{
		super(imageBuffer);
		this.setDescription(name);
		fresh=isFresh;
	}
	
	public String getName()
	{
		return this.getDescription();
	}
	public boolean isFresh()
	{
		return fresh;
	}

}
