package mapMaker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.*;

@SuppressWarnings("serial")
public class SquareMapViewer extends MapViewer implements MouseListener {

	private SquareMap ActiveMap;

	/**
	 * This class provides a JPanel to interact with the map
	 * Each tile is represented by a button containing an icon with shows its part of the map
	 * When given an action listener, it will call ActionPerformed whenever its buttons are pressed, with Col,Row as the action command
	 */
	
	SquareMapViewer(SquareMap m)
	{
		//initialize with appropriately sized gride layout
		super();
		this.setLayout( new GridLayout(m.getWidth(),m.getHeight()) );
		//initiate fields
		ActiveMap=m;
		//set width and height in pixels by multiplying number of tiles by tile dimension in pixels
		int width = ActiveMap.getWidth() * ActiveMap.getTileSet().getWidth();
		int height = ActiveMap.getHeight() * ActiveMap.getTileSet().getHeight();
		this.setPreferredSize(new Dimension(width, height));
		this.addMouseListener(this);
		this.setBackground(Color.WHITE);
	}//end constructor

	@Override
	public void paint(Graphics g)
	{
		//iterate through hexes
		super.paint(g);
		TileSet TSet=ActiveMap.getTileSet();
		int x,y; //pixel coordinates
		int yOfset=TSet.getHeight();
		int xOfset=TSet.getWidth();
		for (int row=0;row<ActiveMap.getHeight();row++)
		{
			for (int col=0;col<ActiveMap.getWidth();col++)
			{
				y=row*yOfset;
				x=col*xOfset;
				Tile[] tiles=ActiveMap.getTiles(col, row);
				for (Tile t:tiles)
				{
					t.paintIcon(this, g, x, y);
				}//end layer loop
			}//end column loop
		}//end row loop
		
	}//override paint

	public int ColumnAt(int x, int y)
	{
		return x / ActiveMap.getTileSet().getWidth();
	}
	public int RowAt(int x, int y)
	{
		return y / ActiveMap.getTileSet().getHeight();
	}
	



	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		/**
		 * generates pages of a printed map, page order is by column then row if the map requires more than one page
		 */
		//first calculate tiles per page, and then the number of pages needed to print the whole map
		SquareTileSet tset=ActiveMap.getTileSet();
		int tileWidth=tset.getWidth()+2;//tile width modified to include border
		int tileHeight=tset.getHeight()+2; 
		
		int pageWidthTiles=( (int) pf.getImageableWidth() )/(tileWidth);//page width in tiles
		int pageHeightTiles=( (int) pf.getImageableHeight() )/(tileHeight);

		int pagesWide=ActiveMap.getWidth()/pageWidthTiles;//how many pages wide the map is
		if (ActiveMap.getWidth()%pageWidthTiles>0) 
			pagesWide++;

		int pagesTall=ActiveMap.getHeight()/pageHeightTiles;//how many pages tall the map is
		if (ActiveMap.getHeight()%pageHeightTiles>0) 
			pagesTall++;
		int totalPages=pagesWide*pagesTall;
		//check if pave exists
		if (pageIndex>=totalPages)
		{
			return Printable.NO_SUCH_PAGE;//tell caller the page they asked for does not exist
		}else {
			//calculate page column and row
			int pageColumn=pageIndex%pagesWide;
			int pageRow=pageIndex/pagesWide;
			//calculate boundry indexes of tiles being printed
			int firstColumn=pageColumn*pageWidthTiles;
			int lastColumn=ActiveMap.getWidth()%pageWidthTiles;//is the last column relative to first column, rather than absolute, to simplify math later
			if (pageColumn==pagesWide-1)
			{
				lastColumn=ActiveMap.getWidth()%pageWidthTiles;
			}else {
				lastColumn=pageWidthTiles;
			}
			int firstRow=pageRow*pageHeightTiles;
			int lastRow;//relative to first collumn, rather than absolute, to simplify math laters
			if (pageRow==pagesTall-1)
			{
				lastRow=ActiveMap.getHeight()%pageHeightTiles;
			}else {
				lastRow=pageHeightTiles;
			}
			//fill in black background to create borders
		    Graphics2D g2d = (Graphics2D)g;
		    g2d.translate(pf.getImageableX(), pf.getImageableY());
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, (lastColumn*tileWidth), (lastRow*tileHeight) );
			//paint tiles onto page
			for (int row=0;row<lastRow;row++)
			{
				for (int col=0;col<lastColumn;col++)
				{
					//col and row are offsets from fist column and first row
					ActiveMap.paintTileAt(firstColumn+col, firstRow+row, //tile location on map
							1+col*tileWidth, 1+row*tileHeight, null, g2d);//paint location
				}//end column loop
			}//end row loop
			//print tiles
			return Printable.PAGE_EXISTS;//tell caller this page is part of the document and is done
		}//end if page exists
	}//end Printable.print
	@Override
	public int getNumberOfPages() {
		SquareTileSet tset=ActiveMap.getTileSet();
		PageFormat pf=PrinterJob.getPrinterJob().defaultPage();
		int tileWidth=tset.getWidth()+2;//tile width modified to include border
		int tileHeight=tset.getHeight()+2; 
		
		int pageWidthTiles=( (int) pf.getImageableWidth() )/(tileWidth);//page width in tiles
		int pageHeightTiles=( (int) pf.getImageableHeight() )/(tileHeight);

		int pagesWide=ActiveMap.getWidth()/pageWidthTiles;//how many pages wide the map is
		if (ActiveMap.getWidth()%pageWidthTiles>0) 
			pagesWide++;

		int pagesTall=ActiveMap.getHeight()/pageHeightTiles;//how many pages tall the map is
		if (ActiveMap.getHeight()%pageHeightTiles>0) 
			pagesTall++;
		return pagesWide*pagesTall;
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
		// do nothing
		
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
		// do nothingb
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// Tdo nothing
		
	}
	
}//end map viewer class
