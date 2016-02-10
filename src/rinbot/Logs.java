package rinbot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Logs {
	
	String fullText = "";
	
	public static class LogsHolder {
		public static final Logs HOLDER_INSTANCE = new Logs();
	}
	public static Logs getInstance() {
		return LogsHolder.HOLDER_INSTANCE;
	}
	private Logs() {
		try (BufferedReader br = new BufferedReader(new FileReader(Utils.GetRootFolder() + "\\logs.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	fullText += line + "\n";
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void SaveLog()
	{
		try {
			FileWriter fw = new FileWriter(Utils.GetRootFolder() + "\\logs.txt");
			fw.write(fullText);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void appendLog(String log)
	{
		fullText += log + "\r\n";
		SaveLog();
	}
	
}