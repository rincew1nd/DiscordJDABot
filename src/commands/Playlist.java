package commands;

import java.util.List;

import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.MusicPlayer;

public class Playlist extends Command
{
	public Playlist() {}

	@Override
	public void ParseCommand(MessageReceivedEvent event) {
		boolean isGet = Has("get");
		boolean isGetAll = Has("all");
		boolean isCurrent = Has("curr");
		boolean isPlay = Has("play");
		boolean isNew = Has("new");
		boolean isAdd = Has("add");
		boolean isDelete = Has("del");
		boolean isDownload = Has("dwl");
		String voicechannel = GetParam("ch:");
		String playlistName = GetParam("nm:");
		String url = GetParam("url:");
		List<String> songsName = GetParams("sg:");
		
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
				event.getTextChannel().sendMessage("Не удалось зайти в канал "
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
			_playlistManager.PrintCurrentPlaylist();
		else if (isNew)
			_playlistManager.NewPlaylist(playlistName);
		else if (isAdd)
			_playlistManager.AddToPlaylist(playlistName, songsName);
		else if (isDelete)
			_playlistManager.DeleteFromPlaylist(playlistName, songsName);
		else if (isDownload)
			if (url != "")
				_playlistManager.DownloadMusic(url, playlistName);
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
