package rinbot;

/**
 *    Copyright 2015-2016 Austin Keener & Michael Ritter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;

public class AudioTest
{
	
		//TODO: Add GETTER SETTER for private things
		private JDA jda;
		private TextChannel channel;
		Player player;
		private String currentPlaylist;
		private LinkedList<File> musicQuery;
		private StringBuilder sBuilder;
	
		AudioTest(JDA jda)
		{
			this.jda = jda;
			musicQuery = new LinkedList<File>();
			sBuilder = new StringBuilder();
			currentPlaylist = "";
			
			ActionListener taskPerformer = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(player != null)
					{
						if(player.isStopped() && !musicQuery.isEmpty())
						{
				            musicQuery.removeFirst();
				            if (!musicQuery.isEmpty())
				            	StartPlaying();
						} else if (player.isPlaying())
						{
							String audioName = musicQuery.getFirst().getName();
							jda.getAccountManager().setGame(audioName.substring(0, audioName.indexOf('.')));
						}
					} else
						jda.getAccountManager().setGame("with Butter Bot");
				}     
			};
			new Timer(1000, taskPerformer).start();
		}
		
		
		///////////////////////////////////////
		//  Функции для проигрывания музыки  //
		///////////////////////////////////////
		
		// Начать воспроизводить музыку
	    public void StartPlaying()
	    {
	    	File audioFile = null;
	    	try {
	    		audioFile = musicQuery.getFirst();
				player = new FilePlayer(musicQuery.getFirst());
	            jda.getAudioManager().setSendingHandler(player);
	            player.play();
			} catch (IOException e) {
	            channel.sendMessage("Не удалось найти файл " + audioFile.getName());
	            e.printStackTrace();
	        } catch (UnsupportedAudioFileException e) {
	        	channel.sendMessage("Не удалось обработать файл. Это не аудио файл или не поддерживаемый формат");
	            e.printStackTrace();
	        } catch (IllegalArgumentException e) {
	        	channel.sendMessage("Не удалось обработать файл. Это не аудио файл или не поддерживаемый формат");
	            e.printStackTrace();
	    	}
	    }

		public void Play(TextChannel channel)
		{
			this.channel = channel;
		}
	    
		// Полностью остановить воспроизведение и очистить плейлист
		public void Stop(TextChannel channel)
		{
			if (jda.getAudioManager().isConnected())
				if (player == null || !player.isPlaying())
					channel.sendMessage("Бот ничего не играет!");
				else
				{
					player.stop();
					musicQuery.clear();
					player = null;
				}
			else
	        	channel.sendMessage("Бот не присоединен ни к одному каналу!\nПрисоединитесь к каналу используя команду !connect %channel%");
		}

		// Пропустить текущий трек
		public void Skip(TextChannel channel)
		{
			if (musicQuery.isEmpty())
				channel.sendMessage("Нечего пропускать");
			else
			{
	        	player.stop();
	        	musicQuery.removeFirst();
	        	if (!musicQuery.isEmpty())
	        		StartPlaying();
			}
		}

		// Перемешать текущий плейлист
		public void Shuffle(TextChannel channel)
		{
			Collections.shuffle(musicQuery);
			StartPlaying();
		}
		
		
		//////////////////////////////////////////
		//  Функции для управления плейлистами  //
		//////////////////////////////////////////
	    
	    // Получить весь список треков из папки \media\music
		public void GetMusicList(TextChannel channel)
		{
			sBuilder.setLength(0);
			sBuilder.append("Список доступной музыки:\r\n");
			
			File folder = new File(System.getProperty("user.dir") + "\\media\\music");
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++)
				if (listOfFiles[i].isFile())
					sBuilder.append(listOfFiles[i].getName() + "\r\n");
			
			channel.sendMessage(sBuilder.toString());
		}

	    // Скачать трек в папку \media\music
	    public String DownloadMusic(String URL, String fileName, TextChannel channel)
	    {
	    	URLConnection conn;
	        InputStream is;
	        OutputStream outstream;
	        String path;
	        
	        if (URL.contains("!y"))
			{
	        	int retryCount = 0;
	        	Message mes = null;
	        	String urlYtb = "";
	        	while(true)
	        	{
	        		try
	        		{
		        		urlYtb = GetYoutubeMusic(URL.substring(2, URL.length()));
	        		} catch (Exception e) {
	        			if (retryCount == 0)
	        			{	
							try {
								URL url = new URL("http://www.youtubeinmp3.com/download/?video=http://www.youtube.com/watch?v="+URL.substring(2, URL.length()) + "&autostart=1");
								url.openConnection();
							} catch (MalformedURLException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
	        				mes = channel.sendMessage("Подождите немного...");
	        				System.out.print("Ждём\n");
	        			}
	        			else if (retryCount == 2)
	        			{
	        				mes.deleteMessage();
	        				channel.sendMessage("Загрузка видео не началась в течении двух минут.\n" +
	        									"Возможно лежит сервер www.youtubeinmp3.com.\n" +
	        									"А если видео слишком большое, повторите через пару минут.");
	        				return null;
	        			}
	        		}

		        	if (urlYtb != "")
		        	{
		        		URL = urlYtb;
		        		break;
		        	}
	        		
					try {
        				retryCount++;
						Thread.sleep(60000);
					} catch (InterruptedException e1) {
						System.out.print("Случился пиздец...");
						e1.printStackTrace();
					}
	        	}
			}
	        
	        if (!new File(System.getProperty("user.dir") + "\\media\\music\\" + fileName + ".mp3").exists())
	        {
	        	path = System.getProperty("user.dir") + "\\media\\music\\" + fileName + ".mp3";
				try {
					conn = new URL(URL).openConnection();
					is = conn.getInputStream();
					outstream = new FileOutputStream(new File(path));
			        byte[] buffer = new byte[4096];
			        int len;
			        while ((len = is.read(buffer)) > 0) {
			            outstream.write(buffer, 0, len);
			        }
			        outstream.close();
				} catch (MalformedURLException e) {
					channel.sendMessage("Неправильный URL");
					e.printStackTrace();
				} catch (IOException e) {
					channel.sendMessage("Неудалось найти файл - " + fileName);
					e.printStackTrace();
				}
				return path;
	        } else
	        	return null;
	    }

	    // Загрузить плейлист из \media\music\playList.json
	    public void LoadPlaylist(String playlistName, TextChannel channel) {
    		JSONObject JSON = new JSONObject();
			try {	
				JSON = new JSONObject(ReadFile("assets\\playlists.json"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			JSONArray playlists = JSON.getJSONArray(playlistName);
			
			musicQuery.clear();
			for (Object music : playlists)
				musicQuery.push(
					new File(System.getProperty("user.pref")+"\\media\\music\\"+music.toString()+".mp3")
				);
			
			currentPlaylist = playlistName;
			
			if (player == null)
				StartPlaying();
		}

	    // Создать плейлист в \media\music\playList.json
	    public void NewPlaylist(String playlistName, TextChannel channel)
	    {
	    	if (playlistName != null)
	    	{
		    	JSONObject playlistsJSON = new JSONObject(ReadFile("assets\\playlists.json"));
		    	
				if (!playlistsJSON.has(playlistName)) {
					JSONArray newPlaylist = new JSONArray();
					playlistsJSON.put(playlistName, newPlaylist);
					currentPlaylist = playlistName;
				} else {
					currentPlaylist = playlistName;
				}
				
				WritePlaylistJSON(playlistsJSON);
	    	} else {
	    		channel.sendMessage("Переданны неверные аргументы");
	    	}
	    }

	    // Добавить трек в текущий плейлист
	    //TODO: ДОБАВИТЬ КОД ДЛЯ ДОБАВЛЕНИЯ ТРЕКОВ В ПЛЕЙЛИСТЫ
	    public void AddToPlaylist(String[] args)
	    {
	    	if (args.length == 1 || args.length == 2)
	    	{
		    	JSONObject playlistsJSON = new JSONObject(ReadFile("media\\playlists.json"));
		    	File fileToCheck = null;
		    	if (args.length == 1)
		    		fileToCheck = new File(System.getProperty("user.pref")+"\\media\\music\\"+args[0]+".mp3");
		    	else
		    		fileToCheck = new File(DownloadMusic(args[0], args[1], channel));
		    		
		    	
		    	JSONArray test = playlistsJSON.getJSONArray("");
		    	//if (fileToCheck.exists()) {
		    	//	playlistsJSON.put();
				//}
		    	
		    	//WritePlaylistJSON(play);
	    	} else {
	    		channel.sendMessage("Переданны неверные аргументы");
	    	}
	    }

	    // Показать все треки в плейлисте
		public void PrintPlaylist()
		{
			if (jda.getAudioManager().isConnected())
				if (musicQuery.isEmpty())
					channel.sendMessage("Очередь пуста");
				else
				{
					sBuilder.setLength(0);
					int i = 1;
					for(File track : musicQuery)
					{
		        		sBuilder.append(i + ": " + track.getName() + "\r\n");
		        		i++;
					}
					channel.sendMessage("Очередь:\r\n" + sBuilder.toString());
				}
			else
	        	channel.sendMessage("Бот не присоединен ни к одному каналу!\nПрисоединитесь к каналу используя команду !connect %channel%");
		}

	    // Показать все плейлисты из \media\music\playList.json
	    public void PrintAllPlaylists()
	    {
		    JSONObject playlists = new JSONObject();
			try {
				playlists = new JSONObject(ReadFile(System.getProperty("user.pref")+"\\assets\\playlists.json"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			sBuilder.setLength(0);
			sBuilder.append("Используйте .playlists [имя плейлиста] для списка песен в плейлисте\n");
			sBuilder.append("Список плейлистов:\n");
			for (String name : playlists.keySet()) 
				sBuilder.append(name.toString()+"\n");
			channel.sendMessage(sBuilder.toString());
	    }

	    // Обновить плейлист в \media\music\playList.json
	    public static void WritePlaylistJSON(JSONObject toWrite) {
	    	FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(System.getProperty("user.pref")+"\\assets\\playlists.json");
				fileWriter.write(toWrite.toString());
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    // Получить ссылку для скачивания трека с Youtube
	    public String GetYoutubeMusic(String id) throws JSONException, Exception
	    {
	    	JSONObject responseJSON = new JSONObject(ReadUrl("http://www.youtubeinmp3.com/fetch/?format=text&video=http://www.youtube.com/watch?v="+id));
			return responseJSON.getString("link");
	    }

	    
	    /////////////////////////////////////////
	    // ---- ВЫНЕСТИ В ОТДЕЛЬНЫЕ КЛАССЫ ----//
	    /////////////////////////////////////////
	    
	    
	    // Получить JSON из внешних источников
	    private String ReadUrl(String urlString) throws Exception {
	        BufferedReader reader = null;
	        try {
	            URL url = new URL(urlString);
	            reader = new BufferedReader(new InputStreamReader(url.openStream()));
	            StringBuffer buffer = new StringBuffer();
	            int read;
	            char[] chars = new char[1024];
	            while ((read = reader.read(chars)) != -1)
	                buffer.append(chars, 0, read); 

	            return buffer.toString();
	        } finally {
	            if (reader != null)
	                reader.close();
	        }
	    }

	    // Считать текст из файла в строку
	    public String ReadFile(String path)
	    {
	    	byte[] encoded;
			try {
				encoded = Files.readAllBytes(Paths.get(path));
	    		return new String(encoded, Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	    }
}