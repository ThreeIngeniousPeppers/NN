package org.NN10min;

import org.encog.Encog;

import java.io.File;


/**
 * Use the saved market neural network, and now attempt to predict for today, and the
 * last 60 days and see what the results are.
 */
public class MarketPredict {
		
	public static void main(String[] args)
	{		
		if( args.length<1 ) {
			System.out.println("MarketPredict [data dir] [generate/train/incremental/evaluate]");
		}
		else
		{
			File dataDir = new File(args[0]);
			if( args[1].equalsIgnoreCase("generate") ) {
				MarketBuildTraining.generate(dataDir);
			} 
			else if( args[1].equalsIgnoreCase("train") ) {
				MarketTrain.train(dataDir);
			} 
			else if( args[1].equalsIgnoreCase("evaluate") ) {
				MarketEvaluate.evaluate(dataDir);
			} else if( args[1].equalsIgnoreCase("prune") ) {
				MarketPrune.incremental(dataDir);
			}
			Encog.getInstance().shutdown();
		}
	}
	
}
