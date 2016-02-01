package rinbot;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class Player {
	public Player(String input, MessageReceivedEvent event) 
	{
		AudioTest au = AudioTest.getInstance();
		CommandHandler commandHandler = new CommandHandler(input, ".pl ", " ");
		TextChannel channel = event.getTextChannel();
		
		if (commandHandler.StartsWith()) 
		{
			au.SetChannel(event.getTextChannel());
			
			boolean isGet = !commandHandler.argumentHandler.Has("get");
			boolean isGetAll = !commandHandler.argumentHandler.Has("getA");
			boolean isCurrent = !commandHandler.argumentHandler.Has("curr");
			boolean isPlay = !commandHandler.argumentHandler.Has("p");
			boolean isAdd = !commandHandler.argumentHandler.Has("a");
			boolean isDelete = !commandHandler.argumentHandler.Has("d");
			String playlistName = commandHandler.argumentHandler.GetArgValue("nm:");
			String songName = commandHandler.argumentHandler.GetArgValue("sg:");
			
			String[] temp = new String[2];
			temp[0] = (playlistName == "") ? "current" : playlistName;
			temp[1] = songName;
			
			if (isGet)
				au.PrintPlaylist();
			else if (isGetAll)
				au.PrintAllPlaylists();
			else if (isCurrent)
				au.PrintPlaylist();
			else if (isPlay)
				au.Play(playlistName, channel);
			else if (isAdd)
				au.AddToPlaylist(temp);
			else if (isDelete)
				au.DeleteFromPlaylist(temp);
			
			event.getMessage().deleteMessage();
		}
	}
}
