package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Player extends Ship {
	
	//starting stats
	private static int INIT_HULL = 5;
	//these are in view mode and must be converted before they are applied
	private static int INIT_MAX_SPEED = 2;
	private static int INIT_ACCEL = 2;
	private static int INIT_TURNING = 2;
	private static int INIT_DAMAGE = 2;
	private static int INIT_RANGE = 2;
	private static int INIT_MAX_CARGO = 3;
	
	//cargo is expandable so we need a constant to know when we can expand it n stuff
	private static final int TRUE_MAX_CARGO = 12;
	
	private Item[] cargo;
	private Item[] equipped;

	//currHull is managed by ship class
	private int maxHull;
	private int currCargo;
	private int maxCargo;
	private int currEquipped;
	private int maxEquipped;
	
	private int gold;
	private int cannonBalls;
	
	private int cannonDamage;
	private float cannonRange;
	
	//player playerSprites:
	//	0 = base ship
	//	1 = base ship w/ cannons
	//	2 = left cannon firing
	//	3 = right cannon firing
	//	4 = both cannons firing
	private Sprite[] playerSprites;
	private Sprite cannonBallSprite;
	private final static int INIT_SPRITE = 1;
	private final static int FIRING_SPRITE_COOLDOWN = 10; 
	
	//These variables make sure the proper sprite is displayed after firing for the proper number of frames
	private int firingLeftSpriteCooldown = 0;
	private int firingRightSpriteCooldown = 0;
	private boolean firingLeft = false;
	private boolean firingRight = false;
	
	//for determining when/where a player can dock
	private LandEntity dockable = null;
	
	//this determines the rate at which a player can take damage
	private final static int PLAYER_DAMAGE_COOLDOWN = 20;
	private int damageCooldown;
	
	public Player(Vector2 pos, Sprite[] playerSprites, Sprite cannonBallSprite, SpiceTraderMap map) {
		super(pos, playerSprites[INIT_SPRITE], map, Utils.statToUse(INIT_MAX_SPEED, 'm'), Utils.statToUse(INIT_ACCEL, 'a'), Utils.statToUse(INIT_TURNING, 't'), 0, INIT_HULL);
		this.playerSprites = playerSprites;
		this.cannonBallSprite = cannonBallSprite;
		
		//initialize stats
		cannonDamage = INIT_DAMAGE;
		cannonRange = Utils.statToUse(INIT_RANGE, 'r');
		
		damageCooldown = 0;
		
		gold = 0;
		cannonBalls = 10;
		
		maxHull = INIT_HULL;
		maxCargo = INIT_MAX_CARGO;
		currCargo = 0;
		currEquipped = 0;
		maxEquipped = 4;
		
		cargo = new Item[TRUE_MAX_CARGO];
		equipped = new Item[maxEquipped];
		
		//calc initial pathfinding to player
		getMap().getPlayerDijkstraMap().calcDijkstraMapToPixelCoords(getHitCenter());
	}
	
	@Override
	public void tick() {
		super.tick();
		
		calcPlayerFiringSprite();
		
		if(getCurrSpeed() > 0)
			getMap().getPlayerDijkstraMap().calcDijkstraMapToPixelCoords(getHitCenter());
		
		if(damageCooldown > 0) 
			damageCooldown--;
	}
	
	@Override
	protected void createHitbox() {
		setHitbox(Ship.getShipHitbox(getWidth(), getHeight(), 3));
	}
	
	public boolean addToCargo(Item item) {
		if(currCargo < maxCargo) {
			for(int i = 0; i < maxCargo; i++) {
				if(cargo[i] == null) {
					cargo[i] = item;
					i = maxCargo;
				}
			}
			currCargo++;
			return true;
		}
		return false;
	}

	public boolean addToEquipped(Item item) {
		if(currEquipped < maxEquipped) {
			for(int i = 0; i < maxEquipped; i++) {
				if(equipped[i] == null) {
					equipped[i] = item;
					i = maxEquipped;
				}
			}
			currEquipped++;

			if(item instanceof EquipableItem) {
				Stats currStats = getStats();
				currStats.add(((EquipableItem) item).getStats());
				applyStats(currStats);
			}

			return true;
		}
		return false;
	}
	
	public void removeFromCargo(Item item) {
		for(int i = 0; i < maxCargo; i++) {
			if(cargo[i] != null && cargo[i].equals(item)) {
				cargo[i] = null;
				currCargo--;
				i = maxCargo;
			}
		}
	}

	public void removeFromCargo(String itemName) {
		for(Item i : cargo) {
			if (i != null && i.getName().equals(itemName)) {
				removeFromCargo(i);
				return;
			}
		}
	}

	public boolean hasItem(Item item) {
		for(Item i : cargo)
			if(i != null && i.getName().equals(item.getName()))
				return true;
		return false;
	}
	
	public void removeFromEquipped(Item item) {
		for(int i = 0; i < maxEquipped; i++) {
			if(equipped[i] != null && equipped[i].equals(item)) {
				equipped[i] = null;
				currEquipped--;
				i = maxEquipped;
			}
		}

		if(item instanceof EquipableItem) {
			Stats currStats = getStats();
			currStats.subtract(((EquipableItem) item).getStats());
			applyStats(currStats);
		}
	}

	private void applyStats(Stats stats) {
		maxHull = stats.hull;
		maxCargo = stats.cargo;
		cannonDamage = stats.damage;
		cannonRange = Utils.statToUse(stats.range, 'r');
		setAccel(Utils.statToUse(stats.accel, 'a'));
		setMaxSpeed(Utils.statToUse(stats.maxSpeed, 'm'));
		setRotationSpeed(Utils.statToUse(stats.turning, 't'));
	}

	public Stats getStats() {
		Stats statsToView = new Stats();
		statsToView.hull = maxHull;
		statsToView.cargo = maxCargo;
		statsToView.damage = cannonDamage;
		statsToView.range = Utils.statToView(cannonRange, 'r');
		statsToView.accel = Utils.statToView(getAccel(), 'a');
		statsToView.maxSpeed = Utils.statToView(getMaxSpeed(), 'm');
		statsToView.turning = Utils.statToView(getRotationSpeed(), 't');
		return statsToView;
	}
	
	public CannonBall fireCannonLeft() {
		if(cannonBalls > 0) {
			cannonBalls--;
			CannonBall shot = new CannonBall(calcBallInitPos(), new Sprite(cannonBallSprite), getDirection() + 90, cannonRange, cannonDamage);
			firingLeft = true;
			firingLeftSpriteCooldown = Player.FIRING_SPRITE_COOLDOWN;
			if(firingRight) 
				setSprite(playerSprites[4]);
			else
				setSprite(playerSprites[2]);
			return shot;
		}
		return null;
	}

	public CannonBall fireCannonRight() {
		if(cannonBalls > 0) {
			cannonBalls--;
			CannonBall shot = new CannonBall(calcBallInitPos(), new Sprite(cannonBallSprite), getDirection() - 90, cannonRange, cannonDamage);
			firingRight = true;
			firingRightSpriteCooldown = Player.FIRING_SPRITE_COOLDOWN;
			if(firingLeft) 
				setSprite(playerSprites[4]);
			else
				setSprite(playerSprites[3]);
			return shot;
		}
		return null;
	}
	
	@Override
	public void strike(int damage) {
		if(damageCooldown == 0) {
			super.strike(damage);
			damageCooldown = PLAYER_DAMAGE_COOLDOWN;
		}
	}

	public void addGold(int i) { gold += i; }

	public void subtractGold(int i) { gold -= i; }
	
	public void setDockable(LandEntity dockable) {
		this.dockable = dockable;
	}
	
	public LandEntity getDockable() {
		return dockable;
	}
	
	public int getMaxHull() {
		return maxHull;
	}

	public int getCurrCargo() {
		return currCargo;
	}

	public boolean isCargoFull() { return !(currCargo < maxCargo); }

	public int getMaxCargo() {
		return maxCargo;
	}

	public int getGold() {
		return gold;
	}

	public int getCannonBalls() {
		return cannonBalls;
	}
	
	public Item getItemFromCargo(int i) {
		if(i < maxCargo)
			return cargo[i];
		else 
			return null;
	}
	
	public Item getItemFromEquipped(int i) {
		if(i < maxEquipped)
			return equipped[i];
		else 
			return null;
	}
	
	//TODO: Move cannon firing stuff to another class so pirate villages can use it
	private Vector2 calcBallInitPos() {
		Vector2 initPos = new Vector2();
		Vector2 centerOfShip = getHitCenter();
		initPos.x = centerOfShip.x - (cannonBallSprite.getWidth()/2);
		initPos.y = centerOfShip.y - (cannonBallSprite.getHeight()/2);
		return initPos;
	}
	
	//logic for setting correct firing playerSprites
	private void calcPlayerFiringSprite() {
		if(firingLeft) {
			firingLeftSpriteCooldown--;
			if(firingLeftSpriteCooldown == 0) {
				firingLeft = false;
				if(firingRight) {
					setSprite(playerSprites[3]);
				}
				else {
					setSprite(playerSprites[Player.INIT_SPRITE]);
				}
			}
		}
		if(firingRight) {
			firingRightSpriteCooldown--;
			if(firingRightSpriteCooldown == 0) {
				firingRight = false;
				if(firingLeft) {
					setSprite(playerSprites[2]);
				}
				else {
					setSprite(playerSprites[Player.INIT_SPRITE]);
				}
			}
		}
	}
}
