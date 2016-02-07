package commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class Test {
	public Test(MessageReceivedEvent event) 
	{
		CommandHandler commandHandler = new CommandHandler(".test", " ", ":")
				.AddKeywords(new ArrayList<String>(){{
					add("one"); add("two"); add("thr:"); add("fur:");
				}})
				.ParseString(event.getMessage().getContent());
		TextChannel channel = event.getTextChannel();
		
		if (commandHandler.isCommand()) 
		{
			event.getMessage().deleteMessage();
			boolean one = commandHandler.GetArgHandler().Has("one");
			boolean two = commandHandler.GetArgHandler().Has("two");
			String thr = commandHandler.GetArgHandler().GetArgValue("thr:");
			List<String> fur = commandHandler.GetArgHandler().GetArgsValues("fur:");
			
			channel.sendMessage(one + " - " + two + " - " + thr + " - " + fur.toString());
		}
		
	}
}