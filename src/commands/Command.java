package commands;

import java.util.ArrayList;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.MusicPlayer;
import rinbot.PlaylistManager;

public class Command {

	MusicPlayer _musicPlayer = MusicPlayer.getInstance();
	PlaylistManager _playlistManager = PlaylistManager.getInstance();
	CommandHandler handler = new CommandHandler();
	
	public Command MakeHandler(String command, String separator, String parameter)
	{
		handler.Setup(command, separator, parameter);
		return this;
	}
	
	public Command AddKeywords(ArrayList<String> keywords)
	{
		handler.AddKeywords(keywords);
		return this;
	}
	
	public Command Parse(MessageReceivedEvent event)
	{
		handler.ParseString(event.getMessage().getContent());
		
		if (handler.isCommand())
		{
			event.getMessage().deleteMessage();
			ParseCommand(event);
		}
		return this;
	}

	public void ParseCommand(MessageReceivedEvent event) {};
	
	public CommandHandler.ArgumentHandler GetArgHandler()
	{
		return handler.GetArgHandler();
	}
	
	public boolean Has(String toFind)
	{
		return handler.GetArgHandler().Has(toFind);
	}
	
	public String GetParam(String param)
	{
		return handler.GetArgHandler().GetArgValue(param);
	}
	
	public ArrayList<String> GetParams(String param)
	{
		return handler.GetArgHandler().GetArgsValues(param);
	}
}