package wscgame;

import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;

public class PlayMusic 
{
	private String filename;
	public PlayMusic(String f)
	{
		filename = f;
	}
	public  AudioClip loadSound() 
	{
		URL url = null;
		try 
		{
			url = new URL("file:" + filename);
		} 
		catch (MalformedURLException e) 
		{
			;
		}
		return JApplet.newAudioClip(url);
	}
	public void play() {
		AudioClip music = loadSound();
		music.play();
	}
}
