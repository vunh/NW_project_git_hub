import java.util.ArrayList;

public class HTMLObjects {
	
	public HTMLDownload download;
	//public String id;	
	public ArrayList<HTMLComp> comp;
	
	public HTMLObjects()
	{
		comp = new ArrayList<HTMLComp>();
	}
	public ArrayList<HTMLComp> getCompList() {
		try{
		return comp ;
		}
		catch(NullPointerException e){ 
			System.out.println(e.getMessage());
			return null;
			}
		}
	
	public String getObjType()
	{
		return download.type;
	}
	
	public double getObjLoadTime()
	{
		return download.receivedTime - download.s_time;
	}
}
	
