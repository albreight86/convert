package org.samsara.music.convert;

import static org.samsara.music.convert.GlobalGenres.GENRE_DLIMITER;
import static org.samsara.music.convert.GlobalGenres.GENRE_FILE_NAME;
import static org.samsara.music.convert.Utils.fs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GlobalGenresTest
{

   @Test
   public void lacksGenresTest()
   {
      boolean lacksGenres = GlobalGenres.lacksGenres("fqName " + GlobalGenres.GENRE_DLIMITER + " iglle figgle fi");
      Assert.assertFalse(lacksGenres);

      lacksGenres = GlobalGenres.lacksGenres("fqName " + " iglle figgle fi");
      Assert.assertTrue(lacksGenres);
   }

   @Test
   public void writeGlobalGenresTest() throws IOException 
   {
      if(!tempDir().exists())
      {
         Files.createDirectory(tempDir().toPath());
      }
      //FileUtils.cleanDirectory(tempDir());
      String tDir = tempDirStr();
      
      String[] lines1 = { "FQsong1.mpa " + GENRE_DLIMITER + " iggle figgle fi",
                         "FQsong2.mpa " + GENRE_DLIMITER + " iggle figgle fi smackme"};
      createStuff(tDir , "subDir1/Artist1" , lines1);
      
      String[] lines2 =  { "FQsong3.mpa " + GENRE_DLIMITER + " iggle figgle fi",
                             "FQsong4.mpa " + GENRE_DLIMITER + " iggle figgle fi smackme"};
      createStuff(tDir , "subDir1/Artist2" , lines2);
      
      File f = new File(tDir);
      GlobalGenres gg = new GlobalGenres(f);
      gg.process();
      
      System.out.print(genStructure());
   }

   public static String genStructure() throws IOException
   {
      return showDir(1, new File(tempDirStr()));
   }

   static String showDir(int indent, File file) throws IOException
   {
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < indent; i++)
         sb.append('-');

      sb.append(file.getName() + "\n");

      if (file.isDirectory())
      {
         File[] files = file.listFiles();
         for (int i = 0; i < files.length; i++)
            showDir(indent + 4, files[i]);
      }
      return sb.toString();
   }

   private void createStuff(String tDir, String str1, String[] lines) throws IOException
   {
      new File(tDir, str1).mkdirs();
      File f = new File(tDir, str1 + "/" + GENRE_FILE_NAME);
      try (PrintWriter pW = new PrintWriter(new FileWriter(f)))
      {
         Arrays.asList(lines).forEach(pW::println);
      }
   }

   private static String tempDirStr()
   {
      return System.getProperty("java.io.tmpdir") + fs + "unitTest";
   }

   private static File tempDir()
   {
      return new File(tempDirStr());
   }
}
