package source;

import org.ahmadsoft.ropes.Rope;
import org.ahmadsoft.ropes.RopeBuilder;

public class DNAcoder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 1 && args[0].equals("--decode"))
		{
			Rope[] results = decode(args[1]);
			System.out.printf("%s -> %s",results[0].toString(),
					                     results[1].toString());
		}
		else if (args.length > 2 && args[0].equals("--encode"))
		{
			System.out.println(encode(args[1],args[2]).toString());
		}
		else
		{
			System.out.println("Usage:");
			System.out.println("DNAcoder --decode dnastring");
			System.out.println("DNAcoder --encode pattern template");
		}

	}
	
	public static Rope[] decode(String DNAstring)
	{
		Rope p = DnaToRna.e;
		Rope t = DnaToRna.e;
		DnaToRna d2r = new DnaToRna();
		d2r.setDNA(DNAstring);
		p = d2r.pattern();
		t = d2r.template();
		return new Rope[] { p, t };
	}
	
	public static Rope encode(String p, String t)
	{
		RopeBuilder rBuild = new RopeBuilder();
		StringBuilder s = new StringBuilder();
		s.append(encodePattern(p));
		s.append(encodeTemplate(t));
		return rBuild.build(s.toString());
	}
	
	private static String encodePattern(String p)
	{
	  int pIndex = 0;
	  StringBuilder patternDna = new StringBuilder();
	  while (pIndex < p.length())
	  {
		  char currentChar = p.charAt(pIndex);
		  switch(currentChar)
		  {
		    case 'I':
		    	pIndex++;
		    	patternDna.append("C");
		    	break;
		    case 'C':
		    	pIndex++;
		    	patternDna.append("F");
		    	break;
		    case 'F':
		    	pIndex++;
		    	patternDna.append("P");
		    	break;
		    case 'P':
		    	pIndex++;
		    	patternDna.append("IC");
		    	break;
		    case '{':
		    	// Deal with skip case.
		    	pIndex++; // gets rid of the '{'.
		    	char nextChar = p.charAt(pIndex);
		    	int n = 0;
		    	while (nextChar!='}') {
		    	  n = (n*10) + Character.digit(nextChar,10);
		    	  pIndex++;
		    	  nextChar = p.charAt(pIndex);
		    	}
		    	pIndex++; // gets rid of the '}'.
		    	// now turn the number into a string of digits
		    	patternDna.append("IP"); // to mark skip case
		    	DnaToRna d = new DnaToRna();
		    	patternDna.append(d.asnat(n).toString());
		    	break;
		    case '[':
		    	// Deal with search case.
		    	pIndex++; // gets rid of the '['.
		    	patternDna.append("IFF"); // to mark search case
		    	nextChar = p.charAt(pIndex);
		    	while (nextChar!=']') {
				  switch(nextChar)
				  {
				    case 'I':
				    	pIndex++;
				    	patternDna.append("C");
				    	break;
				    case 'C':
				    	pIndex++;
				    	patternDna.append("F");
				    	break;
				    case 'F':
				    	pIndex++;
				    	patternDna.append("P");
				    	break;
				    case 'P':
				    	pIndex++;
				    	patternDna.append("IC");
				    	break;
				  }
		    	  nextChar = p.charAt(pIndex);
		    	}
		    	pIndex++; // gets rid of the ']'.
		    	break;
		    case '(':
		    	// Deal with opening group.
		    	pIndex++; // gets rid of the '('.
		    	patternDna.append("IIP");
		    	break;
		    case ')':
		    	// Deal with closing group.
		    	pIndex++; // gets rid of the ')'.
		    	patternDna.append("IIC");
		    	break;
		  }
	  }
	  patternDna.append("IIC");
	  return patternDna.toString();
	}

	private static String encodeTemplate(String t)
	{
      DnaToRna d = new DnaToRna();
	  int tIndex = 0;
	  StringBuilder templateDna = new StringBuilder();
	  while (tIndex < t.length())
	  {
		  char currentChar = t.charAt(tIndex);
		  switch(currentChar)
		  {
		    case 'I':
		    	tIndex++;
		    	templateDna.append("C");
		    	break;
		    case 'C':
		    	tIndex++;
		    	templateDna.append("F");
		    	break;
		    case 'F':
		    	tIndex++;
		    	templateDna.append("P");
		    	break;
		    case 'P':
		    	tIndex++;
		    	templateDna.append("IC");
		    	break;
		    case '<':
		  		tIndex++; // gets rid of the '<'.
		    	char nextChar = t.charAt(tIndex);
		    	int n = 0;
		    	while (nextChar!='_') {
		    	  n = (n*10) + Character.digit(nextChar,10);
		    	  tIndex++;
		    	  nextChar = t.charAt(tIndex);
		    	}
		    	tIndex++; // gets rid of the '_'.
		    	nextChar = t.charAt(tIndex);
		    	int l = 0;
		    	while (nextChar!='>') {
		    	  l = (l*10) + Character.digit(nextChar,10);
		    	  tIndex++;
		    	  nextChar = t.charAt(tIndex);
		    	}
		    	tIndex++; // gets rid of the '>'.
		    	// now turn this into number number in the right order.
		    	templateDna.append("IF");
		    	templateDna.append(d.asnat(l).toString());
		    	templateDna.append(d.asnat(n).toString());
		    	break;		    	
		    case '|':
		  		tIndex++; // gets rid of the initial '|'.
		    	nextChar = t.charAt(tIndex);
		    	n = 0;
		    	while (nextChar!='|') {
		    	  n = (n*10) + Character.digit(nextChar,10);
		    	  tIndex++;
		    	  nextChar = t.charAt(tIndex);
		    	}
		    	tIndex++; // gets rid of the final '|'.
		    	// now turn this into a number
		    	templateDna.append("IIP");
		    	templateDna.append(d.asnat(n).toString());
		    	break;		    	
		  }
	  }
	  templateDna.append("IIC");
	  return templateDna.toString();
	}
	
}
