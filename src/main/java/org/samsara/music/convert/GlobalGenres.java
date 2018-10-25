package org.samsara.music.convert;

import static org.samsara.music.convert.Utils.fs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlobalGenres
{
   static Logger log = LogManager.getLogger(GlobalGenres.class);

   public static final String GLOBAL_GENRES_DIR = "GlobalGenres";
   public static final String GENRE_FILE_NAME = "categories";
   public static final String GENRE_DLIMITER = "cats:";
   
   private static int directoryCounter = 0;

   private File globalGenreDirectory = null;
   private final File topMusicDir;
   private HashMap<String, TreeSet<String>> globalGenreMap = new HashMap<>();

   public GlobalGenres(File topMusicDir)
   {
      this.topMusicDir = topMusicDir;
      globalGenreDirectory = new File(this.topMusicDir, GLOBAL_GENRES_DIR);
      if(!globalGenreDirectory.exists() || !globalGenreDirectory.isDirectory())
      {
         try
         {
            Files.createDirectory(globalGenreDirectory.toPath());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   public void process() throws IOException
   {
      processDirectory(topMusicDir);
   }

   private void processDirectory(File dir) throws IOException
   {
      log.info("proc'ng {} ^th: {}",  ++directoryCounter,  dir);

      for (String child : dir.list())
      {
         File file = new File(dir, child);
         if (file.isDirectory())
         {
            processDirectory(file);
            continue;
         }

         if (child.equals(GENRE_FILE_NAME))
         {
            log.info("processing genres in: " + dir);

            mergeWithGlobalGenres(file);
            continue;
         }
         //log.info("child WHAT FOR?:  = " + child);
      }

      writeGlobalGenres(globalGenreMap);
   }

   /**
    * A category(genre) file contains each song in this directory, followed by
    * the genres the song is in. This merges the information in the genreFile
    * into the global genre files.
    */
   private void mergeWithGlobalGenres(File localGenreFile)
   {
      log.info("local genre File = " + localGenreFile.toString());

      List<String> localSongList = Utils.getFileContents(localGenreFile);

      // key is the genre; value is a treeset of the FQ songs in this genre.
      HashMap<String, TreeSet<String>> localGenreMap = new HashMap<>();

      for (String line : localSongList)
      {
         log.info("  songLine = " + line);
         if (lacksGenres(line))
         {
            log.warn("genres missing: {}, genre file: {}", line, localGenreFile.getAbsolutePath());
            continue;
         }

         String fqSongPath = localGenreFile.getParent() + fs + line.substring(0, line.indexOf(GENRE_DLIMITER));
         log.info("    fqname = " + fqSongPath);
         int a = line.indexOf(GENRE_DLIMITER);
         int b = GENRE_DLIMITER.length()+1;

         String genreString = line.substring(line.indexOf(GENRE_DLIMITER) + GENRE_DLIMITER.length()+1);
         String[] genres = genreString.split(" +");

         for (String genre : genres)
         {
            //HERE
            TreeSet<String> genreSongs = localGenreMap.get(genre);

            if (genreSongs == null)
            {
               genreSongs = new TreeSet<String>();
               localGenreMap.put(genre, genreSongs);
            }

            genreSongs.add(fqSongPath);
         }
      }

      // localGenreMap now contains all the genres (as keys) with their fq songs in a treeset
      
      for(Map.Entry<String, TreeSet<String>> entry : localGenreMap.entrySet())
      {
         TreeSet<String> globalSongsForThisGenre = globalGenreMap.get(entry.getKey());
         if(globalSongsForThisGenre == null)
         {
            globalSongsForThisGenre = new TreeSet<String>(); 
            globalGenreMap.put(entry.getKey(), globalSongsForThisGenre);
         }
         TreeSet<String> localSongsForThisGenre = entry.getValue();
         globalSongsForThisGenre.addAll(localSongsForThisGenre);
      }
   }

   public static boolean lacksGenres(String songLine)
   {
      return songLine.indexOf(GENRE_DLIMITER) == -1;
   }

   private void writeGlobalGenres(HashMap<String, TreeSet<String>> genreMap)
   {
      Utils.deleteFiles(globalGenreDirectory);

      TreeSet<String> songTree;
      for(Map.Entry<String, TreeSet<String>> entry : genreMap.entrySet()) 
      {
         songTree = entry.getValue();
         File f = new File(globalGenreDirectory, entry.getKey());
         try
         { Files.createFile(f.toPath()); }
         catch (IOException e1)
         { e1.printStackTrace(); }
         

         try (PrintWriter pw = new PrintWriter(f))
         {
            songTree.forEach(pw::println);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   public static void main(String[] args) throws IOException
   {
      if (args.length == 0)
      {
         System.out.println("Please specify path to the top directory!");
         System.exit(-1);
      }

      File topDir = new File(args[0]);
      if (!topDir.exists() || !topDir.isDirectory())
      {
         log.info("Top directory doesn't exist, or does but is not a directory: " + topDir.toPath());
         System.exit(-1);
      }

      File globalGenreDir = new File(topDir, GLOBAL_GENRES_DIR);
         log.info("Directory YAYAYA doesn't exist: " + globalGenreDir.toPath());

      log.info("Directory exists? {}, {}", globalGenreDir.exists(), globalGenreDir.toPath()); 
      log.info("Is Directory? {}, {}", globalGenreDir.isDirectory(), globalGenreDir.toPath()); 

      if (!globalGenreDir.exists() || !globalGenreDir.isDirectory())
      {
         log.info("Directory doesn't exist, or does but is not a directory: " + globalGenreDir.toPath());
         System.exit(-1);
      }
      log.info("Top Dir: {}, Global Genre Dir: {}", topDir.getAbsolutePath(), globalGenreDir.getAbsoluteFile());

      // create and fill the GlobalGenres directory
      new GlobalGenres(topDir).process();
   }
}