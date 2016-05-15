import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Experiment {
	public static void main(String[] args)
	{
		String graphdir_cache = "/Users/vunh/Documents/SBU/CourseWork/CSE534 - Network/Project/sample_files_jsondag/with_cache/graphs/";
		//String tempdir_cache = "/Users/vunh/Documents/SBU/CourseWork/CSE534 - Network/Project/sample_files_jsondag/with_cache/summary/wprof_300_5_pro_1/";
		String tempdir_cache = "/Users/vunh/Downloads/test4_cache_files/";
		//String tempdir_cache = "/Users/vunh/Documents/SBU/CourseWork/CSE534 - Network/Project/sample_files_final/with_cache/orig/temp_files_cache2/";
		
		String graphdir_noncache = "/Users/vunh/Documents/SBU/CourseWork/CSE534 - Network/Project/sample_files_jsondag/without_cache/graphs/";
		//String tempdir_noncache = "/Users/vunh/Documents/SBU/CourseWork/CSE534 - Network/Project/sample_files_jsondag/without_cache/summary/wprof_300_5_pro_1/";
		String tempdir_noncache = "/Users/vunh/Downloads/test4_non_cache_files/";
		
		String[] atts = new String[]{"load", "time_download", "time_comp", "time_block", 
				"num_bytes_cp", "num_bytes_all", "num_send_cp", "num_send_all", "num_objs_cp",
				"time_download_html", "time_download_css", "time_download_js", "time_download_img",
				"critical_path"};
		ArrayList<String> attributes = new ArrayList<String>();
		for (int i = 0; i< atts.length; i++)
		{
			attributes.add(atts[i]);
		}
		
		
		File dir = new File(graphdir_cache);
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".json");
		    }
		});
		
		ArrayList<String> cache_pageloadtime = new ArrayList<String>();
		ArrayList<String> noncache_pageloadtime = new ArrayList<String>();
		ArrayList<String> cache_pageloadtime2 = new ArrayList<String>();
		ArrayList<String> noncache_pageloadtime2 = new ArrayList<String>();
		int cache_n_css = 0;
		int cache_n_js = 0;
		int cache_n_img = 0;
		int cache_n_html = 0;
		int noncache_n_css = 0;
		int noncache_n_js = 0;
		int noncache_n_img = 0;
		int noncache_n_html = 0;
		
		ArrayList<CriticalPathInfo> cp_cache =  new ArrayList<CriticalPathInfo>();
		ArrayList<CriticalPathInfo> cp_noncache =  new ArrayList<CriticalPathInfo>();
		
		
		int n_file_matched = 0;
		for (int i = 0; i< files.length; i++)
		{
			File currFile = files[i];
			String jsfilename = currFile.getName();
			//String tempfilename = jsfilename.substring(0, jsfilename.length() - 5) + "--1";
			int dotid = jsfilename.lastIndexOf('.');
			String identity;
			if (jsfilename.charAt(dotid - 1) == '_')
			{
				identity = jsfilename.substring(0, dotid - 1);
			}
			else
			{
				identity = jsfilename.substring(0, dotid);
			}
			//String tempfilename = jsfilename.substring(0, jsfilename.length() - 5);
			if (!jsfilename.startsWith("original.testbed.localhost_"))
			{
				// The case where the file name is not in the correct format
				continue;
			}
			
			// Check corresponding cache temp and non-cache temp/graph files
			//String cache_temp_path = tempdir_cache + tempfilename;
			//String noncache_graph_path = graphdir_noncache + jsfilename;
			//String noncache_temp_path = tempdir_noncache + tempfilename;
			
			String cache_temp_path = findAppropriateFilePath (tempdir_cache, identity, "");
			String noncache_graph_path = findAppropriateFilePath (graphdir_noncache, identity, ".json");
			String noncache_temp_path = findAppropriateFilePath (tempdir_noncache, identity, "");
			
			if ((cache_temp_path == null) || (noncache_graph_path == null) || (noncache_temp_path == null))
			{
				continue;
			}
			n_file_matched++;	// Count for matched file
			
			// Main processing
			// Reading temp - cache and temp - noncache
			Map<String, String> stat_cache = readTempFile(cache_temp_path, attributes);
			Map<String, String> stat_noncache = readTempFile(noncache_temp_path, attributes);
			Map<String, HTMLComp> cache_comp = new HashMap<String, HTMLComp>();
			Map<String, HTMLComp> noncache_comp = new HashMap<String, HTMLComp>();
			Map<String, HTMLDownload> cache_download = new HashMap<String, HTMLDownload>();
			Map<String, HTMLDownload> noncache_download = new HashMap<String, HTMLDownload>();
			
			readJsonFile(graphdir_cache + jsfilename, stat_cache, cache_comp, cache_download);
			readJsonFile(noncache_graph_path, stat_noncache, noncache_comp, noncache_download);
			
			//cache_pageloadtime.add(stat_cache.get("time_download_img"));
			//noncache_pageloadtime.add(stat_noncache.get("time_download_img"));
			//cache_pageloadtime2.add(stat_cache.get("load"));
			//noncache_pageloadtime2.add(stat_noncache.get("num_bytes_all"));
			
			cache_n_css += Integer.parseInt(stat_cache.get("number_css"));
			cache_n_js += Integer.parseInt(stat_cache.get("number_javascript"));
			cache_n_img += Integer.parseInt(stat_cache.get("number_image"));
			cache_n_html += Integer.parseInt(stat_cache.get("number_html"));
			
			noncache_n_css += Integer.parseInt(stat_noncache.get("number_css"));
			noncache_n_js += Integer.parseInt(stat_noncache.get("number_javascript"));
			noncache_n_img += Integer.parseInt(stat_noncache.get("number_image"));
			noncache_n_html += Integer.parseInt(stat_cache.get("number_html"));
			
			
			cp_cache.add(criticalPathObjCount (stat_cache.get("critical_path"), cache_comp, cache_download, Double.parseDouble(stat_cache.get("load"))));
			cp_noncache.add(criticalPathObjCount (stat_noncache.get("critical_path"), noncache_comp, noncache_download, Double.parseDouble(stat_noncache.get("load"))));
			
			
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
		
		// Print out pageload time
		/*System.out.println("\nPage loading time - cache");
		for (int i = 0; i< cache_pageloadtime.size(); i++)
		{
			double temp = Double.parseDouble(cache_pageloadtime.get(i));// / Double.parseDouble(cache_pageloadtime2.get(i));
			System.out.print(temp + "  ");
		}
		System.out.println("\nPage loading time - noncache");
		for (int i = 0; i< noncache_pageloadtime.size(); i++)
		{
			double temp = Double.parseDouble(noncache_pageloadtime.get(i));// / Double.parseDouble(noncache_pageloadtime2.get(i));
			System.out.print(temp + "  ");
		}*/
		
		/*System.out.println(n_file_matched);
		System.out.println(cache_n_css);
		System.out.println(noncache_n_css);
		System.out.println(cache_n_js);
		System.out.println(noncache_n_js);
		System.out.println(cache_n_img);
		System.out.println(noncache_n_img);
		System.out.println(cache_n_html);
		System.out.println(noncache_n_html);*/
		
		System.out.print("\nhtml=[");
		for (int i = 0; i< cp_cache.size(); i++)
		{
			System.out.print(cp_cache.get(i).downloadTime + "   ");
		}
		System.out.print("];");
		
		System.out.print("\ncss=[");
		for (int i = 0; i< cp_cache.size(); i++)
		{
			System.out.print(cp_cache.get(i).computationTime + "   ");
		}
		System.out.print("];");
		
		/*System.out.print("\nhtml=[");
		for (int i = 0; i< cp_cache.size(); i++)
		{
			System.out.print(cp_cache.get(i).html_download + "   ");
		}
		System.out.print("];");
		
		System.out.print("\ncss=[");
		for (int i = 0; i< cp_cache.size(); i++)
		{
			System.out.print(cp_cache.get(i).css_download + "   ");
		}
		System.out.print("];");
		
		System.out.print("\njs=[");
		for (int i = 0; i< cp_cache.size(); i++)
		{
			System.out.print(cp_cache.get(i).js_download + "   ");
		}
		System.out.print("];");
		
		System.out.print("\nimage=[");
		for (int i = 0; i< cp_cache.size(); i++)
		{
			System.out.print(cp_cache.get(i).img_download + "   ");
		}
		System.out.print("];");*/
		
	}
	
	public static boolean checkExistingFile(String filepath)
	{
		File f = new File(filepath);
		if(!f.exists() || f.isDirectory()) { 
		    return false;
		}
		return true;
	}
	
	public static Map<String, String> readTempFile(String path, ArrayList<String> attributes)
	{
		Map<String, String> map = new HashMap<String, String>();
		try
		{
			Scanner scan = new Scanner(new File(path));
		    while (scan.hasNextLine()){
		        String line = scan.nextLine();
		        String[] parts = line.split("\t");
		        String key = parts[0].substring(0, parts[0].length() - 1);
		        if (attributes.contains(key))
		        {
		        	map.put(key, parts[1]);
		        }
		    }
		    
		    scan.close();
		}
	    catch(Exception ex)
	    {
	    	System.out.println(ex.getMessage());
	    }
	    
		return map;
	}
	
	public static String findAppropriateFilePath (String hostdir, String identity, String ext)
	{
		String triedPath = hostdir + identity + ext;
		if (checkExistingFile(triedPath) == true)
		{
			return triedPath;
		}
		
		triedPath = hostdir + identity + "_" + ext;
		if (checkExistingFile(triedPath) == true)
		{
			return triedPath;
		}
		
		return null;
	}
	
	public static void readJsonFile(String jsonPath, Map<String, String> info_bank, Map<String,
			HTMLComp> comp_map, Map<String, HTMLDownload> download_map)
	{
		double time_threshold = 50;		// This is for filtering the cached objects
		try
		{
			Parserf1 parser = new Parserf1(jsonPath);
			ArrayList<HTMLObjects> objList = parser.getListObjects();
			int n_css = 0;
			int n_js = 0;
			int n_img = 0;
			int n_html = 0;
			
			
			for (int i = 0; i< objList.size(); i++)
			{
				double time = objList.get(i).getObjLoadTime();
				int count = 0;
				if (time < time_threshold)
				{
					count = 1;
				}
				
				switch (getTrueTypeName(objList.get(i).getObjType()))
				{
					case "css": n_css += count; break;
					case "javascript": n_js += count; break;
					case "image": n_img += count; break;
					case "html": n_html += count; break;
				}
				
				download_map.put(objList.get(i).download.id, objList.get(i).download);
				
				ArrayList<HTMLComp> complist = objList.get(i).getCompList();
				for (int iComp = 0; iComp< complist.size(); iComp++)
				{
					HTMLComp comp = complist.get(iComp);
					comp_map.put(comp.id, comp);
				}
			}
			
			info_bank.put("number_css", Integer.toString(n_css));
			info_bank.put("number_javascript", Integer.toString(n_js));
			info_bank.put("number_image", Integer.toString(n_img));
			info_bank.put("number_html", Integer.toString(n_html));
		} catch (Exception ex)
		{
			System.out.println("Error in reading json file");
			System.out.println(ex.getMessage());
		}
		
	}
	
	public static String getTrueTypeName(String orgtype)
	{
		if (orgtype.contains("css"))
		{
			return "css";
		}
		
		if (orgtype.contains("javascript"))
		{
			return "javascript";
		}
		
		if (orgtype.contains("image"))
		{
			return "image";
		}
		
		if (orgtype.contains("html"))
		{
			return "html";
		}
		
		return "N/A";
	}
	
	public static String[] readCriticalRenderPath(String rawStr)
	{
		//['download_0', 'r1_c1', 'download_5', 'comp_15', 'r1_c3']
		//rawStr.replace("", replacement)
		String str = new String(rawStr);
		str = str.replace("[", "");
		str = str.replace("]", "");
		str = str.replace("'", "");
		String[] parts = str.split(", ");
		return parts;
	}
	
	// This return fraction of each object type over the overall critical time
	public static CriticalPathInfo criticalPathObjCount (String criticalPath, Map<String,
			HTMLComp> map_comp, Map<String, HTMLDownload> map_download, double criticalTime)
	{
		CriticalPathInfo info = new CriticalPathInfo();
		String[] objs = readCriticalRenderPath(criticalPath);
		
		double sum = 0;
		for (int i = 0; i< objs.length; i++)
		{
			String activity = objs[i];
			
			
			
			if (activity.contains("download"))
			{
				HTMLDownload download = map_download.get(activity);
				String host_obj_type = getTrueTypeName(download.type);
				double download_interval = Math.max(0, download.receivedTime) - Math.max(0, download.s_time);
				//double timeFrac = download_interval / criticalTime;
				double timeFrac = download_interval;
				sum += timeFrac;
				
				info.downloadTime += timeFrac;
				switch(host_obj_type)
				{
				case "html": info.html_download += timeFrac; break;
				case "css":  info.css_download += timeFrac; break;
				case "javascript": info.js_download += timeFrac; break;
				case "image": info.img_download += timeFrac; break;
				}
			}
			else
			{
				HTMLComp comp = map_comp.get(activity);
				String host_obj_type = getTrueTypeName(comp.host_obj_type);
				//double timeFrac = comp.time / criticalTime;
				double timeFrac = comp.time;
				sum += timeFrac;
				
				info.computationTime += timeFrac;
				switch(host_obj_type)
				{
				case "html": info.html_comp += timeFrac; break;
				case "css":  info.css_comp += timeFrac; break;
				case "javascript": info.js_comp += timeFrac; break;
				case "image": info.img_comp += timeFrac; break;
				}
			}
		}
		
		//info.normalize(sum);
		
		return info;
	}
}




