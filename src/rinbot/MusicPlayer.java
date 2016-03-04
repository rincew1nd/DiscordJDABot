package rinbot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;

public class MusicPlayer {
	
	//Default variables 
	private JDA _jda;
	private PlaylistManager _playlistManager = PlaylistManager.getInstance();
	private TextChannel _textChannel;
	private LinkedList<File> _musicQuery;

	//Typical Player variables
	private Player _typicalPlayer;
	private VoiceChannel _typicalChannel = null;
	private String _typicalPlaylist;
	
	///
	private String status; 
	
	//OneShot Player variables
	private Player _oneShotPlayer;
	private boolean _oneShot = false;
	private VoiceChannel _oneShotChannel = null;
	
	public static class MusicPlayerHolder {
		public static final MusicPlayer HOLDER_INSTANCE = new MusicPlayer();
	}
	public static MusicPlayer getInstance() {
		return MusicPlayerHolder.HOLDER_INSTANCE;
	}
	private MusicPlayer() {}
	
	public MusicPlayer MusicPlayerInit(JDA jda)
	{
		_jda = jda;
		_musicQuery = new LinkedList<File>();
		_typicalPlaylist = "";
		
		ActionListener taskPerformer = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (_typicalPlayer != null)
				{
					if (_oneShotPlayer != null && _oneShotPlayer.isStopped() && !_typicalPlayer.isPlaying())
					{
						_oneShot = false;
						if (_typicalPlayer.isPaused())
							Reconnect(_typicalChannel);
			            jda.getAudioManager().setSendingHandler(_typicalPlayer);
			            _typicalPlayer.play();
					} else {
						if(_typicalPlayer.isStopped() && !_musicQuery.isEmpty())
						{
							_musicQuery.removeFirst();
							if (!_musicQuery.isEmpty())
								StartPlaying();
						} else if (_typicalPlayer.isStopped() && _musicQuery.isEmpty())
						{
							if (jda.getAudioManager().isConnected())
								jda.getAudioManager().closeAudioConnection();
						} else if (_typicalPlayer.isPlaying())
						{
							String audioName = _musicQuery.getFirst().getName();
							
							if (!status.equals(audioName))
							{
								jda.getAccountManager().setGame(audioName.substring(0, audioName.indexOf('.')));
								status = audioName;
							}
						}
					}
				} else if (_oneShotPlayer != null)
				{
					if (_oneShot == true && _oneShotPlayer.isStopped())
					{
						_oneShot = false;
						if (jda.getAudioManager().isConnected())
							jda.getAudioManager().closeAudioConnection();
					}
				} else {
					if (status != "with Butter Bot")
					{
						jda.getAccountManager().setGame("with Butter Bot");
						status = "with Butter Bot";
					}
				}
			}     
		};
		new Timer(1000, taskPerformer).start();
		
		return MusicPlayer.MusicPlayerHolder.HOLDER_INSTANCE;
	}
	
	public MusicPlayer SetChannel(TextChannel channel)
	{
		_textChannel = channel;
		return MusicPlayer.MusicPlayerHolder.HOLDER_INSTANCE;
	}
	
	
	///////////////////////////////
	//       GETTER SETTER       //
	///////////////////////////////
	

	public LinkedList<File> GetMusicQuery() { return _musicQuery; };
	public Player GetPlayer() { return _typicalPlayer; };
	public Player GetPlayerOneShot() { return _oneShotPlayer; };
	public String GetPlaylist() { return _typicalPlaylist; };
	public void SetPlaylist(String playlist) { _typicalPlaylist = playlist; };
	
	
	///////////////////////////////////////
	//  Функции для проигрывания музыки  //
	///////////////////////////////////////
	
	
	public void PlayOneshot(File file, VoiceChannel channel)
	{
		if (channel != null)
		{
			if (_typicalPlayer != null)
			{
				_typicalPlayer.pause();
				
				_oneShotChannel = channel;
				Reconnect(_oneShotChannel);
			} else {
				_jda.getAudioManager().openAudioConnection(channel);
			}
			
			File audioFile = null;
	    	try {
	    		audioFile = file;
				_oneShotPlayer = new FilePlayer(audioFile);
	            _jda.getAudioManager().setSendingHandler(_oneShotPlayer);
	            _oneShotPlayer.play();
			} catch (IOException e) {
	            e.printStackTrace();
	        } catch (UnsupportedAudioFileException e) {
	            e.printStackTrace();
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	    	}
	    	
            _oneShot = true;
		} else {
			_textChannel.sendMessage(
				new MessageBuilder().appendString("Канал не найден.").build()
			);
		}
	}
	
	// Начать воспроизводить музыку
    public void StartPlaying()
    {
    	if (_musicQuery.isEmpty())
    		_musicQuery = _playlistManager.LoadPlaylist(_typicalPlaylist);
    	
    	if (_musicQuery != null)
    	{
	    	File audioFile = null;
	    	try {
	    		audioFile = _musicQuery.getFirst();
	    		_typicalPlayer = new FilePlayer(_musicQuery.getFirst());
	            _jda.getAudioManager().setSendingHandler(_typicalPlayer);
	            _typicalPlayer.play();
			} catch (IOException e) {
	            _textChannel.sendMessage(
					new MessageBuilder()
						.appendString("Не удалось найти файл ")
						.appendString(audioFile.getName())
						.build()
				);
	            e.printStackTrace();
	        } catch (UnsupportedAudioFileException e) {
	        	_textChannel.sendMessage(
					new MessageBuilder().appendString("Не удалось обработать файл. Это не аудио файл или не поддерживаемый формат").build()
				);
	            e.printStackTrace();
	        } catch (IllegalArgumentException e) {
	        	_textChannel.sendMessage(
					new MessageBuilder()
						.appendString("Не удалось обработать файл ")
						.appendString(audioFile.getName())
						.appendString(". Это не аудио файл или не поддерживаемый формат")
						.build()
				);
	            e.printStackTrace();
	    	}
    	} else {
			Disconnect();
			_textChannel.sendMessage(
				new MessageBuilder()
					.appendString("Плейлист ")
					.appendString(_typicalPlaylist)
					.appendString(" не найден")
					.build()
			);
    	}
    }

	public void Play(String playlist, VoiceChannel voiceChannel)
	{
		if ((_oneShotPlayer != null && _oneShotPlayer.isPlaying()) || (_typicalPlayer != null && _typicalPlayer.isPlaying()))
		{
			_textChannel.sendMessage(new MessageBuilder().appendString("Бот уже что-то играет").build());
		} else
		{
			if (_jda.getAudioManager().isConnected())
				Reconnect(voiceChannel);
			else
				_jda.getAudioManager().openAudioConnection(voiceChannel);
			
			_typicalChannel = voiceChannel;
			_typicalPlaylist = playlist;
			StartPlaying();
		}
	}
    
	public void Disconnect()
	{
		try {
			Thread.sleep(1000);
			if (_jda.getAudioManager().isConnected())
				_jda.getAudioManager().closeAudioConnection();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void Connect(VoiceChannel channel)
	{
		try {
			Thread.sleep(1000);
			if (!_jda.getAudioManager().isConnected())
				_jda.getAudioManager().openAudioConnection(channel);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    
	public void Reconnect(VoiceChannel channel)
	{
		_jda.getAudioManager().closeAudioConnection();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		_jda.getAudioManager().openAudioConnection(channel);
	}
	
	// Полностью остановить воспроизведение и очистить плейлист
	public void Stop()
	{
		if (_jda.getAudioManager().isConnected())
			if (_typicalPlayer == null || _typicalPlayer.isStopped())
				_textChannel.sendMessage(
					new MessageBuilder().appendString("Бот ничего не играет!").build()
				);
			else
			{
				_typicalPlayer.stop();
				_musicQuery.clear();
				_typicalPlayer = null;
			}
		else
			_textChannel.sendMessage(
        		new MessageBuilder()
        			.appendString("Бот не присоединен ни к одному каналу!\n")
        			.appendString("Присоединитесь к каналу используя команду !connect %channel%")
        			.build()
        	);
	}

	// Пропустить текущий трек
	public void Skip()
	{
		if (_musicQuery.isEmpty())
			_textChannel.sendMessage(
	        	new MessageBuilder().appendString("Нечего пропускать").build()
	        );
		else
		{
			_typicalPlayer.stop();
        	_musicQuery.removeFirst();
        	if (!_musicQuery.isEmpty())
        		StartPlaying();
		}
	}

	// Перемешать текущий плейлист
	public void Shuffle()
	{
		_musicQuery.removeFirst();
		Collections.shuffle(_musicQuery);
	}
}
