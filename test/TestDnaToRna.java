package test;

import source.DnaToRna;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Isobel
 *
 */
public class TestDnaToRna extends TestCase {

	DnaToRna dna2rna;
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test that the DnaToRna constructor can read in a prefix and a non-zipped file, 
	 * and sets the DNA string as a result.
	 * TODO: Find a way to specify filenames without absolute paths.
	 */
	public void testDnaToRna_verify() {
		dna2rna = new DnaToRna("","C:/Coding/Endo/resources/endo.dna");
		Assert.assertFalse(dna2rna.getDNA().equals(DnaToRna.e));
	}
	
	/*
	 * Test that the DnaToRna constructor can read in a prefix and an empty file,
	 * and sets the DNA string as a result to the prefix.
	 */
	public void testDnaToRna_prefix_emptyFile() {
		dna2rna = new DnaToRna("ICFPICFPICFP","C:/Coding/Endo/resources/empty.dna");
		Assert.assertEquals("ICFPICFPICFP",dna2rna.getDNA().toString());
	}

	/*
	 * Test 1 from Figure 8 of spec for the pattern() algorithm.
	 */
	public void testPattern_test1()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("CIIC");
		Assert.assertEquals("I",dna2rna.pattern().toString());
	}
	
	/*
	 * Test 2 from Figure 8 of spec for the pattern() algorithm.
	 */
	public void testPattern_test2()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("IIPIPICPIICICIIF");
		Assert.assertEquals("(.{2})P",dna2rna.pattern().toString());
	}
	
	/*
	 * Test nat() method with a non-zero string.
	 */
	public void testNat_verify()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("CICCCP");
		Assert.assertEquals(29, dna2rna.nat());
	}

	/*
	 * Test nat() method with a string that just has "P" at the end.
	 */
	public void testNat_terminatorOnly()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("P");
		Assert.assertEquals(0, dna2rna.nat());
	}
	
	/*
	 * Test nat() with DNA equal to the empty rope.
	 */
	public void testNat_empty()
	{
		dna2rna = new DnaToRna();
		Assert.assertEquals(0, dna2rna.nat());
	}
}
