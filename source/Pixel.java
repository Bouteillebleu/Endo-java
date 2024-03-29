package source;

public class Pixel {
	public RGB rgb;
	public int alpha;
	public Pixel(int R, int G, int B, int trans) {
	  this.rgb = new RGB(R,G,B);
	  this.alpha = trans;
	}
	public Pixel() {
	  this.rgb = new RGB(0,0,0);
	  this.alpha = 0;
	}
	
	public String toString() {
		return this.rgb.R+","
		      +this.rgb.G+","
		      +this.rgb.B+","
		      +this.alpha;
	}
	
	public boolean equals(Pixel that) {
		return (this.rgb.R == that.rgb.R
			 && this.rgb.G == that.rgb.G
			 && this.rgb.B == that.rgb.B
			 && this.alpha == that.alpha);
	}
}