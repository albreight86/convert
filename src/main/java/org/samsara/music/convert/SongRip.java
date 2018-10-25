package org.samsara.music.convert;

import static org.samsara.music.convert.Utils.fs;

import static org.samsara.music.convert.GlobalGenres.GENRE_DLIMITER;
import static org.samsara.music.convert.GlobalGenres.lacksGenres;
import static org.samsara.music.convert.Utils.logAndThrowRuntime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *  This is used from cd ripper to add this 
 *  song to the local genres (cat) file as the cd is being ripped. 
 */
public class SongRip
{
   static Logger log = LogManager.getLogger(SongRip.class);

   final public String directory;
   final public String name;
   final public List<String> genres;

   public SongRip(String[] args, List<String> genres)
   {
      String songFqName = args[0];
      if (!Utils.isFullyQualified(songFqName))
         Utils.logAndThrowRuntime("Must use fully qualified file name of song");

      if (genres == null || genres.size() == 0)
         Utils.logAndThrowRuntime("At least 1 genre must be specified");

      //not clear why this needs fixing
      songFqName = songFqName.replaceAll("._$", ".m4a");

      directory = songFqName.substring(0, songFqName.lastIndexOf(fs));
      name = songFqName.substring(songFqName.lastIndexOf(fs) + 1);
      this.genres = genres;
      log.info("song: {}, genres: {}", songFqName, this.genres);
      log.info("name: {}, directory: {}", name, directory);
      log.debug("dir = {}, song: {}", directory, name);
   }

   // doesn't sort the file yet (do we want to?)...probably not as it keeps the song order intact.
   static public void addToLocalGenreFile(SongRip song)
   {
      String localGenreFile = createIfNeeded(song);

      String line = formatGenreLine(song.name, song.genres);

      try (Writer writer = new BufferedWriter(
                           new OutputStreamWriter(
                           new FileOutputStream(localGenreFile, true), "UTF-8"))) 
      {
         writer.append(line + "\n");
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static String createIfNeeded(SongRip song)
   {
      File songDir = new File(song.directory);
      if(! songDir.exists())
      {
            try
            { Files.createDirectory(songDir.toPath()); }
            catch (IOException e)
            { e.printStackTrace(); }
      }
      
      String localGenreFile = SongRip.getGenreFilePath(song.directory);
      File f = new File(localGenreFile);
      if(! f.exists())
      {
         try
         { Files.createFile(f.toPath()); }
         catch (IOException e1)
         { e1.printStackTrace(); }
      }
      return localGenreFile;
   }

   static public String getGenreFilePath(String directory)
   {
      return new String(directory + fs + GlobalGenres.GENRE_FILE_NAME);
   }

   static public void emptyFile(File f)
   {
      try (PrintWriter w = new PrintWriter(f)) 
      {
         w.write("");
      }
      catch (Exception ex) {
         Utils.logAndThrowRuntime("Error somehow\n" + ex.getMessage());
      }
   }

   static public String formatGenreLine(String name, List<String> categories)
   {
      return name + " " +GENRE_DLIMITER + " "+ listToString(categories);
   }

//   static public List<String> getGenreFile(String path)
//   {
//      List<String> newList = new ArrayList<String>();
//
//      for(String line : Utils.getFileContents(path))
//      {
//         if (lacksGenres(line))
//            logAndThrowRuntime("no \"" + GENRE_DELIMITER_X + "\" in the line from the category file\n");
//         log.info("name from genre file = {}", getName(line));
//         newList.add(line);
//      }
//      return newList;
//   }

//   static public String getName(String line)
//   {
//      return line.substring(0, line.indexOf(GENRE_DELIMITER_X));
//   }

   /**
    * Add a song and it's categories to a file. two versions of the command
    * line: FQN_song_file cats cat1 cat2 ....catn FQN_song_file FQN_cat_file The
    * FQN_cat_file must contain each category on a single line. - this is used
    * because CD ripper is a pain to change the command line DSP.
    */
   static public void main(String[] args)
   {
      if (args.length == 0) 
      {
         log.error("NO args!");
         throw new IllegalArgumentException();
      }
      log.info("args: " + Arrays.toString(args));

      List<String> songGenres = parseArgs(args);
      log.info("song genres = {}", songGenres);
      SongRip s = new SongRip(args, songGenres);
      addToLocalGenreFile(s);
   }

   static public List<String> parseArgs(String[] args)
   {
      if (args[1] == null)
         logAndThrowRuntime("no genres specified!");

      if (args[1].equalsIgnoreCase(GENRE_DLIMITER))
         return getGenresFromCmdLine(args);
      else 
         return getGenreFromFile(args[1]);
   }

   static public List<String> getGenreFromFile(String filePath)
   {
      if (!new File(filePath).exists())
         Utils.logAndThrowRuntime("genre file does not exist: " + filePath);
      return Utils.getFileContents(filePath);
   }

   static List<String> getGenresFromCmdLine(String[] args)
   {
      List<String> genres = new ArrayList<>();
      for (int i = 2; i < args.length; i++) {
         genres.add(args[i]);
      }
      return genres;
   }

   static public String listToString(List<String> aList)
   {
      StringBuffer sb = new StringBuffer();
      for (String s : aList) {
         sb.append(s + " ");
      }
      return sb.toString();
   }
}
