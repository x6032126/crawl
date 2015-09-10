import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class test {
public static void main(String args[]){
	  Pattern pt = Pattern.compile("[\u4E00-\u9FFF]");
      Matcher m = pt.matcher("jadksdjadjadj");
      pt=null;
       if(m.find()){
   	  System.out.println(1);
      }
       String f="''''111111";
      f= f.replaceAll("1","");
       System.out.println(f);
}
}
