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

import net.dv8tion.jda.*;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

import commands.Cena;
import commands.Playlist;
import commands.Test;
import commands.Z0rde;

import java.io.File;
import java.util.List;
import java.util.Random;

public class MessageListener extends ListenerAdapter
{
	public static JDA jda;
	public static MusicPlayer musicPlayer;
	public static PlaylistManager playlistManager;
	public static UserStatistic statistic;
	
	public static void main(String[] args)
    {
		try {
			jda = new JDABuilder(args[0],args[1]).buildAsync();
	        jda.addEventListener(new MessageListener());
	        musicPlayer = MusicPlayer.getInstance();
	        musicPlayer.MusicPlayerInit(jda);
	        playlistManager = PlaylistManager.getInstance();
	        statistic = new UserStatistic(jda);
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
    }
	
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {	
    	if (event.getMessage().getMentionedUsers().stream().filter(z -> z.getUsername().equals(jda.getSelfInfo().getUsername())).count() == 1)
        	if (event.getMessage().getChannelId().equalsIgnoreCase("95861645816922112"))
        		jda.getTextChannelById("126041398473523201").sendMessage(
        				new MessageBuilder()
        				.appendString("` ")
        				.appendEveryoneMention()
        				.appendString(event.getMessage().getContent().replace("@"+jda.getSelfInfo().getUsername()+" ", ""))
        				.appendString(" `")
        				.build()
        			);
    	if (event.getAuthor().getUsername().equalsIgnoreCase("RinBot")) return;
    	
    	new Cena(event);
    	new Playlist(event);
    	new Test(event);
    	new Z0rde(event);
    	
    	Random rnd = new Random();
    	String message = event.getMessage().getContent();
    	String[] messageArr = message.split("\\s+");
    	
        if (messageArr[0].equalsIgnoreCase(".con"))
        {
        	if (messageArr.length == 2)
        	{
	        	VoiceChannel ch = jda.getVoiceChannelByName(messageArr[1]).get(0);
	        	System.out.println(ch.getName());
	        	if (!ch.checkPermission(jda.getUsersByName("RinBot").get(0), Permission.VOICE_CONNECT))
	        		event.getTextChannel().sendMessage("�� ������� ���� ��� ����� � ����� " + ch.getName());
	        	else
	        	{
	        		event.getJDA().getAudioManager().closeAudioConnection();
	        		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        		event.getJDA().getAudioManager().openAudioConnection(ch);
        			event.getTextChannel().sendMessage("������� ����� � " + ch.getName());
	        	}
        	} else
        		event.getTextChannel().sendMessage("������������ �������� �������.\r\n.con %channel%");
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".dcon"))
		{
        	event.getJDA().getAudioManager().closeAudioConnection();
    		event.getTextChannel().sendMessage("������� ����� �� ������");
        	event.getMessage().deleteMessage();
		} else if (message.equalsIgnoreCase(".skip"))
        {
			musicPlayer.SetChannel(event.getTextChannel());
			musicPlayer.Skip();
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".stop"))
        {
        	musicPlayer.Stop(event.getTextChannel());
        	event.getMessage().deleteMessage();
        } if (messageArr[0].equalsIgnoreCase(".volume"))
        {
        	if(messageArr.length == 2 && tryParseFloat(messageArr[1]))
        	{
        		event.getTextChannel().sendMessage("��������� - " + Float.parseFloat(messageArr[1]));
        		musicPlayer.GetPlayer().setVolume(Float.parseFloat(messageArr[1]));
        	}
        	else
        		event.getTextChannel().sendMessage("������������ ��������� �������.\r\n.volume %�����_�����%");
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".ml"))
        {
        	playlistManager.SetChannel(event.getTextChannel());
	        playlistManager.GetMusicList();
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".help"))
        {
        	MessageBuilder messageBuilder = new MessageBuilder();
        	messageBuilder.appendString("������ ������:\r\n");
        	messageBuilder.appendString("\t.help - ������ ������\r\n");
        	messageBuilder.appendString("\t.cat - ������ ���������� ���� � �����\r\n");
        	messageBuilder.appendString("\t.rnd %�����_�����% - �������� ��������� ����� ����� �� 0 �� %�����_�����%\r\n");
        	messageBuilder.appendString("����:\r\n");
        	messageBuilder.appendString("\t.con %channel% - �������������� � ������\r\n");
        	messageBuilder.appendString("\t.dcon - ������� �� ���� ����\r\n");
        	messageBuilder.appendString("\t.ml - ������ ����������� ������\r\n");
        	messageBuilder.appendString("\t.pl;current - ��������\r\n");
        	messageBuilder.appendString("\t.play;%URL%;%��������% - ��������� ����� � ��������� � � ��������\r\n");
        	messageBuilder.appendString("\t.play;!y%youtube-id%;%��������% - ��������� ����� c youtube � ��������� � � ��������\r\n");
        	messageBuilder.appendString("\t.play;%��������% - ��������� ��������� ����� � ��������\r\n");
        	messageBuilder.appendString("\t.stop - ������������� ������\r\n");
        	messageBuilder.appendString("\t.skip - ���������� �����\r\n");
        	
        	event.getAuthor().getPrivateChannel().sendMessage(messageBuilder.build());
        	event.getMessage().deleteMessage();
        } else if(event.getMessage().getContent().equalsIgnoreCase(".cat"))
        {
        	event.getTextChannel().sendFile(new File(System.getProperty("user.dir") + "\\media\\images\\cat" + rnd.nextInt(7) + ".jpg"));
        	event.getMessage().deleteMessage();
        }
        else if (messageArr[0].equalsIgnoreCase(".rnd"))
        {
        	if(messageArr.length == 2 && tryParseInt(messageArr[1]))
        		event.getTextChannel().sendMessage("��������� ����� - " + rnd.nextInt(Integer.parseInt(messageArr[1])));
        	else
        		event.getTextChannel().sendMessage("������������ ��������� �������.\r\n.random %�����_�����%");
        	event.getMessage().deleteMessage();
        } else if (event.getMessage().getContent().equalsIgnoreCase(".clear"))
        {
        	if (!event.isPrivate())
	            if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_MANAGE))
	                event.getTextChannel().sendMessage("Don't have permissions :,(");
	            else
	            {
	            	Runnable thread = new ClearThread(event);
	            	new Thread(thread).start();
	            }
        	event.getMessage().deleteMessage();
        } else if (messageArr[0].equalsIgnoreCase(".stat") && messageArr[1].contains("@"))
        {
        	event.getTextChannel().sendMessage(
        		new MessageBuilder()
        			.appendString("���������� �� ")
        			.appendMention(event.getMessage().getMentionedUsers().get(0))
        			.appendString(statistic.GetStatistic(event.getMessage().getMentionedUsers().get(0)))
        			.build()
        	);
        	event.getMessage().deleteMessage();
        }
    }
    
    public class ClearThread implements Runnable
    {
    	MessageReceivedEvent event;
    	
    	public ClearThread(MessageReceivedEvent event) {
			this.event = event;
		}
    	
		@Override
		public void run() {
			event.getTextChannel().sendMessage("������� ������...");
			MessageHistory history = new MessageHistory(event.getJDA(), event.getTextChannel());
			List<Message> messages = history.retrieveAll();
			for(Message _message: messages)
				if (_message.getAuthor().getUsername().equalsIgnoreCase("RinBot") ||
					_message.getAuthor().getUsername().equalsIgnoreCase("Butter Bot"))
					_message.deleteMessage();
			event.getAuthor().getPrivateChannel().sendMessage("������ ��������!");
		}
    }
    
    boolean tryParseInt(String value) {  
        try {  
            Integer.parseInt(value);  
            return true;
         } catch (NumberFormatException e) {  
            return false;  
         }  
   }

   boolean tryParseFloat(String value) {  
        try {  
            Float.parseFloat(value);  
            return true;
         } catch (NumberFormatException e) {  
            return false;  
         }  
   }
}