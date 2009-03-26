package test;

import java.util.ArrayList;

import org.ahmadsoft.ropes.Rope;
import org.ahmadsoft.ropes.RopeBuilder;

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
		dna2rna = new DnaToRna("","C:/Coding/Endo/resources/endo.dna","");
		Assert.assertFalse(dna2rna.getDNA().equals(DnaToRna.e));
	}
	
	/*
	 * Test that the DnaToRna constructor can read in a prefix and an empty file,
	 * and sets the DNA string as a result to the prefix.
	 */
	public void testDnaToRna_prefix_emptyFile() {
		dna2rna = new DnaToRna("ICFPICFPICFP","C:/Coding/Endo/resources/empty.dna","");
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
		Assert.assertEquals("({2})P",dna2rna.pattern().toString());
	}
	
	/*
	 * Test template() method with a non-zero string.
	 */
	public void testTemplate_verify()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("CFPICIFIICPCCPPFIIPCICPICICIIC");
		Assert.assertEquals("ICFP<3_4>FC|5|PP",dna2rna.template().toString());
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
	
	/*
	 * Test consts() method with a non-zero string.
	 */
	public void testConsts_verify()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("CPICFPICFIF");
		Assert.assertEquals("IFPCFPC",dna2rna.consts().toString());
	}
	
	/*
	 * Test asnat() with a positive number.
	 */
	public void testAsnat_verify()
	{
		dna2rna = new DnaToRna();
		Assert.assertEquals("CIICCP",dna2rna.asnat(25).toString());
	}
	
	/*
	 * Test asnat() with 0.
	 */
	public void testAsnat_zero()
	{
		dna2rna = new DnaToRna();
		Assert.assertEquals("P",dna2rna.asnat(0).toString());
	}

	/*
	 * Test quote() with a non-empty string.
	 */
	public void testQuote_verify()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("ICFPCFPI");
		Assert.assertEquals("CFPICFPICC",dna2rna.quote(dna2rna.getDNA()).toString());
	}
	
	/*
	 * Test quote() with an empty string.
	 */
	public void testQuote_empty()
	{
		dna2rna = new DnaToRna();
		Assert.assertEquals("",dna2rna.quote(DnaToRna.e).toString());
	}
	
	/*
	 * Test protect() with a non-empty string and non-zero level.
	 */
	public void testProtect_verify()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("ICFPCFPI");
		Assert.assertEquals("FPICCFPICCFF",dna2rna.protect(2,dna2rna.getDNA()).toString());
	}

	/*
	 * Test protect() with a non-empty string and zero level.
	 */
	public void testProtect_zeroLevel()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("ICFPCFPI");
		Assert.assertEquals("ICFPCFPI",dna2rna.protect(0,dna2rna.getDNA()).toString());
	}

	/*
	 * Tests replace() using the data from full iteration test 1 in figure 16.
	 */
	public void testReplace_test1()
	{
		dna2rna = new DnaToRna();
		RopeBuilder rob = new RopeBuilder();
		Rope t = rob.build("PI<0_0>");
		ArrayList<Rope> e = new ArrayList<Rope>();
		e.add(rob.build("CF"));
		dna2rna.setDNA("C");
		dna2rna.replace(t,e);
		Assert.assertEquals("PICFC",dna2rna.getDNA().toString());
	}
	
	/*
	 * Testing the search part of matchreplace(),
	 * with the search result occurring immediately in the remaining DNA.
	 */
	public void testMatchreplace_search_immediateMatch()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("ICIIPIFPCFPICIICICIIFIFPPIFPPIICPICFPP");
		Rope p = dna2rna.pattern(); // "ICIIPIFPCFPICIICICIIF"
		Assert.assertEquals("P([ICFP])P",p.toString());
		Rope t = dna2rna.template(); // "IFPPIFPPIIC"
		Assert.assertEquals("<0_0><0_0>",t.toString());
		ArrayList<Rope> env = dna2rna.matchreplace(p,t);
		Assert.assertEquals(1,env.size());
		Assert.assertEquals("ICFP",env.get(0).toString());
	}
	
	/*
	 * Testing the search part of matchreplace(),
	 * with the search result occurring after five characters
	 * in the remaining DNA.
	 */
	public void testMatchreplace_search_delayedMatch()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("ICIIPIFPCFPICIICICIIFIFPPIFPPIICPIIFFFICFPP");
		Rope p = dna2rna.pattern(); // "ICIIPIFPCFPICIICICIIF"
		Assert.assertEquals("P([ICFP])P",p.toString());
		Rope t = dna2rna.template(); // "IFPPIFPPIIC"
		Assert.assertEquals("<0_0><0_0>",t.toString());
		ArrayList<Rope> env = dna2rna.matchreplace(p,t);
		Assert.assertEquals(1,env.size());
		Assert.assertEquals("IIFFFICFP",env.get(0).toString());
	}

	/*
	 * Full iteration test 1 from Figure 16 of spec.
	 */
	public void testFullIteration_test1()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("IIPIPICPIICICIIFICCIFPPIICCFPC");
	    Rope p = dna2rna.pattern();
	    Assert.assertEquals("({2})P",p.toString());
	    Rope t = dna2rna.template();
	    Assert.assertEquals("PI<0_0>",t.toString());
	    dna2rna.matchreplace(p,t);
	    Assert.assertEquals("PICFC",dna2rna.getDNA().toString());
	}

	/*
	 * Full iteration test 2 from Figure 16 of spec.
	 */
	public void testFullIteration_test2()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("IIPIPICPIICICIIFICCIFCCCPPIICCFPC");
	    Rope p = dna2rna.pattern();
	    Assert.assertEquals("({2})P",p.toString());
	    Rope t = dna2rna.template();
	    Assert.assertEquals("PI<0_7>",t.toString());
	    dna2rna.matchreplace(p,t);
	    Assert.assertEquals("PIICCFCFFPC",dna2rna.getDNA().toString());
	}

	/*
	 * Full iteration test 3 from Figure 16 of spec.
	 */
	public void testFullIteration_test3()
	{
		dna2rna = new DnaToRna();
		dna2rna.setDNA("IIPIPIICPIICIICCIICFCFC");
	    Rope p = dna2rna.pattern();
	    Assert.assertEquals("({4})",p.toString());
	    Rope t = dna2rna.template();
	    Assert.assertEquals("I",t.toString());
	    dna2rna.matchreplace(p,t);
	    Assert.assertEquals("I",dna2rna.getDNA().toString());
	}
}
