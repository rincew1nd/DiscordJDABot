package commands;

import java.util.List;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class SystemCmd extends Command
{
	
	public SystemCmd() {}
	
	@Override
	public void ParseCommand(MessageReceivedEvent event) {
		
		boolean clear = Has("clear");
		boolean help = Has("help");
		
		if (clear && !event.isPrivate())
            if (!event.getTextChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_MANAGE))
                event.getTextChannel().sendMessage("Не хватает прав :(");
            else
            {
            	Runnable thread = new ClearThread(event);
            	new Thread(thread).start();
            }
		else if (help)
		{
			MessageBuilder messageBuilder = new MessageBuilder();
			messageBuilder.appendString("Список команд:\r\n");
			messageBuilder.appendString("\t.help - запрос помощи\r\n");
			messageBuilder.appendString("\t.cat - постит рандомного кота в конфу\r\n");
			messageBuilder
					.appendString("\t.rnd %целое_число% - получить рандомное целое число от 0 до %целое_число%\r\n");
			messageBuilder.appendString("Войс:\r\n");
			messageBuilder.appendString("\t.con %channel% - присоединиться к каналу\r\n");
			messageBuilder.appendString("\t.dcon - выходит из войс чата\r\n");
			messageBuilder.appendString("\t.ml - список загруженной музыки\r\n");
			messageBuilder.appendString("\t.pl;current - плейлист\r\n");
			messageBuilder.appendString("\t.play;%URL%;%название% - скачивает песню и добавляет её в плейлист\r\n");
			messageBuilder.appendString(
					"\t.play;!y%youtube-id%;%название% - скачивает песню c youtube и добавляет её в плейлист\r\n");
			messageBuilder.appendString("\t.play;%название% - добавляет скаченную песню в плейлист\r\n");
			messageBuilder.appendString("\t.stop - останавливает музыку\r\n");
			messageBuilder.appendString("\t.skip - пропускает песню\r\n");

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
