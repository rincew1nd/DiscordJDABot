package commands;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.Utils;

public class Music extends Command
{
	public Music() { }
	
	@Override
	public void ParseCommand(MessageReceivedEvent event) {
		
		boolean connect = Has("con");
		boolean disconnect = Has("dcon");
		boolean list = Has("list");
		boolean skip = Has("skip");
		boolean stop = Has("stop");
		boolean volume = Has("volume");
		String voiceChannel = GetParam("ch:");
		String volumeValue = GetParam("val:");
		
		if (connect)
        {
			VoiceChannel ch = Utils.GetVoiceChannel(event, voiceChannel);

			if (ch != null)
				if (ch.checkPermission(Utils.GetUrself(event), Permission.VOICE_CONNECT))
					event.getJDA().getAudioManager().openAudioConnection(ch);
				else
					event.getTextChannel().sendMessage(
    					new MessageBuilder()
    					.appendString("�� ������� ������������ � ������ ")
    					.appendString(voiceChannel)
    					.appendString("\n�� ������� ����! :(")
    					.build()
    				);
			else
				event.getTextChannel().sendMessage(
					new MessageBuilder()
	    			.appendString("�� ������� ������������ � ������ ")
	    			.appendString(voiceChannel)
	    			.appendString("\n������ �� ����������!")
	    			.build()
	    		);
        } else if (disconnect)
		{
        	if (event.getJDA().getAudioManager().isConnected())
        	{
        		event.getJDA().getAudioManager().closeAudioConnection();
    			event.getTextChannel().sendMessage(
    				new MessageBuilder()
    				.appendString("������� ����� �� ������")
    				.build()
    			);
        	} else
    			event.getTextChannel().sendMessage(
    				new MessageBuilder()
    				.appendString("�� ���������� �� � ������ ������")
    				.build()
    			);
		} else if (list)
        {
        	_playlistManager.GetMusicList();
        } else if (skip)
        {
			_musicPlayer.Skip();
        } else if (stop)
        {
        	_musicPlayer.Stop();
        } if (volume)
        {
        	if(Utils.tryParseFloat(volumeValue))
        	{
        		event.getTextChannel().sendMessage(
        			new MessageBuilder()
        			.appendString("��������� - ")
        			.appendString(""+Float.parseFloat(volumeValue))
        			.build()
        		);
        		_musicPlayer.GetPlayer().setVolume(Float.parseFloat(volumeValue));
        	}
        	else
        		event.getTextChannel().sendMessage(
            		new MessageBuilder()
            		.appendString("������������ ��������� �������.\r\n.volume %�����_�����%")
            		.build()
            	);
        }
	}
}