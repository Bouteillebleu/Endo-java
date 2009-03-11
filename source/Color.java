package source;

import source.RGB;

public class Color {
	  protected RGB rgb;
	  protected int transparency;
	  protected boolean colorific; // true if colour, false if transparent.
	  Color(int R, int G, int B) {
		  this.rgb = new RGB(R,G,B);
		  this.colorific = true;
	  }
	  Color(int trans) {
		  this.transparency = trans;
		  this.colorific = false;
	  }
	  
	  boolean isColoured()
	  {
		  return this.colorific;
	  }

}
