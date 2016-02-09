package commands;

import java.io.File;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.Utils;

public class Cena extends Command
{
	public Cena() {}
	
	@Override
	public void ParseCommand(MessageReceivedEvent event) 
	{
		boolean postMessage = !Has("nm");
		boolean playAudio = !Has("na");
		String voiceChannelToGet = GetParam("ch:");
		
		File test = new File(Utils.GetRootFolder() + "\\media\\cena");
		File cenaSound = new File(Utils.GetRootFolder() + "\\media\\music\\cena.mp3");
		int totalFilesInFolder = test.list().length;
		int cenaToUseID = Utils.GetRandom(totalFilesInFolder);
		File[] cenaFolder = new File(Utils.GetRootFolder() + "\\media\\cena").listFiles();
		File cenaToUse = cenaFolder[cenaToUseID];

		if (postMessage) {
			TextChannel sendCenaTo = event.getTextChannel();
			sendCenaTo.sendMessage("AND HIS NAME IS...");
			sendCenaTo.sendFile(cenaToUse);
			sendCenaTo.sendMessage("JOHN CEEENA! :trumpet: :trumpet: :trumpet:");
		}

		if (playAudio) {
			User bot = event.getJDA().getUserById(event.getJDA().getSelfInfo().getId());

			VoiceChannel playCenaTo = Utils.GetVoiceChannel(event, voiceChannelToGet);

			if (playCenaTo != null)
				if (playCenaTo.checkPermission(bot, Permission.VOICE_CONNECT))
					_musicPlayer.PlayOneshot(cenaSound, playCenaTo);
//				else
//					event.getTextChannel()
//							.sendMessage("Нельзя закинуть Сину в канал " + voiceChannelToGet + "\nНе хватает прав! :(");
//			else
//				event.getTextChannel()
//						.sendMessage("Нельзя закинуть Сину в канал " + voiceChannelToGet + "\nКанала не существует!");
		}
	}
}
