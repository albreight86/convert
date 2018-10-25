This code does two things:
- SongRip.java is executed when using "CD Ripper" to create a genres file in the directory to which the cd is ripped.
- MasterGenre is executed on a largish directory structure of these subdirs, and creates a MasterGenres directory with
  a file for each genre containing the FQ Path to each, and every, song in that genre. These files were originally used as playlists
  when the IPOD environment was used to get the music files and these genre playlists onto an ipod. The upload to ITUNES functionality is deprecated
  and not fully represented in this code.
  
  This code retains the SongRip and MasterGenre functionality. As these days, an IPOD is not necessary; simply copying the music to
  the memory card on the phone is suffecient. 
  
  At this point, the future is to expand the notion of "genre" to augment with other properties, such as playlist and maybe other things.
  
  The "catagories" and "categories.vim" files (identical) should have their formats changed to be JSON.
  Also, android music players need to be investigated concering support of external playlists, as well as exporting shuffle lists. This
  may or may not exist.
  
  The command line for running SongRip in CD Ripper as a "run external" dsp job is:
  - Program: C:\ProgramFiles\Java\jre1.8.0_121\bin\java.exe
  - cmd line: -cp C:\Users\lakos\Dropbox\Music\WorkSpaces\convert\target\classes;C:\Users\lakos\Dropbox\Music\WorkSpaces\convert\target\dependency\* org.samsara.music.convert.SongRip "[outfilelong]" "C:\Users\lakos\Music\genresRef" > C:\convertLogs\output.out 2> C:\convertLogs\output.err
  - the genrefs file is absolutely needed. Note this and the topdir can be anywhere....the two args say where.
 - note, at last try the stderr and out didnt work.....but I have seen them work.
  
  The command lind for running GlobalGenres in bash is:
  - java -cp /mnt/c/Users/lakos/Dropbox/Music/WorkSpaces/convert/target/classes:/mnt/c/Users/lakos/Dropbox/Music/WorkSpaces/log4j-api-2.11.0.jar:/mnt/c/Users/lakos/Dropbox/Music/WorkSpaces/log4j-core-2.11.0.jar org.samsara.music.convert.GlobalGenres /mnt/c/Users/lakos/Music
  
  The log file shows up in convert/ or convert/test-output
  
  
  
