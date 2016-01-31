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
import net.dv8tion.jda.entities.TextChannel;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class AudioTest
{
		public JDA jda;
		public Player player;
		public LinkedList<MusicElem> musicQuery;
    	StringBuilder sBuilder;
	
		AudioTest(JDA jda)
		{
			this.jda = jda;
			musicQuery = new LinkedList<MusicElem>();
			sBuilder = new StringBuilder();
			
			ActionListener taskPerformer = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(player != null)
						if(player.isStopped() && !musicQuery.isEmpty())
						{
				            musicQuery.removeFirst();
				            if (!musicQuery.isEmpty())
				            	StartPlaying(musicQuery.getFirst().channel);
						}
				}     
			};
			new Timer(1000, taskPerformer).start();
		}
		
		public void Playlist(TextChannel channel)
		{
			if (jda.getAudioManager().isConnected())
				if (musicQuery.isEmpty())
					channel.sendMessage("Очередь пуста");
				else
				{
					sBuilder.setLength(0);
					int i = 1;
					for(MusicElem track : musicQuery)
					{
		        		sBuilder.append(i + ": " + track.audioFile.getName() + "\r\n");
		        		i++;
					}
					channel.sendMessage("Очередь:\r\n" + sBuilder.toString());
				}
			else
	        	channel.sendMessage("Бот не присоединен ни к одному каналу!\nПрисоединитесь к каналу используя команду !connect %channel%");
		}
		
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
		
		public void Skip(TextChannel channel)
		{
			if (musicQuery.isEmpty())
				channel.sendMessage("Нечего пропускать");
			else
			{
	        	player.stop();
	        	musicQuery.removeFirst();
	        	if (!musicQuery.isEmpty())
	        		StartPlaying(channel);
			}
		}
		
		public void Play(String[] strarr, TextChannel channel)
		{
			if (jda.getAudioManager().isConnected())
				if (player == null && (strarr.length == 3 || strarr.length == 2))
	            {
	                if(AddMusicToQuery(strarr, channel))
	                	StartPlaying(channel);
		        } else if (strarr.length == 3 || strarr.length == 2)
		        {
		        	if(AddMusicToQuery(strarr, channel) && (player.isStopped() || player.isPaused()))
	            		StartPlaying(channel);
		        } else 
		        	channel.sendMessage("Предоставлены неправильные аргументы!");
			else
	        	channel.sendMessage("Бот не присоединен ни к одному каналу!\nПрисоединитесь к каналу используя команду !connect %channel%");
		}
	    
		public void Stop(TextChannel channel)
		{
			if (jda.getAudioManager().isConnected())
				if (player == null || !player.isPlaying())
					channel.sendMessage("Бот ничего не играет!");
				else
					player.stop();
			else
	        	channel.sendMessage("Бот не присоединен ни к одному каналу!\nПрисоединитесь к каналу используя команду !connect %channel%");
		}
		
	    public void StartPlaying(TextChannel channel)
	    {
	    	File audioFile = null;
	    	try {
	    		audioFile = musicQuery.getFirst().audioFile;
				player = new FilePlayer(musicQuery.getFirst().audioFile);
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
	   
		public boolean AddMusicToQuery(String[] args, TextChannel channel)
	    {
			String path;
			if (args.length == 3)
				path = DownloadMusic(args[1], args[2], channel);
			else
				if (new File(System.getProperty("user.dir") + "\\media\\music\\" + args[1] + ".mp3").exists())
		        	path = System.getProperty("user.dir") + "\\media\\music\\" + args[1] + ".mp3";
				else
				{
		        	channel.sendMessage("Не найдена песня под названием " + args[1]);
		        	return false;
				}
			
	        
	        if (path != null)
	        {
		    	File audioFile = new File(path);
				musicQuery.add(new MusicElem(audioFile, channel));
				return true;
	        } else {
	        	channel.sendMessage("Данное название уже занято!\nПопробуйте команду !play;%sound_name%");
	        	return false;
	        }
	    }
	    
	    public String DownloadMusic(String URL, String fileName, TextChannel channel)
	    {
	    	URLConnection conn;
	        InputStream is;
	        OutputStream outstream;
	        String path;
	        
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
	    
	    public class MusicElem
	    {
	    	File audioFile;
	    	TextChannel channel;
	    	
	    	public MusicElem(File audioFile, TextChannel channel)
	    	{
	    		this.audioFile = audioFile;
	    		this.channel = channel;
	    	}
	    }
}