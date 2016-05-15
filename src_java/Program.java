import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Program {
	public static void main(String[] agrs)
	{
		String graphdir = "/Users/vunh/Documents/SBU/CourseWork/CSE534 - Network/Project/sample_files_backup/without_cache/desktop_wifi-fast_top200_orig_inline/graphs/";
		String tempdir = "/Users/vunh/Documents/SBU/CourseWork/CSE534 - Network/Project/sample_files_backup/without_cache/desktop_wifi-fast_top200_orig_inline/temp_files/";
		
		File dir = new File(graphdir);
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".json");
		    }
		});
		
		Map<String, Integer> objtypenumber = new HashMap<String, Integer>();
		Map<String, Double> objtypetime = new HashMap<String, Double>();
		ArrayList<String> pagetime = new ArrayList<String>();
		
		for (int i = 0; i< files.length; i++)
		{
			System.out.println(i);
			File currFile = files[i];
			String jsfilename = currFile.getName();
			if (!jsfilename.startsWith("original.testbed.localhost_"))
			{
				// The case where the file name is not in the correct format
				continue;
			}
			
			
			// Get the temp file
			String tempfilename = jsfilename.substring(0, jsfilename.length() - 5) + "--1";
			File f = new File(tempdir + tempfilename);
			if(!f.exists() || f.isDirectory()) { 
			    continue;
			}
			
			
			// Do main process here
			pagetime.add(readTempFile(tempdir + tempfilename));
			try {
				Parserf1 json = new Parserf1(graphdir + jsfilename);
				ArrayList<HTMLObjects> objlist = json.getListObjects();
				for (int iObj = 0; iObj < objlist.size(); iObj++)
				{
					HTMLObjects obj = objlist.get(iObj);
					if (objtypetime.containsKey(obj.getObjType()))
					{
						objtypetime.put(obj.getObjType(), objtypetime.get(obj.getObjType()) + obj.getObjLoadTime());
					}
					else
					{
						objtypetime.put(obj.getObjType(), obj.getObjLoadTime());
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Check twin file
			if (i < files.length - 1 && jsfilename.charAt(jsfilename.length() - 6) == '_')
			{
				String nextfilename = files[i+1].getName();
				if (jsfilename.substring(0, jsfilename.length() - 6).equals(nextfilename.substring(0, nextfilename.length() - 5)))
				{
					i = i + 1;
				}
			}
		}
		
		double sumtime = 0;
		for (int i = 0; i< pagetime.size(); i++)
		{
			System.out.println(pagetime.get(i));
			sumtime += Double.parseDouble(pagetime.get(i));
		}
		
		System.out.println(sumtime);
		
		
		for (Map.Entry<String, Double> entry : objtypetime.entrySet()) {
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
		
	}
	
	public static String readTempFile(String path)
	{
		try
		{
			Scanner scan = new Scanner(new File(path));
		    if (scan.hasNextLine()){
		        String line = scan.nextLine();
		        String[] parts = line.split("\t");
		        return parts[1];
		    }
		    
		    scan.close();
		}
	    catch(Exception ex)
	    {
	    	System.out.println(ex.getMessage());
	    }
	    
		return null;
	}
}
