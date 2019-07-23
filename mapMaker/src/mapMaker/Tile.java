package mapMaker;

import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Tile extends ImageIcon {
	private boolean foreground;
	
	public Tile(String name, BufferedImage imageBuffer, boolean transparent)
	{
		super(imageBuffer);
		this.setDescription(name);
		foreground=transparent;
	}
	
	public String getName()
	{
		return this.getDescription();
	}
	public boolean foreground()
	{
		return foreground;
	}

}
