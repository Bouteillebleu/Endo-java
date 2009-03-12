package test;

import source.Bitmap;
import source.Pixel;
import source.RnaToImage;
import source.RnaToImage.Posn;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestRnaToImage extends TestCase {
	
	RnaToImage rna2image;

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
}
