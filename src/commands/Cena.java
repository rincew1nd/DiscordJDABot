package commands;

import java.io.File;
import java.util.Random;
import java.util.ArrayList;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.MusicPlayer;
import rinbot.Utils;

public class Cena
{
	public Cena(MessageReceivedEvent event) 
	{
		MusicPlayer _musicPlayer = MusicPlayer.getInstance();
		CommandHandler commandHandler = new CommandHandler(".cena", " ", ":")
			.AddKeywords(new ArrayList<String>(){{
				add("nm"); add("na"); add("ch:");
			}})
			.ParseString(event.getMessage().getContent());
		if (commandHandler.isCommand()) 
		{
			event.getMessage().deleteMessage();
			
			boolean postMessage = !commandHandler.GetArgHandler().Has("nm");
			boolean playAudio = !commandHandler.GetArgHandler().Has("na");
			String voiceChannelToGet = commandHandler.GetArgHandler().GetArgValue("ch:");
    		File test = new File(Utils.GetRootFolder()+"\\media\\cena");
    		File cenaSound = new File(Utils.GetRootFolder()+"\\media\\music\\cena.mp3");
    		int totalFilesInFolder = test.list().length;
    		Random rand = new Random();
    		int cenaToUseID = rand.nextInt(totalFilesInFolder);
			File[] cenaFolder = new File(Utils.GetRootFolder()+"\\media\\cena").listFiles();
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
						_musicPlayer.PlayOneshot(cenaSound, playCenaTo);
					else
						event.getTextChannel().sendMessage("Нельзя закинуть Сину в канал "
								+ voiceChannelToGet + "\nНе хватает прав! :(");
				else
					event.getTextChannel().sendMessage("Нельзя закинуть Сину в канал "
							+ voiceChannelToGet + "\nКанала не существует!");
			}
		}
	}
}
