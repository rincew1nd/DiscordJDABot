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
                event.getTextChannel().sendMessage("�� ������� ���� :(");
            else
            {
            	Runnable thread = new ClearThread(event);
            	new Thread(thread).start();
            }
		else if (help)
		{
			MessageBuilder messageBuilder = new MessageBuilder();
			messageBuilder.appendString("������ ������:\r\n");
			messageBuilder.appendString("\t.help - ������ ������\r\n");
			messageBuilder.appendString("\t.cat - ������ ���������� ���� � �����\r\n");
			messageBuilder
					.appendString("\t.rnd %�����_�����% - �������� ��������� ����� ����� �� 0 �� %�����_�����%\r\n");
			messageBuilder.appendString("����:\r\n");
			messageBuilder.appendString("\t.con %channel% - �������������� � ������\r\n");
			messageBuilder.appendString("\t.dcon - ������� �� ���� ����\r\n");
			messageBuilder.appendString("\t.ml - ������ ����������� ������\r\n");
			messageBuilder.appendString("\t.pl;current - ��������\r\n");
			messageBuilder.appendString("\t.play;%URL%;%��������% - ��������� ����� � ��������� � � ��������\r\n");
			messageBuilder.appendString(
					"\t.play;!y%youtube-id%;%��������% - ��������� ����� c youtube � ��������� � � ��������\r\n");
			messageBuilder.appendString("\t.play;%��������% - ��������� ��������� ����� � ��������\r\n");
			messageBuilder.appendString("\t.stop - ������������� ������\r\n");
			messageBuilder.appendString("\t.skip - ���������� �����\r\n");

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
			event.getTextChannel().sendMessage("������� ������...");
			MessageHistory history = new MessageHistory(event.getJDA(), event.getTextChannel());
			List<Message> messages = history.retrieveAll();
			for(Message _message: messages)
				if (_message.getAuthor().getUsername().equalsIgnoreCase("RinBot") ||
					_message.getAuthor().getUsername().equalsIgnoreCase("Butter Bot"))
					_message.deleteMessage();
			event.getAuthor().getPrivateChannel().sendMessage("������ ��������!");
		}
    }
}
