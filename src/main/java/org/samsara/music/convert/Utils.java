package org.samsara.music.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;

public class Utils
{
   static Logger log = LogManager.getLogger(Utils.class);
   static final String fs = java.io.File.separator;

   public static void deleteFiles(File dir)
   {
      try
      {
         if(dir.exists())
         {
            log.info("exists and children will be removed: {}", dir.toPath());
            FileUtils.cleanDirectory(dir);
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   public static String getLine(BufferedReader reader)
   {
      try 
      {
         return reader.readLine();
      }
      catch (IOException e) 
      {
         Utils.logAndThrowRuntime("read line exception: " + e.getMessage());
      }
      return null;
   }

   public static void logAndThrowRuntime(String message)
   {
      log.error(message);
      throw new RuntimeException(message);
   }

   public static List<String> getFileContents(String file)
   {
      List<String> contents = new ArrayList<String>();
      try ( BufferedReader reader = new BufferedReader(new FileReader(file)))
      { 
         String line;
         while((line = getLine(reader)) != null)
         {
            contents.add(line);
         }
      }
      catch (IOException e) 
      {
         logAndThrowRuntime("file not found: " + file + "\n" + e.getMessage());
      }
      
      return contents;
   }

   public static List<String> getFileContents(File file)
   {
      List<String> contents = new ArrayList<String>();
      try ( BufferedReader reader = new BufferedReader(new FileReader(file)))
      { 
         String line;
         while((line = getLine(reader)) != null)
         {
            contents.add(line);
         }
      }
      catch (IOException e) 
      {
         logAndThrowRuntime("file not found: " + file + "\n" + e.getMessage());
      }
      
      return contents;
   }

   public static boolean isFullyQualified(String path)
   {
      return path.lastIndexOf(System.getProperty("file.separator")) != -1;
   }
}
