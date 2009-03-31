package test;

import source.Bitmap;
import source.Pixel;
import source.Posn;
import source.RnaToImage;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestRnaToImage extends TestCase {
	
	RnaToImage rna2image;

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		rna2image = null;
		super.tearDown();
	}

	/*
	 * Test that the RnaToImage constructor can read in a file 
	 * and sets the RNA string as a result.
	 * TODO: Find a way to specify filenames without absolute paths.
	 */
	public void testRnaToImage_verify() {
		rna2image = new RnaToImage("D:/Coding/Endo/resources/endo.rna","");
		Assert.assertFalse(rna2image.getRNA().equals(RnaToImage.e));
	}
	
	//PIPIIPCPFFFFFP
	/*
	 * Test that the RnaToImage constructor can read in a file and set
	 * the RNA string correctly.
	 */
	public void testRnaToImage_verifyContents() {
		rna2image = new RnaToImage("D:/Coding/Endo/resources/short.rna","");
		Assert.assertEquals("PIPIIPCPFFFFFP",rna2image.getRNA().toString());
	}

	/*
	 * Test that move() changes the value of position
	 * when not moving off the edge of the bitmap.
	 */
	public void testMove_verify()
	{
		rna2image = new RnaToImage();
		Posn p = rna2image.getPosition();
		// Move east.
		rna2image.setPosition(rna2image.move(p,RnaToImage.Direction.EAST));
		p = rna2image.getPosition();
		Assert.assertEquals(1,p.x);
		Assert.assertEquals(0,p.y);
		// Move south.
		rna2image.setPosition(rna2image.move(p,RnaToImage.Direction.SOUTH));
		p = rna2image.getPosition();
		Assert.assertEquals(1,p.x);
		Assert.assertEquals(1,p.y);
		// Move west.
		rna2image.setPosition(rna2image.move(p,RnaToImage.Direction.WEST));
		p = rna2image.getPosition();
		Assert.assertEquals(0,p.x);
		Assert.assertEquals(1,p.y);
		// Move north.
		rna2image.setPosition(rna2image.move(p,RnaToImage.Direction.NORTH));
		p = rna2image.getPosition();
		Assert.assertEquals(0,p.x);
		Assert.assertEquals(0,p.y);
	}
	
	/*
	 * Test that move() changes the value of position
	 * when moves off the edge of the bitmap are involved.
	 */
	public void testMove_verify_edgeMoves()
	{
		rna2image = new RnaToImage();
		Posn p = rna2image.getPosition();
		// Move west.
		rna2image.setPosition(rna2image.move(p,RnaToImage.Direction.WEST));
		p = rna2image.getPosition();
		Assert.assertEquals(599,p.x);
		Assert.assertEquals(0,p.y);
		// Move north.
		rna2image.setPosition(rna2image.move(p,RnaToImage.Direction.NORTH));
		p = rna2image.getPosition();
		Assert.assertEquals(599,p.x);
		Assert.assertEquals(599,p.y);
		// Move east.
		rna2image.setPosition(rna2image.move(p,RnaToImage.Direction.EAST));
		p = rna2image.getPosition();
		Assert.assertEquals(p.x,0);
		Assert.assertEquals(p.y,599);
		// Move south.
		rna2image.setPosition(rna2image.move(p,RnaToImage.Direction.SOUTH));
		p = rna2image.getPosition();
		Assert.assertEquals(p.x,0);
		Assert.assertEquals(p.y,0);
	}


	/*
	 * Attempts to flood-fill the image red.
	 * Assert won't test this - need to look.
	 */
	public void testRedFill()
	{
		rna2image = new RnaToImage("PIPIIIPPIIPIIP");
		Assert.assertTrue(rna2image.build());
		Pixel p = rna2image.getBitmaps().get(0).at[0][0];
		Assert.assertEquals(rna2image.currentPixel().toString(),p.toString());
	}
	
	/*
	 * Make all the pixels red, then check what the output data are.
	 * Manually sets the bitmaps because build() automatically calls draw(),
	 * which we only want part of for this test.
	 */
	public void testRedFill_noRender()
	{
		rna2image = new RnaToImage();
		Bitmap b = rna2image.getBitmaps().get(0);
		for(int x=0; x<600; x++)
		{
			for(int y=0; y<600; y++)
			{
				b.at[x][y]= new Pixel(255,0,0,255);
			}
		}
		rna2image.setBitmap(b,0);
		int[] rgbData = rna2image.flattenImage();
		for(int i=0; i<600*600; i++)
		{
			Assert.assertEquals(0x00ff0000,rgbData[i]);			
		}
	}
	
	/*
	 * As with testRedFill_noRender(), but calls addColor() and tryfill() instead.
	 */
	public void testRedFill_noRender_nativeMethods()
	{
		rna2image = new RnaToImage();
		rna2image.addColor(RnaToImage.red);
		rna2image.tryfill();
		int[] rgbData = rna2image.flattenImage();
		for(int i=0; i<600*600; i++)
		{
			Assert.assertEquals(0x00ff0000,rgbData[i]);			
		}
	}
	
	/*
	 * First test from Figure 21 of the spec.
	 */
	public void testCurrentPixel_test1()
	{
		rna2image = new RnaToImage();
		rna2image.addColor(RnaToImage.opaque);
		rna2image.addColor(RnaToImage.opaque);
		rna2image.addColor(RnaToImage.transparent);
		Pixel pix = rna2image.currentPixel();
		Assert.assertEquals(0,pix.rgb.R);
		Assert.assertEquals(0,pix.rgb.G);
		Assert.assertEquals(0,pix.rgb.B);
		Assert.assertEquals(170,pix.alpha);
	}
	
	/*
	 * Second test from Figure 21 of the spec.
	 */
	public void testCurrentPixel_test2()
	{
		rna2image = new RnaToImage();
		rna2image.addColor(RnaToImage.cyan);
		rna2image.addColor(RnaToImage.yellow);
		rna2image.addColor(RnaToImage.black);
		Pixel pix = rna2image.currentPixel();
		Assert.assertEquals(85,pix.rgb.R);
		Assert.assertEquals(170,pix.rgb.G);
		Assert.assertEquals(85,pix.rgb.B);
		Assert.assertEquals(255,pix.alpha);
	}
	
	/*
	 * Third test from Figure 21 of the spec.
	 */
	public void testCurrentPixel_test3()
	{
		rna2image = new RnaToImage();
		rna2image.addColor(RnaToImage.opaque);
		rna2image.addColor(RnaToImage.transparent);
		rna2image.addColor(RnaToImage.yellow);
		Pixel pix = rna2image.currentPixel();
		Assert.assertEquals(127,pix.rgb.R);
		Assert.assertEquals(127,pix.rgb.G);
		Assert.assertEquals(0,pix.rgb.B);
		Assert.assertEquals(127,pix.alpha);
	}

	/*
	 * Fourth test from Figure 21 of the spec.
	 * The trickier one.
	 */
	public void testCurrentPixel_test4()
	{
		rna2image = new RnaToImage();
		rna2image.addColor(RnaToImage.transparent);
		for(int i=0;i<3;i++)
			rna2image.addColor(RnaToImage.opaque);
		for(int i=0;i<10;i++)
			rna2image.addColor(RnaToImage.white);
		for(int i=0;i<39;i++)
			rna2image.addColor(RnaToImage.magenta);
		for(int i=0;i<7;i++)
			rna2image.addColor(RnaToImage.red);
		for(int i=0;i<18;i++)
			rna2image.addColor(RnaToImage.black);
		Pixel pix = rna2image.currentPixel();
		Assert.assertEquals(143,pix.rgb.R);
		Assert.assertEquals(25,pix.rgb.G);
		Assert.assertEquals(125,pix.rgb.B);
		Assert.assertEquals(191,pix.alpha);
	}
	
	/*
	 * Tests that setPixel() does indeed set the specified pixel on 
	 * bitmap 0. We know currentPixel() works, so this is okay.
	 */
	public void testSetPixel_verify()
	{
		rna2image = new RnaToImage();
		rna2image.addColor(RnaToImage.red);
		rna2image.setPixel(new Posn(127,127));
		Bitmap b = rna2image.getBitmaps().get(0);
		Pixel p = b.at[127][127];
		Assert.assertEquals(255,p.rgb.R);
		Assert.assertEquals(0,p.rgb.G);
		Assert.assertEquals(0,p.rgb.B);
		Assert.assertEquals(255,p.alpha);
	}
	
	/*
	 * Test that writing to file with the data we expect produces
	 * what we expect - here we want to produce a fully green image.
	 */
	public void testWriteToFile()
	{
		int[] data = new int[600*600];
		for (int i=0; i<600*600; i++)
		{
			data[i] = 0x00ff00;
		}
		rna2image = new RnaToImage();
		rna2image.writeToFile(data);
		
	}
	
	/*
	 * Test that we can add a default bitmap using addBitmap(),
	 * and that both bitmaps have the properties we expect.
	 */
	public void testAddBitmap()
	{
		rna2image = new RnaToImage();
		Bitmap b = rna2image.getBitmaps().get(0);
		for(int x=0; x<600; x++)
		{
			for(int y=0; y<600; y++)
			{
				b.at[x][y]= new Pixel(255,0,0,255);
			}
		}
		rna2image.setBitmap(b,0);
		Bitmap newBitmap = new Bitmap();
		rna2image.addBitmap(newBitmap);
		Bitmap b0 = rna2image.getBitmaps().get(0);
		Bitmap b1 = rna2image.getBitmaps().get(1);
		Pixel redPix = new Pixel(255,0,0,255);
		Pixel defPix = new Pixel(0,0,0,0);
		for(int x=0; x<600; x++)
		{
			for(int y=0; y<600; y++)
			{
				Assert.assertTrue(b1.at[x][y].equals(redPix));
				Assert.assertTrue(b0.at[x][y].equals(defPix));
			}
		}
	}

	
	/*
	 * Test compose() by blending a semi-transparent red bitmap b0
	 * and a fully opaque blue bitmap b1.
	 */
	public void testCompose()
	{
		rna2image = new RnaToImage();
		rna2image.addBitmap(new Bitmap());
		Bitmap b0 = new Bitmap();
		Bitmap b1 = new Bitmap();
		Pixel transRed = new Pixel(255,0,0,127);
		Pixel opaqueBlue = new Pixel(0,0,255,255);
		for(int x=0; x<600; x++)
		{
			for(int y=0; y<600; y++)
			{
				b0.at[x][y] = transRed;
				b1.at[x][y] = opaqueBlue;
			}
		}
		rna2image.setBitmap(b0,0);
		rna2image.setBitmap(b1,1);
		rna2image.compose();
		Assert.assertEquals(1,rna2image.getBitmaps().size());
		Pixel comp = new Pixel(255,0,128,255);
		Bitmap result = rna2image.getBitmaps().get(0);
		for(int x=0; x<600; x++)
		{
			for(int y=0; y<600; y++)
			{
				Assert.assertTrue(result.at[x][y].equals(comp));
			}
		}

	}
	

	/*
	 * Test clip() by combining a semi-transparent red bitmap b0
	 * and a fully opaque green bitmap with blue square in top corner b1.
	 */
	public void testClip()
	{
		rna2image = new RnaToImage();
		rna2image.addBitmap(new Bitmap());
		Bitmap b0 = new Bitmap();
		Bitmap b1 = new Bitmap();
		Pixel transRed = new Pixel(255,0,0,127);
		Pixel opaqueBlue = new Pixel(0,0,255,255);
		Pixel opaqueGreen = new Pixel(0,255,0,255);
		for(int x=0; x<600; x++)
		{
			for(int y=0; y<600; y++)
			{
				b0.at[x][y] = transRed;
				if (x < 100 && y < 100)
				{
					b1.at[x][y] = opaqueBlue;
				}
				else
				{
					b1.at[x][y] = opaqueGreen;
				}
			}
		}
		rna2image.setBitmap(b0,0);
		rna2image.setBitmap(b1,1);
		rna2image.clip();
		Assert.assertEquals(1,rna2image.getBitmaps().size());
		Pixel clipBlue = new Pixel(0,0,127,127);
		Pixel clipGreen = new Pixel(0,127,0,127);
		Bitmap result = rna2image.getBitmaps().get(0);
		for(int x=0; x<600; x++)
		{
			for(int y=0; y<600; y++)
			{
				if (x < 100 && y < 100)
				{
					Assert.assertTrue(result.at[x][y].equals(clipBlue));					
				}
				else
				{
					Assert.assertTrue(result.at[x][y].equals(clipGreen));
				}
			}
		}

		
	}
	
	/*
	 * Test line() by drawing an opaque white line across
	 * an otherwise transparent black bitmap.
	 */
	public void testLine_diagonal()
	{
		rna2image = new RnaToImage();
		rna2image.addColor(RnaToImage.white);
		rna2image.line(new Posn(0,0),new Posn(599,599));
		Bitmap result = rna2image.getBitmaps().get(0);
		Pixel whitePix = new Pixel(255,255,255,255);
		Pixel blackPix = new Pixel(0,0,0,0);
		for(int x=0; x<600; x++)
		{
			for(int y=0; y<600; y++)
			{
				if (x==y)
				{
					Assert.assertTrue(result.at[x][y].equals(whitePix));
				}
				else
				{
					Assert.assertTrue(result.at[x][y].equals(blackPix));
				}
			}
		}
		int[] rgbData = rna2image.flattenImage();
		rna2image.writeToFile(rgbData);
	}
	
	/*
	 * Test that drawing diagonal line and then filling one side gives us
	 * the result we expect (black triangle and white triangle).
	 */
	public void testLine_diagonal_withFill()
	{
		rna2image = new RnaToImage();
		rna2image.addColor(RnaToImage.white);
		rna2image.line(new Posn(0,0),new Posn(599,599));
		Bitmap result = rna2image.getBitmaps().get(0);
		Pixel whitePix = new Pixel(255,255,255,255);
		Pixel blackPix = new Pixel(0,0,0,0);
		rna2image.setPosition(new Posn(0,599));
		rna2image.tryfill();
		for(int x=0; x<600; x++)
		{
			for(int y=0; y<600; y++)
			{
				if (x<=y)
				{
					Assert.assertTrue(result.at[x][y].equals(whitePix));
				}
				else
				{
					Assert.assertTrue(result.at[x][y].equals(blackPix));
				}
			}
		}
		int[] rgbData = rna2image.flattenImage();
		rna2image.writeToFile(rgbData);		
	}
	
	/*
	 * STILL TO TEST:
	 * - clip()
	 */
	
	
}
