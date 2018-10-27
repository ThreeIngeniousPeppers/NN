import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import java.util.Scanner;

/**
 * XOR: This example is essentially the "Hello World" of neural network
 * programming.  This example shows how to construct an Encog neural
 * network to predict the output from the XOR operator.  This example
 * uses backpropagation to train the neural network.
 * 
 * This example attempts to use a minimum of Encog features to create and
 * train the neural network.  This allows you to see exactly what is going
 * on.  For a more advanced example, that uses Encog factories, refer to
 * the XORFactory example.
 * 
 */
public class HelloWorld {

	private static NormalizedField normSum = new NormalizedField(NormalizationAction.Normalize,
            "Sum", 5.0, 0.0, 1.0, 0.0);
	/** normSum.normalize(4) --> number between 0 and 1
	 * The input necessary for XOR.
	 */
	public static double XOR_INPUT[][] = { { 1.0, 1.0 }, { 2.0, 3.0 },
        { 0.0, 1.0 }, { 1.0, 3.0 }, { 2.2, 2.2 }, { 1.7, 1.3 } };
 
	/**
	 * The ideal data necessary for XOR.
	 */
	public static double XOR_IDEAL[][] = { { 2.0 }, { 5.0 }, { 1.0 }, { 4.0 }, { 4.4 }, { 3.0 } };
 
	/**
	 * The main method.
	 * @param args No arguments are used.
	 */
	public static void main(final String args[]) {

	    double NORMALIZED_XOR_INPUT[][] = new double[XOR_INPUT.length][XOR_INPUT[0].length];
	    for (int i=0;i<XOR_INPUT.length;i++) {
            for (int j=0;j<XOR_INPUT[0].length;j++)
                NORMALIZED_XOR_INPUT[i][j] = normSum.normalize(XOR_INPUT[i][j]);
        }

        double NORMALIZED_XOR_IDEAL[][] = new double[XOR_IDEAL.length][XOR_IDEAL[0].length];
        for (int i=0;i<XOR_IDEAL.length;i++) {
            for (int j=0;j<XOR_IDEAL[0].length;j++)
                NORMALIZED_XOR_IDEAL[i][j] = normSum.normalize(XOR_IDEAL[i][j]);
        }
 
		// create a neural network, without using a factory
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
		network.getStructure().finalizeStructure();
		network.reset();
 
		// create training data
		MLDataSet trainingSet = new BasicMLDataSet(NORMALIZED_XOR_INPUT, NORMALIZED_XOR_IDEAL);
 
		// train the neural network
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
 
		int epoch = 1;
 
		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while(train.getError() > 0.001);
		train.finishTraining();

		// test the neural network
		System.out.println("Neural Network Results:");
		for(MLDataPair pair: trainingSet ) {
			final MLData output = network.compute(pair.getInput());
			System.out.println(pair.getInput().getData(0) + ", " + pair.getInput().getData(1)
					+ ", actual=" + normSum.deNormalize(output.getData(0)) + ", ideal="
                    + normSum.deNormalize(pair.getIdeal().getData(0)));
		}


//		Scanner sc = new Scanner(System.in);
//		double[][] arr_in = new double[10][2];
//		double[][] arr_out = new double[10][1];
//		int y = 0;
//		while(y < 10) {
//		    for (int i = 0; i < 10; i++) {
//                arr_in[i][0] = sc.nextDouble();
//                arr_in[i][1] = sc.nextDouble();
//                arr_out[i][0] = 0;
//            }
//            double[][] norm_arr = new double[10][2];
//            for (int i=0;i < 10;i++)  {
//                for (int j=0;j < 2;j++)
//                    norm_arr[i][j] = normSum.normalize(arr_in[i][j]);
//            }
//            MLDataSet testSet = new BasicMLDataSet(norm_arr, arr_out);
//            for(MLDataPair pair: testSet ) {
//                final MLData output = network.compute(pair.getInput());
//                System.out.println(pair.getInput().getData(0) + ", " + pair.getInput().getData(1)
//                        + ", actual=" + normSum.deNormalize(output.getData(0)));
//            }
//            y++;
//        }


		Encog.getInstance().shutdown();
	}
}
