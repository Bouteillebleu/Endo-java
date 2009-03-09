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
	 * Test that the DnaToRna constructor can read in a prefix and a GZIPped file, 
	 * and sets the DNA string as a result.
	 */
	public void testDnaToRna_verify() {
		dna2rna = new DnaToRna("","/resources/endo.zip");
		Assert.assertFalse(dna2rna.getDNA().equals(DnaToRna.e));
	}

}
