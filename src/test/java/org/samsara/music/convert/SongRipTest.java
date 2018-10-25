package org.samsara.music.convert;

import static org.samsara.music.convert.GlobalGenres.GENRE_DLIMITER;
import static org.samsara.music.convert.GlobalGenres.GENRE_FILE_NAME;
import static org.samsara.music.convert.Utils.fs;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SongRipTest
{
   @BeforeMethod
   public void setup()
   {
      File f = new File(tempDir() + fs + GENRE_FILE_NAME);
      if(f.exists())
         SongRip.emptyFile(f);
   }

//   @Test
//   public void getNameTest()
//   {
//      String name = SongRip.getName("fqName" + MasterGenres.GENRE_DELIMITER_X + " iggle figgle fi");
//      Assert.assertEquals(name, "fqName");
//   }

   @Test
   public void showCp()
   {
      String classpathStr = System.getProperty("java.class.path");
      System.out.print(classpathStr);
   }

   @Test
   public void fileNotFQ()
   {
      try
      {
         String args[] = new String [] {"notFullyQualified"};
         new SongRip(args, new ArrayList<String>());
      }
      catch (RuntimeException ex)
      {
         assertEquals("Must use fully qualified file name of song", ex.getMessage());
         return;
      }
      fail();
   }

   @Test
   public void nullGenre()
   {
      String args[] = new String[] {tempDir()};
      try
      {
         new SongRip(args, null);
      }
      catch (RuntimeException ex)
      {
         assertEquals("At least 1 genre must be specified", ex.getMessage());
         return;
      }
      fail();
   }

   @Test
   public void emptyGenreList() throws RuntimeException
   {
      String[] args = new String[] {tempDir()};
      try
      {
         new SongRip(args, new ArrayList<String>());
      }
      catch (RuntimeException ex)
      {
         assertEquals("At least 1 genre must be specified", ex.getMessage());
         return;
      }
      fail();
   }

   @Test
   public void nullGenreParseArgs()
   {
      String[] args = { "", null };
      try
      {
         SongRip.parseArgs(args);
      }
      catch (RuntimeException ex)
      {
         assertEquals("no genres specified!", ex.getMessage());
         return;
      }
      fail();
   }

   @Test
   public void noGenresCmdLineFileDoesntExist()
   {
      String[] args = { "", "noGenresDelimiterAndNotAFile" };
      try
      {
         SongRip.parseArgs(args);
      }
      catch (RuntimeException ex)
      {
         assertEquals("genre file does not exist: noGenresDelimiterAndNotAFile", ex.getMessage());
         return;
      }
      fail();
   }

   @Test
   public void genresOnCmdLine()
   {
      String[] args = { "", GENRE_DLIMITER, "80s", "90s" };
      List<String> genres = SongRip.parseArgs(args);
      assertEquals(2, genres.size());
      assertEquals("80s", genres.get(0));
      assertEquals("90s", genres.get(1));
   }

   @Test
   public void genresInFile() throws FileNotFoundException
   {
      try (PrintWriter pw = new PrintWriter(new File(tempDir(), "smakme"));)
      {
         pw.println("70s");
         pw.println("80s");
         pw.flush();
      }
      catch (FileNotFoundException ex)
      {
         throw ex;
      }

      String[] args = { "", new File(tempDir(), "smakme").getAbsolutePath() };
      List<String> genres = SongRip.parseArgs(args);
      assertEquals(2, genres.size());
      assertEquals("70s", genres.get(0));
      assertEquals("80s", genres.get(1));
   }

   private String tempDir()
   {
      return System.getProperty("java.io.tmpdir") + fs + "unitTest";
   }

   @Test
   public void addSongToLocalGenresFile() throws IOException
   {
      List<String> genres = createGenres();
      String args [] = new String[] {new File(tempDir(), "song1").getAbsolutePath()};

      SongRip song = new SongRip(args, genres);
      SongRip.addToLocalGenreFile(song);

      List<String> genresFromFile = contentsOfLocalGenresFile();
      assertEquals("song1 cats: 80s 90s ", genresFromFile.get(0));
   }

   // @Test
   // public void addSameSongToLocalGenreFile() throws IOException
   // {
   // List<String> genres = createGenres();
   // File songFile = new File(getTempDir(),"song1");
   //
   // SongRip song = new SongRip(songFile.getAbsolutePath(), genres);
   //
   // SongRip.addToLocalGenreFile(song);
   // SongRip.addToLocalGenreFile(song);
   //
   // List<String> songsFromCatFile = retrieveContentsOfLocalCategoryFile();
   // assertEquals(1, songsFromCatFile.size());
   // }

   @Test
   public void addAnotherSongToLocalGenresFile() throws IOException
   {
      List<String> genres = createGenres();

      String args[] = new String[] {new File(tempDir(), "song1").getAbsolutePath()};

      SongRip song1 = new SongRip(args, genres);
      SongRip.addToLocalGenreFile(song1);
      args  = new String[] {new File(tempDir(), "song2").getAbsolutePath()};
      SongRip song2 = new SongRip(args, genres);
      SongRip.addToLocalGenreFile(song2);

      List<String> songsFromGenresFile = contentsOfLocalGenresFile();
      assertEquals(2, songsFromGenresFile.size());
   }

   private List<String> contentsOfLocalGenresFile() throws FileNotFoundException, IOException
   {
      List<String> songsFromGenresFile = new ArrayList<String>();
      try (BufferedReader catFileReader = new BufferedReader(
            new FileReader(new File(tempDir(), GENRE_FILE_NAME)));)
      {
         String songLine = null;
         while ((songLine = catFileReader.readLine()) != null)
         {
            songsFromGenresFile.add(songLine);
         }
      }
      catch (Exception ex)
      {
      }

      return songsFromGenresFile;
   }

   private List<String> createGenres()
   {
      List<String> genres = new ArrayList<>();
      genres.add("80s");
      genres.add("90s");
      return genres;
   }
}
