package rinbot;
import java.io.File;
import java.util.Random;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class Player {
	public Player(String input, MessageReceivedEvent event) 
	{
		CommandHandler commandHandler = new CommandHandler(input, ".play ", " ");
		if (commandHandler.StartsWith()) 
		{
			boolean postMessage = !commandHandler.argumentHandler.Has("nm");
			boolean playAudio = !commandHandler.argumentHandler.Has("na");
			String voiceChannelToGet = commandHandler.argumentHandler.GetArgValue("ch:");
    		File test = new File(MyUtils.GetRootFolder()+"\\media\\cena");
    		int totalFilesInFolder = test.list().length;
    		Random rand = new Random();
    		int cenaToUseID = rand.nextInt(totalFilesInFolder);
			File[] cenaFolder = new File(MyUtils.GetRootFolder()+"\\media\\cena").listFiles();
			File cenaToUse = cenaFolder[cenaToUseID];
			
			if (postMessage) {
				TextChannel sendCenaTo = event.getTextChannel();
				sendCenaTo.sendMessage("AND HIS NAME IS...");
				sendCenaTo.sendFile(cenaToUse);
				sendCenaTo.sendMessage("JOHN CEEENA! :trumpet: :trumpet: :trumpet:");
			}
			
			if (playAudio) 
			{
				VoiceChannel playCenaTo = null;
				if (voiceChannelToGet.equals("")) 
				{
					playCenaTo = event.getGuild().getVoiceStatusOfUser(event.getAuthor()).getChannel();
				}
				else
				{
					event.getGuild()
					.getVoiceChannels().stream()
					.filter(x -> x.getName().equals(voiceChannelToGet))
					.findAny().orElse(null);
				}
				
				//insert playing audio trough player
			}
		}
	}
}
