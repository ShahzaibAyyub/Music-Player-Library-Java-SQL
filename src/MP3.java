import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javazoom.jl.decoder.Equalizer;
import javazoom.jl.decoder.Header;
import javazoom.jl.player.Player;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

/**
 * A class/framework for MP3 file management (read-only) and playback.
 * 
 * Audio system features: 	    play, pause/resume, stop, seek
 * ID3 information available:   title, artist, album, year, genre
 * Other information available: playback position, song duration
 * 
 * Based on the JLayer MP3 library and JID3 library.
 * 
 * JLayer Player functionality was also modified by Tom Hazelton to support 
 * pause/resume (using a community solution), seek (via migration from AdvancedPlayer),
 * and to expose header information for duration/seek calculations
 * 
 * @author Tom Hazelton
 * @date May 13, 2009
 *
 */
public class MP3 {
	
	// MP3 file
	private String filename;
	private File file;	

	// MP3 ID3 tags and properties thereof
	private String title;
	private String year;
	private String album;
	private String artist;
	private String genre;
	private ID3Tag[] tags;
	
	// The JLayer Player that does all the magic
	private Player player;
	
	// Stores a reference to the file header at first frame
	private Header header;
	
	// Used for pause, stop, seek, and play functionality
	private int seekOffset = 0;
	private int pausePosition = 0;
	private boolean playing = false;
	private boolean paused = false;
	private boolean seekUsed = false;
	
	// Who wants to know when the song is done playing?
	private ArrayList<SongFinishedListener> sfls = new ArrayList<SongFinishedListener>();
	
	// True when song reaches its end during play back.  Reset when song is played
	// again.
	private boolean reachedEnd = false;
	

	/**
	 * Instantiates a new MP3 file
	 * @param path The path to the MP3 file on the disk
	 */
	public MP3(String path) {
		this.filename = path;
		file = new File(path);
		
		// leech ID3 info out of the MP3
		initializeID3();
		
		// decode a single frame of the MP3 to get valuable header information
		initHeader();
	}
	
	
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//////// Operations of the MP3 player ////////
	
	//////// Play, stop, pause, resume, seek

	
	/**
	 * Starts playback of the MP3 file.
	 * If the MP3 file is currently paused, playback will resume according to the specifications of the resume() method.
	 * If the MP3 file is not paused and the seek method has been called more recently than the stop method, playback will commence
	 * from the position indicated in the most recent call to seek. If not, playback will commence from the beginning of the song (second 0).
	 * If the MP3 file is already playing, it will be restarted from the beginning.
	 * When the end of the MP3 is reached, the song is stopped (stop() is called).
	 */
	public void play() {
		// are we paused?  just call resume
		if (paused) {
			resume();
			return;
		}
		// we're not paused; are we already playing?
		else if (playing && player != null) { 
			player.close();
		}	
		// play from the current seek offset
		if (seekUsed) {
			play(seekOffset);
		}
		else {
			reachedEnd = false;
			play(0);
		}

	}
	
	
	/**
	 * Stops playback of the MP3 and resets the current playback position to the beginning of the song (second 0).  Also untoggles
	 * pause if it is active.
	 */
	public void stop() { 
		// stops the player if it's playing
		if (player != null) 
			player.close();  
		
		// reset pause/seek information
		playing = false;
		paused = false;
		seekUsed = false;
		seekOffset = 0;
	}

	/**
	 * Pauses playback of the MP3 if it is currently playing (and does nothing otherwise).
	 */
	public void pause() {
		// are we playing? if not do nothing
		if (!playing) 
			return;
		
		// ok, we're playing -- pause where we are
		pausePosition = getPlaybackPositionSeconds();
		stop();
		playing = true;
		paused = true;
		seekOffset = pausePosition;
	}
	
	/**
	 * Resumes playback of the MP3 if it is currently paused (and does nothing otherwise).
	 * If the seek method was called while playback was paused, the MP3 will resume at the time (in seconds) specified in 
	 * the last call to seek.  Otherwise the MP3 will resume from the location at which it was paused.
	 */
	public void resume() {
		if (!playing || !paused) return;
	
		// we used seek, need to restart song
		if (seekUsed) {
			if (player != null) 
				player.close();  
		
			seekUsed = false;
			play(seekOffset);			
		}
		
		// no seek was used
		else {
			play(pausePosition);
			seekOffset = pausePosition;
		}
		
		paused = false;
			
	}

	/**
	 * Sets the playback position.  
	 * If the MP3 is currently playing, playback will continue from the specified position.
	 * If the MP3 is paused or stopped, the next call to play will begin at the specified position (unless stop, which
	 * resets the playback position to 0, is called before play). 
	 * @param position Desired playback position (in seconds)
	 */
	public void seek(int position) {
		// set seek offset to match desired position
		seekOffset = position;
		
		// are we playing?  jump to desired location and start again
		if (playing && !paused) {
			if (player != null) 
				player.close(); 
			
			play(position);
		}		
		
		// we're not playing -- set flag to jump next time we play/unpause
		else {
			seekUsed = true;
		}
	}
	
	
	/**
	 * Sets volume.
	 * @param vol  Specifies the volume.  Must be between 0 (mute) and 10 (max volume)
	 */
	public void setVolume(int vol) {
		if (vol < 0 || vol > 10)
			return;
		
		Equalizer eq = new Equalizer();
		for (int i = 0; i < eq.getBandCount(); i++)
			eq.setBand(i, (float)((float)vol / 5.0 - 1.0)); // normalize to -1.0..1.0
		
		player.setEqualizer(eq);
	}

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//////// Status/information about the MP3 (player) ////////
	
	//////// Playback position, song duration, paused or not, encoding information
	//////// ID3 info: Artist, Title, Album, Year, Genre

	
	
	/**
	 * @return Current playback position (in seconds) of the MP3.
	 */
	public int getPlaybackPositionSeconds() {
		return !playing ? seekOffset : player.getPosition() / 1000 + seekOffset;
	}
	
	/**
	 * @return Duration of MP3 file (in seconds).
	 */
	public int getDurationSeconds() {
		return (int)Math.ceil(header.total_ms((int)(file.length())) / 1000); 
	}
		
	/**
	 * @return true if playback of the the MP3 has been paused, false otherwise 
	 */
	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * @return true if the MP3 is playing, false otherwise 
	 */
	public boolean isPlaying() {
		return playing;
	}
	
	/**
	 * @return true if we reached the end of MP3 during playback and has not
	 * yet been started again.
	 */
	public boolean hasReachedEnd() {
		return reachedEnd;
	}
	
	/**
	 * @return A string giving encoding, licensing information, and bitrate information about the MP3 file
	 */
	public String getEncodingInfo() { 
		return header.toString().trim();
	}
		
		
	/**
	 * @return Artist of MP3 from the ID3 tag
	 */
	public String getArtist() {
		return artist;
	}
		
	/**
	 * @return Title of MP3 from the ID3 tag
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @return Album of MP3 from the ID3 tag
	 */
	public String getAlbum() {
		return album;
	}
	
	/**
	 * @return Year of MP3 from the ID3 tag
	 */
	public String getYear() {
		return year;
	}
	
	/** 
	 * @return Genre of MP3 from the ID3 tag
	 */
	public String getGenre() {
		return genre;
	}

	public String getFilename() {
		return filename;
	}
	/**
	 * @return A String containing artist, title, album, year and genre information from the ID3 tag.
	 */
	public String getID3Info() {
		return "Artist: " + getArtist() + "\n" +
		       "Title:  " + getTitle() + "\n" +
		       "Album:  " + getAlbum() + "\n" +
		       "Year:   " + getYear() + "\n" +
		       "Genre:  " + getGenre();
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//////// Listener methods ////////

	/**
	 * Adds sfl as a listener to SongFinished events.  sfl will be informed
	 * via songDone() when the currently playing song reaches its end.
	 */
	public void addSongFinishedListener(SongFinishedListener sfl) {
		sfls.add(sfl);
	}
	
	
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//////// Private helper methods ////////

	/**
	 * Returns # of frames per second; used to determine how many frames to skip in a seek operation
	 */
	private int getFramesPerSecond() {
		return (int) (1000 / header.ms_per_frame());
	}
	
	
	/**
	 * Examines the ID3 tag(s) of the MP3 file and leeches desired properties.
	 */
	private void initializeID3() {
		MediaFile mf = new MP3File(file);
		try {
			tags = mf.getTags();
			for (int i = 0; i < tags.length; ++i) {
				if (tags[i] instanceof ID3V1_0Tag) {
					ID3V1_0Tag t = (ID3V1_0Tag)tags[i];
					album = album == null ? t.getAlbum() : album;
					title = title == null ? t.getTitle() : title;
					artist = artist == null ? t.getArtist() : artist;
					year = year == null ? t.getYear() : year;
					genre = genre == null ? t.getGenre().toString() : genre;
				}
				else if (tags[i] instanceof ID3V2_3_0Tag) {
					ID3V2_3_0Tag t = (ID3V2_3_0Tag)tags[i];
					album = album == null ? t.getAlbum() : album;
					title = title == null ? t.getTitle() : title;
					artist = artist == null ? t.getArtist() : artist;
					year = year == null ? String.valueOf(t.getYear()) : year;
					genre = genre == null ? t.getGenre() : genre;
				}
			}
		} catch (ID3Exception e) {
//			e.printStackTrace();
		}
		
	}

	/**
	 * Reads a single frame of the MP3 file in order to get persistent header information for use by other methods.
	 */
	private void initHeader() {
		try {
			FileInputStream fis     = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			player = new Player(bis);
		}
		catch (Exception e) {
			System.out.println("Problem playing file " + filename);
			System.out.println(e);
		}
		
		// reads a single frame to get header information
		player.init();		
		header = player.getHeader();
	}

	/**
	 * Called by the playback thread when the song reaches its end.  
	 * Stop the song and notify SongFinishedListeners of this event
	 */
	private void endOfSongReached() {
		stop();
		reachedEnd = true;
		for (SongFinishedListener sfl : sfls) {
			sfl.songDone();
		}
	}
	
	/**
	 * Starts playback of the MP3 file from the specified time index (in seconds).
	 * @param seconds
	 */
	private void play(int seconds) {
		final int pos = seconds;
		try {
			FileInputStream fis     = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			player = new Player(bis);
		}
		catch (Exception e) {
			System.out.println("Problem playing file " + filename);
			System.out.println(e);
		}
			
		new Thread() {
			public void run() {
				try {
					player.play(pos * getFramesPerSecond(), Integer.MAX_VALUE);
					if (player.isComplete())
						endOfSongReached();
				}
				catch (Exception e) { System.out.println(e); }
			}
		}.start();
		
		playing = true;
	}

}

