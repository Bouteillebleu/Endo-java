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
	private boolean finish = false;
	private String outputFilename;
	private BufferedWriter debugbuf;
	private enum LogLevel { NONE, TRACE, VERBOSE, OVERLYVERBOSE, ITERATIONS };
	private LogLevel logging = LogLevel.NONE;
	
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
			  if (args[3].equals("--logging=verbose"))
			  {
				d2r.logging = LogLevel.OVERLYVERBOSE;
			  }
			  else if (args[3].equals("--logging=trace"))
			  {
				d2r.logging = LogLevel.TRACE;
			  }
			  else
			  {
				d2r.logging = LogLevel.VERBOSE;
			  }
			}

		}
		
		d2r.execute();
		if (d2r.logging != LogLevel.NONE)
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
	 * Get the current contents of RNA. Used for testing.
	 */
	public Rope getRNA()
	{
		return RNA;
	}

	public void execute()
	  {
		int iteration = 0;
		long startTime = System.currentTimeMillis();
	    while(!finish && (System.currentTimeMillis() - startTime < 10000))
	    {
	      if (logging == LogLevel.ITERATIONS) System.out.println("Onto iteration "+iteration);
	      if (logging == LogLevel.TRACE) writeLog("iteration "+iteration+"\n");
	      if (logging == LogLevel.TRACE) writeLog("dna = "
	    		  +DNA.subSequence(0,Math.min(10,DNA.length()))
	    		  +(DNA.length()>10 ? "... " : " ")
	    		  +"("+DNA.length()+" bases)\n");
	      // Define a pattern type, set it to p.
	      Rope p = pattern();
	      if (logging == LogLevel.TRACE) writeLog("pattern "
	    		  +p.toString()+"\n");
	      if(finish) break;
	      // Define a template type, set it to t.
	      Rope t = template();
	      if (logging == LogLevel.TRACE) writeLog("template "
	    		  +t.toString()+"\n");
	      if(finish) break;
	      matchreplace(p,t);
	      if (logging == LogLevel.TRACE) writeLog("len(rna) = "
	    		  +RNA.length()/7+"\n");
	      if (logging == LogLevel.TRACE) writeLog("\n");
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
	    int index = 0; // Traverse the DNA string with this, then delete up to it before we leave the function.
	    while(DNA.length() > index && !finish)
	    {
	      char charFirst = DNA.charAt(index);
	      switch (charFirst)
	      {
	        case 'C':
		      if (logging == LogLevel.OVERLYVERBOSE) writeLog("Pattern (append I)");
		      if (logging == LogLevel.VERBOSE) writeLog("I");
	          index += 1; //DNA = DNA.delete(0,1);
	          p = p.append("I");
	          break;	  
	        case 'F':
			  if (logging == LogLevel.OVERLYVERBOSE) writeLog("Pattern (append C)");
		      if (logging == LogLevel.VERBOSE) writeLog("C");
		      index += 1; //DNA = DNA.delete(0,1);
	          p = p.append("C");
	          break;
	        case 'P':
		      if (logging == LogLevel.OVERLYVERBOSE) writeLog("Pattern (append F)");
		      if (logging == LogLevel.VERBOSE) writeLog("F");
		      index += 1; //DNA = DNA.delete(0,1);
	          p = p.append("F");
	          break;
	        case 'I':
	          char charSecond = DNA.charAt(index+1);
	          switch (charSecond)
	          {
	            case 'C':
	  		      if (logging == LogLevel.OVERLYVERBOSE) writeLog("Pattern (append P)");
			      if (logging == LogLevel.VERBOSE) writeLog("P");
			      index += 2; //DNA = DNA.delete(0,2);
	              p = p.append("P");
	              break;
	            case 'P':
	              if (logging == LogLevel.OVERLYVERBOSE) writeLog("Pattern (append nat)");
	              index += 2; //DNA = DNA.delete(0,2);
	              int n = nat();
	              if (logging == LogLevel.VERBOSE) writeLog("{"+n+"}");
	              if (finish) break;
	              // Add "Skip the next n bases" as "{n}".
	              p = p.append("{");
	              p = p.append(Integer.toString(n));
	              p = p.append("}");
	              break;	      
	            case 'F':
	              if (logging == LogLevel.OVERLYVERBOSE) writeLog("Pattern (append seq)");
	              index += 3; //DNA = DNA.delete(0,3);
	              // Interpret next part as encoded sequence of bases.
	              Rope s = consts();
	              if (logging == LogLevel.VERBOSE) writeLog("["+s.toString()+"]");
	              // Add "Search for the sequence s" as "[s]".
	              p = p.append("[");
	              p = p.append(s);
	              p = p.append("]");
	              break;
	            case 'I':
	              char charThird = DNA.charAt(index+2);
	              switch (charThird)
	              {
	                case 'P':
	                  if (logging == LogLevel.OVERLYVERBOSE) writeLog("Pattern (level +)");
	                  if (logging == LogLevel.VERBOSE) writeLog("(");
	                  index += 3; //DNA = DNA.delete(0,3);
	                  level++;
	                  p = p.append("(");
	                  break;
	                case 'C': /* FALL THRU */
	                case 'F':
	                  if (logging == LogLevel.OVERLYVERBOSE) writeLog("Pattern (level -)");
	                  index += 3; //DNA = DNA.delete(0,3);
	                  if (level == 0) { DNA = DNA.delete(0,index); return p; }
	                  else { level--; p = p.append(")"); }
	                  if (logging == LogLevel.VERBOSE) writeLog(")");
	                  break;
	                case 'I':
	                  if (logging == LogLevel.OVERLYVERBOSE) writeLog("Pattern (write RNA)");
	                  RNA = RNA.append(DNA.subSequence(3,10));
	                  index += 10; //DNA = DNA.delete(0,10);
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
	    DNA = DNA.delete(0,index);
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
	    while(DNA.length() > 0 && !finish)
	    {
	      if (DNA.length() == 0)
	      {
	    	  finish = true;
	    	  return e;
	      }
	      char charFirst = DNA.charAt(0);
	      switch (charFirst)
	      {
	        case 'C':
	          if (logging == LogLevel.OVERLYVERBOSE) writeLog("Template (append I)");
	          if (logging == LogLevel.VERBOSE) writeLog("I");
	          DNA = DNA.delete(0,1);
	          t = t.append("I");
	          break;	  
	        case 'F':
		      if (logging == LogLevel.OVERLYVERBOSE) writeLog("Template (append C)");
		      if (logging == LogLevel.VERBOSE) writeLog("C");
	          DNA = DNA.delete(0,1);
	          t = t.append("C");
	          break;
	        case 'P':
		      if (logging == LogLevel.OVERLYVERBOSE) writeLog("Template (append F)");
		      if (logging == LogLevel.VERBOSE) writeLog("F");
	          DNA = DNA.delete(0,1);
	          t = t.append("F");
	          break;
	        case 'I':
	          char charSecond = DNA.charAt(1);
	          switch (charSecond)
	          {
	            case 'C':
	  	          if (logging == LogLevel.OVERLYVERBOSE) writeLog("Template (append P)");
		          if (logging == LogLevel.VERBOSE) writeLog("P");
	              DNA = DNA.delete(0,2);
	              t = t.append("P");
	              break;
	            case 'F': /* FALL THRU */
	            case 'P':
	              if (logging == LogLevel.OVERLYVERBOSE) writeLog("Template (reference)");
	              DNA = DNA.delete(0,2);
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
	              char charThird = DNA.charAt(2);
	              switch (charThird)
	              {
	                case 'C': /* FALL THRU */
	                case 'F':
	                  if (logging == LogLevel.OVERLYVERBOSE) writeLog("Template (end)");
	                  if (logging == LogLevel.VERBOSE) writeLog("\n");
	                  DNA = DNA.delete(0,3);
	                  return t;
	                case 'P':
	                  if (logging == LogLevel.OVERLYVERBOSE) writeLog("Template (length)");
	                  DNA = DNA.delete(0,3);
		              int m = nat();
		              if (finish) break;
	                  t = t.append("|");
		              t = t.append(Integer.toString(m));
	                  t = t.append("|");
	                  if (logging == LogLevel.VERBOSE) writeLog("|"+m+"|");
	                  break;
	                case 'I':
	                  if (logging == LogLevel.OVERLYVERBOSE) writeLog("Template (write RNA)");
	                  RNA = RNA.append(DNA.subSequence(3,10));
	                  DNA = DNA.delete(0,10);
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
		  ArrayList<Rope> environment = new ArrayList<Rope>();
		  ArrayList<Integer> openItems = new ArrayList<Integer>();
		  while (pat.length() > 0)
		  {
			  char currentChar = pat.charAt(0);
			  switch(currentChar)
			  {
			    case 'I': /* FALL THRU */
			    case 'C': /* FALL THRU */
			    case 'F': /* FALL THRU */
			    case 'P':
			    	if (logging == LogLevel.OVERLYVERBOSE) writeLog("Matching (base)");
			    	pat = pat.delete(0,1);
			    	if (DNA.charAt(index) == currentChar)
			    	{
			    		index++;
			    	}
			    	else
			    	{
			    		if (logging == LogLevel.TRACE) writeLog("failed match\n");
			    		return environment;
			    	}
			    	break;
			    case '{':
			    	// Deal with skip case.
			    	if (logging == LogLevel.OVERLYVERBOSE) writeLog("Matching (skip)");
			    	pat = pat.delete(0,1); // gets rid of the '{'.
			    	char nextChar = pat.charAt(0);
			    	int n = 0;
			    	while (nextChar!='}') {
			    	  n = (n*10) + Character.digit(nextChar,10);
			    	  pat = pat.delete(0,1);
			    	  nextChar = pat.charAt(0);
			    	}
			    	pat = pat.delete(0,1); // gets rid of the '}'.
			    	index += n;
			    	if (index >= DNA.length())
			    	{
			    		if (logging == LogLevel.TRACE) writeLog("failed match\n");
			    		return environment;
			    	}
			    	break;
			    case '[':
			    	// Deal with search case.
			    	if (logging == LogLevel.OVERLYVERBOSE) writeLog("Matching (search)");
			    	StringBuilder s = new StringBuilder();
			    	pat = pat.delete(0,1); // gets rid of the '['.
			    	nextChar = pat.charAt(0);
			    	while (nextChar!=']') {
			    	  s.append(nextChar);
			    	  pat = pat.delete(0,1);
			    	  nextChar = pat.charAt(0);
			    	}
			    	pat = pat.delete(0,1); // gets rid of the ']'.
			    	int firstMatch = DNA.indexOf(s.toString(),index) + s.length();
			    	if (firstMatch >= index) // This covers case where firstMatch is -1, i.e. no match
			    	{
			    		index = firstMatch;
			    	}
			    	else
			    	{
			    		if (logging == LogLevel.TRACE) writeLog("failed match\n");
			    		return environment;
			    	}
			    	break;
			    case '(':
			    	// Deal with opening group.
			    	if (logging == LogLevel.OVERLYVERBOSE) writeLog("Matching (group open)");
			    	pat = pat.delete(0,1); // gets rid of the '('.
			    	openItems.add(0,index);
			    	break;
			    case ')':
			    	// Deal with closing group.
			    	if (logging == LogLevel.OVERLYVERBOSE) writeLog("Matching (group close)");
			    	pat = pat.delete(0,1); // gets rid of the ')'.
			    	environment.add(DNA.subSequence(openItems.get(0),index));
			    	openItems.remove(0);
			    	break;
			  }
		  }
		DNA = DNA.delete(0,index);
		if (logging == LogLevel.TRACE)
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
		  while (t.length() > 0)
		  {
			  char currentChar = t.charAt(0);
			  switch(currentChar)
			  {
			  	case 'I': /* FALL THRU */
			  	case 'C': /* FALL THRU */
			  	case 'F': /* FALL THRU */
			  	case 'P':
			  		if (logging == LogLevel.OVERLYVERBOSE) writeLog("Replacing (base)");
			  		t = t.delete(0,1);  
			  		r = r.append(currentChar);
			  		break;
			  	case '<':
			    	if (logging == LogLevel.OVERLYVERBOSE) writeLog("Replacing (reference)");
			  		t = t.delete(0,1); // gets rid of the '<'.
			    	char nextChar = t.charAt(0);
			    	int n = 0;
			    	while (nextChar!='_') {
			    	  n = (n*10) + Character.digit(nextChar,10);
			    	  t = t.delete(0,1);
			    	  nextChar = t.charAt(0);
			    	}
			    	t = t.delete(0,1); // gets rid of the '_'.
			    	nextChar = t.charAt(0);
			    	int l = 0;
			    	while (nextChar!='>') {
			    	  l = (l*10) + Character.digit(nextChar,10);
			    	  t = t.delete(0,1);
			    	  nextChar = t.charAt(0);
			    	}
			    	t = t.delete(0,1); // gets rid of the '>'.
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
			    	if (logging == LogLevel.OVERLYVERBOSE) writeLog("Replacing (length)");
			  		t = t.delete(0,1); // gets rid of the initial '|'.
			    	nextChar = t.charAt(0);
			    	n = 0;
			    	while (nextChar!='|') {
			    	  n = (n*10) + Character.digit(nextChar,10);
			    	  t = t.delete(0,1);
			    	  nextChar = t.charAt(0);
			    	}
			    	t = t.delete(0,1); // gets rid of the final '|'.
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
          if (logging == LogLevel.OVERLYVERBOSE) writeLog("Replacing done");
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
		  while(DNA.length() > 0 && DNA.charAt(0)!='P')
		  {
			  buildingNumber.append(DNA.charAt(0));
			  DNA = DNA.delete(0,1);
		  }
		  if (DNA.length() == 0)
		  {
			  finish = true;
			  return 0;
		  }
		  DNA = DNA.delete(0,1); // to remove the terminating P...
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
		while(DNA.length()>0)
		{
			switch (DNA.charAt(0))
			{
			  case 'C':
		    	if (logging == LogLevel.OVERLYVERBOSE) writeLog("consts (adding I)");
				DNA = DNA.delete(0,1);
				decoded = decoded.append("I");
				break;
			  case 'F':
				if (logging == LogLevel.OVERLYVERBOSE) writeLog("consts (adding C)");
				DNA = DNA.delete(0,1);
				decoded = decoded.append("C");
				break;
			  case 'P':
				if (logging == LogLevel.OVERLYVERBOSE) writeLog("consts (adding F)");
				DNA = DNA.delete(0,1);
				decoded = decoded.append("F");
				break;
			  case 'I':
				if (DNA.charAt(1) == 'C')
				{
					if (logging == LogLevel.OVERLYVERBOSE) writeLog("consts (adding P)");
					DNA = DNA.delete(0,2);
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
		  if (logging != LogLevel.NONE) writeLog("Finished processing (writing RNA to file)");
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
	  
	  private void writeLog(String step)
	  {
		  try {
			  if (logging == LogLevel.OVERLYVERBOSE)
			  {
				  debugbuf.write("Step: "+step+", ");
				  debugbuf.write("DNA length: "+DNA.length()+", ");
				  debugbuf.write("Start of DNA: "+DNA.subSequence(0,Math.min(10,DNA.length())));
				  debugbuf.newLine();
			  }
			  else
			  {
				  debugbuf.write(step);
			  }
			  debugbuf.flush();
		  } catch (IOException e) {
			  System.out.println("Problem writing to Endo debug log in writeLog.");
			  e.printStackTrace();
		  }
	  }
	
}
