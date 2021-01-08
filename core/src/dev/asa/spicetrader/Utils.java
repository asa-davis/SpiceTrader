package dev.asa.spicetrader;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;

public class Utils {
	
	//inclusive
	public static int randInt(int min, int max) {
		return (int) ((Math.random() * (max - min + 1)) + min);
	}
	
	//i made this function because i kept having to fetch all the neighbors for a tile and check if they were in bounds.
	//returns a list containing vectors representing the coords for neighboring tiles.
	//out of bounds tiles will be NULL - this is bad practice and really they should just not be part of the list but well here we are.
	//if includeCurrTile is true, the neighbors are ordered in the list like this:
	//	0|1|2
	//	-----
	//	3|4|5
	//	-----
	//	6|7|8
	//if includeCurrTile is false, they will be ordered like this:
	//	0|1|2
	//	-----
	//	3|x|4
	//	-----
	//	5|6|7	
	public static List<int[]> getNeighborCoords(int x, int y, int numCols, int numRows, boolean includeCurrTile, boolean includeDiag) {
		List<int[]> neighbors = new ArrayList<int[]>();
		int neighborY;
		int neighborX;
		for(int yShift = 1; yShift >= -1; yShift--) {
			for(int xShift = -1; xShift <= 1; xShift++) {
				neighborY = y + yShift;
				neighborX = x + xShift;
				//check for out of bounds
				if(neighborY >= 0 && neighborY < numRows && neighborX >= 0 && neighborX < numCols) {
					neighbors.add(new int[] {neighborX, neighborY});
				} else {
					neighbors.add(null);
				}
			}
		}
		
		//yikes
		if(!includeCurrTile)
			neighbors.remove(4);
		
		if(!includeDiag) {
			neighbors.remove(0);
			neighbors.remove(1);
			if(!includeCurrTile) {
				neighbors.remove(3);
				neighbors.remove(4);
			}
			else {
				neighbors.remove(4);
				neighbors.remove(5);
			}

		}
		return neighbors;
	}
	
	public static float round(float val, int decimals) {
		return (float) (Math.round(val * Math.pow(10, decimals))/Math.pow(10, decimals));
	}
	
	//to round to nearest third, pass (val, 3);
	public static float roundToNearestFraction(float val, float fraction) {
		float valRoundedOff = (int)val;
		double nextThresh = valRoundedOff;
		double lastThresh = valRoundedOff;
		for(int i = 0; i < 1/fraction; i++) {
			nextThresh += fraction;
			if(val < nextThresh) {
				double split = lastThresh + ((nextThresh - lastThresh)/2);
				if(val < split)
					return (float) lastThresh;
				else
					return (float) nextThresh;
			}
			lastThresh = nextThresh;
		}
		//if for some reason things don't work out, function returns NaN
		System.out.println("rounding function failed with arguments " + val + ", " + fraction);
		return 1/0;
	}
	
	
	//returns three fonts in order from smallest to largest
	public static BitmapFont[] getPixelFonts() {
		BitmapFont[] fonts = new BitmapFont[4];
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Perfect DOS VGA 437 Win.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!'()>?";

		parameter.size = 16;
		fonts[0] = generator.generateFont(parameter);
		fonts[0].setColor(Color.DARK_GRAY);
		
		parameter.size = 24;
		fonts[1] = generator.generateFont(parameter);
		fonts[1].setColor(Color.DARK_GRAY);
		
		parameter.size = 32;
		fonts[2] = generator.generateFont(parameter);
		fonts[2].setColor(Color.DARK_GRAY);
		
		parameter.size = 48;
		fonts[3] = generator.generateFont(parameter);
		fonts[3].setColor(Color.DARK_GRAY);
		
		generator.dispose();
		return fonts;
	}
	
	public static Vector2 getRandShipPos(Sprite sprite, SpiceTraderMap map) {
		float xPos = (float) Utils.randInt(0, (int) (map.getSizePixels().x - sprite.getWidth()));
		float yPos = (float) Utils.randInt(0, (int) (map.getSizePixels().y - sprite.getHeight()));
		return new Vector2(xPos, yPos);
	}
	
	//for fitting movement stats to a nicer 1 - 11 range instead of their true float values
	//true value ranges:
	//		max speed 	0.8 - 3		 	
	//		accel		0.02 - 0.2	
	//		turning		1 - 6		
	//		range		4 - 9		
	//		damage		1 - 10 	
	
	//for scaling a number x in a range [min, max] to a different range [a, b] we can use
	
//		   (b-a)(x - min)
//	f(x) = --------------  + a
//			  max - min
	
	
	public static int statToView(float val, char stat) {
		float min = 0;
		float max = 0;
		float a = 1;
		float b = 10;
		
		switch(stat) {
			case 'm':
				min = 0.8f;
				max = 3;
				break;
			case 'a':
				min = 0.02f;
				max = 0.2f;
				break;
			case 't':
				min = 1;
				max = 6;
				break;
			case 'r':
				min = 4;
				max = 9;
				break;
			case 'd':
				min = 1;
				max = 10;
				break;
			default:
				System.out.println("You passed a weird stat character to Utils.statToView() and you're probably about to get an error");
		}
		
		return (int) ((((b - a) * (val - min))/(max - min)) + a);
	}
	
	public static float statToUse(int val, char stat) {
		float min = 1;
		float max = 10;
		float a = 0;
		float b = 0;
		
		switch(stat) {
		case 'm':
			a = 0.8f;
			b = 3;
			break;
		case 'a':
			a = 0.02f;
			b = 0.2f;
			break;
		case 't':
			a = 1;
			b = 6;
			break;
		case 'r':
			a = 4;
			b = 9;
			break;
		case 'd':
			a = 1;
			b = 10;
			break;
		default:
			System.out.println("You passed a weird stat character to Utils.statToView() and you're probably about to get an error");
		}
		
		return ((((b - a) * (val - min))/(max - min)) + a);
	}
	
	public static float scaleToRange(float input, float inMin, float inMax, float outMin, float outMax) {
		return ((((outMax - outMin) * (input - inMin))/(inMax - inMin)) + outMin);
	}
}
