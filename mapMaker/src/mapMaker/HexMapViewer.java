package mapMaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class HexMapViewer extends MapViewer implements MouseListener {

	private HexMap ActiveMap;
	private HexTileSet TSet;
	public HexMapViewer(HexMap m) {
		super();
		ActiveMap=m;
		TSet=(HexTileSet)m.getTileSet();
		this.setPreferredSize(
				new Dimension(ActiveMap.getWidth()*TSet.getWidth()+TSet.getWidth()/2,
						((ActiveMap.getHeight()*3+1 )*TSet.getHeight()  )/4) 
				);
		this.addMouseListener(this);
		this.setBackground(Color.WHITE);
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
			col=x/tWidth;
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
		int row=2 * (int)(y/(tHeight*1.5) );
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
		super.paint(g);
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
				Tile[] tiles=ActiveMap.getTiles(col, row);
				for (Tile t:tiles)
				{
					t.paintIcon(this, g, x, y);
				}//end layer loop
			}//end column loop
		}//end row loop
		
	}//override paint
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		//I'm just cutting off the tiles at the edge of the page
		int PMapWidth=ActiveMap.getWidth()*TSet.getWidth();//mapHeight in pixels
		int PMapHeight=(ActiveMap.getHeight()*TSet.getHeight()*3+1)/4;
		//determin the number of pages wide and tall
		int pagesWide=(int)pageFormat.getImageableWidth()/PMapWidth;
		if ((int)pageFormat.getImageableWidth()%PMapWidth !=0)
			pagesWide++;
		int pagesTall=(int)pageFormat.getImageableHeight()/PMapHeight;
		if ((int)pageFormat.getImageableHeight()%PMapHeight !=0)
			pagesTall++;
		int totalPages=pagesWide*pagesTall;
		//check if pave exists
		if (pageIndex>=totalPages)
		{
			return Printable.NO_SUCH_PAGE;//tell caller the page they asked for does not exist
		}else {
			int pageCol=pageIndex%pagesWide;
			int pageRow=pageIndex/pagesWide;
    		BufferedImage mapCopy=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    		Graphics g = mapCopy.getGraphics();
    		paint(g);
    		g.dispose();
    		BufferedImage subMap=mapCopy.getSubimage((int)pageFormat.getImageableWidth()*pageCol, 
    				(int)pageFormat.getImageableHeight()*pageRow, 
    				(int)pageFormat.getImageableWidth(), 
    				(int)pageFormat.getImageableHeight());
			graphics.drawImage(subMap, (int)pageFormat.getImageableX(), (int)pageFormat.getImageableY(), null);
			return Printable.PAGE_EXISTS;
		}

	}//end print 

	@Override
	public int getNumberOfPages() {
		PageFormat pageFormat=PrinterJob.getPrinterJob().defaultPage();
		int PMapWidth=ActiveMap.getWidth()*TSet.getWidth();//mapHeight in pixels
		int PMapHeight=(ActiveMap.getHeight()*TSet.getHeight()*3+1)/4;
		//determin the number of pages wide and tall
		int PagesWide=(int)pageFormat.getImageableWidth()/PMapWidth;
		if ((int)pageFormat.getImageableWidth()%PMapWidth !=0)
			PagesWide++;
		int PagesTall=(int)pageFormat.getImageableHeight()/PMapHeight;
		if ((int)pageFormat.getImageableHeight()%PMapHeight !=0)
			PagesTall++;
		return PagesWide*PagesTall;
	}

	@Override
	public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
		return PrinterJob.getPrinterJob().defaultPage();
	}

	@Override
	public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
		return this;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//identify selected tile and report to action listener
		int x=e.getX();
		int y=e.getY();
		this.selectedColumn=this.ColumnAt(x, y);
		this.selectedRow=this.RowAt(x, y);
		if (!Listeners.isEmpty())
		{
			ActionEvent e2=new ActionEvent(this,ActionEvent.ACTION_FIRST ,x+","+y);
			for (ActionListener l:Listeners)
			{
				l.actionPerformed(e2);
			}
		}
	}//end mouse pressed

	@Override
	public void mouseReleased(MouseEvent e) {
		//do nothing		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//do nothing
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//do nothing
		
	}

}
