package source;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class GenomeReader {

	public HashMap<Integer,Gene> geneList;
	
	public class Gene {
		String name;
		int length;
		
		Gene(int length, String name)
		{
			this.name = name;
			this.length = length;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public int getLength()
		{
			return this.length;
		}
	}
	
	public GenomeReader(String geneListFilename)
	{
		try {
			BufferedReader in = new BufferedReader(
					            new InputStreamReader(
					            new FileInputStream(geneListFilename)));
			geneList = new HashMap<Integer,Gene>();
			String line = in.readLine();
			while (line != null)
			{
				String[] data = line.split(",");
				geneList.put(Integer.parseInt(data[0],16),
						     new Gene(Integer.parseInt(data[1],16),
						    		  data[2])
						     );
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			System.out.printf("Problem reading from %s.\n",geneListFilename);
		}

	}

}
