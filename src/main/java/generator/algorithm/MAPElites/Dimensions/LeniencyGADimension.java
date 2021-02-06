package generator.algorithm.MAPElites.Dimensions;

import java.util.List;


import finder.PatternFinder;
import finder.patterns.CompositePattern;
import finder.patterns.meso.ChokePoint;
import finder.patterns.meso.DeadEnd;
import finder.patterns.meso.GuardedTreasure;
import game.Room;
import game.Tile;
import game.tiles.BossEnemyTile;
import generator.algorithm.ZoneIndividual;

public class LeniencyGADimension extends GADimension {

	double patternMultiplier = 4.0; 
	
	public LeniencyGADimension(float granularity)
	{
		super();
		dimension = DimensionTypes.LENIENCY;
		this.granularity = granularity;
	}

	@Override
	public double CalculateValue(ZoneIndividual individual, Room target) 
	{
		Room individualRoom = individual.getPhenotype().getMap(-1, -1, null, null, null);
		
		//needed values to be calculated
		individualRoom.getPatternFinder().findMicroPatterns();
		individualRoom.calculateEnemyDensitySparsity();
		individualRoom.calculateTreasureDensitySparsity();
		individualRoom.calculateDoorSafeness();
		
		//weights for the sum
		double w1 = 0.3;
		double w2 = 0.3;
		double w3 = 0.4;
		
		//TODO: Problem with the log10 when the enemy or treasure count is 0
		//Starting value
		double starting_value = 1.0;
		for( Tile t : individualRoom.customTiles)
		{
			if(t instanceof BossEnemyTile)
			{
				starting_value *= 0.5;
			}
		}
		
		
		//If we have no enemies the room shouldn't be challenging nor non lenient
		//unless we have bossess
		if(individualRoom.getEnemyCount() == 0)
			return starting_value;
		
		//Independent calculation
		double a = w1 *  (Math.log10(individualRoom.getEnemyCount()) * individualRoom.calculateEnemySparsity());
		double b = w2 * (Math.log10(individualRoom.getEnemyCount()) * individualRoom.calculateEnemyDensity());
		double c = w3 * (1.0 - individualRoom.getDoorSafeness());
		
		//Treasure calculation to increase leniency!
		double d = 0.5 *  (Math.log10(individualRoom.getTreasureCount()) * individualRoom.calculateTreasureSparsity());
		double f = 0.5 * (Math.log10(individualRoom.getTreasureCount()) * individualRoom.calculateTreasureDensity());
		
		//If there are no treasures their calculations shouldn't be considered
		if(individualRoom.getTreasureCount() == 0)
		{
			d = 0.0;
			f = 0.0;
		}
		
		//Final Leniency
		return (1.0 - ((1.0 * (a + b + c)) - (0.5 * (d+f))))*starting_value;
//
//		double traversableWeight = 0.1;
//		double doorsSafetyWeight = 0.45;
//		double generalDangerWeight = 0.45;
//		double enemyToleranceThreshold = 1.0;
//		
////		double enemies = individualRoom.getEnemyCount();
////		double logEnemyCount = Math.log(enemies);
////		double enemySparse = individualRoom.calculateEnemySparsity();
////		
////		double combo = logEnemyCount * enemySparse;
//		double dangerRate = 0.0;
//		
//		if(individualRoom.getEnemyCount() != 0)
//			dangerRate = Math.min(1.0, (0.5* individualRoom.calculateEnemySparsity() + 0.5* individualRoom.calculateEnemyDensity()));
//		
////		 Math.log10(individualRoom.getEnemyCount()) *
//		individualRoom.calculateDoorSafeness();
//		
//		double v = individualRoom.getDoorSafeness();
//
//		double firstValue = (traversableWeight * individualRoom.emptySpacesRate());
//		double secondValue = (doorsSafetyWeight * (1.0 - individualRoom.getDoorSafeness()));
////		double thirdValue = (generalDangerWeight * (individualRoom.emptySpacesRate() - dangerRate));
//		double thirdValue = (generalDangerWeight * (dangerRate));
//		double enemyTolerance = traversableWeight * (enemyToleranceThreshold - Math.log10(individualRoom.getEnemyCount()));
//		
////		return Math.min(1.0, 1.0 - (firstValue + secondValue - thirdValue));
//		
//		double resultA = 1.0 - (firstValue + secondValue - thirdValue);
//		double resultB = 1.0 - (firstValue + secondValue + thirdValue);
//		double resultA_e = 1.0 - (firstValue + secondValue - (generalDangerWeight * (individualRoom.emptySpacesRate() - dangerRate)));
//		double resultB_e = 1.0 - (firstValue + secondValue + (generalDangerWeight * (individualRoom.emptySpacesRate() - dangerRate)));
//		double resultC = 1.0 - (secondValue + thirdValue + enemyTolerance);
//		
//		return Math.min(1.0, resultC);

	}

	@Override
	public double CalculateValue(Room individualRoom, Room target) {

		//needed values to be calculated
		individualRoom.getPatternFinder().findMicroPatterns();
		individualRoom.calculateEnemyDensitySparsity();
		individualRoom.calculateTreasureDensitySparsity();
		individualRoom.calculateDoorSafeness();
		
		//weights for the sum
		double w1 = 0.3;
		double w2 = 0.3;
		double w3 = 0.4;
		
		//Starting value
		double starting_value = 1.0;
		for( Tile t : individualRoom.customTiles)
		{
			if(t instanceof BossEnemyTile)
			{
				starting_value *= 0.5;
			}
		}
		
		
		//If we have no enemies the room shouldn't be challenging nor non lenient
		//unless we have bossess
		if(individualRoom.getEnemyCount() == 0)
			return starting_value;
		
		//Independent calculation
		double a = w1 *  (Math.log10(individualRoom.getEnemyCount()) * individualRoom.calculateEnemySparsity());
		double b = w2 * (Math.log10(individualRoom.getEnemyCount()) * individualRoom.calculateEnemyDensity());
		double c = w3 * (1.0 - individualRoom.getDoorSafeness());
		
		//Treasure calculation to increase leniency!
		double d = 0.5 *  (Math.log10(individualRoom.getTreasureCount()) * individualRoom.calculateTreasureSparsity());
		double f = 0.5 * (Math.log10(individualRoom.getTreasureCount()) * individualRoom.calculateTreasureDensity());
		
		//If there are no treasures their calculations shouldn't be considered
		if(individualRoom.getTreasureCount() == 0)
		{
			d = 0.0;
			f = 0.0;
		}
		
		//Final Leniency
		return (1.0 - ((1.0 * (a + b + c)) - (0.5 * (d+f))))*starting_value;
	}
	
	public static double getValue(Room individualRoom)
	{
		//needed values to be calculated
		individualRoom.getPatternFinder().findMicroPatterns();
		individualRoom.calculateEnemyDensitySparsity();
		individualRoom.calculateTreasureDensitySparsity();
		individualRoom.calculateDoorSafeness();
		
		//weights for the sum
		double w1 = 0.3;
		double w2 = 0.3;
		double w3 = 0.4;
		
		//Starting value
		double starting_value = 1.0;
		for( Tile t : individualRoom.customTiles)
		{
			if(t instanceof BossEnemyTile)
			{
				starting_value *= 0.5;
			}
		}
		
		
		//If we have no enemies the room shouldn't be challenging nor non lenient
		//unless we have bossess
		if(individualRoom.getEnemyCount() == 0)
			return starting_value;
		
		//Independent calculation
		double a = w1 *  (Math.log10(individualRoom.getEnemyCount()) * individualRoom.calculateEnemySparsity());
		double b = w2 * (Math.log10(individualRoom.getEnemyCount()) * individualRoom.calculateEnemyDensity());
		double c = w3 * (1.0 - individualRoom.getDoorSafeness());
		
		//Treasure calculation to increase leniency!
		double d = 0.5 *  (Math.log10(individualRoom.getTreasureCount()) * individualRoom.calculateTreasureSparsity());
		double f = 0.5 * (Math.log10(individualRoom.getTreasureCount()) * individualRoom.calculateTreasureDensity());
		
		//If there are no treasures their calculations shouldn't be considered
		if(individualRoom.getTreasureCount() == 0)
		{
			d = 0.0;
			f = 0.0;
		}
		
		//Final Leniency
		return (1.0 - ((1.0 * (a + b + c)) - (0.5 * (d+f))))*starting_value;
	}
}
