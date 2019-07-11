package mapMaker;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class HexMapViewer extends MapViewer {

	private HexMap ActiveMap;
	private HexTileSet TSet;
	public HexMapViewer(HexMap m) {
		// TODO Auto-generated constructor stub
		super();
		ActiveMap=m;
		TSet=(HexTileSet)m.getTileSet();
	}

	public int ColumnAt(int x, int y)
	{
		/**
		 * provides the collumn of the tile occupying the map contianing the pixel with the provided coordinates
		 * returns -1 if not in a tile, hexes outside of map are garrenteed to be shown
		 */
		//first check if this will be easy or hard
		int tWidth=TSet.getWidth();
		int tHeight=TSet.getHeight();
		int col=-1;
		//Create a rectangle representing the top of an even rowed hex to the top of the one bellow it. Anything outside the hex will be ofset by half a hex
		int x2=x%(tWidth);
		int y2=y%(int)(tHeight*1.5);
		if (TSet.inHex(x2, y2))
		{
			col=x/tWidth-1;
		}else {
			col=(x-tWidth/2)/tWidth;
		}

		if (col<0 || col>=ActiveMap.getWidth())//return -1 if not in map
				col=-1;
		return col;
	}
	public int RowAt(int x, int y)
	{
		/**
		 * provides the collumn of the tile occupying the map contianing the pixel with the provided coordinates
		 * returns -1 if not in a tile
		 */
		int tWidth=TSet.getWidth();
		int tHeight=TSet.getHeight();
		//Create a rectangle representing the top of an even rowed hex to the top of the one bellow it. Anything outside the hex will be eitehr above or bellow
		int x2=x%(tWidth);
		int row=(y/(int)( tHeight*1.5) )*2;
		int y2=y%(int)(tHeight*1.5);
		if (TSet.inHex(x2, y2))
		{
			//do nothing
		}else {
			if (y2>tHeight/2)
			{
				row++;
			}else {
				row--;
			}
		}//end if in hex

		if (row<0 || row>=ActiveMap.getHeight())//return -1 if not in map
				row=-1;
		return row;
	}
	
	@Override
	public void paint(Graphics g)
	{
		//iterate through hexes
		int x,y; //pixel coordinates
		int yOfset=(TSet.getHeight()*3)/4;
		int xOfset=(TSet.getWidth()/2);
		for (int row=0;row<ActiveMap.getHeight();row++)
		{
			for (int col=0;col<ActiveMap.getWidth();col++)
			{
				y=row*yOfset;
				x=col*TSet.getWidth();
				if (row%2==1)
					x+=xOfset;
				Tile[] tiles=ActiveMap.getTiles(row, col);
				for (Tile t:tiles)
				{
					t.paintIcon(this, g, x, y);
				}//end layer loop
			}//end column loop
		}//end row loop
		
	}//override paint
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfPages() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

}
