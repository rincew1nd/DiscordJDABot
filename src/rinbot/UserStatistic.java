package rinbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Map;
import javax.swing.Timer;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.User;

public class UserStatistic {

	private StringBuilder sBuilder;
	private List<UserGameStatistic> Statistic;
	private List<String> Games;
	private Map<String, Integer> HelpSearcher;
	private String separ = "\";\"";

	UserStatistic(JDA jda)
	{
		sBuilder = new StringBuilder();
		int sleep = 10;
		
		Statistic = LoadUserStatistic();
		Games = LoadGameNames();
		HelpSearcher = FindUserPos(Statistic);
		
		ActionListener taskPerformer = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(User user : jda.getUsers())
				{
					if (!(user.getCurrentGame() == null ||
						user.getId().equals("141930680484495360") ||
						user.getId().equals("141983932332769280")))
					{
						int pos = (HelpSearcher.get(user.getId()) == null) ? -1 : HelpSearcher.get(user.getId());
						if(pos == -1)
						{
							Map<String, Integer> gamesStat = new HashMap<String, Integer>();
							gamesStat.put(user.getCurrentGame(), sleep);
							UserGameStatistic ugs = new UserGameStatistic(user.getId(), gamesStat);
							Statistic.add(ugs);
							HelpSearcher.put(user.getId(), Statistic.indexOf(ugs));
						} else {
							UserGameStatistic ugs = Statistic.get(pos);
							if (ugs.gamesTime.containsKey(user.getCurrentGame()))
								ugs.gamesTime.put(user.getCurrentGame(), ugs.gamesTime.get(user.getCurrentGame()) + sleep);
							else
								ugs.gamesTime.put(user.getCurrentGame(), sleep);
						}
						
						if(!Games.contains(user.getCurrentGame()))
							Games.add(user.getCurrentGame());
					}
				}
				
				try {
					UpdateFiles(Statistic, Games);
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}     
		};
		new Timer(sleep*1000, taskPerformer).start();
	}
	
	public List<UserGameStatistic> LoadUserStatistic()
	{
		List<String> lines = null;
		try {
			lines = Files.readAllLines(FileSystems.getDefault().getPath("statistic\\statistic.txt"), Charset.defaultCharset());
		} catch (IOException e) {
			System.out.println("Не удалось найти файл - statistic\\statistic.txt");
		}
		
		List<UserGameStatistic> userStatistic = new ArrayList<UserGameStatistic>();
		for (String line : lines)
		{
			String[] lineArr = line.split(separ);
			Map<String, Integer> gamesStat = new HashMap<String, Integer>();
			
			for (int i = 1; i < lineArr.length; i+=2)
				if(lineArr[i+1] != null)
					gamesStat.put(lineArr[i], Integer.parseInt(lineArr[i+1]));
			
			UserGameStatistic ugs = new UserGameStatistic(lineArr[0], gamesStat);
			userStatistic.add(ugs);
		}
			
		return userStatistic;
	}
	
	public List<String> LoadGameNames()
	{
		List<String> lines = null;
		try {
			lines = Files.readAllLines(FileSystems.getDefault().getPath("statistic\\games.txt"), Charset.defaultCharset());
		} catch (IOException e) {
			System.out.println("Не удалось найти файл - statistic\\games.txt");
		}
		
		List<String> Games = new ArrayList<String>();
		for (String line : lines)
			for (String game : line.split(separ))
					Games.add(game);
		
		return Games;
	}
	
	public Map<String, Integer> FindUserPos(List<UserGameStatistic> lugs)
	{
		Map<String, Integer> Dic = new HashMap<String, Integer>();
		
		for (int i = 0; i < lugs.size(); i++)
			Dic.put(lugs.get(i).userId, i);
		
		return Dic;
	}
	
	public void UpdateFiles(List<UserGameStatistic> lugs, List<String> games) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter("statistic\\statistic.txt");
		for(UserGameStatistic ugs : lugs)
		{
			sBuilder.setLength(0);
			sBuilder.append(ugs.userId + separ);
			for (String key : ugs.gamesTime.keySet())
				sBuilder.append(key + separ + ugs.gamesTime.get(key) + separ);
			writer.write(sBuilder.toString().substring(0, sBuilder.toString().length()-3) + "\r\n");
		}
		writer.close();

		writer = new PrintWriter("statistic\\games.txt");
		sBuilder.setLength(0);
		for (String game : games)
			sBuilder.append(game + separ);
		writer.write(sBuilder.toString().substring(0, sBuilder.toString().length()-3));
		writer.close();
	}
	
	public String GetStatistic(User user)
	{
		int pos = (HelpSearcher.get(user.getId()) == null) ? -1 : HelpSearcher.get(user.getId());
		if (pos != -1)
		{
			UserGameStatistic ugs = Statistic.get(pos);
			
			sBuilder.setLength(0);
			sBuilder.append(":\r\n");
			for (String key : ugs.gamesTime.keySet())
				sBuilder.append("Игра - " + key + "; Время(сек) - " + ugs.gamesTime.get(key) + "\n");
			
			return sBuilder.toString();
		} else {
			return " отсутствует";
		}
	}
	
	class UserGameStatistic
	{
		String userId;
		Map<String, Integer> gamesTime;
		
		public UserGameStatistic(String userId, Map<String, Integer> gameTime)
		{
			this.userId = userId;
			this.gamesTime = gameTime;
		}
	}
}
