package org.alphaquest.java.math;

import org.alphaquest.java.Game;

/**
 * Rectangle Class. Handles Rectangle rendering and Intersection.
 * @see {@literal Constructors: }
 * <p>
 * {@link #Rectangle(int x, int y, int w, int h)}
 * <p>
 * {@link #Rectangle()}
 * <p>
 * {@literal Methods: }
 * <p>
 * {@link #generateGraphics(int color)}
 * <p>
 * {@link #generateGraphics(int borderWidth, int color)}
 * <p>
 * @author Michael, David, Brandon
 */
public class Rectangle {
	
	/**
	 * <b>X</b> position of a rectangle object.
	 * @see Rectangle
	 */
	public int x;
	
	/**
	 * <b>Y</b> position of a rectangle object.
	 * @see Rectangle
	 */
	public int y;
	
	/**
	 * <b>Width</b> of a rectangle object.
	 * @see Rectangle
	 */
	public int w;
	
	/**
	 * <b>Height</b> position of a rectangle object.
	 * @see Rectangle
	 */
	public int h;
	
	/**
	 * Int array of pixels
	 * @see Rectangle
	 */
	private int[] pixels;
	
	/**
	 * Rectangle constructor that accepts <b>X position, Y position, Width, and Height</b>.
	 * @param x <b>int</b>
	 * @param y <b>int</b>
	 * @param w <b>int</b>
	 * @param h <b>int</b>
	 * @see #Rectangle()
	 */
	public Rectangle(int x, int y, int w, int h){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	/**
	 * Blank Rectangle constructor that sets int <b>(x, y, w, h)</b> to 0.
	 * @see #Rectangle(int x, int y, int w, int h)
	 */
	public Rectangle(){
		this(0,0,0,0);
	}
	
	//TODO: Talking Points
	public boolean intersects(Rectangle otherRectangle) {
		if(x > otherRectangle.x + otherRectangle.w || otherRectangle.x > x + w) {
			return false;
		}
		
		if(y > otherRectangle.y + otherRectangle.h || otherRectangle.y > y + h) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Generates a rectangle by accepting a <b>color</b>.
	 * @param color <b>int</b>
	 * @see #generateGraphics(int borderWidth, int color)
	 */
	public void generateGraphics(int color) {
		pixels = new int[w*h];
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				pixels[x + y * w] = color;
			}
		}
	}
	
	/**
	 * Generates a rectangle with borders by accepting a <b>borderWidth</b> and <b>color</b>.
	 * @param borderWidth <b>int</b>
	 * @param color	<b>int</b>
	 * @see #generateGraphics(int color)
	 */
	public void generateGraphics(int borderWidth, int color) {
		pixels = new int[w*h];
		
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = Game.alpha;
		}
		
		//top 
		for(int y = 0; y < borderWidth; y++) {
			for(int x = 0; x < w; x++) {
				pixels[x + y * w] = color;
			}
		}
		
		//left
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < borderWidth; x++) {
				pixels[x + y * w] = color;
			}
		}
		
		//right
		for(int y = 0; y < h; y++) {
			for(int x = w - borderWidth; x < w; x++) {
				pixels[x + y * w] = color;
			}
		}
		
		//bottom
		for(int y = h - borderWidth; y < h; y++) {
			for(int x = 0; x < w; x++) {
				pixels[x + y * w] = color;
			}
		}
	}
	
	/**
	 * Get method for returning pixels.
	 * @return <b>pixels int[]</b>
	 */
	public int[] getPixels() {
		if(pixels != null) {
			return pixels;
		} else {
			System.err.println("Attempted to retrive pixels from a Rectangle without generated graphics");
		}
		return null;
	}
	
	public String toString() {
		return "[ " + x + ", " + y + ", " + w + ", " + h + "]";
	}
}
