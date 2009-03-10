package source;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

import org.ahmadsoft.ropes.Rope;
import org.ahmadsoft.ropes.RopeBuilder;


public class DnaToRna {

	private static RopeBuilder rb = new RopeBuilder();
	public static final Rope e = rb.build("");
	private Rope DNA = e;
	private Rope RNA = e;
	private boolean finish = false;
	
	// Default constructor that takes no arguments. For testing.
	public DnaToRna()
	{
		return;
	}
	
	/*
	 * TODO: Sort out how to do this with the Zip input stream reader?
	 */
	public DnaToRna(String prefix, String endoZipFilename)
	{
	  try {
		//BufferedReader in = new BufferedReader(new InputStreamReader(new ZipInputStream(new FileInputStream(endoZipFilename))));
	    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(endoZipFilename)));
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
	    while(!finish)
	    {
	      // Define a pattern type, set it to p.
	      Rope p = pattern();
	      if(finish) break;
	      // Define a template type, set it to t.
	      Rope t = template();
	      if(finish) break;
	      matchreplace(p,t);
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
	   *     TODO: Decide between using regex notation or custom notation; if custom, document.
	   *     
	   *     CHOSEN: Go for regex notation at the moment. If it turns out to be unsustainable,
	   *     reconsider and replan; for now, we'll just match it with the built in Ropes regex
	   *     matcher in matchreplace().
	   *     
	   *     GOTCHA: Ropes.delete(start,end) deletes from position start
	   *     to position end-1 (thus delete(0,0) does nothing and causes
	   *     infinite loop). This is not helpful.
	   */
	  public Rope pattern()
	  {
	    Rope p = e;
	    int level = 0;
	    while(!finish)
	    {
	      char charFirst = DNA.charAt(0);
	      switch (charFirst)
	      {
	        case 'C':
	          DNA = DNA.delete(0,1);
	          p = p.append("I");
	          break;	  
	        case 'F':
	          DNA = DNA.delete(0,1);
	          p = p.append("C");
	          break;
	        case 'P':
	          DNA = DNA.delete(0,1);
	          p = p.append("F");
	          break;
	        case 'I':
	          char charSecond = DNA.charAt(1);
	          switch (charSecond)
	          {
	            case 'C':
	              DNA = DNA.delete(0,2);
	              p = p.append("P");
	              break;
	            case 'P':
	              DNA = DNA.delete(0,2);
	              int n = nat();
	              if (finish) break;
	              // Add "Skip the next n bases".
	              // We can do this with the regex ".{n}".
	              p = p.append(".{");
	              p = p.append(Integer.toString(n));
	              p = p.append("}");
	              break;	      
	            case 'F':
	              DNA = DNA.delete(0,3);
	              // Interpret next part as encoded sequence of bases.
	              Rope s = consts();
	              // Add "Search for the sequence s".
	              // We can do this with the regex ".*?" and then s.
	              p = p.append(".*?");
	              p = p.append(s);
	              break;
	            case 'I':
	              char charThird = DNA.charAt(2);
	              switch (charThird)
	              {
	                case 'P':
	                  DNA = DNA.delete(0,3);
	                  level++;
	                  p = p.append("(");
	                  break;
	                case 'C': /* FALL THRU */
	                case 'F':
	                  DNA = DNA.delete(0,3);
	                  if (level == 0) return p;
	                  else { level--; p = p.append(")"); }
	                  break;
	                case 'I':
	                  RNA = RNA.append(DNA.subSequence(3,9));
	                  DNA = DNA.delete(0,10);
	                  break;
	                default:
	                  finish = true;
	              }
	              break;
	            default:
	              finish = true;
	          }
	          break;
	        default:
	          finish = true;
	      }
	    }
	    return e;
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
	  private Rope template()
	  {
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
	  private void matchreplace(Rope pat, Rope t)
	  {

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
	  private void replace(Rope t,ArrayList<Rope> e)
	  {
		  
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
	   *     
	   *      TODO: As with nat(), figure out a non-recursive way of doing this.
	   */
	  private Rope asnat(int n)
	  {
		  return e;
	  }
	  
	  /*
	   * - consts() : [Decodes a sequence of bases]
	   *     Loop through DNA until we reach a code we don't recognise or until we run out of DNA
	   *     (return to whatever called this method).
	   *      As with pattern() and nat(), as we process, store the results of our processed DNA
	   *     in one place (the decoded DNA) and remove what we've processed from the DNA string.
	   *      TODO: As with nat(), figure out how to store the decoded DNA in a way that doesn't
	   *     involve recursive calls to the same method.
	   */
	  private Rope consts()
	  {
	    char charFirst = DNA.charAt(0);
	    switch (charFirst)
	    {
	      case 'C':
	        DNA = DNA.delete(0,0);
	        return consts().insert(0,"I");
	      case 'F':
	        DNA = DNA.delete(0,0);
	        return consts().insert(0,"C");
	      case 'P':
	        DNA = DNA.delete(0,0);
	        return consts().insert(0,"F");
	      case 'I':
	        char charSecond = DNA.charAt(1);
	        switch (charSecond)
	        {
	          case 'C':
	            DNA = DNA.delete(0,1);
	            return consts().insert(0,"P");
	          default:
	            return e;
	        }
	      default:
	        return e;
	    }
	  }

	  /*
	   * - protect(l,d) : [Repeatedly encodes a sequence of bases using quote()]
	   *     Call the quote() method on d repeatedly until it's been done l times. (This is in the 
	   *     spec as recursive, but can easily be made iterative.)
	   */
	  private void protect(int l, Rope d)
	  {
		  
	  }
	  
	  /*
	   * - quote() : [Encode a sequence of bases]
	   *     Go through all the bases in a DNA string, turning them into their "quoted" forms. (As
	   *     for protect(), this is in the spec as recursive but can easily be made iterative.)
	   */
	  private Rope quote(Rope d)
	  {
		  return e;
	  }
	  
	  private void finish()
	  {
	    // Outputs RNA string.
	  }

	
}
