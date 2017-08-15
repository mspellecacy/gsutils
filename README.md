# GSUtils

* Don't play CSGO.
* Don't play DotA2.
* Don't play Minecraft.
* Own [Steel Series Rival 700](https://steelseries.com/gaming-mice/rival-700).
* __Write code.__

I wanted to put my shiny new toy to some practical use, so I wrote a Java app to push 'game' events, in this case system stats and weather, through the [GameSense Engine](https://github.com/SteelSeries/gamesense-sdk/tree/master/doc/api)'s API to my fancy mouse.

### End result
* App
   ![App](http://i.imgur.com/bFk7w6A.png)
* Output
   ![Functional Prototype](http://i.imgur.com/iMbdqrN.png)
    
## Downloads
* [Windows Installer](http://frakle.com/gsutils/gsutils.exe)
* [Jar](http://frakle.com/gsutils/gsutils-jar.zip)
* (Sorry mac, use the jar? Entirely untested on OSX)
 
 Jar requires [JRE8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)  
 
## How to build
__You will need:__
* [JDK 8+](http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven](https://maven.apache.org/what-is-maven.html)
 
__FULL BUILD (with Native bins)__
```bash
$ mvn clean package jfx:native assembly:single
```
* For .exe builds you need to have [Inno Setup](http://www.jrsoftware.org/isinfo.php) installed. 

#### Notes
JavaFX is extremely new to me, so I'm fairly certain I'm doing everything wrong. 
Why doesn't Game Sense support linux?