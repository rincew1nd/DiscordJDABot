package rinbot;
import java.io.File;
import java.util.Random;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class Cena {
	public Cena(String input, MessageReceivedEvent event) 
	{
		AudioTest aute = AudioTest.getInstance();
		CommandHandler commandHandler = new CommandHandler(input, ".cena", " ");
		if (commandHandler.StartsWith()) 
		{
			System.out.println("test");
			
			boolean postMessage = !commandHandler.argumentHandler.Has("nm");
			boolean playAudio = !commandHandler.argumentHandler.Has("na");
			String voiceChannelToGet = commandHandler.argumentHandler.GetArgValue("ch:");
    		File test = new File(MyUtils.GetRootFolder()+"\\media\\cena");
    		File cenaSound = new File(MyUtils.GetRootFolder()+"\\media\\music\\cena.mp3");
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
					playCenaTo = event.getGuild()
					.getVoiceChannels().stream()
					.filter(x -> x.getName().equals(voiceChannelToGet))
					.findAny().orElse(null);
				}
				
				aute.PlayOneshot(cenaSound, playCenaTo);
			}
		}
	}
}
