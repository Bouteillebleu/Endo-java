package test;

import source.Pixel;
import source.RnaToImage;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestRnaToImage extends TestCase {
	
	RnaToImage rna2image;

	/*
	 * Test method for 'source.RnaToImage.RnaToImage(String)'
	 */
	public void testRnaToImage()
	{

	}

	/*
	 * Test method for 'source.RnaToImage.addColor(Color)'
	 */
	public void testAddColor()
	{

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
		Assert.assertEquals(170,pix.transparency);
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
		Assert.assertEquals(255,pix.transparency);
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
		Assert.assertEquals(127,pix.transparency);
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
		Assert.assertEquals(191,pix.transparency);

	}
}
