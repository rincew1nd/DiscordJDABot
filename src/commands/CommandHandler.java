package commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler {
	private String _cmd;
	private String _arg;
	private String _param;
	private List<String> _keywords;

	private boolean _isCommand;
	private ArgumentHandler _argumentHandler;
	
	private String _textInput;
	
	public CommandHandler(String command, String argSplitter, String paramSplitter) 
	{
		_cmd = command;
		_arg = argSplitter;
		_param = paramSplitter;
	}
	
	public CommandHandler AddKeywords(ArrayList<String> keywords)
	{
		_keywords = keywords;
		
		return this;
	}

	public CommandHandler ParseString(String input)
	{
		_isCommand = input.startsWith(_cmd);
		_textInput = input.replace(_cmd, "");
		_argumentHandler = new ArgumentHandler();
		
		return this;
	}
	
	public boolean isCommand()
	{
		return _isCommand;
	}

	public ArgumentHandler GetArgHandler()
	{
		return _argumentHandler;
	}
	
	public class ArgumentHandler {
		List<String> args;
		
		public ArgumentHandler() 
		{
			args = new ArrayList<String>();
			if (_keywords != null)
			{
				String argument = "";
				for (String element : _textInput.split(_arg))
				{
					if (!element.equals(""))
					{
						if (element.contains(_param))
						{
							System.out.println(element.substring(0, element.indexOf(_param)+1));
							if (_keywords.contains(element.substring(0, element.indexOf(_param)+1)))
							{
								if (argument.length() != 0)
									args.add(argument);
								argument = element;
							}
						} else if (_keywords.contains(element))
						{
							if (argument.length() != 0)
								args.add(argument);
							args.add(element);
						} else {
							argument += " " + element;
						}
					}
				}
				args.add(argument);
			}
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
			return args.stream()
					.filter(z -> z.equals(toFind))
					.count() != 0;
		}
		
		public String GetArgValue(String mainArg) 
		{
			return args.stream()
					.filter(z -> z.startsWith(mainArg))
					.findAny().orElse("")
					.replace(mainArg, "");
		}
		
		public List<String> GetArgsValues(String mainArg)
		{
			List<String> result = new ArrayList<String>();
			for(String element : args.stream()
									.filter(z -> z.startsWith(mainArg))
									.collect(Collectors.toList()))
				result.add(element.replace(mainArg, ""));
			
			return result;
		}
	}
}
