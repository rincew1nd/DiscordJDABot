package commands;

import java.util.List;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class Test extends Command
{
	public Test() {}
	
	@Override
	public void ParseCommand(MessageReceivedEvent event)
	{
		boolean one = Has("one");
		String two = GetParam("two:");
		List<String> thr = GetParams("thr:");
			
		event.getTextChannel().sendMessage(one + " - " + two + " - " + thr.toString());
	}
}