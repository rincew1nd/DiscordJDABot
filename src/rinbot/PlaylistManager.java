package rinbot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.TextChannel;

public class PlaylistManager {

	private StringBuilder _sBuilder;
	private MusicPlayer _musicPlayer = MusicPlayer.getInstance();
	private TextChannel _textChannel;
	
	public static class PlaylistManagerHolder {
		public static final PlaylistManager HOLDER_INSTANCE = new PlaylistManager();
	}
	public static PlaylistManager getInstance() {
		return PlaylistManagerHolder.HOLDER_INSTANCE;
	}
	private PlaylistManager() {}
	
	public MusicPlayer SetChannel(TextChannel channel)
	{
		_textChannel = channel;
		return MusicPlayer.MusicPlayerHolder.HOLDER_INSTANCE;
	}
	
	//////////////////////////////////////////
	//  Функции для управления плейлистами  //
	//////////////////////////////////////////
	
	// Получить весь список треков из папки \media\music
	public void GetMusicList()
	{
		_sBuilder.setLength(0);
		_sBuilder.append("Список доступной музыки:\r\n");
		
		File folder = new File(System.getProperty("user.dir") + "\\media\\music");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++)
		if (listOfFiles[i].isFile())
			
		_sBuilder.append(listOfFiles[i].getName() + "\r\n");
		
		_textChannel.sendMessage(_sBuilder.toString());
	}

	// Скачать трек в папку \media\music
	public String DownloadMusic(String URL, String fileName)
	{
		String path;
		
		if (fileName == "")
		{
			fileName = URL.substring(URL.lastIndexOf('/')+1, URL.lastIndexOf('.')-1);
			int i = 0;
			while (new File(System.getProperty("user.dir") + "\\media\\music\\" + fileName + ".mp3").exists())
			{
				if (fileName.contains("("))
				fileName = fileName.substring(0, fileName.lastIndexOf('(')-1) + "(" + i + ")";
				else
				fileName = fileName + "(" + i + ")";
				i++;
			}
		}

		if (!new File(System.getProperty("user.dir") + "\\media\\music\\" + fileName + ".mp3").exists())
		{
			path = System.getProperty("user.dir") + "\\media\\music\\" + fileName + ".mp3";
			try {
			URLConnection conn = new URL(URL).openConnection();
			InputStream is = conn.getInputStream();
			
			OutputStream outstream = new FileOutputStream(new File("\\media\\music\\" + fileName + ".mp3"));
			byte[] buffer = new byte[4096];
			int len;
			while ((len = is.read(buffer)) > 0) {
				outstream.write(buffer, 0, len);
			}
			outstream.close();
		} catch (MalformedURLException e) {
			_textChannel.sendMessage("Неправильный URL");
			e.printStackTrace();
		} catch (IOException e) {
			_textChannel.sendMessage("Неудалось найти файл - " + fileName);
			e.printStackTrace();
		}
			return path;
		} else
			return null;
	}

	// Загрузить плейлист из \media\music\playList.json
	public LinkedList<File> LoadPlaylist(String playlistName) {
		JSONObject playlistsJSON = Utils.ReadFromJSON("\\media\\music\\playlists.json");
		
		if(playlistsJSON.has(playlistName))
		{
			JSONArray playlists = Utils.ReadFromJSON("\\media\\music\\playlists.json").getJSONArray(playlistName);
		
			LinkedList<File> musicQuery = new LinkedList<File>();
			for (Object music : playlists)
				musicQuery.push(
					new File(Utils.GetRootFolder()+"\\media\\music\\"+music.toString()+".mp3")
				);
	
			return musicQuery;
		} else
			return null;
	}

	// Создать плейлист в \media\music\playList.json
	public void NewPlaylist(String playlistName)
	{
		if (playlistName != null)
		{
			JSONObject playlistsJSON = Utils.ReadFromJSON("\\media\\music\\playlists.json");
			
			if (!playlistsJSON.has(playlistName)) {
				JSONArray newPlaylist = new JSONArray();
				playlistsJSON.put(playlistName, newPlaylist);
				_textChannel.sendMessage(
					new MessageBuilder()
						.appendString("Создан плейлист ")
						.appendString(playlistName)
						.build()
				);
				Utils.WriteToJSON("\\media\\music\\playlists.json", playlistsJSON);
			} else {
				_textChannel.sendMessage(
					new MessageBuilder()
						.appendString("Плейлист ")
						.appendString(playlistName)
						.appendString(" уже существует").build()
				);
			}
		}
	}

	// Добавить трек в плейлист
	public void AddToPlaylist(String playlistName, List<String> songs)
	{
		if (songs.size() != 0)
		{
			JSONObject playlistsJSON = Utils.ReadFromJSON("\\media\\music\\playlists.json");
		
			if(playlistsJSON.has(playlistName))
			{
				JSONArray playlist = playlistsJSON.getJSONArray(playlistName);
				
				for (String song : songs)
				{
					File fileToCheck = null;
					fileToCheck = new File(Utils.GetRootFolder()+"\\media\\music\\"+song+".mp3");
					
					if (fileToCheck.exists()) {
						playlist.put(fileToCheck.getName().split("\\.")[0]);
					}
					playlistsJSON.put(playlistName, (Object)playlist);
				}
				
				Utils.WriteToJSON("\\media\\music\\playlists.json", playlistsJSON);
			} else 
				_textChannel.sendMessage(
					new MessageBuilder()
						.appendString("Плейлист ")
						.appendString(playlistName)
						.appendString(" не найден. Сначала создайте его")
						.build()
				);
		}
	}

	// Удалить трек из плейлиста
	public void DeleteFromPlaylist(String playlistName, List<String> songs)
	{
		if (songs.size() > 0)
		{
			JSONObject playlistsJSON = Utils.ReadFromJSON("\\media\\music\\playlists.json");
			
			if(playlistsJSON.has(playlistName))
			{
				JSONArray playlist = playlistsJSON.getJSONArray(playlistName);
				
				for (String song : songs)
					for (int j=0; j<playlist.length(); j++)
					{
						if(playlist.get(j).toString().equals(song))
							playlist.remove(j);
					}
				playlistsJSON.put(playlistName, (Object)playlist);
				
				Utils.WriteToJSON("\\media\\music\\playlists.json", playlistsJSON);
			} else
				_textChannel.sendMessage(
					new MessageBuilder()
						.appendString("Плейлист ")
						.appendString(playlistName)
						.appendString(" не найден. Сначала создайте его")
						.build()
				);
		}
	}

	// Показать все треки в плейлисте
	public void PrintPlaylist(String playlistName)
	{
		JSONObject playlistsJSON = Utils.ReadFromJSON("\\media\\music\\playlists.json");
		
		JSONArray playlist = playlistsJSON.getJSONArray(playlistName);
		if (playlist.length() != 0)
		{
			MessageBuilder builder = new MessageBuilder();
			builder.appendString("Плейлист ").appendString(playlistName).appendString(":\n");
			for(Object track : playlist)
				builder.appendString(track.toString()).appendString(" \n");
			_textChannel.sendMessage(builder.build());
		}
	}

	// Показать все плейлисты из \media\music\playList.json
	public void PrintAllPlaylists()
	{
		JSONObject playlistsJSON = Utils.ReadFromJSON("\\media\\music\\playlists.json");

		MessageBuilder builder = new MessageBuilder();
		builder.appendString("Используйте .playlists [имя плейлиста] для списка песен в плейлисте\n");
		builder.appendString("Список плейлистов:\n");
		for (String name : playlistsJSON.keySet())
			builder.appendString(name.toString()).appendString(" \n");
		_textChannel.sendMessage(builder.build());
	}
}