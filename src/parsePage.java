

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.net.URLDecoder;

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
 * @parseFromString
 * extract link from <a> tags in a web page
 */
public class parsePage {
	
	public static void parseFromString(String p, Connection conn) throws Exception {
		
		Parser parser = new Parser();
		Parser.getConnectionManager().setProxyHost("10.1.156.87");
        Parser.getConnectionManager().setProxyPort(8080);
		parser.setInputHTML(p);
		   p=null;
		//HasAttributeFilter filter = new HasAttributeFilter("href");
			NodeFilter frameFilter = new NodeFilter() {
				public boolean accept(Node node) {
					if (node.getText().startsWith("href="+'"')) {
						return true;
					} else {
						return false;
					}
				}
			};
			OrFilter linkFilter = new OrFilter(new NodeClassFilter(
					LinkTag.class), frameFilter);
			 //得到所有经过过滤的标签
			//NodeFilter aFilter=new NodeClassFilter(LinkTag.class);
			
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			int count = list.size();
			parser=null;
			linkFilter=null;
		      Node node=null;
		      LinkTag link = null;
				String nextlink = null;
				//String mainurl = ;
				//String wpurl = "http://club.kdnet.net";
				String sql = null;
				ResultSet rs = null;
				PreparedStatement pstmt = null;
				Statement stmt = null;
				String tag = null;
			//process every link on this page
			for(int i=0; i<count; i++) {
				 node = list.elementAt(i);
				
				if(node instanceof LinkTag) {
					link = (LinkTag) node;
					nextlink = link.extractLink();
					String mainurl = "club.kdnet.net";
					//String wpurl = mainurl + "wp-content/";             
					//only save page from "http://johnhany.net"
					if(nextlink.contains(mainurl)) {
					
						//do not save any page from "wp-content"
						
						try {
							//check if the link already exists in the database
							sql = "SELECT * FROM record WHERE URL = '" + nextlink + "'";
							stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
							rs = stmt.executeQuery(sql);

							if(rs.next()) {
								
				            }else {
				            	//if the link does not exist in the database, insert it
				            	sql = "INSERT INTO record (URL, crawled) VALUES ('" + nextlink + "',0)";
				            	pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				            	pstmt.execute();
				            	//System.out.println(nextlink);
				            	
				            	//use substring for better comparison performance
				            	nextlink = nextlink.substring(mainurl.length());
				            	//System.out.println(nextlink);
				            	
				            	//if(nextlink.startsWith("tag/")) {
				            		//tag = nextlink.substring(4, nextlink.length()-1);
				            		//decode in UTF-8 for Chinese characters
				            		//tag = URLDecoder.decode(tag,"UTF-8");
				            		//sql = "INSERT INTO tags (tagname) VALUES ('" + tag + "')";
					            	//pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					            	//if the links are different from each other, the tags must be different
					            	//so there is no need to check if the tag already exists
					            	//pstmt.execute();
				            	//}
				            }
						} catch (SQLException e) {
							//handle the exceptions
							System.out.println("SQLException: " + e.getMessage());
						    System.out.println("SQLState: " + e.getSQLState());
						    System.out.println("VendorError: " + e.getErrorCode());
						} 
						
					}
				}
			}
			
		
			//close and release the resources of PreparedStatement, ResultSet and Statement
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {}
			}
			pstmt = null;
			
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {}
			}
			rs = null;
			
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e3) {}
			}
			stmt = null;
	}
}