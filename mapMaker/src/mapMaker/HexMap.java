package mapMaker;

public class HexMap extends Map {

	private HexTileSet mapTileSet;
	
	public HexMap(int width, int height)
	{
		/**
		 * creates a new blank map with desired height and width
		 */
		super(width,height);
		mapTileSet=new HexTileSet(36);//default size is one inch under default print format
	}//end constructor
	public HexMap(int width, int height, HexTileSet t)
	{
		super(width,height);
		mapTileSet=t;
	}
		
	@Override
	public TileSet getTileSet() {
		return mapTileSet;
	}


	@Override
	public MapViewer getMapViewer() {
		return new HexMapViewer(this);
	}
	@SuppressWarnings("static-access")
	@Override
	public String getMapType() {
		return this.hexMap;
	}

	
}
