TODO (12/15/20):
- generalize dijkstra map methods so pirates can have arbitrary destinations for wandering behavior
- extend Menu/MenuManager to implement hud/inventory menus
- implement trading 
    - add datastructures to Village to hold current trade, inventory, etc.
    - implement execution of trades through DockedMenu
- improve map hitboxes 
    - use different rectangle/polygon based on neighbor bitmask instead of always 16x16 square
- add falling off world death
- work on starting area idea
- work on upgrade mechanic
- work on sea monster mechanic

- Fixes/Refactoring:
    - replace all instances of MainGame constants being passed as parameters with direct references ie MainGame.CONST_VAR?
    - instead of getting entire path, pirates should only be requesting next tile from dijkstra map
