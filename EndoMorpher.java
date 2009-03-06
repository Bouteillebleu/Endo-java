// EndoMorpher.java

// Compile: $JAVAC -Xlint -cp ropes-1.1.3/ropes.jar EndoMorpher.java
// Run: $JAVA -classpath .:ropes-1.1.3/ropes.jar EndoMorpher filename

// This one class probably does all the work, I admit.
// I'm still a bit hazy about how packages and so on fit together.

import java.awt.image.BufferedImage;

import org.ahmadsoft.ropes.*;

public class EndoMorpher {

  private static RopeBuilder rb = new RopeBuilder();
  public static final Rope e = rb.build("");
  public static Rope DNA = e;
  public static Rope RNA = e;
  public static boolean finish = false;
  
  public static void main(String args[])
  {
    // The input we have is:
    // * DNA prefix. Either in text form as args[0], or in a file.
    // * Endo's base DNA. In a file whose path is args[1].
    // * Various options for stepping through output / tracing execution.
    
	  // So, what does this do? From the spec:

	  // ** Turns DNA into RNA
	  //    -  execute() method.
	  // ** Simulates biosynthesis from RNA by generating an image
	  //    -  build() method.

	  // We can separate these into two classes,
	  // or because I'm lame we could just separate them into two functions
	  // with more little methods hanging off.

	  
  }
  
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

  private void finish()
  {
    // Outputs RNA string.
  }

  public void build()
  {

  }

}