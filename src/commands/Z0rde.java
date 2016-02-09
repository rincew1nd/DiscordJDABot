package commands;

import java.util.List;

import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.Utils;

public class Z0rde extends Command
{
	public Z0rde() {}

	@Override
	public void ParseCommand(MessageReceivedEvent event) {
		event.getTextChannel().sendMessage("http://z0r.de/" + Utils.GetRandom(GetLastZ0rde()));
	}
	
	public int GetLastZ0rde()
	{
		String URLContent = Utils.GetPageContent("http://z0r.de/0");
		List<String> matches = Utils.FindByRegex(URLContent, "<a href=\"[0-9]+\">&laquo; Previous</a>");
		
		return Integer.parseInt(Utils.FindByRegex(matches.iterator().next(), "\"[0-9]+\"").iterator().next().replace("\"", ""));
	}
}
