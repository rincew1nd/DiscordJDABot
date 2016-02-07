package commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.MusicPlayer;
import rinbot.PlaylistManager;

public class Playlist {
	public Playlist(MessageReceivedEvent event) 
	{
		MusicPlayer _musicPlayer = MusicPlayer.getInstance();
		PlaylistManager _playlistManager = PlaylistManager.getInstance();
		CommandHandler _commandHandler = new CommandHandler(".pl", " ", ":")
				.AddKeywords(new ArrayList<String>() {{
					add("get"); add("all"); add("curr");
					add("play"); add("add"); add("del"); add("dwl");
					add("ch:"); add("nm:"); add("sg:"); add("url:");
				}})
				.ParseString(event.getMessage().getContent());
		TextChannel _channel = event.getTextChannel();
		
		if (_commandHandler.isCommand()) 
		{
			event.getMessage().deleteMessage();
			_musicPlayer.SetChannel(_channel);
			_playlistManager.SetChannel(_channel);
			
			boolean isGet = _commandHandler.GetArgHandler().Has("get");
			boolean isGetAll = _commandHandler.GetArgHandler().Has("all");
			boolean isCurrent = _commandHandler.GetArgHandler().Has("curr");
			boolean isPlay = _commandHandler.GetArgHandler().Has("play");
			boolean isAdd = _commandHandler.GetArgHandler().Has("add");
			boolean isDelete = _commandHandler.GetArgHandler().Has("del");
			boolean isDownload = _commandHandler.GetArgHandler().Has("dwl");
			String voicechannel = _commandHandler.GetArgHandler().GetArgValue("ch:");
			String playlistName = _commandHandler.GetArgHandler().GetArgValue("nm:");
			String URL = _commandHandler.GetArgHandler().GetArgValue("url:");
			List<String> songsName = _commandHandler.GetArgHandler().GetArgsValues("sg:");
			
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
			
			if (isPlay && playlistName != "")
			{
				if (voiceChannelToConnect == null)
				{
					_channel.sendMessage("Не удалось зайти в канал "
								+ voicechannel + "\nКанала не существует!");
				} else {
					Runnable thread = new PlayThread(playlistName, voiceChannelToConnect);
	        		new Thread(thread).start();
				}
			}
			else if (isGet && playlistName != "" && songsName.size() == 0)
				_playlistManager.PrintPlaylist(playlistName);
			else if (isGet && isGetAll && playlistName == "" && songsName.size() == 0)
				_playlistManager.PrintAllPlaylists();
			else if (isGet && isCurrent && playlistName == "" && songsName.size() == 0)
				_playlistManager.PrintPlaylist(_musicPlayer.GetPlaylist());
			else if (isAdd)
				_playlistManager.AddToPlaylist(playlistName, songsName);
			else if (isDelete)
				_playlistManager.DeleteFromPlaylist(playlistName, songsName);
			else if (isDownload)
				_playlistManager.DownloadMusic(URL, playlistName);
		}
	}
	
	public class PlayThread implements Runnable
    {
		String _playlistName;
		VoiceChannel _voiceChannelToConnect;
		
    	public PlayThread(String playlistName, VoiceChannel voiceChannelToConnect) {
			_playlistName = playlistName;
			_voiceChannelToConnect = voiceChannelToConnect;
		}
    	
		@Override
		public void run() {
			MusicPlayer.getInstance().Play(_playlistName, _voiceChannelToConnect);
		}
    }
}
