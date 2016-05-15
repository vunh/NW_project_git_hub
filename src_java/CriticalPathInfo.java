
public class CriticalPathInfo
{
	public double downloadTime;
	public double computationTime;
	public double html_download;
	public double html_comp;
	public double css_download;
	public double css_comp;
	public double js_download;
	public double js_comp;
	public double img_download;
	public double img_comp;
	
	public CriticalPathInfo()
	{
		downloadTime = 0;
		computationTime = 0;
		html_download = 0;
		html_comp = 0;
		css_download = 0;
		css_comp = 0;
		js_download = 0;
		js_comp = 0;
		img_download = 0;
		img_comp = 0;
	}
	
	public void normalize (double sum)
	{
		downloadTime = downloadTime / sum;
		computationTime = computationTime / sum;
		html_download =  html_download/ sum;
		html_comp = html_comp / sum;
		css_download = css_download / sum;
		css_comp = css_comp / sum;
		js_download = js_download / sum;
		js_comp = js_comp / sum;
		img_download = img_download / sum;
		img_comp =  img_comp/sum;
	}
}
