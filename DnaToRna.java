import org.ahmadsoft.ropes.Rope;
import org.ahmadsoft.ropes.RopeBuilder;


public class DnaToRna {

	  private static RopeBuilder rb = new RopeBuilder();
	  public static final Rope e = rb.build("");
	  private Rope DNA = e;
	  private boolean finish = false;
	  
	  public void execute()
	  {
	    // We already set DNA to the prefix + Endo's base in main().
	    // So now let's repeat.
	    while(!finish)
	    {
	      // Define a pattern type, set it to p.
	      Rope p = pattern(); // (placeholder)
	      // Define a template type, set it to t.
	      Rope t = template(); // (placeholder)
	      matchreplace(p,t);
	    }
	    finish();
	  }

	  public Rope pattern()
	  {
	    Rope p = e;
	    int level = 0;
	    while(!finish)
	    {
	      // Let's have some sort of crazy nested switch statement here.
	      char charFirst = DNA.charAt(0);
	      switch (charFirst)
	      {
	        case 'C':
	          DNA = DNA.delete(0,0);
	          p = p.append("I");
	          break;	  
	        case 'F':
	          DNA = DNA.delete(0,0);
	          p = p.append("C");
	          break;
	        case 'P':
	          DNA = DNA.delete(0,0);
	          p = p.append("F");
	          break;
	        case 'I':
	          char charSecond = DNA.charAt(1);
	          switch (charSecond)
	          {
	            case 'C':
	              DNA = DNA.delete(0,0);
	              p = p.append("P");
	              break;
	            case 'P':
	              DNA = DNA.delete(0,1);
	              // Interpret the next thing in the DNA string
	              // as a natural number.
	              int n = nat();
	              // Add an instruction to p -
	              // "skip the next n bases".
	              // We can do this with the regex ".{n}".
	              p = p.append(".{");
	              p = p.append(Integer.toString(n));
	              p = p.append("}");
	              break;	      
	            case 'F':
	              DNA = DNA.delete(0,2);
	              // Interpret the next thing in the DNA string
	              // as an encoded sequence of bases.
	              Rope s = consts();
	              // Add an instruction to p -
	              // "search for the sequence s".
	              //
	              // I need to check what exactly this does,
	              // but under the interpretation I think it has,
	              // we can do this with the regex ".*?" and then s.
	              p = p.append(".*?");
	              p = p.append(s);
	              break;
	            case 'I':
	              char charThird = DNA.charAt(2);
	              switch (charThird)
	              {
	                case 'P':
	                  DNA = DNA.delete(0,2);
	                  level++;
	                  p = p.append("(");
	                  break;
	                case 'C': /* FALL THRU */
	                case 'F':
	                  if (level == 0) return p;
	                  else level--;
	                  break;
	                case 'I':
	                  RNA = RNA.append(DNA.subSequence(3,9));
	                  DNA = DNA.delete(0,9);
	                  break;
	                default:
	                  finish = true;
	                  finish();
	              }
	              break;
	            default:
	              finish = true;
	              finish();
	          }
	          break;
	        default:
	          finish = true;
	          finish();
	      }
	    }
	    return e;
	  }

	  private Rope template()
	  {
	    return e;
	  }

	  private void matchreplace(Rope pat, Rope t)
	  {

	  }
	  
	  private int nat()
	  {
	    char charStart = DNA.charAt(0);
	    switch (charStart)
	    {
	      case 'P':
	        DNA = DNA.delete(0,0);
	        return 0;
	      case 'I': /* FALL THRU */
	      case 'F':
	        DNA = DNA.delete(0,0);
	        return 2*nat();
	      case 'C':
	        DNA = DNA.delete(0,0);
	        return(2*nat())+1;
	      default:
	        finish = true;
	        finish();
	    }
	  }

	  /*
	   * What methods do we need?
	   * 
	   * - pattern() : [Specifies a pattern for pattern-matching]
	   *     Loop through DNA until we reach a code telling us it's the end of the pattern, 
	   *     or until we reach an unrecognised code (this means we trash the pattern and go
	   *     to output the RNA), or until we run out of DNA (again, trash the pattern and go 
	   *     to output the RNA).
	   *      As we process, store the results of our processed DNA in one place (the pattern)
	   *     and remove what we've processed from the DNA string.
	   * 
	   * - template() : [Specifies a template to be matched for pattern-matching]
	   *     Loop through DNA until we reach a code telling us it's the end of the template,
	   *     or until we reach an unrecognised code (this means we trash the template and go
	   *     to output the RNA), or until we run out of DNA (again, trash the template and go
	   *     to output the RNA).
	   *      As we process, store the results of our processed DNA in one place (the template)
	   *     and remove what we've processed from the DNA string.
	   * 
	   * - nat() : [Decodes a natural number]
	   *     Loop through DNA until we reach a code telling us it's the end of the number
	   *     we're reading, or until we run out of DNA (trash the pattern and number and go
	   *     to output the RNA).
	   *      As with pattern(), as we process, store the results of our processed DNA in one 
	   *     place (the number) and remove what we've processed from the DNA string.
	   *      TODO: Figure out how to store the number in a way that doesn't involve recursive
	   *     calls to the same method (even though this is the way it is specified, it's unlikely
	   *     to be the best idea).
	   * 
	   * - consts() : [Decodes a sequence of bases]
	   *     Loop through DNA until we reach a code we don't recognise or until we run out of DNA
	   *     (return to whatever called this method).
	   *      As with pattern() and nat(), as we process, store the results of our processed DNA
	   *     in one place (the decoded DNA) and remove what we've processed from the DNA string.
	   *      TODO: As with nat(), figure out how to store the decoded DNA in a way that doesn't
	   *     involve recursive calls to the same method.  
	   */

	
}
