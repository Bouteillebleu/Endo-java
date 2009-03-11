package source;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.ahmadsoft.ropes.Rope;
import org.ahmadsoft.ropes.RopeBuilder;

public class RnaToImage {

  /*
   * Inner classes.
   */
  private class Posn {
	  protected int x;
	  protected int y;
	  Posn(int x, int y) { this.x = x; this.y = y; }
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
  private static RopeBuilder rb = new RopeBuilder();
  private static final Rope e = rb.build("");
  private static Color black   = new Color(0  ,0  ,0  );
  private static Color red     = new Color(255,0  ,0  );
  private static Color green   = new Color(0  ,255,0  );
  private static Color yellow  = new Color(255,255,0  );
  private static Color blue    = new Color(0  ,0  ,255);
  private static Color magenta = new Color(255,0  ,255);
  private static Color cyan    = new Color(0  ,255,255);
  private static Color white   = new Color(255,255,255);
  
  private static Color transparent = new Color(0);
  private static Color opaque      = new Color(255);
  
  private Rope RNA = e;
  private ArrayList<Color> bucket = new ArrayList<Color>();
  private Posn position = new Posn(0,0);
  private Posn mark = new Posn(0,0);
  private Direction dir = Direction.EAST;
  private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
  
  public RnaToImage(String rnaFilename)
  {
	bitmaps.add(new Bitmap());
	try {
	    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rnaFilename)));
		StringBuilder buildingRNA = new StringBuilder();
		while (in.ready())
		{
		  buildingRNA.append((char)in.read());
		}
		this.RNA = rb.build(buildingRNA.toString());
	} catch (IOException e) {
	    System.out.println("Problem with reading from Endo's RNA file.");
	    e.printStackTrace();
	}  
  }
  
  public void build()
  {
	while (RNA.length() >= 7)
	{
		String currentBase = RNA.subSequence(0,7).toString();
		RNA.delete(0,7);
		if (currentBase.equals("PIPIIIC")) {
			addColor(black);
		} else if (currentBase.equals("PIPIIIP")) {
			addColor(red);
		} else if (currentBase.equals("PIPIICC")) {
			addColor(green);
		} else if (currentBase.equals("PIPIICF")) {
			addColor(yellow);
		} else if (currentBase.equals("PIPIICP")) {
			addColor(blue);
		} else if (currentBase.equals("PIPIIFC")) {
			addColor(magenta);
		} else if (currentBase.equals("PIPIIFF")) {
			addColor(cyan);
		} else if (currentBase.equals("PIPIIPC")) {
			addColor(white);
		} else if (currentBase.equals("PIPIIPF")) {
			addColor(transparent);
		} else if (currentBase.equals("PIPIIPP")) {
			addColor(opaque);
		} else if (currentBase.equals("PIIPICP")) {
			bucket.clear();
		} else if (currentBase.equals("PIIIIIP")) {
			position = move(position,dir);
		} else if (currentBase.equals("PCCCCCP")) {
			dir = turnCounterClockwise(dir);
		} else if (currentBase.equals("PFFFFFP")) {
			dir = turnClockwise(dir);
		} else if (currentBase.equals("PCCIFFP")) {
			mark = position;
		} else if (currentBase.equals("PFFICCP")) {
			line(position,mark);
		} else if (currentBase.equals("PIIPIIP")) {
			tryfill();
		} else if (currentBase.equals("PCCPFFP")) {
			addBitmap(new Bitmap());
		} else if (currentBase.equals("PFFPCCP")) {
			compose();
		} else if (currentBase.equals("PFFICCF")) {
			clip();
		} else {
			// Do nothing.
		}
	}
	draw();
  }
  
  public void addColor(Color c)
  {
	  bucket.add(0,c);
  }
  
  public Posn move(Posn p, Direction d)
  {
	  return new Posn(0,0);
  }
  
  public Direction turnCounterClockwise(Direction d)
  {
	  return Direction.NORTH;
  }
  
  public Direction turnClockwise(Direction d)
  {
	  return Direction.NORTH;
  }
  
  public void line(Posn start, Posn end)
  {
	  
  }
  
  public void tryfill()
  {
	  
  }
  
  public void addBitmap(Bitmap b)
  {
	  
  }
  
  public void compose()
  {
	  
  }
  
  public void clip()
  {
	  
  }
  
  public void draw()
  {
	  
  }
	
}
