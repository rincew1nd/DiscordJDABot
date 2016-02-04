package commands;

import java.util.List;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class Test {
	public Test(MessageReceivedEvent event) 
	{
		CommandHandler commandHandler = new CommandHandler(event.getMessage().getContent(), ".test ", " ");
		TextChannel channel = event.getTextChannel();
		
		if (commandHandler.StartsWith()) 
		{
			event.getMessage().deleteMessage();
			boolean one = commandHandler.argumentHandler.Has("one:");
			String two = commandHandler.argumentHandler.GetArgValue("two:");
			List<String> thr = commandHandler.argumentHandler.GetArgsValues("thr:");
			
			channel.sendMessage(one + " - " + two + " - " + thr.toString());
		}
		
	}
}