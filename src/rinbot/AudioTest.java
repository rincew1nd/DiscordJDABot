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
import net.dv8tion.jda.entities.VoiceChannel;

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
		private Player player;
		private TextChannel channel;
		private String currentPlaylist;
		private VoiceChannel currentChannel = null;
		private LinkedList<File> musicQuery;
		private StringBuilder sBuilder;

		//OneShot Player variables
		private Player playerOneShot;
		private boolean oneShot = false;
		private VoiceChannel lastChannel = null;
		
		public static class AudioTestHolder {
			public static final AudioTest HOLDER_INSTANCE = new AudioTest();
		}
			
		public static AudioTest getInstance() {
			return AudioTestHolder.HOLDER_INSTANCE;
		}
		
		private AudioTest() {}
		
		public AudioTest AudioTestInit(JDA jda)
		{
			this.jda = jda;
			musicQuery = new LinkedList<File>();
			sBuilder = new StringBuilder();
			currentPlaylist = "";
			
			ActionListener taskPerformer = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(playerOneShot!= null && playerOneShot.isStopped() && oneShot)
					{
						oneShot = false;
						Reconnect(lastChannel);
			            jda.getAudioManager().setSendingHandler(player);
			            player.play();
					} else if(player == null && playerOneShot != null && playerOneShot.isStopped())
					{
						jda.getAudioManager().closeAudioConnection();
					}
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
			
			return AudioTest.AudioTestHolder.HOLDER_INSTANCE;
		}
		
		public AudioTest SetChannel(TextChannel channel)
		{
			this.channel = channel;
			return AudioTest.AudioTestHolder.HOLDER_INSTANCE;
		}
		
		
		///////////////////////////////
		//       GETTER SETTER       //
		///////////////////////////////
		
		
		public Player GetPlayer() { return player; };
		public Player GetPlayerOneShot() { return playerOneShot; };
		
		
		///////////////////////////////////////
		//  ������� ��� ������������ ������  //
		///////////////////////////////////////
		
		
		public void PlayOneshot(File file, VoiceChannel channel)
		{
			if (player != null)
			{
				player.pause();
				Reconnect(channel);
	            oneShot = true;
			} else {
				jda.getAudioManager().openAudioConnection(channel);
			}
			
			File audioFile = null;
	    	try {
	    		audioFile = file;
				playerOneShot = new FilePlayer(audioFile);
	            jda.getAudioManager().setSendingHandler(playerOneShot);
	            playerOneShot.play();
			} catch (IOException e) {
	            e.printStackTrace();
	        } catch (UnsupportedAudioFileException e) {
	            e.printStackTrace();
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	    	}
		}
		
		// ������ �������������� ������
	    public void StartPlaying()
	    {
	    	if (musicQuery.isEmpty())
	    		LoadPlaylist(currentPlaylist, channel);
	    	
	    	File audioFile = null;
	    	try {
	    		audioFile = musicQuery.getFirst();
				player = new FilePlayer(musicQuery.getFirst());
	            jda.getAudioManager().setSendingHandler(player);
	            player.play();
			} catch (IOException e) {
	            channel.sendMessage("�� ������� ����� ���� " + audioFile.getName());
	            e.printStackTrace();
	        } catch (UnsupportedAudioFileException e) {
	        	channel.sendMessage("�� ������� ���������� ����. ��� �� ����� ���� ��� �� �������������� ������");
	            e.printStackTrace();
	        } catch (IllegalArgumentException e) {
	        	channel.sendMessage("�� ������� ���������� ����. ��� �� ����� ���� ��� �� �������������� ������");
	            e.printStackTrace();
	    	}
	    }

		public void Play(String playlist, VoiceChannel voiceChannel)
		{
			if (currentChannel != null || jda.getAudioManager().isConnected())
			{
				if (player.isPlaying() || playerOneShot.isPlaying())
					channel.sendMessage("��� ��� ���-�� ������");
				else
					if (currentChannel != channel)
					{
						Reconnect(voiceChannel);
						currentChannel = voiceChannel;
						currentPlaylist = playlist;
						StartPlaying();
					}
			} else {
				jda.getAudioManager().openAudioConnection(voiceChannel);
				currentChannel = voiceChannel;
				currentPlaylist = playlist;
				StartPlaying();
			}
		}
	    
		public void Reconnect(VoiceChannel channel)
		{
			if (jda.getAudioManager().isConnected())
				lastChannel = jda.getAudioManager().getConnectedChannel();
			
			jda.getAudioManager().closeAudioConnection();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			jda.getAudioManager().openAudioConnection(channel);
		}
		
		// ��������� ���������� ��������������� � �������� ��������
		public void Stop(TextChannel channel)
		{
			if (jda.getAudioManager().isConnected())
				if (player == null || !player.isPlaying())
					channel.sendMessage("��� ������ �� ������!");
				else
				{
					player.stop();
					musicQuery.clear();
					player = null;
				}
			else
	        	channel.sendMessage("��� �� ����������� �� � ������ ������!\n�������������� � ������ ��������� ������� !connect %channel%");
		}

		// ���������� ������� ����
		public void Skip(TextChannel channel)
		{
			if (musicQuery.isEmpty())
				channel.sendMessage("������ ����������");
			else
			{
	        	player.stop();
	        	musicQuery.removeFirst();
	        	if (!musicQuery.isEmpty())
	        		StartPlaying();
			}
		}

		// ���������� ������� ��������
		public void Shuffle(TextChannel channel)
		{
			Collections.shuffle(musicQuery);
			StartPlaying();
		}
		
		
		//////////////////////////////////////////
		//  ������� ��� ���������� �����������  //
		//////////////////////////////////////////
	    
	    // �������� ���� ������ ������ �� ����� \media\music
		public void GetMusicList(TextChannel channel)
		{
			sBuilder.setLength(0);
			sBuilder.append("������ ��������� ������:\r\n");
			
			File folder = new File(System.getProperty("user.dir") + "\\media\\music");
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++)
				if (listOfFiles[i].isFile())
					sBuilder.append(listOfFiles[i].getName() + "\r\n");
			
			channel.sendMessage(sBuilder.toString());
		}

	    // ������� ���� � ����� \media\music
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
	        				mes = channel.sendMessage("��������� �������...");
	        				System.out.print("���\n");
	        			}
	        			else if (retryCount == 2)
	        			{
	        				mes.deleteMessage();
	        				channel.sendMessage("�������� ����� �� �������� � ������� ���� �����.\n" +
	        									"�������� ����� ������ www.youtubeinmp3.com.\n" +
	        									"� ���� ����� ������� �������, ��������� ����� ���� �����.");
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
						System.out.print("�������� ������...");
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
					channel.sendMessage("������������ URL");
					e.printStackTrace();
				} catch (IOException e) {
					channel.sendMessage("��������� ����� ���� - " + fileName);
					e.printStackTrace();
				}
				return path;
	        } else
	        	return null;
	    }

	    // ��������� �������� �� \media\music\playList.json
	    public void LoadPlaylist(String playlistName, TextChannel channel) {
    		JSONArray playlists = ReadFromJSON("\\media\\music\\playlists.json").getJSONArray(playlistName);
			
			musicQuery.clear();
			for (Object music : playlists)
				musicQuery.push(
					new File(MyUtils.GetRootFolder()+"\\media\\music\\"+music.toString()+".mp3")
				);
			
			currentPlaylist = playlistName;
		}

	    // ������� �������� � \media\music\playList.json
	    public void NewPlaylist(String playlistName, TextChannel channel)
	    {
	    	if (playlistName != null)
	    	{
		    	JSONObject playlistsJSON = ReadFromJSON("\\media\\music\\playlists.json");
		    	
				if (!playlistsJSON.has(playlistName)) {
					JSONArray newPlaylist = new JSONArray();
					playlistsJSON.put(playlistName, newPlaylist);
					currentPlaylist = playlistName;
				} else {
					currentPlaylist = playlistName;
				}

		    	WriteToJSON("\\media\\music\\playlists.json", playlistsJSON);
	    	} else {
	    		channel.sendMessage("��������� �������� ���������");
	    	}
	    }

	    // �������� ���� � ������� ��������
	    public void AddToPlaylist(String playlistName, String[] songs)
	    {
	    	if (songs.length != 0)
	    	{
		    	JSONObject playlistsJSON = ReadFromJSON("\\media\\music\\playlists.json");
		    	
		    	File fileToCheck = null;
		    	//if (songs.length == 1)
		    	fileToCheck = new File(MyUtils.GetRootFolder()+"\\media\\music\\"+songs[0]+".mp3");
		    	//else
		    	//	fileToCheck = new File(DownloadMusic(args[0], args[1], channel));
				JSONArray playlist = playlistsJSON.getJSONArray(playlistName);

				if(playlist.length() != 0)
				{
			    	if (fileToCheck.exists()) {
			    		playlist.put(fileToCheck.getName().split("\\.")[0]);
					}
			    	playlistsJSON.put(songs[0], true);

			    	WriteToJSON("\\media\\music\\playlists.json", playlistsJSON);
				} else 
					channel.sendMessage("�������� " + playlistName + " �� ������");
	    	} else {
	    		channel.sendMessage("��������� �������� ���������");
	    	}
	    }

	    // �������� ���� � ������� ��������
//	    public void DeleteFromPlaylist(String playlistName, String[] songs)
//	    {
//	    	if (args.length > 2)
//	    	{
//		    	JSONObject playlistsJSON = ReadFromJSON("\\media\\music\\playlists.json");
//		    	
//		    	if (args[0] == "current") args[0] = currentPlaylist; 
//				JSONArray playlists = playlistsJSON.getJSONArray(args[0]);
//				
//				if(playlists != null)
//				{
//					for (int i=1; i<args.length; i++)
//				    	for (int j=0; j<playlists.length(); j++)
//				    		if(playlists.get(j) == args[i])
//				    			playlists.remove(j);
//			    	playlistsJSON.put(args[0], playlists);
//			    	
//			    	WriteToJSON("\\media\\music\\playlists.json", playlistsJSON);
//				} else
//					channel.sendMessage("�������� " + args[0] + " �� ������");
//	    	} else {
//	    		channel.sendMessage("��������� �������� ���������");
//	    	}
//	    }
	    
	    // �������� ��� ����� � ���������
		public void PrintPlaylist(String playlistName)
		{
			JSONObject playlistsJSON = ReadFromJSON("\\media\\music\\playlists.json");
	    	
			JSONArray playlist = playlistsJSON.getJSONArray(playlistName);
			if (playlist.length() != 0)
			{
				sBuilder.setLength(0);
				sBuilder.append("�������� " + playlistName + ":\n");
				for(Object track : playlist)
					sBuilder.append(track.toString() + "\n");
				channel.sendMessage(sBuilder.toString());
			}
		}

		public void PrintPlaylist()
		{
			if (currentPlaylist != "")
				PrintPlaylist(currentPlaylist);
			else
				channel.sendMessage("� ������ ������ ������� �������� �� �����");
		}
		
	    // �������� ��� ��������� �� \media\music\playList.json
	    public void PrintAllPlaylists()
	    {
		    JSONObject playlists = new JSONObject();
			try {
				playlists = new JSONObject(ReadFile(MyUtils.GetRootFolder()+"\\media\\music\\playlists.json"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			sBuilder.setLength(0);
			sBuilder.append("����������� .playlists [��� ���������] ��� ������ ����� � ���������\n");
			sBuilder.append("������ ����������:\n");
			for (String name : playlists.keySet()) 
				sBuilder.append(name.toString()+"\n");
			channel.sendMessage(sBuilder.toString());
	    }
	    
	    // �������� ������ ��� ���������� ����� � Youtube
	    public String GetYoutubeMusic(String id) throws JSONException, Exception
	    {
	    	JSONObject responseJSON = new JSONObject(ReadUrl("http://www.youtubeinmp3.com/fetch/?format=text&video=http://www.youtube.com/watch?v="+id));
			return responseJSON.getString("link");
	    }

	    
	    /////////////////////////////////////////
	    // ---- ������� � ��������� ������ ----//
	    /////////////////////////////////////////
	    
	    // ��������� JSON ����
	    public JSONObject ReadFromJSON(String path)
	    {
	    	JSONObject JSON = new JSONObject();
			try {	
				JSON = new JSONObject(ReadFile(MyUtils.GetRootFolder()+path));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return JSON;
	    }
	    
	    // �������� � JSON ����
	    public void WriteToJSON(String path, JSONObject toWrite) {
	    	FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(MyUtils.GetRootFolder()+path);
				fileWriter.write(toWrite.toString());
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    // �������� ����� �� ������� ����������
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

	    // ������� ����� �� ����� � ������
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