package source;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RnaToImage {

  /*
   * Inner classes.
   */
  private class Posn {
	  protected int x;
	  protected int y;
	  Posn(int x, int y) { this.x = x; this.y = y; }
  }
  private class RGB {
	  protected int R;
	  protected int G;
	  protected int B;
	  RGB(int R, int G, int B) { this.R = R; this.G = G; this.B = B; }
  }
  private class Pixel {
	  protected RGB rgb;
	  protected int transparency;
	  Pixel(int R, int G, int B, int trans) {
		  this.rgb = new RGB(R,G,B);
		  this.transparency = trans;
	  }
	  Pixel() {
		  this.rgb = new RGB(0,0,0);
		  this.transparency = 0;
	  }
  }
  private class Color { // HORRIBLE
	  protected RGB rgb;
	  protected int transparency;
	  protected boolean colorific; // true if colour, false if trans.
	  Color(int R, int G, int B) {
		  this.rgb = new RGB(R,G,B);
		  this.colorific = true;
	  }
	  Color(int trans) {
		  this.transparency = trans;
		  this.colorific = false;
	  }
  }
  private class Bitmap {
	  protected Pixel[][] at = new Pixel[600][600];
  }
  private enum Direction {
	  NORTH,
	  EAST,
	  SOUTH,
	  WEST
  }

  /*
   * Class-global variables.
   */
  private ArrayList<Color> bucket = new ArrayList<Color>();
  private Posn pos = new Posn(0,0);
  private Posn mark = new Posn(0,0);
  private Direction dir = Direction.EAST;
  private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
  
  void RnaToImage()
  {
	bitmaps.add(new Bitmap());  
  }
  
  public void build()
  {

  }
	
}
