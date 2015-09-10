

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.mysql.jdbc.Statement;


/*
 * WPsiteCrawler
 * a web crawler for single WordPress site
 * Author:	John Hany
 * Site:	http://johnhany.net/
 * Source code updates:	https://github.com/johnhany/WPCrawler
 * 
 * Using:	Apache HttpComponents 4.3 -- http://hc.apache.org/
 * 			HTML Parser 2.0 -- http://htmlparser.sourceforge.net/
 * 			MySQL Connector/J 5.1.27 -- http://dev.mysql.com/downloads/connector/j/
 * Thanks for their work!
 */

/*
 * @getByString
 * get page content from an url link
 */
public class httpGet {

    public final static void getByString(String url, Connection conn) throws Exception {
        //CloseableHttpClient httpclient = HttpClients.createDefault();
    	  System.out.println("正在访问网址"+url);     
    	Statement stmt = null;
    	      URI uri=new URI(url,false,"utf-8");
    	      Pattern pt =null;// Pattern.compile("[\u4E00-\u9FFF]");
    	      Matcher m = null;//pt.matcher(url);
    	     // pt=null;
    	    url=uri.toString();
    	if(url==null){//||m.find()){
			String sql = "UPDATE record SET crawled = 1 WHERE URL = '" + url + "'";
			stmt = (Statement) conn.createStatement();
			stmt.executeUpdate(sql);
			sql=null;
			stmt=null;
			//m=null;
			return;}
        try {
        	HttpClient httpClient=new HttpClient();
            //int statusCode1 = httpClient.executeMethod(postMethod);
        	httpClient.getHostConfiguration().setProxy("10.1.156.87", 8080);
        	//System.out.println(1);
        	//httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-16");
        	PostMethod postMethod = new PostMethod(url);
        	
        	httpClient.executeMethod(postMethod);
        	int statuscode = postMethod.getStatusCode();
	        if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||
	            (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||
	            (statuscode == HttpStatus.SC_SEE_OTHER) ||
	(statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {	 
	        	Header header=postMethod.getResponseHeader("location");
	        	   if (header!=null){
	        	      String newuri=header.getValue();
	        	      if((newuri==null)||(newuri.equals("")))
	        	         newuri="/";
	        	      String tmp = newuri.toLowerCase();
	        	      if (!tmp.startsWith("http://") && !tmp.startsWith("https://")) {	        	  
	        	          newuri=new URI(uri, newuri, false).toString();
	        	      }
	        	     
	        	  
	        	           postMethod = new PostMethod(newuri);
	        	         httpClient.executeMethod(postMethod);
	        	         System.out.println("Redirect:"+postMethod.getStatusLine().toString());
	        	         //redirect.releaseConnection();
	        	   }else 
	        	    System.out.println("Invalid redirect");
	
	        }
       InputStream txtis = postMethod.getResponseBodyAsStream();
       BufferedReader br = new BufferedReader(new InputStreamReader(txtis,"ISO-8859-1"));  
       txtis=null;
       String p=null;
       String temp=null;
       while((temp=br.readLine())!=null){ 
        p+=temp; 
       } 
       temp=null;
       br=null;
         p=new String(p.getBytes("ISO-8859-1"),"gbk");
        PreparedStatement pstmt = null;
         pt = Pattern.compile("(?s)(?<=的原帖：).*?(?=div>)");
         m = pt.matcher(p);
         pt=null;
         List<String>result=new ArrayList<String>();
         while(m.find()){
      	   result.add(m.group());
         }
         m=null;
               File file = new File("F://re1.txt");
        	   FileOutputStream fos = new FileOutputStream(file,true);
        	   OutputStreamWriter osw = new OutputStreamWriter(fos);
        	   BufferedWriter bw = new BufferedWriter(osw);
        	   for(int i=0;i<result.size();i++){
        	       
        	       bw.write("节点开始："+result.get(i));
        	       bw.newLine();
        	       bw.write("网址为："+url);
        	       bw.newLine();
        	       System.out.println("=========================================");
        	       System.out.println("已获取回复");
        	       System.out.println("=========================================");
        	       System.out.println(result.get(i));
        	      }
        	   url=null;
        	   bw.flush();
        	   bw.close();
        	   osw.close();
        	   fos.close();
        	   result=null;
        	   parsePage.parseFromString(p,conn);
        	  				 } catch (IOException e) { 
        	  		
        	  				  e.printStackTrace();
        	  			
        	  				   
        	  				  }
        	
    
         }
        
    
}
