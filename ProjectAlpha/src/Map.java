import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Handles Map functions.
 * <p>
 * @see <b>{@literal Constructor: }</b>
 * <p> 
 * {@link #Map(File mapFile, Tiles tileSet)}
 * <p>
 * <b>{@literal Methods: }</b>
 * <p>
 * {@link #setTile(int tileX, int tileY, int tileID)}
 * <p>
 * {@link #removeTile(int tileX, int tileY)}
 * <p>
 * {@link #saveMap()}
 * <p>
 * {@link #setTile(int tileX, int tileY, int tileID)}
 * @author Michael, David, Brandon
 */
public class Map {
	
	/**
	 * <b>Tiles</b> object - used in rendering.
	 * @see #render(RenderHandler renderer, int xZoom, int yZoom)
	 */
	private Tiles tileSet;
	
	/**
	 * Used to determine if the map is to be filled.
	 * <p>
	 * <b>tileID</b> = <b>fillTileID</b>.
	 * <p>
	 * -1 by default
	 * @see #Map(File mapFile, Tiles tileSet)
	 */
	private int fillTileID = -1;
	
	/**
	 * ArrayList of MappedTile objects.
	 * @see #Map(File mapFile, Tiles tileSet)
	 */
	private ArrayList<MappedTile> mappedTiles = new ArrayList<MappedTile>();
	
	/**
	 * HashMap for comments in txt file.
	 * @see #Map(File mapFile, Tiles tileSet)
	 */
	private HashMap<Integer, String> comments = new HashMap<Integer, String>();
	
	/**
	 * File object = map txt file.
	 * @see #Map(File mapFile, Tiles tileSet)
	 */
	private File mapFile;
	
	/**
	 * Map constructor.
	 * <p>
	 * Scans mapFile for fill and placement of tiles.
	 * @param mapFile <b>File</b>
	 * @param tileSet <b>Tiles</b>
	 * @see Map
	 */
	public Map(File mapFile, Tiles tileSet) {
		this.tileSet = tileSet;
		this.mapFile = mapFile;
		
		try {
			Scanner scanner = new Scanner(mapFile);
			int currentLine = 0;
			
			while(scanner.hasNextLine()){
				
				//read each line and create a tile
				String line = scanner.nextLine();
				if(!line.startsWith("//")) {
					
					if(line.contains(":")) {
						String[] splitString = line.split(":");
						
						if(splitString[0].equalsIgnoreCase("Fill")) {
							fillTileID = Integer.parseInt(splitString[1]);
							continue;
						}
					}
					
					String[] splitString = line.split(",");
					
					if(splitString.length >= 3) {
						MappedTile mappedTile = new MappedTile(Integer.parseInt(splitString[0]), Integer.parseInt(splitString[1]), Integer.parseInt(splitString[2]));
						mappedTiles.add(mappedTile);
					}
				}else {
					comments.put(currentLine, line);
				}
				currentLine++;
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets a <b>tileID</b> at a specific <b>X</b> and <b>Y</b> position.
	 * @param tileX <b>int</b>
	 * @param tileY <b>int</b>
	 * @param tileID <b>int</b>
	 * @see {@link #removeTile(int tileX, int tileY)}
	 * <p>
	 * {@link MappedTile}
	 */
	public void setTile(int tileX, int tileY, int tileID) {
		boolean foundTile = false;
		
		for(int i = 0; i < mappedTiles.size(); i++) {
			MappedTile mappedTile = mappedTiles.get(i);
			if(mappedTile.x == tileX && mappedTile.y == tileY) {
				mappedTile.id = tileID;
				foundTile = true;
				break;
			}
		}
		
		if (!foundTile) {
			mappedTiles.add(new MappedTile(tileID, tileX, tileY));
		}
	}
	
	/**
	 * Removes a tileID at a specific <b>X</b> and <b>Y</b> position.
	 * @param tileX <b>int</b>
	 * @param tileY <b>int</b>
	 * @see #setTile(int tileX, int tileY, int tileID)
	 */
	public void removeTile(int tileX, int tileY) {
		for(int i = 0; i < mappedTiles.size(); i++) {
			MappedTile mappedTile = mappedTiles.get(i);
			if(mappedTile.x == tileX && mappedTile.y == tileY) {
				mappedTiles.remove(i);
			}
		}
	}
	
	/**
	 * Saves all modified <b>tileID</b> on the screen.
	 * @see #mappedTiles
	 */
	public void saveMap() {
		try {
			int currentLine = 0;
			if(mapFile.exists()) {
				mapFile.delete();
			}
			mapFile.createNewFile();
			
			PrintWriter printWriter = new PrintWriter(mapFile);
			
			if(fillTileID >= 0) {
				
				if(comments.containsKey(currentLine)) {
					printWriter.println(comments.get(currentLine));
					currentLine++;
				}
				
				printWriter.println("Fill:" + fillTileID);
			}
			
			for(int i = 0; i < mappedTiles.size(); i++) {
				if(comments.containsKey(currentLine)) {
					printWriter.println(comments.get(currentLine));
				}
				
				MappedTile tile = mappedTiles.get(i);
				printWriter.println(tile.id + "," + tile.x + "," + tile.y);
				
				currentLine++;
			}
			
			printWriter.close();
		}catch(java.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Renders map to the Jframe.
	 * @param renderer <b>RenderHandler</b>
	 * @param xZoom <b>int</b>
	 * @param yZoom <b>int</b>
	 */
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		int tileWidth = 16 * xZoom;
		int tileHeight = 16 * yZoom;
		//System.out.println(mappedTiles.size());
		
		if(fillTileID >=0) {
			
			Rectangle camera = renderer.getCamera();
			
			for(int y = camera.y - tileHeight - (camera.y % tileHeight); y < camera.y + camera.h; y += tileHeight) {
				for(int x = camera.x - tileWidth - (camera.x % tileWidth); x < camera.x + camera.w; x += tileWidth) {
					tileSet.renderTile(fillTileID, renderer, x, y, xZoom, yZoom);
				}
			}
		}
		for(int tileIndex = 0; tileIndex < mappedTiles.size(); tileIndex++) {
			MappedTile mappedTile = mappedTiles.get(tileIndex);
			
			tileSet.renderTile(mappedTile.id, renderer, mappedTile.x * tileWidth, mappedTile.y * tileHeight, xZoom, yZoom);
		}
	}
	
	/**
	 * Tile ID in the tileSet.
	 * <p>
	 * Nested class inside Map, used to construct MappedTile objects.
	 * @author Michael, David, Brandon
	 * @see <b>{@literal Constructor: }</b>
	 * <p> 
	 * {@link #MappedTile(int id, int x, int y)}
	 * <p>
	 * <b>{@literal Public Variables: }</b>
	 * <p>
	 * <b>int </b> {@link #id}
	 * <p>
	 * <b>int </b> {@link #x}
	 * <p>
	 * <b>int </b> {@link #y}
	 */
	class MappedTile{
		
		/**
		 * Public variable of MappedTile.
		 * <p>
		 * Used to store tile id's.
		 * @see MappedTile
		 */
		public int id;
		
		/**
		 * Public variable of MappedTile.
		 * <p>
		 * Used to store tile <b>X</b> posistion's.
		 * @see MappedTile
		 */
		public int x;
		
		/**
		 * Public variable of MappedTile.
		 * <p>
		 * Used to store tile <b>Y</b> posistion's.
		 * @see MappedTile
		 */
		public int y;
		
		/**
		 * MappedTile constructor.
		 * @param id <b>int</b>
		 * @param x <b>int</b>
		 * @param y <b>int</b>
		 * @see MappedTile
		 */
		public MappedTile(int id, int x, int y) {
			this.id = id;
			this.x = x;
			this.y = y;
		}
	}
}
