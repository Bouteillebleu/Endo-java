package source;

public class Bitmap {
	  // First index is x, second is y.
	  public Pixel[][] at = new Pixel[600][600];
	  
	  public Bitmap()
	  {
		  for (int x=0; x<600; x++)
		  {
			  for (int y=0; y<600; y++)
			  {
				  this.at[x][y] = new Pixel();
			  }
		  }
	  }
}

