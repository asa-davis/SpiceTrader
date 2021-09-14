
**IntelliJ Setup Instructions**
- File > New > Project from Version Control
- Select this repo
- Select Gradle project and click next a bunch
- New Run Configuration > Application
    - JDK: Bundled
    - Module: SpiceTrader.desktop.main
    - Main Method: dev.asa.spicetrader.desktop.DesktopLauncher
    - Working Directory: [clone location]


**Living List of Stuff to Work On - SETUP GITHUB PROJECTS TO TRACK THIS STUFF**
- Features to Add
    - keep pirate villages from spawning right next to merchants/villages
    - keep merchants from spawning right next to villages
    - open ship menu when player docks and close when they leave
    - when player clicks buy/sell but can't, display reason
    - more visual differences between merchant and village menus
    - make item visualizer display stats for equipable items
    - pirate wandering
    - upgrade shops
    - rate of fire stat
    - send pirates back to base if you dock, dont pause game
    - (IMPORTANT) different map hitboxes for different tile types
    - falling off world death/outer world textures
    - sea monsters
    - quest/winning the game (sea monsters)
    - add better pirate death textures
    - whitewater trail behind ships
    - Startup screen and loading screen
    
- Features to Investigate
    - health bars above enemies
    - customize starting stats at beginning of game, fallout style
    - randomly generated equippable items, bordlerlands style
    - click approach to cannons: allow player to shoot with mouse as long as shot is within a small arc of cannon direction
    - have camera rotate when you turn - would need to change textures to top down only :(
    - rewards for killing pirates
    - persistant upgrades/unlocks?
    - biomes

- Fixes/Refactoring
    - merchants spawning on invalid locations!!
    - only players should have strike cooldown, not pirates
    - replace all instances of Vector2 being used for tile coords with int[]s. why use floats to store int values??
    - merge village factory class with ent factory. What do we do with village location inner class???
    - fix bug where you can sail underneath pirates
    - replace all instances of MainGame constants being passed as parameters with direct references ie MainGame.CONST_VAR?
    - fix bug where dead pirates board player in the time between when they take a hit and get deleted
    - fix damage flash bug
    - pirates shouldn't come after player when he gets within certain radius of village (then we can remove the code that pauses game when docked)
    - keep pirate villages from spawning near trading villages 
    - keep villages from spawning on top of trees, or remove trees under village locations
