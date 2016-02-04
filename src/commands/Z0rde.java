package commands;

import java.util.List;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.MyUtils;

public class Z0rde {
	public Z0rde(MessageReceivedEvent event) 
	{
		CommandHandler commandHandler = new CommandHandler(event.getMessage().getContent(), ".z0rde", " ");
		TextChannel channel = event.getTextChannel();
		
		if (commandHandler.StartsWith())
		{
			event.getMessage().deleteMessage();
			
			channel.sendMessage("http://z0r.de/" + MyUtils.GetRandom(GetLastZ0rde()));
		}
	}
	
	public int GetLastZ0rde()
	{
		String URLContent = MyUtils.GetPageContent("http://z0r.de/0");
		List<String> matches = MyUtils.FindByRegex(URLContent, "<a href=\"[0-9]+\">&laquo; Previous</a>");
		
		return Integer.parseInt(MyUtils.FindByRegex(matches.iterator().next(), "\"[0-9]+\"").iterator().next().replace("\"", ""));
	}
}
