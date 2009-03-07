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