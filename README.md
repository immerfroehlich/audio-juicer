# Audio Juicer

Simple Tool that rips wav files from audio CDs and converts them to MP3 or whatever you like
by using command line tools.
Technically it's a frontend to cdparanoia, lame and others (in future versions).

Build
========

Uses: cdparanoia and lame (ffmpeg for flac?) for audio and
imageagick for image resizing command line tools
and lsblk for device detection (should be included in every LSB linux I guess).

The build currently has only been tested on Ubuntu 18.04 LTS (Bionic Beaver) for the amd64 platform.
The following build instruction works for the mentioned combination.

To build and run it do the following:
´´´bash
sudo apt-get install openjdk-11-jdk gradle ant make gcc libdiscid0 libdiscid-dev cdparanoia lame imagemagick libcdio-utils
maybe [gradle-debian-helper openjfx libopenjfx-jni]
´´´

Make sure that Ubuntu/Debian uses the correct java version. Run the following command and
see if it returns an OpenJDK 11 version:
java --version
javac --version

If another JDK is returned then run the following command and select the OpenJDK 11 version.
sudo update-alternatives --config java
sudo update-alternatives --config javac 

git clone --recurse-submodules -j8 https://github.com/immerfroehlich/audio-juicer

cd audio-juicer

./gradlew build

Extract the content of build/distributions/JavaJuicer.zip to where you want the compiled application
to be and run ./JavaJuicer in the bin directory.

BE WARNED: Currently the tool has a few fixed configurations for the paths. Currently all ripped audio will
be saved in ~/Musik/Archiv


Eclipse IDE configuration
============================

2 things must (unfortunately) be done manually.
Native-Libraries dependencies and Java 9 modules (for javafx) in run configuration.

https://openjfx.io/openjfx-docs/#IDE-Eclipse

Features
==========

Important features (sorted by priority from the most important on the top):
- [ ] Free Software GPLv2 or GPLv3
- [ ] Fixed lame presets (with joint stereo) [Really needed? Just preset standard implemented. For classical music FLAC would probably better.
- [x] Musicbrainz / CDDB retrieval of Artist name, Album name, Song name
- [x] Artwork retrieval
- [ ] Musicbrainz data submission (for CDs that are not yet listed)
- [ ] Debian package maintaining
- [ ] For Archiving purposes Hash generation for each file and comparison (sha2)
- [ ] For Archiving: Parallel generation of mp3 and flac
- [ ] GUI (JavaFX or Swing)
- [ ] User presets (just enter the command line)
- [ ] If not available easy user upload of artist name etc to Musicbrainz (is this really needed? Musicbrainz already has a GUI on their web page, ask the user to input their CD info on their web page.)
- [ ] (Archive database?)


TODO / Backlog
------------------------
- [x] Pregap track (inaudible) - don't convert it to mp3. Remove the wav (Example: Genesis - Selling England..., Vangelis - Blade Runner)
- [x] Add support for hidden audible pregap tracks (Example: Die Ärzte - 13)
- [ ] What about titles that are too long? Example Al Di Meola - Casino
- [x] Selection of cdrom device
- [ ] Remove Jitpack.io from the build and directly integrate the submodules with git and gradle
- [ ] Migrate to an OpenJDK version that is supported by Ubuntu (currently that is OpenJDK 11)
- [ ] Read TOC and isrc for submission of discId to musicbrainz
- [ ] If there is only one disc for discid don't ask the user for input.
- [ ] Show progress bar during cdparanoia ripping process
- [ ] Logging for debug and info and error
- [ ] Verbose mode with logging info
- [ ] The first release year must be searched by a special query and not via discid. Often the first release has been a vinyl/Schellack record
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


Used libraries:
=======================
mp3agic - for manipulation of id3 tags
URL: https://github.com/mpatric/mp3agic
Licence: MIT

coverartarchive-api - For image download from Cover Art Archive
URL: https://github.com/lastfm/coverartarchive-api
Licence: Apache 2.0

lsblk --json --output name,type
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
https://windowsreport.com/cd-ripper-software-windows-10/


Feature Overview:
ABCDE - Uses musicbrainz ws1 service which is out of support, currently not working


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

