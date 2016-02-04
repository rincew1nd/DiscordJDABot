package commands;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.AudioTest;

public class Voice {
	public Voice(MessageReceivedEvent event) 
	{
		AudioTest aute = AudioTest.getInstance();
		CommandHandler commandHandler = new CommandHandler(event.getMessage().getContent(), ".voice", " ");
		TextChannel channel = event.getTextChannel();
		
		
	}
}
