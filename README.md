TODO (12/16/20)
- Features to Add
    - pirate wandering (use dijkstra map class?)
    - pirates shouldn't come after player when he gets within certain radius of village (then we can remove the code that pauses game when docked)
    - hud/inventory menus (extend menu/menu manager)
    - trading 
        - expand on docked menu
        - add datastructures to Village to hold current trade, inventory, etc.
    - different map hitboxes for different tile types
    - falling off world death
    - starting area/upgrade shop
    - sea monsters
    - quest/winning the game
    
- Features to Investigate
    - have camera rotate when you turn

- Fixes/Refactoring
    - replace all instances of MainGame constants being passed as parameters with direct references ie MainGame.CONST_VAR?
    - instead of getting entire path, pirates should only be requesting next tile from dijkstra map
    - generalize dijkstra map methods
