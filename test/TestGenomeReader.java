package test;

import source.GenomeReader;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestGenomeReader extends TestCase {

	/*
	 * Test method for 'source.GenomeReader.GenomeReader(String)'
	 * Data in test list:
	 * 000510,000018,AAA_geneTablePageNr
	 * 2ccd88,03c7f0,M-class-planet
	 * 0c4589,000018,__array_index
	 */
	public void testGenomeReader() {
		GenomeReader gr = new GenomeReader("D:/Coding/Endo/resources/testgenelist.txt");
		Assert.assertEquals(3,gr.geneList.size());
		Assert.assertEquals("AAA_geneTablePageNr",gr.geneList.get(1296).getName());
		Assert.assertEquals(24,gr.geneList.get(1296).getLength());
		Assert.assertEquals("M-class-planet",gr.geneList.get(2936200).getName());
		Assert.assertEquals(247792,gr.geneList.get(2936200).getLength());
		Assert.assertEquals("__array_index",gr.geneList.get(804233).getName());
		Assert.assertEquals(24,gr.geneList.get(804233).getLength());
	}

}
