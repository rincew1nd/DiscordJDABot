package rinbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class Utils {

	static StringBuilder sBuilder = new StringBuilder();
	static Random rnd = new Random();
	static User bot;
	
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
    
	public static JSONObject ReadFromJSON(String path)
	{
		JSONObject JSON = new JSONObject();
		try {	
			JSON = new JSONObject(ReadFile(Utils.GetRootFolder()+path));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return JSON;
	}
	
	public static void WriteToJSON(String path, JSONObject toWrite) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(Utils.GetRootFolder()+path);
			fileWriter.write(toWrite.toString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static String ReadUrl(String urlString)
	{
		BufferedReader reader = null;
		try {
			URL url;
			url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read); 
			
			return buffer.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	public static String ReadFile(String path)
	{
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
			return null;
	}
	
    public static boolean tryParseInt(String value) {  
        try {  
            Integer.parseInt(value);  
            return true;
         } catch (NumberFormatException e) {  
            return false;  
         }  
   }

    public static boolean tryParseFloat(String value) {  
        try {  
            Float.parseFloat(value);  
            return true;
         } catch (NumberFormatException e) {  
            return false;  
         }  
   }
	
	public static VoiceChannel GetVoiceChannel(MessageReceivedEvent event, String voiceChannelToGet)
	{
		VoiceChannel playTo = null;
		if (voiceChannelToGet.equals(""))
			playTo = event.getGuild().getVoiceStatusOfUser(event.getAuthor()).getChannel();
		else
			playTo = event.getGuild().getVoiceChannels().stream()
					.filter(x -> x.getName().equals(voiceChannelToGet)).findAny().orElse(null);
		return playTo;
	}

	public static VoiceChannel GetUserVoiceChannel(MessageReceivedEvent event)
	{
		return event.getGuild().getVoiceStatusOfUser(event.getAuthor()).getChannel();
	}
	
	public static User GetUrself(MessageReceivedEvent event)
	{
		if (bot != null)
			return bot;
		else
		{
			bot = event.getJDA().getUserById(event.getJDA().getSelfInfo().getId());
			return bot;
		}
	}
}