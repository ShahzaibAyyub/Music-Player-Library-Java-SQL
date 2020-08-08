import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.Statement;


public class MusicApp {

    /** The name of the MySQL account to use (or empty for anonymous) */
    private final String userName = "root";

    /** The password for the MySQL account (or empty for anonymous) */
    private final String password = "root";

    /** The name of the computer running MySQL */
    private final String serverName = "localhost";

    /** The port of the MySQL server (default is 3306) */
    private final int portNumber = 3306;

    /** The name of the database we are testing with (this default is installed with MySQL) */
    private final String dbName = "MusicApp";

    /** The name of the table we are testing with */
    //private final String tableName = "songs";

    private Statement stmt;

    /**
     * Get a new database connection
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);
        System.out.println("Trying to get connection! ");
        conn = DriverManager.getConnection("jdbc:mysql://"
                + this.serverName + ":" + this.portNumber + "/"
                + this.dbName,connectionProps);
        System.out.println("Connection achieved! ");
        return conn;
    }



    public boolean executeUpdate(Connection conn, String command) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(command); // This will throw a SQLException if it fails
            return true;
        } finally {

            // This will run whether we throw an exception or not
            if (stmt != null) { stmt.close(); }
        }
    }


    public void run() {

        // Connect to MySQL
        Connection conn = null;
        try {
            conn = this.getConnection();
            System.out.println("Connection name is : "+conn.getClass().getName());
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to the database!");
            e.printStackTrace();
            return;
        }


        // UNCOMMENT THIS CODE IF DATABASE DOES NOT EXISTS.
        /*
        // Create a table
        try {
            stmt= conn.createStatement();
            String createString =                                       // Creating Album Table
                    "CREATE TABLE `MusicApp`.`Albums` (" +
                            "`albumName` VARCHAR(45) NOT NULL," +
                            "PRIMARY KEY (`albumName`))";
            this.executeUpdate(conn, createString);

            String input="INSERT INTO `MusicApp`.`Albums` (`albumName`) " +
                    "VALUES ('Default')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Albums` (`albumName`) " +
                    "VALUES ('Thriller')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Albums` (`albumName`) " +
                    "VALUES ('Tom\\'s Favourite Tunes')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Albums` (`albumName`) " +
                    "VALUES ('Classics')";
            this.executeUpdate(conn, input);

            createString =                                               //Create Artist Table
                    "CREATE TABLE `MusicApp`.`Artists` (" +
                            "`artistName` VARCHAR(45) NOT NULL," +
                            "PRIMARY KEY (`artistName`))";
            this.executeUpdate(conn, createString);

           input="INSERT INTO `MusicApp`.`Artists` (`artistName`) " +
                    "VALUES ('Micheal Jackson')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Artists` (`artistName`) " +
                    "VALUES ('Joe Henderson')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Artists` (`artistName`) " +
                    "VALUES ('The Tokens')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Artists` (`artistName`) " +
                    "VALUES ('Tom Hazelton')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Artists` (`artistName`) " +
                    "VALUES ('Celine Dion')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Artists` (`artistName`) " +
                    "VALUES ('Pet Shop Boys')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Artists` (`artistName`) " +
                    "VALUES ('Wild Cherry')";
            this.executeUpdate(conn, input);


            createString =                                                  // Create Song Table
                    "CREATE TABLE `MusicApp`.`Songs` (" +
                    "`Path` VARCHAR(45) NOT NULL," +
                    "`Title` VARCHAR(45) NOT NULL," +
                    "`Track_Number` INT NOT NULL," +
                    "`Album` VARCHAR(45) NOT NULL," +
                    "`Art1` VARCHAR(45) NULL," +
                    "`Art2` VARCHAR(45) NULL," +
                    "`Art3` VARCHAR(45) NULL," +
                    "PRIMARY KEY (`Path`)," +
                    "FOREIGN KEY (`album`) REFERENCES  `MusicApp`.`albums`(albumName) ON UPDATE CASCADE , " +
                    "FOREIGN KEY (`art1`) REFERENCES  `MusicApp`.`artists`(artistName) ON DELETE SET NULL ON UPDATE CASCADE , " +
                    "FOREIGN KEY (`art2`) REFERENCES  `MusicApp`.`artists`(artistName) ON DELETE SET NULL ON UPDATE CASCADE , " +
                    "FOREIGN KEY (`art3`) REFERENCES  `MusicApp`.`artists`(artistName) ON DELETE SET NULL ON UPDATE CASCADE ) ";

            this.executeUpdate(conn, createString);

            input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`, `Art1`) " +
                    "VALUES ('The Girl is Mine', 'D:\\\\IntelliJ IDEA\\\\AP\\\\MusicApp\\\\src\\\\1.mp3', '1', 'Thriller', 'Micheal Jackson')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`, `Art1`) " +
                    "VALUES ('Billie Jean', 'D:\\\\IntelliJ IDEA\\\\AP\\\\MusicApp\\\\src\\\\2.mp3', '2', 'Thriller', 'Micheal Jackson')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`, `Art1`)" +
                    " VALUES ('P.Y.T. (Pretty Young Thing)', 'D:\\\\IntelliJ IDEA\\\\AP\\\\MusicApp\\\\src\\\\3.mp3', '3', 'Thriller', 'Micheal Jackson')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`, `Art1`) " +
                    "VALUES ('Blue Bossa', 'D:\\\\IntelliJ IDEA\\\\AP\\\\MusicApp\\\\src\\\\4.mp3', '1','Tom\\'s Favourite Tunes', 'Joe Henderson')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`, `Art1`, `Art2`) " +
                    "VALUES ('The Lion Sleeps Tonight', 'D:\\\\IntelliJ IDEA\\\\AP\\\\MusicApp\\\\src\\\\5.mp3', '2', 'Tom\\'s Favourite Tunes', 'The Tokens','Tom Hazelton')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`, `Art1`) " +
                    "VALUES ('I Surrender', 'D:\\\\IntelliJ IDEA\\\\AP\\\\MusicApp\\\\src\\\\6.mp3', '2', 'Classics', 'Celine Dion')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`, `Art1`) " +
                    "VALUES ('Play that Funky Music', 'D:\\\\IntelliJ IDEA\\\\AP\\\\MusicApp\\\\src\\\\7.mp3', '1', 'Default', 'Wild Cherry')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`, `Art1`, `Art2`) " +
                    "VALUES ('Go West', 'D:\\\\IntelliJ IDEA\\\\AP\\\\MusicApp\\\\src\\\\8.mp3', '7', 'Default', 'Pet Shop Boys', 'Tom Hazelton')";
            this.executeUpdate(conn, input);
            input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`)" +
                    " VALUES ('Mystery Track of Mystery', 'D:\\\\IntelliJ IDEA\\\\AP\\\\MusicApp\\\\src\\\\9.mp3', '10', 'Default')";
            this.executeUpdate(conn, input);

        } catch (Exception e) {
            System.out.println("ERROR: Could not create the table");
            e.printStackTrace();
            return;
        }
        */



        int input=1;
        Scanner in = new Scanner(System.in);
        while (input!=0)
        {
            System.out.println("////////////////////////////////////////////////////////////////////////////////");
            System.out.println("                               MUSIC APP MENU");
            System.out.println("////////////////////////////////////////////////////////////////////////////////");
            System.out.println("1. VIEW DATABASE (R1)");
            System.out.println("2. VIEW ALL ARTISTS (R1.1)");
            System.out.println("3. VIEW ALL ALBUMS (R1.2)");
            System.out.println("4. EDIT SONGS, ARTISTS OR ALBUMS (R3)");
            System.out.println("5. VIEW SONGS OF PARTICULAR ARTIST (R4)");
            System.out.println("6. VIEW SONGS OF PARTICULAR ALBUM (R5)");
            System.out.println("7. ADD OR REMOVE SONGS FROM LIBRARY (R6)");
            System.out.println("8. ADD OR REMOVE ARTISTS FROM SONGS (R7)");
            System.out.println("9. ADD OR REMOVE SONGS FROM ALBUMS (R8)");
            System.out.println("10. Play Song");
            System.out.println("0. EXIT MUSIC APP");
            System.out.println("ENTER OPTION NUMBER--->");
            input=in.nextInt();

            if (input == 0)
            {
                System.out.print("////////////////   MUSIC APP CLOSED !!!   ///////////////////");
                return;
            }
            else if (input == 1)
            {
                this.ViewDatabase(conn);
            }
            else if (input == 2)
            {
                this.ViewArtists(conn);
            }
            else if (input == 3)
            {
                this.ViewAlbums(conn);
            }
            else if (input == 4)
            {
                System.out.println("////////////////   EDIT MENU   ///////////////////");
                System.out.println("1. Edit Album Name");
                System.out.println("2. Edit Artist Name");
                System.out.println("3. Edit Song Name");
                int input1;
                Scanner in1 = new Scanner(System.in);
                input1=in.nextInt();

                if (input1==1 ) this.editAlbum(conn);
                else if (input1==2) this.editArtist(conn);
                else if (input1==3) this.editSong(conn);
                else System.out.print("Enter Correct Option Number!");
            }
            else if (input == 5)
            {
                this.searchArtist(conn);

            }
            else if (input == 6)
            {
                this.searchAlbum(conn);
            }
            else if (input == 7)
            {
                System.out.println("////////////////   ADD/REMOVE SONGS FROM LIBRARY MENU   ///////////////////");
                System.out.println("1. ADD SONG TO LIBRARY");
                System.out.println("2. REMOVE SONG FROM LIBRARY");
                System.out.println("Choose option ---> ");
                int input1;
                Scanner in1 = new Scanner(System.in);
                input1=in.nextInt();

                if (input1==1 ) this.addSongLibrary(conn);
                else if (input1==2) this.removeSongLibrary(conn);
                else System.out.print("Enter Correct Option Number!");

            }
            else if (input == 8)
            {
                System.out.println("////////////////   ADD/REMOVE ARTISTS FROM SONGS MENU   ///////////////////");
                System.out.println("1. ADD ARTIST TO SONG");
                System.out.println("2. REMOVE ARTIST FROM SONG");
                System.out.println("Choose option ---> ");
                int input1;
                Scanner in1 = new Scanner(System.in);
                input1=in.nextInt();

                if (input1==1 ) this.addArtistSong(conn);
                else if (input1==2) this.removeArtistSong(conn);
                else System.out.print("Enter Correct Option Number!");
            }
            else if (input == 9)
            {
                System.out.println("////////////////   ADD/REMOVE SONGS FROM ALBUM MENU   ///////////////////");
                System.out.println("1. ADD SONG TO ALBUM");
                System.out.println("2. REMOVE SONG FROM ALBUM");
                System.out.println("Choose option ---> ");
                int input1;
                Scanner in1 = new Scanner(System.in);
                input1=in.nextInt();

                if (input1==1 ) this.addSongAlbum(conn);
                else if (input1==2) this.removeSongAlbum(conn);
                else System.out.print("Enter Correct Option Number!");
            }
            else if (input == 10)
            {
                this.playMP3(conn);
            }
        }
    }


    public void ViewDatabase(Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM songs";
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("");
            System.out.println("////////////////////////  MusicApp Database  ///////////////////////");
            while (rs.next()) {
                String path = rs.getString("Path");
                String title = rs.getString("Title");
                Integer track_number = rs.getInt("Track_Number");
                String album = rs.getString("Album");
                String art1 = rs.getString("Art1");
                String art2 = rs.getString("Art2");
                String art3 = rs.getString("Art3");

                System.out.print("Title: " + title);
                System.out.print(",  Track Number: " + track_number);
                System.out.println(",  Path: " + path);
                System.out.print("Album: " + album);
                System.out.print(",  Artist 1: " + art1);
                System.out.print(",  Artist 2: " + art2);
                System.out.println(",  Artist 3: " + art3);
                System.out.println("");


            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void ViewAlbums(Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT Album\n" +
                    "FROM `MusicApp`.`songs`;";
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("");
            System.out.println("////////////////  Total Albums (Alphabetical Order)  ///////////////////");
            List<String> alphaAlbum = new ArrayList<String>();
            while (rs.next()) {
                String album = rs.getString("Album");
                alphaAlbum.add(album);
            }
            Collections.sort(alphaAlbum);
            Set<String> set = new HashSet<>(alphaAlbum);
            alphaAlbum.clear();
            alphaAlbum.addAll(set);
            for(String s : alphaAlbum){ System.out.println("Album Name : " + s ); }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void ViewArtists(Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT Art1\n" +
                    "FROM `MusicApp`.`songs`;";
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("");
            System.out.println("////////////////  Total Artists (Alphabetical Order)  ///////////////////");
            List<String> alphaArtist = new ArrayList<String>();
            while (rs.next()) {
                if (rs.getString("Art1") != null) {
                    String art1 = rs.getString("Art1");
                    alphaArtist.add(art1);
                }
            }
            sql = "SELECT Art2\n" +
                    "FROM `MusicApp`.`songs`;";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("Art2") != null) {
                    String art2 = rs.getString("Art2");
                    alphaArtist.add(art2);
                }
            }
            sql = "SELECT Art3\n" +
                    "FROM `MusicApp`.`songs`;";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("Art3") != null) {
                    String art3 = rs.getString("Art3");
                    alphaArtist.add(art3);
                }
            }
            Collections.sort(alphaArtist);
            Set<String> set = new HashSet<>(alphaArtist);
            alphaArtist.clear();
            alphaArtist.addAll(set);
            for(String s : alphaArtist){ System.out.println("Artist Name : " + s ); }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void searchAlbum(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter Album Name --->");
            boolean bool = false;
            String input;
            Scanner in = new Scanner(System.in);
            input = in.nextLine();
            stmt = conn.createStatement();
            String sql = "SELECT Path, Title, Track_Number\n" +
                    "FROM `MusicApp`.`songs` WHERE Album = '" + input + "';";
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println(input);
            System.out.println(rs);
            System.out.println(input + " (ALBUM) has the following songs:");
            while (rs.next()) {
                String path = rs.getString("Path");
                String title = rs.getString("Title");
                Integer track_number = rs.getInt("Track_Number");

                System.out.print("Title: " + title);
                System.out.print(", Track Number: " + track_number);
                System.out.println(", Path: " + path);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void searchArtist(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter Artist Name --->");
            boolean bool = false;
            String input;
            Scanner in = new Scanner(System.in);
            input = in.nextLine();
            stmt = conn.createStatement();
            String sql = "SELECT Path, Title, Track_Number\n" +
                    "FROM `MusicApp`.`songs` WHERE Art1 = '" + input + "' OR Art2 = '" + input + "' OR Art3 = '" + input + "' ;";
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println(input + " (ARTIST) has the following songs:");
            while (rs.next()) {
                String path = rs.getString("Path");
                String title = rs.getString("Title");
                Integer track_number = rs.getInt("Track_Number");

                System.out.print("Title: " + title);
                System.out.print(", Track Number: " + track_number);
                System.out.println(", Path: " + path);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void editAlbum(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter Album name to be edited please:");
            String old,update;
            Scanner in = new Scanner(System.in);
            old = in.nextLine();
            System.out.println("Enter NEW album name please:");
            update = in.nextLine();
            stmt = conn.createStatement();
            String sql = "UPDATE `MusicApp`.`albums` SET `albumName` = '" + update + "' WHERE (`albumName` = '" + old + "');";
            stmt.executeUpdate(sql);
        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void editArtist(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter Artist name to be edited please:");
            String old,update;
            Scanner in = new Scanner(System.in);
            old = in.nextLine();
            System.out.println("Enter NEW artist name please:");
            update = in.nextLine();
            stmt = conn.createStatement();
            String sql = "UPDATE `MusicApp`.`artists` SET `artistName` = '" + update + "' WHERE (`artistName` = '" + old + "');";
            stmt.executeUpdate(sql);
        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void editSong(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter song name to be edited please:");
            String old,update;
            Scanner in = new Scanner(System.in);
            old = in.nextLine();
            System.out.println("Enter NEW song name please:");
            update = in.nextLine();
            stmt = conn.createStatement();
            String sql = "UPDATE `MusicApp`.`songs` SET `title` = '" + update + "' WHERE (`title` = '" + old + "');";
            stmt.executeUpdate(sql);
        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addArtistSong(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter song name to be EDITED please:");
            String old,update;
            Scanner in = new Scanner(System.in);
            old = in.nextLine();
            System.out.println("Enter artist name to be ADDED please:");
            update = in.nextLine();
            stmt = conn.createStatement();
            String sql = "SELECT Art1, Art2, Art3\n" +
                    "FROM `MusicApp`.`songs` WHERE title = '" + old + "';";
            ResultSet rs = stmt.executeQuery(sql);
            String a1=null,a2=null,a3=null;
            while (rs.next()) {

                a1 = rs.getString("Art1");
                a2 = rs.getString("Art2");
                a3 = rs.getString("Art3");
                //System.out.println(a1 + a2 + a3);
            }
            if (a1==null){
                sql = "UPDATE `MusicApp`.`songs` SET `Art1` = '" + update + "' WHERE (`Title` = '" + old + "');";
                stmt.executeUpdate(sql);
            }
            else if  (a2==null){
                sql = "UPDATE `MusicApp`.`songs` SET `Art2` = '" + update + "' WHERE (`Title` = '" + old + "');";
                stmt.executeUpdate(sql);
            }
            else if  (a3==null){
                sql = "UPDATE `MusicApp`.`songs` SET `Art3` = '" + update + "' WHERE (`Title` = '" + old + "');";
                stmt.executeUpdate(sql);
            }
        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void removeArtistSong(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter song name to be EDITED please:");
            String old,update;
            Scanner in = new Scanner(System.in);
            old = in.nextLine();
            System.out.println("Enter artist name to be REMOVED please:");
            update = in.nextLine();
            stmt = conn.createStatement();
            //String sql = "SET FOREIGN_KEY_CHECKS=0;";
            //stmt.execute(sql);
            String sql = "SELECT Art1, Art2, Art3\n" +
                    "FROM `MusicApp`.`songs` WHERE title = '" + old + "';";
            ResultSet rs = stmt.executeQuery(sql);
            String a1=null,a2=null,a3=null;
            while (rs.next()) {

                a1 = rs.getString("Art1");
                a2 = rs.getString("Art2");
                a3 = rs.getString("Art3");
                //System.out.println(a1 + a2 + a3);
            }
            if (a1.equals(update)){
                sql = "UPDATE `MusicApp`.`songs` SET `Art1` = '" + null + "' WHERE (`Title` = '" + old + "');";
                stmt.executeUpdate(sql);
            }
            else if  (a2.equals(update)){
                sql = "UPDATE `MusicApp`.`songs` SET `Art2` = '" + null + "' WHERE (`Title` = '" + old + "');";
                stmt.executeUpdate(sql);
            }
            else if  (a3.equals(update)){
                sql = "UPDATE `MusicApp`.`songs` SET `Art3` = '" + null + "' WHERE (`Title` = '" + old + "');";
                stmt.executeUpdate(sql);
            }
            //sql = "SET FOREIGN_KEY_CHECKS=0;";
            //stmt.execute(sql);
        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addSongAlbum(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter song name to be ADDED please:");
            String old,update;
            Scanner in = new Scanner(System.in);
            old = in.nextLine();
            System.out.println("Enter album name to which the song is to be ADDED please:");
            update = in.nextLine();
            stmt = conn.createStatement();
            String sql = "UPDATE `MusicApp`.`songs` SET `Album` = '"+update+"' WHERE (`Title` = '"+old+"');";
            stmt.executeUpdate(sql);

        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void removeSongAlbum(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter song name to be REMOVED please:");
            String old,update;
            Scanner in = new Scanner(System.in);
            old = in.nextLine();
            System.out.println("Enter album name to which the song is to be REMOVED please:");
            update = in.nextLine();
            stmt = conn.createStatement();
            String sql = "UPDATE `MusicApp`.`songs` SET `Album` = 'Default' WHERE (`Title` = '"+old+"');";
            stmt.executeUpdate(sql);

        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void removeSongLibrary(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter song name to be REMOVED please:");
            String old;
            Scanner in = new Scanner(System.in);
            old = in.nextLine();
            stmt = conn.createStatement();
            String sql = "DELETE FROM `musicapp`.`songs` WHERE (`Title` = '"+old+"');";
            stmt.executeUpdate(sql);

        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addSongLibrary(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter song name to be ADDED please:");
            String name,path,tn,album,artist;
            Scanner in = new Scanner(System.in);
            name = in.nextLine();
            System.out.println("Enter song path to be ADDED please:;");
            path = in.nextLine();
            System.out.println("Enter song track number to be ADDED please:;");
            tn = in.nextLine();
            System.out.println("Enter song album to be ADDED please:;");
            album = in.nextLine();
            System.out.println("Enter song artist to be ADDED please:;");
            artist = in.nextLine();
            String input="INSERT INTO `MusicApp`.`Songs` (`title`, `path`, `track_number`,`Album`,`Art1`)" +
                    " VALUES ('"+name+"', '"+path+"', '"+tn+"', '"+album+"', '"+artist+"')";
            this.executeUpdate(conn, input);

        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void playMP3(Connection conn){
        Statement stmt = null;
        try {
            System.out.println("Enter song name to be played: ");
            String input,path=null;
            Scanner in = new Scanner(System.in);
            input = in.nextLine();
            stmt = conn.createStatement();
            String sql = "SELECT Path, Art1, Art2, Art3\n" +
                    "FROM `MusicApp`.`songs` WHERE title = '" + input + "';";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                path = rs.getString("Path");
            }
            MP3 playerMP3 = new MP3(path);//specify a path for the mp3 file here
            playerMP3.play();
            System.out.println("Currently playing your song!");
            System.out.println("");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MusicApp app = new MusicApp();
        app.run();

    }
}
