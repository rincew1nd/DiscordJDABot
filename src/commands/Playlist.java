package commands;

import java.util.List;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.AudioTest;

public class Playlist {
	public Playlist(MessageReceivedEvent event) 
	{
		AudioTest au = AudioTest.getInstance();
		CommandHandler commandHandler = new CommandHandler(event.getMessage().getContent(), ".pl", ";");
		TextChannel channel = event.getTextChannel();
		
		if (commandHandler.StartsWith()) 
		{
			event.getMessage().deleteMessage();
			au.SetChannel(channel);
			
			boolean isGet = commandHandler.argumentHandler.Has("get");
			boolean isGetAll = commandHandler.argumentHandler.Has("all");
			boolean isCurrent = commandHandler.argumentHandler.Has("curr");
			boolean isPlay = commandHandler.argumentHandler.Has("play");
			boolean isAdd = commandHandler.argumentHandler.Has("add");
			boolean isDelete = commandHandler.argumentHandler.Has("del");
			String voicechannel = commandHandler.argumentHandler.GetArgValue("ch:");
			String playlistName = commandHandler.argumentHandler.GetArgValue("nm:");
			List<String> songsName = commandHandler.argumentHandler.GetArgsValues("sg:");
			
			VoiceChannel voiceChannelToConnect = null;
			if (voicechannel.equals("")) 
			{
				voiceChannelToConnect = event.getGuild().getVoiceStatusOfUser(event.getAuthor()).getChannel();
			}
			else
			{
				voiceChannelToConnect = event.getGuild()
				.getVoiceChannels().stream()
				.filter(x -> x.getName().equals(voicechannel))
				.findAny().orElse(null);
			}
			
			if (voiceChannelToConnect == null)
			{
				channel.sendMessage("Не удалось зайти в канал "
							+ voicechannel + "\nКанала не существует!");
			} else {
				if (isGet && playlistName != "" && songsName.size() == 0)
					au.PrintPlaylist(playlistName);
				else if (isGet && isGetAll && playlistName == "" && songsName.size() == 0)
					au.PrintAllPlaylists();
				else if (isGet && isCurrent && playlistName == "" && songsName.size() == 0)
					au.PrintPlaylist();
				else if (isPlay && playlistName != "")
				{
					Runnable thread = new PlayThread(playlistName, voiceChannelToConnect);
	        		new Thread(thread).start();
				}
				else if (isAdd)
					au.AddToPlaylist(playlistName, songsName);
				else if (isDelete)
					au.DeleteFromPlaylist(playlistName, songsName);
			}
			
			event.getMessage().deleteMessage();
		}
	}
	
	public class PlayThread implements Runnable
    {
		String playlistName;
		VoiceChannel voiceChannelToConnect;
		
    	public PlayThread(String playlistName, VoiceChannel voiceChannelToConnect) {
			this.playlistName = playlistName;
			this.voiceChannelToConnect = voiceChannelToConnect;
		}
    	
		@Override
		public void run() {
			AudioTest.getInstance().Play(playlistName, voiceChannelToConnect);
		}
    }
}
