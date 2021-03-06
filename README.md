# Audio Juicer

Simple Tool that extracts wav files from audio CDs and converts them to MP3 or whatever you like
by using command line tools.
Technically it's a frontend to cdparanoia, lame and others (in future versions).

Build
========

Uses: cdparanoia and lame (ffmpeg for flac?) for audio and
imagemagick for image resizing command line tools
and lsblk for device detection (should be included in every LSB linux I guess).

The build currently has only been tested on Ubuntu 18.04 LTS (Bionic Beaver) 
and on Debian 10 (Buster) for the amd64 platform.
The following build instruction works for the mentioned combination.

To build and run it do the following:
sudo apt-get install git openjdk-11-jdk gradle ant make gcc libdiscid0 libdiscid-dev cdparanoia lame imagemagick libcdio-utils openjfx libopenjfx-jni

maybe [gradle-debian-helper wget]

Make sure that Ubuntu/Debian uses the correct java version. Run the following command and
see if it returns an OpenJDK 11 version:
java --version
javac --version

If another JDK is returned then run the following command and select the OpenJDK 11 version.
sudo update-alternatives --config java
sudo update-alternatives --config javac 

git clone --recurse-submodules -j8 https://github.com/immerfroehlich/audio-juicer

cd audio-juicer

./gradlew distZip

Call
./run.sh
to run the application

Or you can copy the distribution files wherever you like. You can find them after calling distZip under
./java-juicer/build/distributions/java-juicer-x.x.zip

Please DON'T USE the ./gradlew run target, the application will start, but the relative paths to the native libraries
will not be correct. Just use the distribution files.

From time to time call
git pull --recurse-submodules
to get the latest changes and then rebuild the project with "./gradlew distZip".


Eclipse IDE configuration
============================

Call

./gradlew eclipse

to create the needed eclipse files. Then import the project via File->Import -> General->Existing Projects into Workspace. Click on Options->search for nested project then
select all nested projects. Click OK.

2 things must (unfortunately) be done manually.
Native-Libraries dependencies and Java 9 modules (for javafx) in run configuration.

https://openjfx.io/openjfx-docs/#IDE-Eclipse

Here is an example configuration for VM-Arguments of the Application configuration (Run->Run Configurations):
--add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics --module-path /home/andreas/.gradle/caches/modules-2/files-2.1/org.openjfx/javafx-fxml/11.0.2/1b8a331d5f393b48de0aef59b3c967a312967290/javafx-fxml-11.0.2-linux.jar:/home/andreas/.gradle/caches/modules-2/files-2.1/org.openjfx/javafx-controls/11.0.2/5f6929050a744aab39ebfc1e8e5dd03bcd2ad47b/javafx-controls-11.0.2-linux.jar:/home/andreas/.gradle/caches/modules-2/files-2.1/org.openjfx/javafx-graphics/11.0.2/ef4c34f0ca77cd99100b76c2ccf1dce383572bb1/javafx-graphics-11.0.2-linux.jar:/home/andreas/.gradle/caches/modules-2/files-2.1/org.openjfx/javafx-base/11.0.2/8db178adb1085d455e5ef643a48c34959c8771c1/javafx-base-11.0.2-linux.jar -Djava.library.path=../libdiscid-java-wrapper/native/build/linux/amd64

Features
==========

Important features:
- [ ] Free Software GPLv2 or GPLv3
- [ ] Fixed lame presets (with joint stereo) [Really needed? Just preset standard implemented. For classical music FLAC would probably better.
- [x] Musicbrainz / CDDB retrieval of Artist name, Album name, Song name
- [x] Artwork retrieval
- [/] Musicbrainz data submission (for CDs that are not yet listed)
- [ ] Debian package maintaining
- [ ] For Archiving purposes Hash generation for each file and comparison (sha2)
- [ ] For Archiving: Parallel generation of mp3 and flac
- [x] GUI (JavaFX or Swing)
- [ ] User presets (just enter the command line) [??? Is this really needed? Or just stick to the standard presets that lame offers]
- [ ] If not available easy user upload of artist name etc to Musicbrainz (is this really needed? Musicbrainz already has a GUI on their web page, ask the user to input their CD info on their web page.)
- [ ] (Archive database?)


TODO / Backlog
------------------------
- Mp3 Creation
    - [x] Bug: The album title is currently not ID3 tagged. Instead track name is used.
- [ ] Bug: ConfigurationServiceTest overwrites the users config file. Why? I stubbed the file opening with Mockito.
- [ ] Use the drive selection for ripping and musicbrainz retrieval currently it is not used.
- [ ] Bug: Progress bar doesn't disappear after work is finished. No finish dialog. [Reproduction try to load a CD from musicbrainz, that is not listed there]
- [x] Add support for Multi-CD releases. Ask the user to select the right CD.
	- [ ] Automate the selection of the right CD in multi CD releases (if possible).
- [x] The pregap track dialog is not asking any question, but the answers are yes and no. Add the question. After clicking yes the process doesn't stop - Fix it.
- [x] Use different naming schemes for different kinds of releases - album (e.g. single artist release), compilation (e.g. Bravo Hits), interpreted music (e.g. classical music), audio books
    - [x] Select the kind of release before ripping.
    - [ ] Selecting a naming scheme after adding a new leads to IndexOutOfBoundException is this a bug in JavaFX???
    - [ ] Create a modal dialog to add/edit/remove  naming schemes currently this is not very user friendly (and maybe just select the default one).
- GUI
    - [ ] Show a dialog for not supported pregap tracks.
- Configuration
    - [ ] Add a few common used default naming schemes.
- [x] Track numbers will currently not be displayed correctly in the main view table - Fix it.
- [x] Pregap track (inaudible) - don't convert it to mp3. Remove the wav (Example: Genesis - Selling England..., Vangelis - Blade Runner)
- [x] Add support for hidden audible pregap tracks (Example: Die Ärzte - 13)
- [x] Selection of cdrom device
- [x] Migrate to an OpenJDK version that is supported by Ubuntu (currently that is OpenJDK 11)
- [ ] What about titles that are too long? Example Al Di Meola - Casino
- [x] Remove Jitpack.io from the build and directly integrate the submodules with git and gradle
- [ ] Remove the CoverartArchive API dependency and implement the file download myself (java or wget?) 
- [x] Read TOC and isrc for submission of discId to musicbrainz
- Musicbrainz data submission
    - [x] Upload TOC and ISRC to musicbrainz for unknown releases (via http link).
    - [ ] Extract the track names and other information from CD-TEXT info. For example with cdda-player -l from the libcdio package.
    - [ ] Or enter the data manually and submit to musicbrainz.
- [x] If there is only one disc for discid don't ask the user for input.
- Ripping
    - [ ] cdparanoia is extremely slow. Either make it faster by disabling paranoia mode or find an alternative to it.
- Progress Bar
    - [x] Show progress bar during cdparanoia ripping process
    - [ ] Rip every track on it's own so an accurate progress bar can be shown.
    - [x] Pimping the progress bar. Show the current work (converting track 1/15). I guess this also means to have a Task for each work?
- [ ] Logging for debug and info and error
- [ ] Verbose mode with logging info
- [x] The first release year must be searched by a special query and not via discid. Often the first release has been a vinyl/Schellack record
    - [x] Now the CD title is used as a query.
    - [ ] Add manual input of first release year.
    - [ ] Currently not all releases for a CD title will be retrieved. See Gustav Holst - The Planets which doesn't find the 1981 first release for the record.
    - [ ] Maybe use the Release Group from musicbrainz to get the first release year. But I don't know if the data quality is good enough. 
- [ ] Blade Runner throws IndexOutOfBoundsException
java.lang.IndexOutOfBoundsException: Index: 12, Size: 12
	at java.util.ArrayList.rangeCheck(ArrayList.java:653)
	at java.util.ArrayList.get(ArrayList.java:429)
	at App.createMp3OfEachWav(App.java:208)
	at App.main(App.java:94)
Seems to be a pregap track
- [ ] Input filtering and validation with retry possibility
- [ ] Adding Strings to resource loader
- [ ] Localization
- [ ] Make it compileable and runnable on Windows with MinGW
- [ ] Always use the tracks artist, try it with a compilation CD e.g. Bravo Hits. For Gustav Holst the Planets the artist name is simply just Holst. Ask the editor.
- [x] Implement settings dialog with at least the root/ripping path as a first setting and save it to the users home path.
- [x] Implement drive selection combobox. Use /dev/cdrom as the standard. [Note: cdrom is not found by lsblk, therefore not used.]
- [ ] Get away from the questions tirade for missing artwork and implement it the GUI way - which means graphically show that no artwork was found.
	And add a button to manually select artwork.
- [ ] ?Show an info dialog on first start explaining that this is a frontend to Musicbrainz for ripping. And if any data is not OK, go to musicbrainz and correct it.
- [x] Bug: The dialogs are missing action description that is given in command line. 


Known Issues:
======================
- When using KDE, modal input Dialogs are sometimes reduced in size so that the application
won't be useable anymore.
As a workaround I tried to set
dialog.setResizable(true);
but it may be that some dialogs won't have the fix.
The bug was fixed in some OpenJFX 12 version. Currently it seems there will be no backport to OpenJFX 11.
See https://github.com/javafxports/openjdk-jfx/issues/222 

Used libraries:
=======================
mp3agic - for manipulation of id3 tags
URL: https://github.com/mpatric/mp3agic
Licence: MIT

coverartarchive-api - For image download from Cover Art Archive
URL: https://github.com/lastfm/coverartarchive-api
Licence: Apache 2.0

lsblk --json --output name,type -a
Outputs all bulk devices as json

imagemagick - To convert the artwork


Alternatives
=============

Linux:
- ripperX
- Asunder
- KIO-audiocd (Dolphin integration)
- K3B
- Sound Juicer
- ABCDE (A better CD encoder - https://abcde.einval.com/wiki/)
- Audex
- Rubyripper
- Flacon
- Whipper
- CDNfiy
- Grip (https://sourceforge.net/projects/grip/)

Source: https://www.slant.co/topics/2443/~best-cd-rippers-for-linuxs

Windows:
- CDex (https://cdex.mu/)
- fre:ac (https://www.freac.org/)

Source: https://windowsreport.com/cd-ripper-software-windows-10/


Feature Overview:
ABCDE - Uses musicbrainz ws1 service which is out of support, currently not working
Sound Juicer is not working under Ubuntu for years now (which is why I started this project)


Development Knowledge Ressources
=================================

Information retrieval sites:
------------------------
https://musicbrainz.org/
https://coverartarchive.org/

Used commands:
--------------------
Lame:
lame --preset standard sample.wav sample.mp3
(Joint Stereo is default, -q 0 for best quality algorithms but more computing power needed)

ImageMagick (Front cover resizing):
convert -resize 300x300 front.jpg front_small.jpg


Ideas on using and installing Audio Juicer in Windows
-----------------------------------------------------
- CDex can be called from the command line
	"-autorip" will rip all tracks
	Source: https://hydrogenaud.io/index.php?topic=40092.0
-Or directly use the libcdio library which has the cdparanoia library included for windows
 but this will probably take many more hours.
	
- Lame is also available for Windows and is licenced LGPL
	Source: https://lame.sourceforge.io/
	
- Replacement for imagemagik ???
	- Maybe http://www.graphicsmagick.org/ it says it is a copy of imagemagick with MIT style licence.
	Source: https://alternativeto.net/software/imagemagick/
	
- Redhat distributes an OpenJDK for windows, maybe it is possible to extract the vm
  and repackage it, if redistribution is allowed:
  https://access.redhat.com/documentation/en-us/openjdk/11/html/openjdk_11_for_windows_getting_started_guide/getting_started_with_openjdk_for_windows#installing_openjdk_zip_bundle
  
- The so called Microsoft Desktop Bridge allows to package Java Applications for the Windows Store
	Source: https://stackoverflow.com/a/43945785
	Source 2: https://github.com/crramirez/uwp_bridge_java


Infos:
-----------------------



How to use Ant tasks within Gradle:
https://docs.gradle.org/current/userguide/ant.html

How to rip cds from command line:
https://www.cyberciti.biz/faq/linux-ripping-and-encoding-audio-files/

How to find all cd drives:
https://www.cyberciti.biz/faq/linux-find-out-the-name-of-dvd-cd-rom-writer-blu-ray-device/

Lame command line documentation: (search for preset)
https://svn.code.sf.net/p/lame/svn/trunk/lame/USAGE

Cdparanoia command line documentation:
https://xiph.org/paranoia/manual.html


https://en.wikipedia.org/wiki/List_of_online_music_databases

Java Native Interface Tutorial:
https://developer.ibm.com/tutorials/j-jni/


Possible libraries:
https://github.com/schnatterer/musicbrainzws2-java

Alternative libdiscid Java Wrapper: https://github.com/dietmar-steiner/JMBDiscId/blob/master/src/org/musicbrainz/discid/JMBDiscId.java

https://github.com/lastfm/coverartarchive-api


https://github.com/syntelos/cddb (this looks dirty, like CPP-Code, no unmarshalling of XML, no clearly defined API, no licence)


Java EE is now EE4J/Jakarta EE maintained by Eclipse Foundation
------------------------------------------------------------
In 2018 Oracle throw out Java EE of the JDK.
The Eclipse Foundation is now maintaining it under the name Jakarta EE / EE4J.
See:
https://www.heise.de/developer/meldung/Aus-Java-EE-wird-Jakarta-EE-3979348.html
https://www.heise.de/developer/meldung/Enterprise-Java-Die-Waechter-von-Java-EE-sind-nun-die-Jakarta-EE-Botschafter-4654134.html
https://jakartaee-ambassadors.io/2018/01/11/where-is-java-ee-going/

Jakarta EE project homepage:
https://projects.eclipse.org/projects/ee4j.jakartaee-platform

How to add Jakarta EE to dependency management:
https://medium.com/@Leejjon_net/migrate-a-jersey-based-micro-service-to-java-11-and-deploy-to-app-engine-7ba41a835992


Debian Java packaging:
-----------------------------------
Debian Git Repository:
https://salsa.debian.org/public

Debian (package) Policy:
https://www.debian.org/doc/debian-policy/

Debian Java package Policy:
https://www.debian.org/doc/packaging-manuals/java-policy/

Gradle Debian Helper:
https://wiki.debian.org/Java/Packaging/Gradle

Infos on how to use gradle-debian-helper:
http://debian.2.n7.nabble.com/how-to-use-debian-gradle-helper-td4402253.html

JDEB - Create Debian debs of Java builds using ant:
https://github.com/tcurdt/jdeb


JavaFX (openjfx) in Ubuntu:
----------------------

Installing JavaFX (openjfx) in Ubuntu:
https://askubuntu.com/questions/1091157/javafx-missing-ubuntu-18-04

OpenJFX packages in maven repository:
https://mvnrepository.com/search?q=openjfx

JavaFX with gradle plugin:
https://kofler.info/javafx-programm-mit-gradle/


JavaFX general usage:
-----------------------

TableView example:
https://riptutorial.com/javafx/example/7288/sample-tableview-with-2-columns

JavaFX concurrency:
https://docs.oracle.com/javafx/2/threads/jfxpub-threads.htm

Progress Bar Binding:
https://stackoverflow.com/questions/36333902/binding-progress-bar-to-a-service-in-javafx


Handling native libraries (here libdiscid)
---------------------------------------------
https://stackoverflow.com/questions/29437888/using-gradle-with-native-dependencies

https://discuss.gradle.org/t/setting-eclipse-native-library-location/11075

Unter Linux: Aufruf System.loadNative(name)
Die Datei muss lib[name].so heißen.
Aufruf mit java -Djava.library.path=relativ oder absolut


libdiscid-java Probleme
-------------------------
Fehler beim Aufruf:
java -Djava.library.path=native/linux/amd64:/home/andreas/bin/workspaces/java_projects/libdiscid/_build -cp build:lib/benow-launch.jar test.org.musicbrainz.discid.Query
java: symbol lookup error: /home/andreas/bin/workspaces/java_projects/libdiscid-java/native/linux/amd64/libdiscid-java.so: undefined symbol: discid_new

Nicht korrekt verlinkt?
Siehe Kommentar von Joni:
https://stackoverflow.com/questions/17322885/jni-using-symbol-lookup-error

Aufruf von
ldd libdiscid-java.so
liefert:
statically linked

Sollten hier nicht eigentlich die konkret verlinkten Bibliotheken stehen?

Hinweis vom Ant Build:
cd ..; java -cp build:libdiscid-java.jar -Djava.library.path=native/linux/amd64 test.ca.benow.jni.LinkerTest
     [exec] Library linking validated.  Ensure to specify the native directory in the java.library.path, ie:
     [exec]     java -Djava.library.path=native/linux/amd64 org.some.Class
     [exec] It works!
     
Antwort: Die Reihenfolge der Parameter ist entscheidend.
-L nach -o
https://stackoverflow.com/a/24257685

     
Java Native Interface and C/C++ Tutorials:
------------------------------------------

Generating c header files not needed anymore:
https://stackoverflow.com/questions/28947767/javah-failing-to-create-c-header-file-for-jni-class

https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html#zz-4.

http://www.willemer.de/informatik/cpp/pointer.htm
https://stackoverflow.com/questions/13553752/convert-char-pointer-to-unsigned-char-array

Libdiscid example: https://github.com/metabrainz/libdiscid/blob/master/examples/disc_metadata.c

https://docs.gradle.org/current/userguide/building_cpp_projects.html#cpp_pluginhttps://docs.gradle.org/current/userguide/building_cpp_projects.html#cpp_plugin
https://docs.gradle.org/current/userguide/cpp_library_plugin.html

Vielleicht sollte ich libdiscid einfach kopieren und das Ant-Build-Skript verwenden.

Beispiel für javah-Benutzung:
https://stackoverflow.com/a/39247196

javah -d ~/bin/workspaces/java_projects/libdiscid-java-wrapper/native -classpath ~/bin/workspaces/java_projects/libdiscid-java-wrapper/build de.immerfroehlich.discid.DiscIdCalculator

https://stackoverflow.com/questions/579734/assigning-strings-to-arrays-of-characters
https://stackoverflow.com/questions/6238785/newstringutf-and-freeing-memory

https://www.cs-fundamentals.com/c-programming/static-and-dynamic-linking-in-c.php


Gradle building JNI native projects:
--------------------------------------
Examples:
https://github.com/luncliff/cpp-gradle-in-kor
https://github.com/cory-johannsen/gradle-jni-example

https://docs.gradle.org/current/userguide/native_software.html



Gradle
------------------------------------------
https://stackoverflow.com/questions/35244961/how-do-i-add-default-jvm-arguments-with-gradle
https://stackoverflow.com/questions/38663328/use-ant-project-as-gradle-subproject
https://developer.android.com/studio/projects/gradle-external-native-builds
     

Github Projekte direkt im Gradle Projekt als Dependency einbinden:
-----------------------------------------------------------------
https://jitpack.io/

How to deal with Runtime.exec
-------------------------------------------
https://www.javaworld.com/article/2071275/when-runtime-exec---won-t.html

Linux Bash Operators (won't work in exec because it isn't a Bash)
--------------------------------------------------------------------
https://www.putorius.net/linux-io-file-descriptors-and-redirection.html

Using Musicbrainz:
---------------------------------------------------------------
Web Service v2:
https://musicbrainz.org/doc/Development/XML_Web_Service/Version_2#Relationships

Json Web Service:
https://musicbrainz.org/doc/Development/JSON_Web_Service

Entity explanation:
https://wiki.musicbrainz.org/MusicBrainz_Database/Schema

High-Res Entity Relationship Diagram:
https://wiki.musicbrainz.org/-/images/5/52/ngs.png

Query Disc by discid:
https://musicbrainz.org/ws/2/discid/<discid>/?fmt=json
https://musicbrainz.org/ws/2/discid/OLLSzCTuujhgIKijhSwdw7ljJ2I-/?fmt=json

https://musicbrainz.org/ws/2/discid/OLLSzCTuujhgIKijhSwdw7ljJ2I-/?fmt=json&inc=recordings+artist-credits

Query Release (???) by id:
https://musicbrainz.org/ws/2/release/<releaseid>/?&fmt=json

Search for Release:
https://musicbrainz.org/ws/2/release?query=Die+Bibel+Geschichten+Testament&fmt=json

Submitting Date (XML also possible for JSON?):
https://musicbrainz.org/doc/Development/XML_Web_Service/Version_2#Submitting_data


Cover-Art:
----------------------

Cover-Art-Archive:
https://coverartarchive.org/release/<mbid>
https://coverartarchive.org/release/3f6bf902-b5af-4d4a-836a-681db6099896

http://id3.org/id3v2.3.0

https://unix.stackexchange.com/questions/84915/add-album-art-cover-to-mp3-ogg-file-from-command-line-in-batch-mode

lame --ti /path/to/file.jpg audio.mp3

Discussions about jpeg file size for embedding images into mp3:
https://www.richardfarrar.com/embedding-album-art-in-mp3-files/

Resizing images with imagemagick:
https://askubuntu.com/questions/1164/how-to-easily-resize-images-via-command-line


REST Services with JSON in Java (with Jersey [and Jackson?]):
--------------------------------------------------------------
https://vaadin.com/tutorials/consuming-rest-services-from-java-applications
https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/modules-and-dependencies.html
https://stackoverflow.com/questions/44088493/jersey-stopped-working-with-injectionmanagerfactory-not-found
https://stackoverflow.com/questions/23442440/messagebodyreader-not-found-for-media-type-application-json
https://stackoverflow.com/questions/5455014/ignoring-new-fields-on-json-objects-using-jackson

http://www.jsonschema2pojo.org/

Gradle and Kotlin Infos:
----------------------------
https://docs.gradle.org/current/userguide/userguide_single.html#build_lifecycle
https://docs.gradle.org/5.6.2/dsl/org.gradle.api.Project.html

https://kotlinlang.org/docs/tutorials/getting-started-eclipse.html
https://play.kotlinlang.org/byExample/01_introduction/01_Hello%20world
https://kotlinlang.org/docs/reference/native-overview.html

Allowed characters in specific file system file names:
-------------------------------------------------------
https://stackoverflow.com/questions/19503697/unicode-filenames-on-fat-32
https://serverfault.com/questions/242110/which-common-charecters-are-illegal-in-unix-and-windows-filesystems

