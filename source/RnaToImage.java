package source;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.ahmadsoft.ropes.Rope;
import org.ahmadsoft.ropes.RopeBuilder;

public class RnaToImage {

  /*
   * Inner classes.
   */
  public class Posn {
	  public int x;
	  public int y;
	  Posn(int x, int y) { this.x = x; this.y = y; }
  }
  public enum Direction {
	  NORTH,
	  EAST,
	  SOUTH,
	  WEST
  }

  /*
   * Class-global variables.
   */
  private static RopeBuilder rb = new RopeBuilder();
  public static final Rope e = rb.build("");
  public static Color black   = new Color(0  ,0  ,0  );
  public static Color red     = new Color(255,0  ,0  );
  public static Color green   = new Color(0  ,255,0  );
  public static Color yellow  = new Color(255,255,0  );
  public static Color blue    = new Color(0  ,0  ,255);
  public static Color magenta = new Color(255,0  ,255);
  public static Color cyan    = new Color(0  ,255,255);
  public static Color white   = new Color(255,255,255);
  
  public static Color transparent = new Color(0);
  public static Color opaque      = new Color(255);
  
  private Rope RNA = e;
  private ArrayList<Color> bucket = new ArrayList<Color>();
  private Posn position = new Posn(0,0);
  private Posn mark = new Posn(0,0);
  private Direction dir = Direction.EAST;
  private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
  private String rnaOutputFilename;
  
  /*
   * Main method.
   */
  public static void main(String args[])
  {
	  // TODO: Checking our input.
	  RnaToImage r2i = new RnaToImage(args[0],args[1]);
	  r2i.build();
  }
  
  /*
   * Constructor that takes no arguments. Used for testing.
   */
  public RnaToImage()
  {
	bitmaps.add(new Bitmap());
  }
  
  /*
   * Constructor that takes one argument, which is an RNA string.
   * Used for testing.
   */
  public RnaToImage(String rnaInput)
  {
	  bitmaps.add(new Bitmap());
	  this.RNA = rb.build(rnaInput);
      this.rnaOutputFilename = "D:/Coding/Endo/endo.png";

  }
  
  public RnaToImage(String rnaInputFilename, String rnaOutputFilename)
  {
	bitmaps.add(new Bitmap());
	this.rnaOutputFilename = rnaOutputFilename;
	try {
	    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rnaInputFilename)));
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
  
	/*
	 * Get the current contents of RNA. Used for testing.
	 */
	public Rope getRNA()
	{
		return RNA;
	}

	/*
	 * Set RNA to a different value. Used for testing (so as to not rely on file reading).
	 */
	public void setRNA(String newRNA)
	{
		this.RNA = rb.build(newRNA);
	}

	/*
	 * Get the current contents of position. Used for testing.
	 */
	public Posn getPosition()
	{
		return this.position;
	}
	
	/*
	 * Sets position. Used for testing.
	 */
	public void setPosition(Posn p)
	{
		this.position = p;
	}
	
	/*
	 * Gets the list of bitmaps. Used for testing.
	 */
	public ArrayList<Bitmap> getBitmaps()
	{
		return this.bitmaps;
	}
	
  public void build()
  {
	while (RNA.length() >= 7)
	{
		String currentBase = RNA.subSequence(0,7).toString();
		RNA = RNA.delete(0,7);
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
  
  public Pixel currentPixel()
  {
	  int numColors = 0;
	  int red = 0;
	  int green = 0;
	  int blue = 0;
	  int alpha = 0;
	  for (int i=0; i<bucket.size(); i++)
	  {
		  if (bucket.get(i).isColoured())
		  {
			  numColors++;
			  red += bucket.get(i).rgb.R;
			  green += bucket.get(i).rgb.G;
			  blue += bucket.get(i).rgb.B;			  
		  }
		  else
		  {
			  alpha += bucket.get(i).transparency;
		  }
	  }
	  if (numColors > 0)
	  {
		  red /= numColors;
		  green /= numColors;
		  blue /= numColors;
	  }
	  if (numColors < bucket.size())
	  {
		  alpha /= bucket.size() - numColors;
	  }
	  else
	  {
		  alpha = 255;
	  }	  
	  return new Pixel(red*alpha/255,
			           green*alpha/255,
			           blue*alpha/255,
			           alpha);
  }
  
  public Posn move(Posn p, Direction d)
  {
	  switch(d)
	  {
	    default: /* FALL THRU */
	    case NORTH:
	    	return new Posn( p.x         ,(p.y+599)%600);
	    case EAST:
	    	return new Posn((p.x+1)%600  , p.y         );
	    case SOUTH:
	    	return new Posn( p.x         ,(p.y+1)%600  );
	    case WEST:
	    	return new Posn((p.x+599)%600, p.y         );
	  }
  }
  
  public Direction turnCounterClockwise(Direction d)
  {
	  switch(d)
	  {
	    default: /* FALL THRU */
	    case NORTH:
	    	return Direction.WEST;
	    case EAST:
	    	return Direction.NORTH;
	    case SOUTH:
	    	return Direction.EAST;
	    case WEST:
	    	return Direction.SOUTH;
	  }
  }
  
  public Direction turnClockwise(Direction d)
  {
	  switch(d)
	  {
	    default: /* FALL THRU */
	    case NORTH:
	    	return Direction.EAST;
	    case EAST:
	    	return Direction.SOUTH;
	    case SOUTH:
	    	return Direction.WEST;
	    case WEST:
	    	return Direction.NORTH;
	  }
  }
  
  public Pixel getPixel(Posn p)
  {
	  return bitmaps.get(0).at[p.x][p.y];
  }
  
  public void setPixel(Posn p)
  {
	  Bitmap changedBitmap = bitmaps.get(0);
	  changedBitmap.at[p.x][p.y] = currentPixel();
	  bitmaps.set(0,changedBitmap);
  }
  
  public void line(Posn start, Posn end)
  {
	  int deltaX = end.x - start.x;
	  int deltaY = end.y - start.y;
	  int d = Math.max(Math.abs(deltaX),Math.abs(deltaY));
	  int c;
	  if (deltaX*deltaY <= 0)
	  {
		  c = 1;
	  }
	  else
	  {
		  c = 0;
	  }
	  int x = (start.x*d) + ((d-c)/2);
	  int y = (start.y*d) + ((d-c)/2);
	  for (int i=0; i<d; i++)
	  {
		  setPixel(new Posn(x/d,y/d));
		  x = x + deltaX;
		  y = y + deltaY;
	  }
	  
  }
  
  /*
   * tryfill() : [Attempts to flood-fill the area that 'position' is in]
   * 
   * The spec also has a method called fill() that recursively calls itself
   * to fill in surrounding pixels. This is implemented here by keeping a 
   * queue of the neighbouring pixels that need to be tested.
   */
  public void tryfill()
  {
	  Pixel newColour = currentPixel();
	  Pixel oldColour = getPixel(position);
	  if (!newColour.equals(oldColour))
	  {
		  ArrayDeque<Posn> pixelsToTest = new ArrayDeque<Posn>();
		  pixelsToTest.add(position);
		  while(pixelsToTest.size() > 0)
		  {
			  Posn p = pixelsToTest.removeFirst();
			  if (getPixel(p).equals(oldColour))
			  {
				  setPixel(p);
				  if (p.x > 0)   pixelsToTest.addLast(new Posn(p.x-1,p.y  ));
				  if (p.x < 599) pixelsToTest.addLast(new Posn(p.x+1,p.y  ));
				  if (p.y > 0)   pixelsToTest.addLast(new Posn(p.x  ,p.y-1));
				  if (p.y < 599) pixelsToTest.addLast(new Posn(p.x  ,p.y+1));
			  }
		  }
		  
	  }
  }
  
  public void addBitmap(Bitmap b)
  {
	  if (bitmaps.size() < 10)
		  bitmaps.add(0,b);
  }
  
  public void compose()
  {
	  if (bitmaps.size() > 1)
	  {
		  for (int x=0; x<600; x++)
		  {
			  for (int y=0; y<600; y++)
			  {
				  Pixel b0 = bitmaps.get(0).at[x][y];
				  Pixel b1 = bitmaps.get(1).at[x][y];
				  Bitmap changedBitmap = bitmaps.get(0);
				  changedBitmap.at[x][y] = new Pixel(
						                     b0.rgb.R + b1.rgb.R*(255-b0.alpha)/255,
						                     b0.rgb.G + b1.rgb.G*(255-b0.alpha)/255,
						                     b0.rgb.B + b1.rgb.B*(255-b0.alpha)/255,
						                     b0.alpha + b1.alpha*(255-b0.alpha)/255);
				  bitmaps.set(1,changedBitmap);
			  }
		  }
		  bitmaps.remove(0);
	  }
  }
  
  public void clip()
  {
	  if (bitmaps.size() > 1)
	  {
		  for (int x=0; x<600; x++)
		  {
			  for (int y=0; y<600; y++)
			  {
				  Pixel b0 = bitmaps.get(0).at[x][y];
				  Pixel b1 = bitmaps.get(1).at[x][y];
				  Bitmap changedBitmap = bitmaps.get(0);
				  changedBitmap.at[x][y] = new Pixel(
						                     b1.rgb.R*b0.alpha/255,
						                     b1.rgb.G*b0.alpha/255,
						                     b1.rgb.B*b0.alpha/255,
						                     b1.alpha*b0.alpha/255);
				  bitmaps.set(1,changedBitmap);
			  }
		  }
		  bitmaps.remove(0);
	  }	  
  }
  
  public void draw()
  {
	  // http://www.cap-lore.com/code/java/JavaPixels.html was some help here.
	  BufferedImage output = new BufferedImage(600,600,BufferedImage.TYPE_INT_RGB);
	  Bitmap b = bitmaps.get(0);
	  // Ignore transparency on the bitmap for this.
	  for (int x=0; x<600; x++)
	  {
		  for (int y=0; y<600; y++)
		  {
			int rgb = ((b.at[x][y].rgb.R) >> 16)
			         + ((b.at[x][y].rgb.G) >>  8)
			         + ((b.at[x][y].rgb.B));
			output.setRGB(x,y,rgb);
		  }
	  }
	  try {
		  ImageIO.write(output,"png",new File(this.rnaOutputFilename));
		  System.out.printf("Image written to %s.\n",rnaOutputFilename);
	  } catch (IOException e) {
		  System.out.printf("Problem writing to %s.\n",rnaOutputFilename);
	  }
	  
  }
	
}
