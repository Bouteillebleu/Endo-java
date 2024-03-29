package source;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
//import java.util.zip.ZipInputStream;

import org.ahmadsoft.ropes.Rope;
import org.ahmadsoft.ropes.RopeBuilder;


public class DnaToRna {

	private static RopeBuilder rb = new RopeBuilder();
	public static final Rope e = rb.build("");
	private Rope DNA = e;
	private Rope RNA = e;
	private int DNAindex = 0;
	private boolean finish = false;
	private String outputFilename;
	private BufferedWriter debugbuf;
	private enum LogLevel { NONE, TRACE, VERBOSE, ITERATIONS, GENEMATCH };
	private LogLevel logging = LogLevel.NONE;
	private GenomeReader genomeRead;
	
	public static void main(String args[])
	{
		// TODO: Checking our input.
		DnaToRna d2r = new DnaToRna(args[0],args[1],args[2]);

		// If we've got a fourth and it's --logging, set logging on, build.
		if (args.length > 3 && args[3].startsWith("--logging"))
		{
			if (args[3].equals("--logging=iterations"))
			{
				d2r.logging = LogLevel.ITERATIONS;
			}
			else
			{
			  try {
				  d2r.debugbuf = new BufferedWriter(new FileWriter("D:/Coding/Endo/endo.log"));
				  d2r.debugbuf.write("Endo DNA processing log");
				  d2r.debugbuf.newLine();
				  d2r.debugbuf.write("=======================");
				  d2r.debugbuf.newLine();
				  d2r.debugbuf.flush();
			  } catch (IOException e) {
				  System.out.println("Problem writing to Endo debug log.");
			      e.printStackTrace();
			  }
			  if (args[3].equals("--logging=trace"))
			  {
				d2r.logging = LogLevel.TRACE;
			  }
			  else if (args[3].equals("--logging=genematch"))
			  {
				d2r.logging = LogLevel.GENEMATCH;
				d2r.genomeRead = new GenomeReader("D:/Coding/Endo/resources/genelist.txt");
			  }
			  else
			  {
				d2r.logging = LogLevel.VERBOSE;
			  }
			}

		}
		
		d2r.execute();
		if (d2r.logging == LogLevel.VERBOSE ||
			d2r.logging == LogLevel.TRACE ||
			d2r.logging == LogLevel.GENEMATCH)
		{
			try { 
				d2r.debugbuf.close();
			} catch (IOException e) {
				System.out.println("Problem closing Endo debug log.");
				e.printStackTrace();
			}
		}
}
	
	// Default constructor that takes no arguments. For testing.
	public DnaToRna()
	{
		return;
	}
	
	/*
	 * TODO: Sort out how to do this with the Zip input stream reader?
	 */
	public DnaToRna(String prefix, String endoFilename, String outputFilename)
	{
	  this.outputFilename = outputFilename;
	  try {
		//BufferedReader in = new BufferedReader(new InputStreamReader(new ZipInputStream(new FileInputStream(endoZipFilename))));
	    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(endoFilename)));
		StringBuilder buildingDNA = new StringBuilder();
		buildingDNA.append(prefix);
		while (in.ready())
		{
		  buildingDNA.append((char)in.read());
		}
		this.DNA = rb.build(buildingDNA.toString());
		in.close();
	  } catch (IOException e) {
	    System.out.println("Problem with reading from Endo's DNA file.");
	    e.printStackTrace();
	  }
	}
	
	/*
	 * Get the current contents of DNA. Used for testing.
	 */
	public Rope getDNA()
	{
		return DNA;
	}
	
	/*
	 * Set DNA to a different value. Used for testing (so as to not rely on file reading).
	 */
	public void setDNA(String newDNA)
	{
		this.DNA = rb.build(newDNA);
	}
	
	/*
	 * Deletes DNA up to DNAindex, and resets DNAindex.
	 */
	public void deleteDNAuptoIndex()
	{
		DNA = DNA.delete(0,DNAindex);
		DNAindex = 0;
	}
	/*
	 * Get the current contents of RNA. Used for testing.
	 */
	public Rope getRNA()
	{
		return RNA;
	}

	public void execute()
	  {
		int iteration = 0;
		//long startTime = System.currentTimeMillis();
	    while(!finish && DNA.length() > 0) // && (System.currentTimeMillis() - startTime < 10000))
	    {
	      if (logging == LogLevel.ITERATIONS) System.out.println("Onto iteration "+iteration);
	      if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH)
	    	  writeLog("iteration "+iteration+"\n");
	      if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH) 
	    	  writeLog("dna = "
	    		  +DNA.subSequence(0,Math.min(10,DNA.length()))
	    		  +(DNA.length()>10 ? "... " : " ")
	    		  +"("+DNA.length()+" bases)\n");
		  DNAindex = 0;
	      // Define a pattern type, set it to p.
	      Rope p = pattern();
	      if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH)
	    	  writeLog("pattern "+p.toString()+"\n");
	      if (logging == LogLevel.GENEMATCH)
	    	  writeLog(tryGeneMatch(p));
	      if(finish) break;
	      // Define a template type, set it to t.
	      Rope t = template();
	      if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH)
	    	  writeLog("template "+t.toString()+"\n");
	      if(finish) break;
	      deleteDNAuptoIndex();
	      matchreplace(p,t);
	      if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH)
	    	  writeLog("len(rna) = "+RNA.length()/7+"\n");
	      if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH)
	    	  writeLog("\n");
	      ++iteration;
	    }
	    finish();
	  }
	  
	/*
	   * - pattern() : [Specifies a pattern for pattern-matching]
	   *     Loop through DNA until we reach a code telling us it's the end of the pattern, 
	   *     or until we reach an unrecognised code (this means we trash the pattern and go
	   *     to output the RNA), or until we run out of DNA (again, trash the pattern and go 
	   *     to output the RNA).
	   *      As we process, store the results of our processed DNA in one place (the pattern)
	   *     and remove what we've processed from the DNA string.
	   *     
	   *     Uses custom notation for skip and search, as naive regex matching won't work
	   *     for matchreplace(), so there's no advantage to standard regex notation.
	   *     
	   *     GOTCHA: Ropes.delete(start,end) deletes from position start
	   *     to position end-1 (thus delete(0,0) does nothing and causes
	   *     infinite loop). This is not helpful.
	   */
	  public Rope pattern()
	  {
	    Rope p = e;
	    int level = 0;
	    while(DNAindex < DNA.length() && !finish)
	    {
	      char charFirst = DNA.charAt(DNAindex);
	      switch (charFirst)
	      {
	        case 'C':
		      if (logging == LogLevel.VERBOSE) writeLog("I");
		      DNAindex += 1; //DNA = DNA.delete(0,1);
	          p = p.append("I");
	          break;	  
	        case 'F':
		      if (logging == LogLevel.VERBOSE) writeLog("C");
		      DNAindex += 1; //DNA = DNA.delete(0,1);
	          p = p.append("C");
	          break;
	        case 'P':
		      if (logging == LogLevel.VERBOSE) writeLog("F");
		      DNAindex += 1; //DNA = DNA.delete(0,1);
	          p = p.append("F");
	          break;
	        case 'I':
	          char charSecond = DNA.charAt(DNAindex+1);
	          switch (charSecond)
	          {
	            case 'C':
			      if (logging == LogLevel.VERBOSE) writeLog("P");
			      DNAindex += 2; //DNA = DNA.delete(0,2);
	              p = p.append("P");
	              break;
	            case 'P':
	              DNAindex += 2; //DNA = DNA.delete(0,2);
	              int n = nat();
	              if (logging == LogLevel.VERBOSE) writeLog("{"+n+"}");
	              if (finish) break;
	              // Add "Skip the next n bases" as "{n}".
	              p = p.append("{");
	              p = p.append(Integer.toString(n));
	              p = p.append("}");
	              break;	      
	            case 'F':
	              DNAindex += 3; //DNA = DNA.delete(0,3);
	              // Interpret next part as encoded sequence of bases.
	              Rope s = consts();
	              if (logging == LogLevel.VERBOSE) writeLog("["+s.toString()+"]");
	              // Add "Search for the sequence s" as "[s]".
	              p = p.append("[");
	              p = p.append(s);
	              p = p.append("]");
	              break;
	            case 'I':
	              char charThird = DNA.charAt(DNAindex+2);
	              switch (charThird)
	              {
	                case 'P':
	                  if (logging == LogLevel.VERBOSE) writeLog("(");
	                  DNAindex += 3; //DNA = DNA.delete(0,3);
	                  level++;
	                  p = p.append("(");
	                  break;
	                case 'C': /* FALL THRU */
	                case 'F':
	                  DNAindex += 3; //DNA = DNA.delete(0,3);
	                  if (level == 0) { return p; }
	                  else { level--; p = p.append(")"); }
	                  if (logging == LogLevel.VERBOSE) writeLog(")");
	                  break;
	                case 'I':
	                  RNA = RNA.append(DNA.subSequence(DNAindex+3,DNAindex+10));
	                  DNAindex += 10; //DNA = DNA.delete(0,10);
	                  break;
	                default:
	                  finish = true;
	                  break;
	              }
	              break;
	            default:
	              finish = true;
	              break;
	          }
	          break;
	        default:
	          finish = true;
	          break;
	      }
	    }
	    return p;
	  }

	  /*
	   * - template() : [Specifies a template to be matched for pattern-matching]
	   *     Loop through DNA until we reach a code telling us it's the end of the template,
	   *     or until we reach an unrecognised code (this means we trash the template and go
	   *     to output the RNA), or until we run out of DNA (again, trash the template and go
	   *     to output the RNA).
	   *      As we process, store the results of our processed DNA in one place (the template)
	   *     and remove what we've processed from the DNA string.
	   */
	  public Rope template()
	  {
		if (logging == LogLevel.VERBOSE) writeLog(" -> ");
	    Rope t = e;
	    while(DNAindex < DNA.length() && !finish)
	    {
	      if (DNA.length() == DNAindex)
	      {
	    	  finish = true;
	    	  return e;
	      }
	      char charFirst = DNA.charAt(DNAindex);
	      switch (charFirst)
	      {
	        case 'C':
	          if (logging == LogLevel.VERBOSE) writeLog("I");
	          DNAindex += 1; //DNA = DNA.delete(0,1);
	          t = t.append("I");
	          break;	  
	        case 'F':
		      if (logging == LogLevel.VERBOSE) writeLog("C");
		      DNAindex += 1; //DNA = DNA.delete(0,1);
	          t = t.append("C");
	          break;
	        case 'P':
		      if (logging == LogLevel.VERBOSE) writeLog("F");
		      DNAindex += 1; //DNA = DNA.delete(0,1);
	          t = t.append("F");
	          break;
	        case 'I':
	          char charSecond = DNA.charAt(DNAindex+1);
	          switch (charSecond)
	          {
	            case 'C':
		          if (logging == LogLevel.VERBOSE) writeLog("P");
		          DNAindex +=2; //DNA = DNA.delete(0,2);
	              t = t.append("P");
	              break;
	            case 'F': /* FALL THRU */
	            case 'P':
	              DNAindex += 2; //DNA = DNA.delete(0,2);
	              int level = nat();
	              if (finish) break;
	              int n = nat();
	              if (finish) break;
	              t = t.append("<");
	              t = t.append(Integer.toString(n));
	              t = t.append("_");
	              t = t.append(Integer.toString(level));
	              t = t.append(">");
	              if (logging == LogLevel.VERBOSE) writeLog("<"+n+"_"+level+">");
	              break;
	            case 'I':
	              char charThird = DNA.charAt(DNAindex+2);
	              switch (charThird)
	              {
	                case 'C': /* FALL THRU */
	                case 'F':
	                  if (logging == LogLevel.VERBOSE) writeLog("\n");
	                  DNAindex +=3; //DNA = DNA.delete(0,3);
	                  return t;
	                case 'P':
	                  DNAindex += 3; //DNA = DNA.delete(0,3);
		              int m = nat();
		              if (finish) break;
	                  t = t.append("|");
		              t = t.append(Integer.toString(m));
	                  t = t.append("|");
	                  if (logging == LogLevel.VERBOSE) writeLog("|"+m+"|");
	                  break;
	                case 'I':
	                  RNA = RNA.append(DNA.subSequence(DNAindex+3,DNAindex+10));
	                  DNAindex +=10; //DNA = DNA.delete(0,10);
	                  break;
	                default:
	                  finish = true;
	                break;
	              }
	              break;
	            default:
	              finish = true;
	              break;
	          }
	          break;
	        default:
	          finish = true;
	          break;
	      }
	    }
	    return e;
	  }

	  /*
	   * - matchreplace(p,t) : [Attempts to match a pattern to part of the DNA string then replace
	   *                        appropriate parts of the match with a template]
	   *     Loop through the supplied pattern, keeping track of the parts it is comparing it to
	   *     in the DNA, until we reach a part of the pattern that does not match (stop the
	   *     matching and return to processing pattern-template-matchreplace).
	   *      As we process, keep track of where we are in the DNA string. When we come to the start
	   *     of groups (parts of the pattern where ( starts and ) ends them), store the index of the
	   *     start of that group; when we come to the end of groups, add the string of bases that 
	   *     were in that group to a list of "environments" that will be modified.
	   *      If we get through the whole pattern without stopping the matching, remove what we've
	   *     processed from the DNA string, and replace the environments with the templates using
	   *     replace().
	   */
	  public ArrayList<Rope> matchreplace(Rope pat, Rope t)
	  {
		  int index = 0;
		  int pIndex = 0;
		  ArrayList<Rope> environment = new ArrayList<Rope>();
		  ArrayList<Integer> openItems = new ArrayList<Integer>();
		  while (pIndex < pat.length())
		  {
			  char currentChar = pat.charAt(pIndex);
			  switch(currentChar)
			  {
			    case 'I': /* FALL THRU */
			    case 'C': /* FALL THRU */
			    case 'F': /* FALL THRU */
			    case 'P':
			    	pIndex++;
			    	if (DNA.charAt(index) == currentChar)
			    	{
			    		index++;
			    	}
			    	else
			    	{
			    		if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH) writeLog("failed match\n");
			    		return environment;
			    	}
			    	break;
			    case '{':
			    	// Deal with skip case.
			    	pIndex++; // gets rid of the '{'.
			    	char nextChar = pat.charAt(pIndex);
			    	int n = 0;
			    	while (nextChar!='}') {
			    	  n = (n*10) + Character.digit(nextChar,10);
			    	  pIndex++;
			    	  nextChar = pat.charAt(pIndex);
			    	}
			    	pIndex++; // gets rid of the '}'.
			    	index += n;
			    	if (index > DNA.length())
			    	{
			    		if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH) writeLog("failed match\n");
			    		return environment;
			    	}
			    	break;
			    case '[':
			    	// Deal with search case.
			    	StringBuilder s = new StringBuilder();
			    	pIndex++; // gets rid of the '['.
			    	nextChar = pat.charAt(pIndex);
			    	while (nextChar!=']') {
			    	  s.append(nextChar);
			    	  pIndex++;
			    	  nextChar = pat.charAt(pIndex);
			    	}
			    	pIndex++; // gets rid of the ']'.
			    	int firstMatch = DNA.indexOf(s.toString(),index) + s.length();
			    	if (firstMatch >= s.length() && firstMatch >= index) // This covers case where firstMatch is -1, i.e. no match
			    	{
			    		index = firstMatch;
			    	}
			    	else
			    	{
			    		if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH) writeLog("failed match\n");
			    		return environment;
			    	}
			    	break;
			    case '(':
			    	// Deal with opening group.
			    	pIndex++; // gets rid of the '('.
			    	openItems.add(0,index);
			    	break;
			    case ')':
			    	// Deal with closing group.
			    	pIndex++; // gets rid of the ')'.
			    	environment.add(DNA.subSequence(openItems.get(0),index));
			    	openItems.remove(0);
			    	break;
			  }
		  }
		DNA = DNA.delete(0,index);
		if (logging == LogLevel.TRACE || logging == LogLevel.GENEMATCH)
		{
			writeLog("successful match of length "+index+"\n");
			for (int loop=0; loop<environment.size(); ++loop)
			{
				Rope thing = environment.get(loop);
				writeLog("e["+loop+"] = "
					+thing.subSequence(0,Math.min(10,thing.length()))
			    	+(thing.length()>10 ? "... " : " ")
			    	+"("+thing.length()+" bases)\n");

			}
		}
		replace(t,environment);
		return environment;
	  }
	  
	  /*
	   * - replace(t,e) : [Uses the contents of matched base strings to make a replacement DNA string
	   *                   to prepend to the existing DNA string]
	   *     Loop through the supplied template.
	   *      As we process, keep track of what we need to add to the replacement; this will either be
	   *     bases, repeatedly-quoted versions of matched base strings, or lengths of matched base
	   *     strings encoded as DNA.
	   *      Once we have got through the supplied template, prepend the DNA to be added to the
	   *     existing DNA string.
	   */
	  public void replace(Rope t,ArrayList<Rope> environment)
	  {
		  Rope r = e;
		  int tIndex = 0;
		  while (tIndex < t.length())
		  {
			  char currentChar = t.charAt(tIndex);
			  switch(currentChar)
			  {
			  	case 'I': /* FALL THRU */
			  	case 'C': /* FALL THRU */
			  	case 'F': /* FALL THRU */
			  	case 'P':
			  		tIndex++;  
			  		r = r.append(currentChar);
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
			    	if (n < environment.size())
			    	{
				    	r = r.append(protect(l,environment.get(n)));			    		
			    	}
			    	else
			    	{
			    		r = r.append(protect(l,e));
			    	}
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
			    	if (n < environment.size())
			    	{
			    		r = r.append(asnat(environment.get(n).length()));
			    	}
			    	else
			    	{
			    		r = r.append(asnat(0));
			    	}
			    	break;
			  }
		  }
		  DNA = r.append(DNA);
	  }
	  
	  /*
	   * - nat() : [Decodes a natural number]
	   *     Loop through DNA until we reach a code telling us it's the end of the number
	   *     we're reading, or until we run out of DNA (trash the pattern and number and go
	   *     to output the RNA).
	   *      As with pattern(), as we process, store the results of our processed DNA in one 
	   *     place (the number) and remove what we've processed from the DNA string.
	   */
	  public int nat()
	  {
		  // Iterative version. Read up to number terminator, then process rope.
		  StringBuilder buildingNumber = new StringBuilder();
		  while(DNAindex < DNA.length() && DNA.charAt(DNAindex)!='P')
		  {
			  buildingNumber.append(DNA.charAt(DNAindex));
			  DNAindex +=1; //DNA = DNA.delete(0,1);
		  }
		  if (DNA.length() == DNAindex)
		  {
			  finish = true;
			  return 0;
		  }
		  DNAindex +=1; //DNA = DNA.delete(0,1); // to remove the terminating P...
		  Rope number = rb.build(buildingNumber); // ...which this rope doesn't include.
		  if (number.equals(e))
		  {
			  return 0;
		  }
		  else
		  {
			  int value = 0;
			  for (int pos=0; pos<number.length(); pos++)
			  {
				  if (number.charAt(pos)=='C')
					  value += Math.pow(2, pos); // Need an integer version.
			  }
			  return value;
		  }
		  
		/*
		 * Recursive version, as specified in the report:
		 * 
		 * char charStart = DNA.charAt(0);
	     * switch (charStart)
	     * {
	     *   case 'P':
	     *     DNA = DNA.delete(0,1);
	     *     return 0;
	     *   case 'I': FALL THRU
	     *   case 'F':
	     *     DNA = DNA.delete(0,1);
	     *     return 2*nat();
	     *   case 'C':
	     *     DNA = DNA.delete(0,1);
	     *     return(2*nat())+1;
	     *   default:
	     *     finish = true;
	     *     return 0;
	     * }
		 */
	    
	  }

	  /*
	   * - asnat() : [Encodes a natural number]
	   *     Take a natural number and returns a representation of it encoded in DNA form.
	   *      Numbers are in binary with most significant bit last, and terminated with P.
	   *     So, for example, decimal 10 (binary 1010) would be stored as ICICP, and decimal 25
	   *     (binary 11001) would be stored as CIICCP.
	   */
	  public Rope asnat(int n)
	  {
		  Rope number = e;
		  for (; n >0; n/=2)
		  {
			 number = number.append(n % 2 == 0 ? "I" : "C");
		  }
		  number = number.append("P");
		  return number;
	  }
	  
	  /*
	   * - consts() : [Decodes a sequence of bases]
	   *     Loop through DNA until we reach a code we don't recognise or until we run out of DNA
	   *     (return to whatever called this method).
	   *      As with pattern() and nat(), as we process, store the results of our processed DNA
	   *     in one place (the decoded DNA) and remove what we've processed from the DNA string.
	   */
	  public Rope consts()
	  {
		Rope decoded = e;
		while(DNAindex < DNA.length())
		{
			switch (DNA.charAt(DNAindex))
			{
			  case 'C':
		    	DNAindex += 1; //DNA = DNA.delete(0,1);
				decoded = decoded.append("I");
				break;
			  case 'F':
				DNAindex += 1; //DNA = DNA.delete(0,1);
				decoded = decoded.append("C");
				break;
			  case 'P':
				DNAindex += 1; //DNA = DNA.delete(0,1);
				decoded = decoded.append("F");
				break;
			  case 'I':
				if (DNA.charAt(DNAindex+1) == 'C')
				{
					DNAindex += 2; //DNA = DNA.delete(0,2);
					decoded = decoded.append("P");
				}
				else
				{
					return decoded;
				}
			}
		}
		finish = true;
		return decoded;

		/*
		 * Recursive version as in the spec:
		 * 
	     * char charFirst = DNA.charAt(0);
	     * switch (charFirst)
	     * {
	     *   case 'C':
	     *     DNA = DNA.delete(0,0);
	     *     return consts().append("I");
	     *   case 'F':
	     *     DNA = DNA.delete(0,0);
	     *     return consts().append("C");
	     *   case 'P':
	     *     DNA = DNA.delete(0,0);
	     *     return consts().append("F");
	     *   case 'I':
	     *     char charSecond = DNA.charAt(1);
	     *     switch (charSecond)
	     *     {
	     *       case 'C':
	     *         DNA = DNA.delete(0,1);
	     *         return consts().append("P");
	     *       default:
	     *         return e;
	     *     }
	     *   default:
	     *     return e;
	     * }
	     */
	  }

	  /*
	   * - protect(l,d) : [Repeatedly encodes a sequence of bases using quote()]
	   *     Call the quote() method on d repeatedly until it's been done l times.
	   */
	  public Rope protect(int l, Rope d)
	  {
		  Rope prot = d;
		  for(int i=0; i<l; i++)
		  {
			  prot = quote(prot);
		  }
		  return prot;
	  }
	  
	  /*
	   * - quote() : [Encode a sequence of bases]
	   *     Go through all the bases in a DNA string, turning them into their "quoted" forms. (As
	   *     for protect(), this is in the spec as recursive but can easily be made iterative.)
	   */
	  public Rope quote(Rope d)
	  {
		  Rope quoted = e;
		  for (int i=0; i< d.length(); i++)
		  {
			  char currentChar = d.charAt(i);
			  switch(currentChar)
			  {
			  	case 'I':
			  	  quoted = quoted.append("C");
			  	  break;
			  	case 'C':
			  	  quoted = quoted.append("F");
			  	  break;
			  	case 'F':
			  	  quoted = quoted.append("P");
			  	  break;
			  	case 'P':
			  	  quoted = quoted.append("IC");
			  	  break;
			  }
		  }
		  return quoted;
	  }
	  
	  private void finish()
	  {
		  if (logging == LogLevel.VERBOSE)
			  writeLog("Finished processing (writing RNA to file)");
	    // Outputs RNA string to file endo.rna.
		try {
		  BufferedWriter buf = new BufferedWriter(new FileWriter(outputFilename));
		  Iterator<Character> it = RNA.iterator();
		  while (it.hasNext())
		  {
			buf.write(it.next());  
		  }
		  buf.flush();
		  buf.close();
		  System.out.printf("RNA written to %s.\n",outputFilename);
		} catch (IOException e) {
		  System.out.println("Problem writing to endo.rna.");
		}
	  }
	  
	  private String tryGeneMatch(Rope p)
	  {
		  
			return "";
	  }

	  
	  private void writeLog(String step)
	  {
		  try {
			  debugbuf.write(step);
			  debugbuf.flush();
		  } catch (IOException e) {
			  System.out.println("Problem writing to Endo debug log in writeLog.");
			  e.printStackTrace();
		  }
	  }
	
}
