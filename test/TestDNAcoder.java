package test;

import org.ahmadsoft.ropes.Rope;
import source.DNAcoder;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestDNAcoder extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'source.DNAcoder.decode(String)'
	 */
	public void testDecode() {
		Rope[] patternTemplate = DNAcoder.decode("IIPIFFCPICFPPICIICCIICIPPPFIIC");
		Assert.assertEquals(patternTemplate[0].toString(),"([IFPCFFP])I");
		Assert.assertEquals(patternTemplate[1].toString(),"<0_0>C");
	}

	/*
	 * Test method for 'source.DNAcoder.encode(String, String)'
	 */
	public void testEncode() {
		Rope encoded = DNAcoder.encode("([IFPCFFP])I","<0_0>C");
		Assert.assertEquals(encoded.toString(),"IIPIFFCPICFPPICIICCIICIFPPFIIC");
	}

}
