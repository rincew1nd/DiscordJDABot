package commands;

import java.util.List;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import rinbot.UserStatistic;

public class Statistic extends Command{

	UserStatistic statistic;
	
	public Statistic(JDA jda) {
		statistic = new UserStatistic(jda);
	}
	
	 @Override
	public void ParseCommand(MessageReceivedEvent event) {
		 
		 boolean games = Has("games");
		 List<User> mentions = GetMentions();
		 
		 if (games)
			 if (mentions.size() != 0)
				 for (User user : mentions)
					 event.getTextChannel().sendMessage(
						new MessageBuilder()
						.appendString("Статистика по ")
						.appendMention(user)
						.appendString(statistic.GetStatistic(user))
						.build()
				     );
			 else
				 event.getTextChannel().sendMessage(
					new MessageBuilder()
					.appendString("Упомяните (@) хотя бы одного пользователя.")
					.build()
				);
	}
}
