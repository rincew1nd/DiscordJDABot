package rinbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtils {

	static StringBuilder sBuilder = new StringBuilder();
	static Random rnd = new Random();
	
    public static String GetRootFolder() {
    	String decodedPath = new File(".").getAbsolutePath();
    	decodedPath = decodedPath.substring(0,decodedPath.length()-2);
    	return decodedPath;
    }

    public static List<String> FindByRegex(String userNameString, String pattern){
    	List<String> allMatches = new ArrayList<String>();
        Matcher matches = Pattern.compile(pattern).matcher(userNameString);
        while (matches.find())
        	allMatches.add(matches.group());
        return allMatches;
    }
    
    public static String GetPageContent(String URL)
    {
    	try
    	{
    		sBuilder.setLength(0);
    		
	    	URL oracle = new URL(URL);
	        BufferedReader in = new BufferedReader(
	        new InputStreamReader(oracle.openStream()));
	        
	        String inputLine;
	        while ((inputLine = in.readLine()) != null)
	            sBuilder.append(inputLine);
	        in.close();
	        return sBuilder.toString();
    	}
    	catch (IOException e)
    	{
    		return "";
    	}
    }
    
    public static int GetRandom(int last)
    {
    	return rnd.nextInt(last);
    }
}
