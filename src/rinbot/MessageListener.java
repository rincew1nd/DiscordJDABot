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

import java.io.File;
import java.util.List;
import java.util.Random;

public class MessageListener extends ListenerAdapter
{
	public static JDA jda;
	public static AudioTest aute;
	public static StringBuilder sBuilder;
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args)
    {
		try {
			jda = new JDABuilder(args[0], args[1]).build();
	        jda.addEventListener(new MessageListener());
	        aute = new AudioTest(jda);
	        sBuilder = new StringBuilder();
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
    	if (event.getAuthor().getUsername().equalsIgnoreCase("RinBot")) return;
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
		} else if (message.split(";")[0].equalsIgnoreCase(".play"))
        {
        	Runnable thread = new PlayThread(event);
        	new Thread(thread).start();
        	event.getMessage().deleteMessage();
        	jda.getUsersByName("RinBot");
        } else if (message.equalsIgnoreCase(".skip"))
        {
        	aute.Skip(event.getTextChannel());
        	event.getMessage().deleteMessage();
        } else if (event.getMessage().getContent().split(";")[0].equalsIgnoreCase(".pl"))
        {
        	messageArr = event.getMessage().getContent().split(";");
        	if (messageArr[1].equalsIgnoreCase("current"))
        		aute.Playlist(event.getTextChannel());
        	//else if (messageArr[1].equalsIgnoreCase("available"))
        	//	aute.AvailablePlaylist(event.getTextChannel());
        	//else if (messageArr[1].equalsIgnoreCase("load") && messageArr.length == 3)
        	//	aute.LoadPlayList(messageArr, event.getTextChannel());
        	//else if (messageArr[1].equalsIgnoreCase("save") && messageArr.length == 3)
        	//	aute.SavePlayList(messageArr, event.getTextChannel());
        	//else if (messageArr[1].equalsIgnoreCase("look") && messageArr.length == 3)
        	//	aute.LookPlayList(messageArr, event.getTextChannel());
        	//else if (messageArr[1].equalsIgnoreCase("add") && messageArr.length == 4)
        	//	aute.LookPlayList(messageArr, event.getTextChannel());
        	//else if (messageArr[1].equalsIgnoreCase("delete") && messageArr.length == 5)
        	//	aute.LookPlayList(messageArr, event.getTextChannel());
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".stop"))
        {
        	aute.Stop(event.getTextChannel());
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".ml"))
        {
        	aute.GetMusicList(event.getTextChannel());
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".help"))
        {
        	sBuilder.setLength(0);
        	
        	sBuilder.append("������ ������:\r\n");
        	sBuilder.append("\t.help - ������ ������\r\n");
        	sBuilder.append("\t.cat - ������ ���������� ���� � �����\r\n");
        	sBuilder.append("\t.rnd %�����_�����% - �������� ��������� ����� ����� �� 0 �� %�����_�����%\r\n");
        	sBuilder.append("����:\r\n");
        	sBuilder.append("\t.con %channel% - �������������� � ������\r\n");
        	sBuilder.append("\t.dcon - ������� �� ���� ����\r\n");
        	sBuilder.append("\t.ml - ������ ����������� ������\r\n");
        	sBuilder.append("\t.pl;current - ��������\r\n");
        	sBuilder.append("\t.play;%URL%;%��������% - ��������� ����� � ��������� � � ��������\r\n");
        	sBuilder.append("\t.play;!y%youtube-id%;%��������% - ��������� ����� c youtube � ��������� � � ��������\r\n");
        	sBuilder.append("\t.play;%��������% - ��������� ��������� ����� � ��������\r\n");
        	sBuilder.append("\t.stop - ������������� ������\r\n");
        	sBuilder.append("\t.skip - ���������� �����\r\n");
        	
        	event.getAuthor().getPrivateChannel().sendMessage(sBuilder.toString());
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
        }
        	
        else if (event.getMessage().getContent().equalsIgnoreCase(".clear"))
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
        }
    }
    
    public class PlayThread implements Runnable
    {
    	MessageReceivedEvent event;
    	
    	public PlayThread(MessageReceivedEvent event) {
			this.event = event;
		}
    	
		@Override
		public void run() {
			String[] strarr = event.getMessage().getContent().split(";");
        	
            aute.Play(strarr, event.getTextChannel());
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
}