package org.NN10min;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.market.MarketDataDescription;
import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.MarketMLDataSet;
import org.encog.ml.data.market.loader.MarketLoader;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.time.TimeUnit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MarketEvaluate {

	enum Direction {
		up, down
	};

	public static Direction determineDirection(double d) {
		if (d < 0)
			return Direction.down;
		else
			return Direction.up;
	}

	public static MarketMLDataSet grabData() {
		MarketLoader loader = new CryptoCompareLoader();
		MarketMLDataSet result = new MarketMLDataSet(loader,
				Config.INPUT_WINDOW, Config.PREDICT_WINDOW);
		result.setSequenceGrandularity(TimeUnit.MINUTES);
		MarketDataDescription desc = new MarketDataDescription(Config.TICKER,
				MarketDataType.CLOSE, true, true);
		result.addDescription(desc);

		Calendar end = new GregorianCalendar();// end today
		Calendar begin = (Calendar) end.clone();// begin 30 days ago
		begin.add(Calendar.HOUR, -1);

		result.load(begin.getTime(), end.getTime());
		result.generate();

		return result;

	}

	public static void evaluate(File dataDir) {

		File file = new File(dataDir, Config.NETWORK_FILE);

		if (!file.exists()) {
			System.out.println("Can't read file: " + file.getAbsolutePath());
			return;
		}

		BasicNetwork network = (BasicNetwork)EncogDirectoryPersistence.loadObject(file);	

		MarketMLDataSet data = grabData();


		DecimalFormat format = new DecimalFormat("#0.0000");

		try {
            PrintWriter outcsv = new PrintWriter("plots/outmin.csv");
            outcsv.println("time,actual,predicted");


            int count = 0;
            int correct = 0;
            double curActualVal = data.getPoints().get(0).getData(0);
            double curPredictedVal = data.getPoints().get(0).getData(0);
            outcsv.println(0 + "," + curActualVal + "," + curPredictedVal);
            System.out.println(data.getSequenceGrandularity());
            for (MLDataPair pair : data) {
                MLData input = pair.getInput();
                MLData actualData = pair.getIdeal();
                MLData predictData = network.compute(input);

                double actual = actualData.getData(0);
                double predict = predictData.getData(0);
                double diff = Math.abs(predict - actual);

                Direction actualDirection = determineDirection(actual);
                Direction predictDirection = determineDirection(predict);

                if (actualDirection == predictDirection)
                    correct++;

                count++;

                System.out.println("Day " + count + ":actual="
                        + format.format(actual) + "(" + actualDirection + ")"
                        + ",predict=" + format.format(predict) + "("
                        + predictDirection + ")" + ",diff=" + diff);

                curActualVal += actual;
                curPredictedVal += predict;
                outcsv.println(count + "," + (curActualVal) + "," + (curPredictedVal));

            }
            double percent = (double) correct / (double) count;
            System.out.println("Direction correct:" + correct + "/" + count);
            System.out.println("Directional Accuracy:"
                    + format.format(percent * 100) + "%");
            outcsv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

	}
}
