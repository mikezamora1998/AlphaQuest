package org.alphaquest.java.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.alphaquest.java.Game;
import org.alphaquest.java.delegate.GameObject;
import org.alphaquest.java.delegate.LevelElements;
import org.alphaquest.java.math.Rectangle;
import org.alphaquest.java.render.RenderHandler;

/**
 * Handles Map functions, Collisions, and Tiles.
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

	private Block[][] blocks;
	private int blockStartX, blockStartY;

	private int blockWidth = 6;
	private int blockHeight = 6;
	private int blockPixelWidth = blockWidth * 16;
	private int blockPixelHeight = blockHeight * 16;
	
	private int numLayers;

	private LevelElements level;

	private Game game;
	
	Rectangle tileRectangle = new Rectangle();
	
	MappedTile tileLeft;
	MappedTile tileRight;
	MappedTile tileTop;
	MappedTile tileBottom;
	
	//TODO: Talking Points
	/**
	 * Map constructor.
	 * <p>
	 * Scans mapFile for fill and placement of tiles.
	 * @param mapFile <b>File</b>
	 * @param tileSet <b>Tiles</b>
	 * @see Map
	 */
	public Map(File mapFile, Tiles tileSet, LevelElements level, Game game) {
		this.level = level;
		this.game = game;
		this.mapFile = mapFile;
		this.tileSet = tileSet;
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		try {
			Scanner scanner = new Scanner(mapFile);
			int currentLine = 0;
			while(scanner.hasNextLine()) {
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
					if(splitString.length >= 4) {
						MappedTile mappedTile = new MappedTile(Integer.parseInt(splitString[0]),
															   Integer.parseInt(splitString[1]),
															   Integer.parseInt(splitString[2]),
															   Integer.parseInt(splitString[3]));
						if(mappedTile.x < minX)
							minX = mappedTile.x;
						if(mappedTile.y < minY)
							minY = mappedTile.y;
						if(mappedTile.x > maxX)
							maxX = mappedTile.x;
						if(mappedTile.x > maxY)
							maxY = mappedTile.y;

						if(numLayers <= mappedTile.layer)
							numLayers = mappedTile.layer + 1;

						mappedTiles.add(mappedTile);
					}
				} else {
					comments.put(currentLine, line);
				}
				currentLine++;
			}

			if(mappedTiles.size() == 0) {
				minX = -blockWidth;
				minY = -blockHeight;
				maxX = blockWidth;
				maxY = blockHeight;
			} 

			blockStartX = minX;
			blockStartY = minY;
			int blockSizeX = (maxX + blockWidth) - minX;
			int blockSizeY = (maxY + blockHeight) - minY;
			blocks = new Block[blockSizeX][blockSizeY];

			//Loop through all mappedTiles in the entire level and add them to the blocks.
			for(int i = 0; i < mappedTiles.size(); i++) {
				MappedTile mappedTile = mappedTiles.get(i);
				int blockX = (mappedTile.x - minX)/blockWidth;
				int blockY = (mappedTile.y - minY)/blockHeight;
				assert(blockX >= 0 && blockX < blocks.length && blockY >= 0 && blockY < blocks[0].length);

				if(blocks[blockX][blockY] == null)
					blocks[blockX][blockY] = new Block();

				blocks[blockX][blockY].addTile(mappedTile);
			}
			scanner.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public MappedTile getTile(int layer, int tileX, int tileY) {
		int blockX = (tileX - blockStartX)/blockWidth;
		int blockY = (tileY - blockStartY)/blockHeight;

		if(blockX < 0 || blockX >= blocks.length || blockY < 0 || blockY >= blocks[0].length)
			return null;

		Block block = blocks[blockX][blockY];

		if(block == null)
			return null;

		return block.getTile(layer, tileX, tileY);
	}
	
	public Tiles getTileSet() {
		return tileSet;
	}
	
	public MappedTile getCollisionTile(Collision collision) {
		MappedTile tile = new MappedTile();
		switch(collision) {
		case BOTTOM:
			tile = tileBottom;
			break;
		case LEFT:
			tile = tileLeft;
			break;
		case RIGHT:
			tile = tileRight;
			break;
		case TOP:
			tile = tileTop;
			break;
		default:
			break;
		}
		return tile;
	}
	
	public void setCollisionTile(MappedTile tile, Collision collision) {
		switch(collision) {
		case BOTTOM:
			tileBottom = tile;
			break;
		case LEFT:
			tileLeft = tile;
			break;
		case RIGHT:
			tileRight = tile;
			break;
		case TOP:
			tileTop = tile;
			break;
		default:
			break;
		}
	}
	
	public Collision checkCollision(Rectangle rect, int layer, int xZoom, int yZoom, Collision collision) {
		
		//System.out.println("rect.x: " + rect.x + ", rect.y: " + rect.y + ", rect.w: " + rect.w + ", rect.h: " + rect.h);
		int w = 16;
		
		int tileWidth = 16;
		
		int tileWidthZoom = tileWidth * xZoom;
		int tileHeightZoom = tileWidth * yZoom;

		//Coordinates to check all tiles in a radius of 4 around the player
		int topLeftX = (rect.x - (tileWidth * 4))/tileWidthZoom;
		int topLeftY = (rect.y - (tileWidth * 4))/tileHeightZoom;
		int bottomRightX = (rect.x + rect.w + (tileWidth * 4))/tileWidthZoom;
		int bottomRightY = (rect.y + rect.h + (tileWidth * 4))/tileHeightZoom;

		//System.out.println("topLeftX: " + topLeftX + ", topLeftY: " + topLeftY + ", bottomRightX: " + bottomRightX + ", bottomRightY: " + bottomRightY);
		//Starting at the top left tile and going to the bottom right
		for(int x = topLeftX; x < bottomRightX; x++)
			for(int y = topLeftY; y < bottomRightY; y++) {
				//System.out.println("X: " + x + ", Y: " + y);
				MappedTile tile = getTile(layer, x, y);
				
				
				if(tile != null) {
					int collisionType = tileSet.collisionType(tile.id);
					
					//System.out.println("checking collision on tile: " + tileSet.tileName(tile.id) + ", with " + collision + " Collision");
					
					//Full tile collision
					if(collisionType == 0) {
						
						//System.out.println("collision id: " + collisionType);
						
						tileRectangle = new Rectangle(tile.x*tileWidthZoom, tile.y*tileHeightZoom, tileWidth, w);
						if(tileRectangle.intersects(rect) && collision == Collision.BOTTOM) {
							tileRectangle.generateGraphics(0xffffffff);
							System.out.println("collision with Bottom");
							tileBottom = tile;
							return Collision.BOTTOM;
						}

						//Left of tile collision
						tileRectangle = new Rectangle(tile.x*tileWidthZoom, tile.y*tileHeightZoom, w, tileWidth);
						if(tileRectangle.intersects(rect) && collision == Collision.RIGHT) {
							tileRectangle.generateGraphics(0xffffffff);
							System.out.println("collision with Right");
							tileRight = tile;
							return Collision.RIGHT;
						}

						//Bottom of tile collision
						tileRectangle = new Rectangle(tile.x*tileWidthZoom, tile.y*tileHeightZoom + tileHeightZoom, tileWidth, w);
						Rectangle adjustedRect = new Rectangle(rect.x, rect.y + rect.h, rect.w, 1);
						if(tileRectangle.intersects(adjustedRect) && collision == Collision.TOP) {
							tileRectangle.generateGraphics(0xffffffff);
							System.out.println("collision with Top");
							tileTop = tile;
							return Collision.TOP;
						}

						//Right of tile collision
						tileRectangle = new Rectangle(tile.x*tileWidthZoom + tileWidthZoom, tile.y*tileHeightZoom, w, tileWidth);
						if(tileRectangle.intersects(rect) && collision == Collision.LEFT) {
							tileRectangle.generateGraphics(0xffffffff);
							System.out.println("collision with Left");
							tileLeft = tile;
							return Collision.LEFT;
						}
						
					//Top of tile collision
					} else if(collisionType == 1) {
						tileRectangle = new Rectangle(tile.x*tileWidthZoom, tile.y*tileHeightZoom, tileWidth, w);
						if(tileRectangle.intersects(rect) && collision == Collision.BOTTOM) {
							tileRectangle.generateGraphics(0xffffffff);
							System.out.println("collision with Bottom");
							tileBottom = tile;
							return Collision.BOTTOM;
						}

					//Left of tile collision
					} else if(collisionType == 2) {
						tileRectangle = new Rectangle(tile.x*tileWidthZoom, tile.y*tileHeightZoom, w, tileWidth);
						if(tileRectangle.intersects(rect) && collision == Collision.RIGHT) {
							tileRectangle.generateGraphics(0xffffffff);
							System.out.println("collision with Right");
							tileRight = tile;
							return Collision.RIGHT;
						}

					//Bottom of tile collision
					} else if (collisionType == 3) {
						tileRectangle = new Rectangle(tile.x*tileWidthZoom, tile.y*tileHeightZoom + tileHeightZoom, tileWidth, w);
						Rectangle adjustedRect = new Rectangle(rect.x, rect.y + rect.h, rect.w, 1);
						if(tileRectangle.intersects(adjustedRect) && collision == Collision.TOP) {
							tileRectangle.generateGraphics(0xffffffff);
							System.out.println("collision with Top");
							tileTop = tile;
							return Collision.TOP;
						}

					//Right of tile collision
					} else if (collisionType == 4) {
						tileRectangle = new Rectangle(tile.x*tileWidthZoom + tileWidthZoom, tile.y*tileHeightZoom, w, tileWidth);
						if(tileRectangle.intersects(rect) && collision == Collision.LEFT) {
							tileRectangle.generateGraphics(0xffffffff);
							System.out.println("collision with Left");
							tileLeft = tile;
							return Collision.LEFT;
						}
						
					//Ends Level
					} else if (collisionType == 5) {
						Rectangle tileRectangle = new Rectangle(tile.x*tileWidthZoom + tileWidthZoom, tile.y*tileHeightZoom, w, tileWidth);
						if(tileRectangle.intersects(rect)) {
							level.endLevel();
							return Collision.END;
						}
					}
				}
			}
		return Collision.NULL;
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
	public void setTile(int layer, int tileX, int tileY, int tileID) {
		if(layer >= numLayers)
			numLayers = layer + 1;

		for(int i = 0; i < mappedTiles.size(); i++) {
			MappedTile mappedTile = mappedTiles.get(i);
			if(mappedTile.x == tileX && mappedTile.y == tileY) {
				mappedTile.id = tileID;
				return;
			}
		}
		
		MappedTile mappedTile = new MappedTile(layer, tileID, tileX, tileY);
		mappedTiles.add(mappedTile);

		//Add to blocks
		int blockX = (tileX - blockStartX)/blockWidth;
		int blockY = (tileY - blockStartY)/blockHeight;
		if(blockX >= 0 && blockY >= 0 && blockX < blocks.length && blockY < blocks[0].length) {
			if(blocks[blockX][blockY] == null)
				blocks[blockX][blockY] = new Block();

			blocks[blockX][blockY].addTile(mappedTile);
		} else {
			int newMinX = blockStartX;
			int newMinY = blockStartY;
			int newLengthX = blocks.length;
			int newLengthY = blocks[0].length;

			if(blockX < 0) {
				int increaseAmount = blockX * -1;
				newMinX = blockStartX - blockWidth*increaseAmount;
				newLengthX = newLengthX + increaseAmount;
			} else if(blockX >= blocks.length)
				newLengthX = blocks.length + blockX;

			if(blockY < 0) {
				int increaseAmount = blockY * -1;
				newMinY = blockStartY - blockHeight*increaseAmount;
				newLengthY = newLengthY + increaseAmount;
			} else if(blockY >= blocks[0].length)
				newLengthY = blocks[0].length + blockY;

			Block[][] newBlocks = new Block[newLengthX][newLengthY];

			for(int x = 0; x < blocks.length; x++)
				for(int y = 0; y < blocks[0].length; y++)
					if(blocks[x][y] != null) {
						newBlocks[x + (blockStartX - newMinX)/blockWidth][y + (blockStartY - newMinY)/blockHeight] = blocks[x][y];
					}

			blocks = newBlocks;
			blockStartX = newMinX;
			blockStartY = newMinY;
			blockX = (tileX - blockStartX)/blockWidth;
			blockY = (tileY - blockStartY)/blockHeight;
			if(blocks[blockX][blockY] == null)
				blocks[blockX][blockY] = new Block();
			blocks[blockX][blockY].addTile(mappedTile);
		}
	}
	
	//TODO: Talking Points Editor
	/**
	 * Removes a tileID at a specific <b>X</b> and <b>Y</b> position.
	 * @param tileX <b>int</b>
	 * @param tileY <b>int</b>
	 * @see #setTile(int tileX, int tileY, int tileID)
	 */
	public void removeTile(int layer, int tileX, int tileY) {
		for(int i = 0; i < mappedTiles.size(); i++) {
			MappedTile mappedTile = mappedTiles.get(i);
			if(mappedTile.layer == layer && mappedTile.x == tileX && mappedTile.y == tileY) {
				mappedTiles.remove(i);

				//Remove from block
				int blockX = (tileX - blockStartX)/blockWidth;
				int blockY = (tileY - blockStartY)/blockHeight;
				assert(blockX >= 0 && blockY >= 0 && blockX < blocks.length && blockY < blocks[0].length);
				blocks[blockX][blockY].removeTile(mappedTile);
			}
		}
	}
	
	/**
	 * Saves all modified <b>tileID</b> on the screen.
	 * @see #mappedTiles
	 */
	public void saveMap() {
		String[] file = game.saveMap();
		String filePath = file[1] + "\\" + file[0];
		System.out.println(filePath);
		
		File newMap = new File(filePath);
		
		try {
			int currentLine = 0;
			if(newMap.exists()) {
				newMap.delete();
			}
			newMap.createNewFile();
			
			PrintWriter printWriter = new PrintWriter(newMap);
			
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
				printWriter.println(tile.layer + "," + tile.id + "," + tile.x + "," + tile.y);
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
	public void render(RenderHandler renderer, GameObject[] objects, int xZoom, int yZoom) {
		int tileWidth = 16 * xZoom;
		int tileHeight = 16 * yZoom;

		if(fillTileID >= 0) {
			Rectangle camera = renderer.getCamera();

			for(int y = camera.y - tileHeight - (camera.y % tileHeight); y < camera.y + camera.h; y+= tileHeight) {
				for(int x = camera.x - tileWidth - (camera.x % tileWidth); x < camera.x + camera.w; x+= tileWidth) {
					tileSet.renderTile(fillTileID, renderer, x, y, xZoom, yZoom);
				}
			}
		}

		for(int layer = 0; layer < numLayers; layer++) {
			//System.out.println("camX: "+renderer.getCamera().x+ ", camY: "+renderer.getCamera().y+", camW: "+renderer.getCamera().w+", camH: "+renderer.getCamera().h);

			int topLeftX = renderer.getCamera().x;
			int topLeftY = renderer.getCamera().y;
			int bottomRightX = renderer.getCamera().x + renderer.getCamera().w;
			int bottomRightY = renderer.getCamera().y + renderer.getCamera().h;

			int leftBlockX = (topLeftX/tileWidth - blockStartX - 16)/blockWidth;
			int blockX = leftBlockX;
			int blockY = (topLeftY/tileHeight - blockStartY - 16)/blockHeight;
			int pixelX = topLeftX;
			int pixelY = topLeftY;

			while(pixelX <= bottomRightX && pixelY < bottomRightY) {
				
				//System.out.println("BlockX: " + blockX + ", BlockY: " + blockY + ", BlocksLength: " + blocks.length + ", [0]: " + blocks[0].length);
				if(blockX >= 0 && blockY >= 0 && blockX < blocks.length && blockY < blocks[0].length) {
					if(blocks[blockX][blockY] != null)
						//System.out.println("render"+ layer);
						blocks[blockX][blockY].render(renderer, layer, tileWidth, tileHeight, xZoom, yZoom);
				}

				blockX++;
				pixelX += blockPixelWidth;

				if(pixelX > bottomRightX) {
					pixelX = topLeftX;
					blockX = leftBlockX;
					blockY++;
					pixelY += blockPixelHeight;
					if(pixelY > bottomRightY)
						break;
				}
			}

			for(int i = 0; i < objects.length; i++)
				if(objects[i].getLayer() == layer)
					objects[i].render(renderer, xZoom, yZoom);
				else if(objects[i].getLayer() + 1 == layer) {
					Rectangle rect = objects[i].getRectangle();

					int tileBelowX = rect.x/tileWidth;
					int tileBelowX2 = (int) Math.floor((rect.x + rect.w/2*xZoom*1.0)/tileWidth);
					int tileBelowX3 = (int) Math.floor((rect.x + rect.w*xZoom*1.0)/tileWidth);

					int tileBelowY = (int) Math.floor((rect.y + rect.h*yZoom*1.0)/tileHeight);

					if(getTile(layer, tileBelowX, tileBelowY) == null && 
					   getTile(layer, tileBelowX2, tileBelowY) == null && 
					   getTile(layer, tileBelowX3, tileBelowY) == null)
						objects[i].render(renderer, xZoom, yZoom);
				}
		}

		for(int i = 0; i < objects.length; i++)
			if(objects[i].getLayer() == Integer.MAX_VALUE)
				objects[i].render(renderer, xZoom, yZoom);
		
		//renderer.renderRectangle(tileRectangle, xZoom, yZoom, false);
	}
	
	//Block represents a 6/6 block of tiles
	@SuppressWarnings("unchecked")
	private class Block {
		public ArrayList<MappedTile>[] mappedTilesByLayer;

		public Block() {
			mappedTilesByLayer = new ArrayList[numLayers];
			for(int i = 0; i < mappedTilesByLayer.length; i++)
				mappedTilesByLayer[i] = new ArrayList<MappedTile>();
		}

		public void render(RenderHandler renderer, int layer, int tileWidth, int tileHeight, int xZoom, int yZoom) {
			if(mappedTilesByLayer.length > -1) {
				ArrayList<MappedTile> mappedTiles = mappedTilesByLayer[layer];
				for(int tileIndex = 0; tileIndex < mappedTiles.size(); tileIndex++) {
					MappedTile mappedTile = mappedTiles.get(tileIndex);
					//System.out.println("render"+ tileIndex);
					tileSet.renderTile(mappedTile.id, renderer, mappedTile.x * tileWidth, mappedTile.y * tileHeight, xZoom, yZoom);
				}
			}
		}

		public void addTile(MappedTile tile) {
			if(mappedTilesByLayer.length <= tile.layer) {
				ArrayList<MappedTile>[] newTilesByLayer = new ArrayList[tile.layer + 1];

				int i = 0;
				for(i = 0; i < mappedTilesByLayer.length; i++)
					newTilesByLayer[i] = mappedTilesByLayer[i];
				for(; i < newTilesByLayer.length; i++)
					newTilesByLayer[i] = new ArrayList<MappedTile>();

				mappedTilesByLayer = newTilesByLayer;
			}
			mappedTilesByLayer[tile.layer].add(tile);
		}

		public void removeTile(MappedTile tile) {
			mappedTilesByLayer[tile.layer].remove(tile);
		}

		public MappedTile getTile(int layer, int tileX, int tileY) {
			for(MappedTile tile : mappedTilesByLayer[layer]) {
				if(tile.x == tileX && tile.y == tileY)
					return tile;
			}
			return null;
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
	class MappedTile {
		
		public int layer;
		
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
		public MappedTile(int layer, int id, int x, int y){
			this.layer = layer;
			this.id = id;
			this.x = x;
			this.y = y;
		}

		public MappedTile() {}
	}
}
