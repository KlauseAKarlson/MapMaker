package mapMaker;

public class SquareMap extends Map{


	private SquareTileSet mapTileSet;
	
	public SquareMap(int width, int height)
	{
		/**
		 * creates a new blank map with desired height and width
		 */
		super(width,height);
		mapTileSet=new SquareTileSet(72,72);//default size is one inch under default print format
	}//end constructor
	public SquareMap(int width, int height, SquareTileSet t)
	{
		/**
		 * creates a new blank map with desired height and width
		 */
		super(width,height);
		mapTileSet=t;//use default size for now
	}//end constructor
		
	public SquareTileSet getTileSet()
	{
		return mapTileSet;
	}//end get tile set
	

	@Override
	public MapViewer getMapViewer() {
		return new SquareMapViewer(this);
	}
	@SuppressWarnings("static-access")
	@Override
	public String getMapType() {
		return this.squareMap;
	}
	
}//end map class
