package commands;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.AudioTest;

public class Z0rde {
	public Z0rde(MessageReceivedEvent event) 
	{
		AudioTest aute = AudioTest.getInstance();
		CommandHandler commandHandler = new CommandHandler(event.getMessage().getContent(), ".zorde", " ");
		TextChannel channel = event.getTextChannel();
		
		if (commandHandler.StartsWith())
		{
			event.getMessage().deleteMessage();
			boolean thr = commandHandler.argumentHandler.Has("max");
			
			if (thr)
				channel.sendMessage("");
		}
	}
	
	public int GetLastZ0rde()
	{
		URL url;
		try {
			url = new URL("http://www.z0r.de/0");
			url.openConnection();
			Object allPage = url.getContent();
			//<a href="7424">« Previous</a>
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
}
