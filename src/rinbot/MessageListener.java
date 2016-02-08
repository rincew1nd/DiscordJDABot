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
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

import commands.Cena;
import commands.Playlist;
import commands.SystemCmd;
import commands.Test;
import commands.Z0rde;

import java.io.File;
import java.util.ArrayList;

public class MessageListener extends ListenerAdapter
{
	private static JDA jda;
	private static MusicPlayer musicPlayer;
	private static PlaylistManager playlistManager;
	private static UserStatistic statistic;
	private static Test test;
	private static Cena cena;
	private static Z0rde z0rde;
	private static Playlist playlist;
	private static SystemCmd system;
	
	@SuppressWarnings("serial")
	public static void main(String[] args)
    {
		try {
			jda = new JDABuilder(args[0],args[1]).buildAsync();
	        jda.addEventListener(new MessageListener());
	        musicPlayer = MusicPlayer.getInstance();
	        
	        test = (Test) new Test()
	        	.MakeHandler(".test", " ", ":")
	        	.AddKeywords(
	        		new ArrayList<String>() {{
	        			add("one"); add("two:"); add("thr:");
	        		}}
	        	);
	        
	        cena = (Cena) new Cena()
	        		.MakeHandler(".cena", " ", ":")
	    			.AddKeywords(new ArrayList<String>(){{
	    				add("nm"); add("na"); add("ch:");
	    			}});
	        
	        z0rde = (Z0rde) new Z0rde()
	        		.MakeHandler(".z0rde", " ", "");
	        
	        playlist = (Playlist) new Playlist()
	        		.MakeHandler(".pl", " ", ":")
					.AddKeywords(new ArrayList<String>() {{
						add("get"); add("all"); add("curr");
						add("play"); add("add"); add("del"); add("dwl");
						add("ch:"); add("nm:"); add("sg:"); add("url:");
					}});

	        system = (SystemCmd) new SystemCmd()
	        		.MakeHandler(".sys", " ", ":")
					.AddKeywords(new ArrayList<String>() {{
						add("clear"); add("help");
					}});
	        
	        // ВЫПИЛИТЬ
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
    	
    	playlist.Parse(event);
    	test.Parse(event);
    	cena.Parse(event);
    	z0rde.Parse(event);
    	system.Parse(event);
    	
    	String message = event.getMessage().getContent();
    	String[] messageArr = message.split("\\s+");
    	
        if (messageArr[0].equalsIgnoreCase(".con"))
        {
        	if (messageArr.length == 2)
        	{
	        	VoiceChannel ch = jda.getVoiceChannelByName(messageArr[1]).get(0);
	        	System.out.println(ch.getName());
	        	if (!ch.checkPermission(jda.getUsersByName("RinBot").get(0), Permission.VOICE_CONNECT))
	        		event.getTextChannel().sendMessage("Не хватает прав для входа в канал " + ch.getName());
	        	else
	        	{
	        		event.getJDA().getAudioManager().closeAudioConnection();
	        		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        		event.getJDA().getAudioManager().openAudioConnection(ch);
        			event.getTextChannel().sendMessage("Успешно вошел в " + ch.getName());
	        	}
        	} else
        		event.getTextChannel().sendMessage("Неправильные атрибуты команды.\r\n.con %channel%");
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".dcon"))
		{
        	event.getJDA().getAudioManager().closeAudioConnection();
    		event.getTextChannel().sendMessage("Успешно вышел из канала");
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
        	if(messageArr.length == 2 && Utils.tryParseFloat(messageArr[1]))
        	{
        		event.getTextChannel().sendMessage("Громкость - " + Float.parseFloat(messageArr[1]));
        		musicPlayer.GetPlayer().setVolume(Float.parseFloat(messageArr[1]));
        	}
        	else
        		event.getTextChannel().sendMessage("Неправильные аргументы команды.\r\n.volume %целое_число%");
        	event.getMessage().deleteMessage();
        } else if (message.equalsIgnoreCase(".ml"))
        {
        	playlistManager.SetChannel(event.getTextChannel());
	        playlistManager.GetMusicList();
        	event.getMessage().deleteMessage();
        } else if(event.getMessage().getContent().equalsIgnoreCase(".cat"))
        {
        	event.getTextChannel().sendFile(new File(System.getProperty("user.dir") + "\\media\\images\\cat" + Utils.GetRandom(7) + ".jpg"));
        	event.getMessage().deleteMessage();
        }
        else if (messageArr[0].equalsIgnoreCase(".rnd"))
        {
        	if(messageArr.length == 2 && Utils.tryParseInt(messageArr[1]))
        		event.getTextChannel().sendMessage("Случайное число - " + Utils.GetRandom(Integer.parseInt(messageArr[1])));
        	else
        		event.getTextChannel().sendMessage("Неправильные аргументы команды.\r\n.random %целое_число%");
        	event.getMessage().deleteMessage();
        } else if (messageArr[0].equalsIgnoreCase(".stat") && messageArr[1].contains("@"))
        {
        	event.getTextChannel().sendMessage(
        		new MessageBuilder()
        			.appendString("Статистика по ")
        			.appendMention(event.getMessage().getMentionedUsers().get(0))
        			.appendString(statistic.GetStatistic(event.getMessage().getMentionedUsers().get(0)))
        			.build()
        	);
        	event.getMessage().deleteMessage();
        }
    }
    

}