package rinbot;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import commands.Cena;
import commands.Music;
import commands.Playlist;
import commands.Statistic;
import commands.SystemCmd;
import commands.Test;
import commands.Z0rde;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter
{
	private static JDA jda;
	private static Test test;
	private static Cena cena;
	private static Z0rde z0rde;
	private static Playlist playlist;
	private static SystemCmd system;
	private static Music music;
	private static Statistic statistic;
	
	@SuppressWarnings("serial")
	public static void main(String[] args)
    {
		try {
			jda = new JDABuilder(args[0],args[1]).buildAsync();
	        jda.addEventListener(new MessageListener());
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	    MusicPlayer.getInstance().MusicPlayerInit(jda);
	        
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
				add("play"); add("new"); add("add"); add("del"); add("dwl");
				add("ch:"); add("nm:"); add("sg:"); add("url:");
			}});

	    system = (SystemCmd) new SystemCmd()
	    		.MakeHandler(".sys", " ", ":")
			.AddKeywords(new ArrayList<String>() {{
				add("clear"); add("help"); add("logs");
			}});
	    
	    music = (Music) new Music()
	    	.MakeHandler(".msc", " ", ":")
			.AddKeywords(new ArrayList<String>() {{
				add("con"); add("dcon"); add("ch:");
				add("skip"); add("stop"); add("shuffle");
				add("volume"); add("list");
			}});
	    
	    statistic = (Statistic) new Statistic(jda)
	    		.MakeHandler(".stat", " ", ":")
	    		.AddKeywords(new ArrayList<String>() {{
				add("games");
			}});
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
    	
    	music.Parse(event);
    	playlist.Parse(event);
    	test.Parse(event);
    	cena.Parse(event);
    	z0rde.Parse(event);
    	system.Parse(event);
    	statistic.Parse(event);
    }
    

}