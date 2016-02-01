package rinbot;

import java.io.File;

public class MyUtils {

    public static String GetRootFolder() {
    	String decodedPath = new File(".").getAbsolutePath();
    	decodedPath = decodedPath.substring(0,decodedPath.length()-2);
    	return decodedPath;
    }

	
}
