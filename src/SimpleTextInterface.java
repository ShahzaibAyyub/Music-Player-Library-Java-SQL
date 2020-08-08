
public class SimpleTextInterface {
	
	public static MP3 playerMP3;

	public static void main(String[] args) {

		 MP3 playerMP3 = new MP3("D:\\IntelliJ IDEA\\AP\\MusicApp\\src\\10.mp3");//specify a path for the mp3 file here
		 playerMP3.play();
		
		 //playerMP3.stop();`
		
 	}
}
