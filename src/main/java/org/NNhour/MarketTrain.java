package org.NNhour;

import org.encog.Encog;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

import java.io.File;

public class MarketTrain {

	public static void train(File dataDir) {

		final File networkFile = new File(dataDir, Config.NETWORK_FILE);
		final File trainingFile = new File(dataDir, Config.TRAINING_FILE);

		// network file
		if (!networkFile.exists()) {
			System.out.println("Can't read file: " + networkFile.getAbsolutePath());
			return;
		}
		
		BasicNetwork network = (BasicNetwork)EncogDirectoryPersistence.loadObject(networkFile);

		// training file
		if (!trainingFile.exists()) {
			System.out.println("Can't read file: " + trainingFile.getAbsolutePath());
			return;
		}
		
		final MLDataSet trainingSet = EncogUtility.loadEGB2Memory(trainingFile);

		// train the neural network
		EncogUtility.trainConsole(network, trainingSet, Config.TRAINING_MINUTES);
						
		System.out.println("Final Error: " + network.calculateError(trainingSet));
		System.out.println("Training complete, saving network.");
		EncogDirectoryPersistence.saveObject(networkFile, network);
		System.out.println("Network saved.");
		
		Encog.getInstance().shutdown();

	}
}
