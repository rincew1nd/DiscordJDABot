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
			jda = new JDABuilder("email", "password").build();
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
    	
        if(event.getMessage().getContent().equalsIgnoreCase(".cat"))
        {
        	event.getTextChannel().sendFile(new File(System.getProperty("user.dir") + "\\media\\images\\cat" + rnd.nextInt(7) + ".jpg"));
        	event.getMessage().deleteMessage();
        }
        else if (messageArr[0].equalsIgnoreCase(".random"))
        {
        	if(messageArr.length == 2 && tryParseInt(messageArr[1]))
        		event.getTextChannel().sendMessage("��������� ����� - " + rnd.nextInt(Integer.parseInt(messageArr[1])));
        	else
        		event.getTextChannel().sendMessage("������������ ��������� �������.\r\n.random %�����_�����%");
        	event.getMessage().deleteMessage();
        }
        	
        else if (event.getMessage().getContent().equalsIgnoreCase(".clear") && event.getAuthor().getUsername().equalsIgnoreCase("����0"))
        {
        	if (!event.isPrivate())
	            if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_MANAGE))
	                event.getTextChannel().sendMessage("Don't have permissions :,(");
	            else
	            {
	                MessageHistory history = new MessageHistory(event.getJDA(), event.getTextChannel());
	                List<Message> messages = history.retrieveAll();
	                messages.forEach(Message::deleteMessage);
	            }
        	event.getMessage().deleteMessage();
        } else if (messageArr[0].equalsIgnoreCase(".connect"))
        {
        	if (messageArr.length == 2)
        	{
	        	VoiceChannel ch = jda.getVoiceChannelByName(messageArr[1]).get(0);
	        	System.out.println(ch.getName());
	        	event.getJDA().getAudioManager().openAudioConnection(ch);
        		event.getTextChannel().sendMessage("������� ����� � %channel%");
        	} else
        		event.getTextChannel().sendMessage("������������ �������� �������.\r\n.connect %channel%");
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".disconnect"))
		{
        	event.getJDA().getAudioManager().closeAudioConnection();
    		event.getTextChannel().sendMessage("������� ����� � %channel%");
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
        } else if (message.equalsIgnoreCase(".playlist"))
        {
        	aute.Playlist(event.getTextChannel());
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".stop"))
        {
        	aute.Stop(event.getTextChannel());
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".musicList"))
        {
        	aute.GetMusicList(event.getTextChannel());
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".help"))
        {
        	sBuilder.setLength(0);
        	
        	sBuilder.append("������ ������:\r\n");
        	sBuilder.append("\t.help - ������ ������\r\n");
        	sBuilder.append("\t.cat - ������ ���������� ���� � �����\r\n");
        	sBuilder.append("\t.random %�����_�����% - �������� ��������� ����� ����� �� 0 �� %�����_�����%\r\n");
        	sBuilder.append("����:\r\n");
        	sBuilder.append("\t.connect %channel% - �������������� � ������\r\n");
        	sBuilder.append("\t.disconnect - ������� �� ���� ����\r\n");
        	sBuilder.append("\t.musicList - ������ ����������� ������\r\n");
        	sBuilder.append("\t.play;%URL%;%��������% - ��������� ����� � ��������� � � ��������\r\n");
        	sBuilder.append("\t.play;%��������% - ��������� ��������� ����� � ��������\r\n");
        	sBuilder.append("\t.stop - ������������� ������\r\n");
        	sBuilder.append("\t.skip - ���������� �����\r\n");
        	
        	event.getAuthor().getPrivateChannel().sendMessage(sBuilder.toString());
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
    
    boolean tryParseInt(String value) {  
        try {  
            Integer.parseInt(value);  
            return true;  
         } catch (NumberFormatException e) {  
            return false;  
         }  
   }
}