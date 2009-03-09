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

}
