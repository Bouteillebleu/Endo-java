package source;

public class Pixel {
	public RGB rgb;
	public int transparency;
	Pixel(int R, int G, int B, int trans) {
	  this.rgb = new RGB(R,G,B);
	  this.transparency = trans;
	}
	Pixel() {
	  this.rgb = new RGB(0,0,0);
	  this.transparency = 0;
	}
}