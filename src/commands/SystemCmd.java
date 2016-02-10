package commands;

import java.io.File;
import java.util.List;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.Utils;

public class SystemCmd extends Command
{
	
	public SystemCmd() {}
	
	@Override
	public void ParseCommand(MessageReceivedEvent event) {
		
		boolean clear = Has("clear");
		boolean help = Has("help");
		boolean logs = Has("logs");
		
		if (clear && !event.isPrivate())
            if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_MANAGE))
                event.getTextChannel().sendMessage("Не хватает прав :(");
            else
            {
            	Runnable thread = new ClearThread(event);
            	new Thread(thread).start();
            }
		else if (logs)
		{
			event.getAuthor().getPrivateChannel().sendFile(new File(Utils.GetRootFolder() + "\\logs.txt"));
		}
		else if (help)
		{
			MessageBuilder messageBuilder = new MessageBuilder();
			messageBuilder.appendString("Список команд:\r\n")
			.appendString("\t.sys help - запрос помощи\r\n")
			.appendString("Войс:\r\n")
			.appendString("\t.msc con ch:%channel% - присоединиться к каналу\r\n")
			.appendString("\t.msc dcon - выходит из войс чата\r\n")
			.appendString("\t.msc skip - пропустить трек\r\n")
			.appendString("\t.msc stop - остановить воспроизведение\r\n")
			.appendString("\t.msc shuffle - перемешеть плейлист\r\n")
			.appendString("\t.msc volume - установить громкость музыки (0.0-1.0)\r\n")
			.appendString("\t.msc list - список загруженной музыки\r\n")
			.appendString("\t.pl get nm:%name% - получить список треков плейлиста\r\n")
			.appendString("\t.pl get all - получить список всех плейлистов\r\n")
			.appendString("\t.pl get curr - получить текущий плейлист\r\n")
			.appendString("\t.pl new nm:%name% - создать плейлист\r\n")
			.appendString("\t.pl add nm:%name% sg:%name% - добавить трек в плейлист\r\n")
			.appendString("\t.pl del nm:%name% sg:%name%- удалить трек из плейлиста\r\n")
			.appendString("\t.pl dwl nm:%name% url:%name%- скачать трек по URL (только mp3)\r\n")
			.appendString(".cena - CENA\r\n")
			.appendString("\t.cena ch:%name% - CENA COMES TO CHANNEL %NAME%\r\n")
			.appendString("\t.cena nm - CENA SPEACHLESS\r\n")
			.appendString("\t.cena na - CANE VOICELESS :<\r\n")
			.appendString(".stat games %MENTION% - получить стату по игорям юзера\r\n")
			.appendString(".z0rde - рандомный з0рд\r\n");

			event.getAuthor().getPrivateChannel().sendMessage(messageBuilder.build());
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
			event.getTextChannel().sendMessage("Начинаю чистку...");
			MessageHistory history = new MessageHistory(event.getJDA(), event.getTextChannel());
			List<Message> messages = history.retrieveAll();
			for(Message _message: messages)
				if (_message.getAuthor().getUsername().equalsIgnoreCase("RinBot") ||
					_message.getAuthor().getUsername().equalsIgnoreCase("Butter Bot"))
					_message.deleteMessage();
			event.getAuthor().getPrivateChannel().sendMessage("Чистка окончена!");
		}
    }
}
