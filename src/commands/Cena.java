package commands;
import java.io.File;
import java.util.Random;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.AudioTest;
import rinbot.MyUtils;

public class Cena implements ICommandHandler {
	public Cena(MessageReceivedEvent event) 
	{
		AudioTest aute = AudioTest.getInstance();
		CommandHandler commandHandler = new CommandHandler(event.getMessage().getContent(), ".cena", " ");
		if (commandHandler.StartsWith()) 
		{
			event.getMessage().deleteMessage();
			
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
			
			if (postMessage)
			{
				TextChannel sendCenaTo = event.getTextChannel();
				sendCenaTo.sendMessage("AND HIS NAME IS...");
				sendCenaTo.sendFile(cenaToUse);
				sendCenaTo.sendMessage("JOHN CEEENA! :trumpet: :trumpet: :trumpet:");
			}
			
			if (playAudio) 
			{
				User bot = event.getJDA().getUserById(
						event.getJDA().getSelfInfo().getId()
					);
				
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
				
				if (playCenaTo != null)
					if (playCenaTo.checkPermission(bot, Permission.VOICE_CONNECT))
						aute.PlayOneshot(cenaSound, playCenaTo);
//					else
//						event.getTextChannel().sendMessage("Нельзя закинуть Сину в канал "
//								+ voiceChannelToGet + "\nНе хватает прав! :(");
//				else
//					event.getTextChannel().sendMessage("Нельзя закинуть Сину в канал "
//							+ voiceChannelToGet + "\nКанала не существует!");
			}
			
		}
	}
}
