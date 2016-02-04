package commands;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler {
	////Sample:
	//...handle input...
	//CommandHandler musicCommandHandler = new CommandHandler(input, ".music");
	//if (musicCommandHandler.StartsWith())
	//{
	//	if (musicCommandHandler.argumentHandler.Has(play)) 
	//		{
	//			player.Play();
	//		}
	//
	//	String songToUse = musicCommandHandler.argumentHandler.GetArgValue(song:));
	//}
	
	
	public String cmd;
	public String textInput;
	public ArgumentHandler argumentHandler;
	
	CommandHandler(String input, String command, String argSplitter) 
	{
		textInput = input;
		cmd = command;		
		if (StartsWith()) 
		{
			argumentHandler = new ArgumentHandler(
					input.replace(command, ""),
						argSplitter
					);
		}
	}
	
	
	public boolean StartsWith() 
	{
		return textInput.startsWith(cmd);
	}
	
	public boolean Contains() 
	{
		return textInput.contains(cmd);
	}
	
	
	public class ArgumentHandler {
		List<String> args;
		
		public ArgumentHandler(String input, String splitBy) 
		{
			args = new ArrayList<String>(Arrays.asList(input.split(splitBy)));
		}

		public boolean Empty() 
		{
			return (args.size() == 0) ? true : false;
		}

		public int Count() 
		{
			return args.size();
		}
		
		public boolean Has(String toFind) 
		{
			//".music play"
			return args.stream()
					.filter(z -> z.equals(toFind))
					.findAny().orElse(null)!=null;
		}
		
		public String GetArgValue(String mainArg) 
		{
			//song:Darude Sandstorm
			//cmdHandler.argumentHandler.GetArgValue("u:") = Darude Sandstorm
			return args.stream()
					.filter(z -> z.startsWith(mainArg))
					.findAny().orElse("")
					.replace(mainArg, "");
		}
		
		public List<String> GetArgsValues(String mainArg)
		{
			//song:Darude Sandstorm song:Sad Trumpet
			List<String> result = new ArrayList<String>();
			for(String element : args.stream()
									.filter(z -> z.startsWith(mainArg))
									.collect(Collectors.toList()))
				result.add(element.replace(mainArg, ""));
			
			return result;
		}
	}
}
