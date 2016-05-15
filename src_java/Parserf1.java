import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;



public class Parserf1 {

	ArrayList<HTMLObjects> arr;
	
	public Parserf1(String path ) throws IOException
	{
		arr = new ArrayList<HTMLObjects>();
		
		JsonReader jsonReader = new JsonReader(new FileReader(path));
		   // ArrayList<HTMLObjects> arr = new ArrayList<HTMLObjects>(200); 
		    jsonReader.beginObject();

		    while (jsonReader.hasNext()) {
		    	try {

		    	String name = jsonReader.nextName();
		    	//System.out.print(name);
		        if (name.contains("n_download_no_trigger")) {
		      	 //System.out.println(": "+jsonReader.nextDouble());
		        	jsonReader.nextDouble();
		        }
		        if (name.contains("start_activity")) {
		       	 //System.out.println(": "+jsonReader.nextString());
		        	jsonReader.nextString();
		        }
		        if (name.contains("name")) {
		        //System.out.println(": "+jsonReader.nextString());
		        	jsonReader.nextString();
		        }
		        if (name.contains("load_activity")) {
		        	//System.out.println(": "+jsonReader.nextDouble());
		        	jsonReader.nextDouble();
		        }
		        if (name.equals("objs")) {
		             //readApp(jsonReader);
		        	jsonReader.beginArray();
		            while  (jsonReader.hasNext()) {
		            	arr.add(readApp(jsonReader));
		            }
		            jsonReader.endArray();

		        }
		        if (name.equals("deps")) {
		        	jsonReader.beginArray();
		            while  (jsonReader.hasNext()) {
		            	readApp2(jsonReader);
		            }
		            jsonReader.endArray();
		        }
		    	}
		    	 catch (IllegalStateException e) {
		    		    //System.err.println("IllegalStateException: " + e.getMessage());
		    	 }
		    }

		   jsonReader.endObject();
		   jsonReader.close();

		}

	
	public ArrayList<HTMLObjects> getListObjects()
	{
		return arr;
	}
	

public static void main(String[] args) throws IOException, NullPointerException {
	Parserf1 var = new Parserf1("/Users/vunh/Documents/SBU/CourseWork/CSE534 - Network/Project/sample_files/with_cache/desktop_wifi-fast_top200_orig_inline/graphs/original.testbed.localhost_www.fc2.com_.json");
	ArrayList<HTMLObjects> objlist = var.getListObjects();
	for (int i = 0; i< 1; i++)
	{
		System.out.println(objlist.get(i).getObjType());
		System.out.println(objlist.get(i).getObjLoadTime());
		ArrayList<HTMLComp> compList = objlist.get(i).getCompList();
		for (i = 0; i< 2; i++)
		{
			System.out.println(compList.get(i).id);
			System.out.println(compList.get(i).type);
			System.out.println(compList.get(i).time);
		}
		//System.out.println(objlist.get(i).getCompList());
	}
	
}  

public static HTMLObjects readApp(JsonReader jsonReader) throws IOException{
    HTMLObjects obj = new HTMLObjects();
	//ArrayList<HTMLComp> comp = new ArrayList();
	jsonReader.beginObject();
     while (jsonReader.hasNext()) {
    	 try {
         String name = jsonReader.nextName();
       // System.out.print(name);
         if (name.contains("when_comp_start"))
       	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("url"))
        	 //System.out.println(": "+jsonReader.nextString());
        	 jsonReader.nextString();
         if (name.contains("id"))
        	 //System.out.println(": "+jsonReader.nextString());
        	 jsonReader.nextString();
         
         if (name.contains("comps")) {
        	 jsonReader.beginArray();
             while  (jsonReader.hasNext()) {
            	 HTMLComp temp = readApp1(jsonReader);
            	obj.comp.add(temp);
             }
             jsonReader.endArray();
        	 
         }
         if (name.contains("download")) {
        	 obj.download = readApp3(jsonReader);
        	 for (int i = 0; i< obj.getCompList().size(); i++)
        	 {
        		 obj.getCompList().get(i).host_obj_type = obj.download.type;
        	 }
         }
         //else
       // 	 continue;
    	 }
    	 catch (IllegalStateException | NullPointerException e) {
    		    //System.err.println("IllegalStateException: " + e.getMessage());
    		 System.out.println(e.getMessage());
    	 }
     }
    jsonReader.endObject();
    
    return obj;
  }
public static HTMLDownload readApp3(JsonReader jsonReader) throws IOException{
	HTMLDownload download = new HTMLDownload();
    jsonReader.beginObject();
     while (jsonReader.hasNext()) {
    	 try {
         String name = jsonReader.nextName();
        // System.out.print(name);
         if (name.contains("receiveFirst"))
        	// System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("len"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("dns"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("receiveHeadersEnd"))
        	 download.receiveHeadersEnd = jsonReader.nextDouble();
        if (name.contains("sslEnd"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	jsonReader.nextDouble();
         if (name.contains("connectEnd"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("connectStart"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("id"))
        	 //System.out.println(": "+jsonReader.nextString());
        	 download.id = jsonReader.nextString();
         if (name.contains("receivedTime"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 //jsonReader.nextDouble();
        	 download.receivedTime = jsonReader.nextDouble();
         if (name.contains("sslStart"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("dnsStart"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("receiveLast"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 //jsonReader.nextDouble();
        	 download.receiveLast = jsonReader.nextDouble();
         if (name.contains("s_time"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 //jsonReader.nextDouble();
        	 download.s_time = jsonReader.nextDouble();
         if (name.contains("sendEnd"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("sendStart"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("type"))
        	 download.type = jsonReader.nextString();
        // else 
        //	 continue;
    	 }
    	 catch (IllegalStateException e) {
 		   // System.err.println("IllegalStateException: " + e.getMessage());
 	 }

     }
    jsonReader.endObject();
    return download;
}
public static HTMLComp readApp1(JsonReader jsonReader) throws IOException{
	HTMLComp comp = new HTMLComp();
    jsonReader.beginObject();
     while (jsonReader.hasNext()) {
    	 try {
         String name = jsonReader.nextName();
        // System.out.print(name);
         //String str = jsonReader.nextString();
        
         if (name.contains("s_time"))
        	// System.out.println(": "+jsonReader.nextString());
        	jsonReader.nextString();
         else if (name.equals("time"))
         {
        	 //System.out.println(": "+jsonReader.nextString());
        	 comp.time = jsonReader.nextDouble();
        	 if (comp.time < 0)
        	 {
        		 comp.time = 0;
        	 }
         }
         else if (name.contains("id"))
        	 //System.out.println(": "+jsonReader.nextString());
        	 comp.id = jsonReader.nextString();
         else if (name.contains("type")&& jsonReader.peek() != JsonToken.NULL){
        	 //System.out.println(": "+jsonReader.nextString());
        	 	 	
        		 comp.type = jsonReader.nextString();
         }
         else if (name.contains("e_time"))
        	 //System.out.println(": "+jsonReader.nextString());
        	jsonReader.nextString();
         else {
             jsonReader.skipValue();
           }
            
    	 }
    	 catch (IllegalStateException e) {
 		   // System.err.println("IllegalStateException: " + e.getMessage());
 	 }
    	 

     }
    jsonReader.endObject();
    return comp;
  
}
public static void readApp2(JsonReader jsonReader) throws IOException{
    jsonReader.beginObject();
     while (jsonReader.hasNext()) {
    	 try {
         String name = jsonReader.nextName();
         //System.out.print(name);
         if (name.contains("a2"))
        	// System.out.println(": "+jsonReader.nextString());
        	 jsonReader.nextString();
         if (name.contains("time"))
        	 //System.out.println(": "+jsonReader.nextDouble());
        	 jsonReader.nextDouble();
         if (name.contains("id"))
        	// System.out.println(": "+jsonReader.nextString());
        	 jsonReader.nextString();
         if (name.contains("a1"))
        	 //System.out.println(": "+jsonReader.nextString());
        	 jsonReader.nextString();
         
            
    	 }
    	 catch (IllegalStateException e) {
 		   // System.err.println("IllegalStateException: " + e.getMessage());
 	 }

     }
    jsonReader.endObject();
  
}

}

