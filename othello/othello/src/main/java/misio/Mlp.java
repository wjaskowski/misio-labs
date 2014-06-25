package misio;

import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import java.math.BigDecimal;

public class Mlp {
	
	public static class Layer {
		
		public class Neuron {

			// main constructor
			public Neuron(int prev_n_neurons, java.util.Random rand) {
				// each neuron know the weights of each connection
				// with neurons of the previous layer
				_synapticWeights = new double[prev_n_neurons];

				// set default weights
				for (int i = 0; i < prev_n_neurons; ++i)
					_synapticWeights[i] = rand.nextDouble() - 0.5f;
			}

			// activate the neuron with given inputs, return the output
			public double activate(double inputs[]) {
				_activation = 0.0f;
				assert (inputs.length == _synapticWeights.length);

				for (int i = 0; i < inputs.length; ++i)
					// dot product (produit scalaire)
					_activation += inputs[i] * _synapticWeights[i];

				// phi(_activation), our activation function (tanh(x))
				//double result = 2.0f / (1.0f + (double) Math.exp((-_activation) * lambda)) - 1.0f;
				double result = 1.0f / (1.0f + (double) Math.exp((-_activation)));
				return result;
			}

			public double getActivationDerivative() // dphi(_activation)
			{
				double expmlx = (double) Math.exp(_activation);
				return - expmlx/((expmlx + 1) * (expmlx + 1));
				//double expmlx = (double) Math.exp(lambda * _activation);
				//return 2 * lambda * expmlx / ((1 + expmlx) * (1 + expmlx));
			}

			public double[] getSynapticWeights() {
				return _synapticWeights;
			}

			public double getSynapticWeight(int i) {
				return _synapticWeights[i];
			}

			public void setSynapticWeight(int i, double v) {
				_synapticWeights[i] = v;
			}

			// --------
			private double _activation;
			private double[] _synapticWeights;

			// parameter of the sigmoid
			static final double lambda = 1.5f;
		}

		// main constructor
		public Layer(int prev_n_neurons, int n_neurons, java.util.Random rand) {
			// all the layers/neurons must use the same random number generator
			_n_neurons = n_neurons + 1;
			_prev_n_neurons = prev_n_neurons + 1;

			// allocate everything
			_neurons = new ArrayList<Neuron>();
			_outputs = new double[_n_neurons];

			for (int i = 0; i < _n_neurons; ++i)
				_neurons.add(new Neuron(_prev_n_neurons, rand));
		}

		// add 1 in front of the out vector
		public static double[] add_bias(double[] in) {
			double out[] = new double[in.length + 1];
			for (int i = 0; i < in.length; ++i)
				out[i + 1] = in[i];
			out[0] = 1.0f;
			return out;
		}

		// compute the output of the layer
		public double[] evaluate(double in[]) {
			double inputs[];

			// add an input (bias) if necessary
			if (in.length != getWeights(0).length)
				inputs = add_bias(in);
			else
				inputs = in;

			assert (getWeights(0).length == inputs.length);

			// stimulate each neuron of the layer and get its output
			for (int i = 1; i < _n_neurons; ++i)
				_outputs[i] = _neurons.get(i).activate(inputs);

			// bias treatment
			_outputs[0] = 1.0f;

			return _outputs;
		}

		public int size() {
			return _n_neurons;
		}

		public double getOutput(int i) {
			return _outputs[i];
		}

		public double getActivationDerivative(int i) {
			return _neurons.get(i).getActivationDerivative();
		}

		public double[] getWeights(int i) {
			return _neurons.get(i).getSynapticWeights();
		}

		public double getWeight(int i, int j) {
			return _neurons.get(i).getSynapticWeight(j);
		}

		public void setWeight(int i, int j, double v) {
			_neurons.get(i).setSynapticWeight(j, v);
		}

		// --------
		private int _n_neurons, _prev_n_neurons;
		private ArrayList<Neuron> _neurons;
		private double _outputs[];
	}


	// main constructor
	public Mlp(int nn_neurons[]) {
		Random rand = new Random();

		// create the required layers
		_layers = new ArrayList<Layer>();
		for (int i = 0; i < nn_neurons.length; ++i)
			_layers.add(new Layer(i == 0 ? nn_neurons[i] : nn_neurons[i - 1],
					nn_neurons[i], rand));

		_delta_w = new ArrayList<double[][]>();
		for (int i = 0; i < nn_neurons.length; ++i)
			_delta_w.add(new double[_layers.get(i).size()][_layers.get(i)
					.getWeights(0).length]);

		_grad_ex = new ArrayList<double[]>();
		for (int i = 0; i < nn_neurons.length; ++i)
			_grad_ex.add(new double[_layers.get(i).size()]);
	}

	public double[] evaluate(double[] inputs) {
		// propagate the inputs through all neural network
		// and return the outputs
		assert (false);

		double outputs[] = new double[inputs.length];

		for (int i = 0; i < _layers.size(); ++i) {
			outputs = _layers.get(i).evaluate(inputs);
			inputs = outputs;
		}

		return outputs;
	}

	private double evaluateError(double nn_output[], double desired_output[]) {
		double d[];

		// add bias to input if necessary
		if (desired_output.length != nn_output.length)
			d = Layer.add_bias(desired_output);
		else
			d = desired_output;

		assert (nn_output.length == d.length);

		double e = 0;
		for (int i = 0; i < nn_output.length; ++i)
			e += (nn_output[i] - d[i]) * (nn_output[i] - d[i]);

		return e;
	}

	public double evaluateQuadraticError(ArrayList<double[]> examples,
			ArrayList<double[]> results) {
		// this function calculate the quadratic error for the given
		// examples/results sets
		assert (false);

		double e = 0;

		for (int i = 0; i < examples.size(); ++i) {
			e += evaluateError(evaluate(examples.get(i)), results.get(i));
		}

		return e;
	}

	private void evaluateGradients(double[] results) {
		// for each neuron in each layer
		for (int c = _layers.size() - 1; c >= 0; --c) {
			for (int i = 0; i < _layers.get(c).size(); ++i) {
				// if it's output layer neuron
				if (c == _layers.size() - 1) {
					_grad_ex.get(c)[i] = 2
							* (_layers.get(c).getOutput(i) - results[0])
							* _layers.get(c).getActivationDerivative(i);
				} else { // if it's neuron of the previous layers
					double sum = 0;
					for (int k = 1; k < _layers.get(c + 1).size(); ++k)
						sum += _layers.get(c + 1).getWeight(k, i)
								* _grad_ex.get(c + 1)[k];
					_grad_ex.get(c)[i] = _layers.get(c)
							.getActivationDerivative(i) * sum;
				}
			}
		}
	}

	private void resetWeightsDelta() {
		// reset delta values for each weight
		for (int c = 0; c < _layers.size(); ++c) {
			for (int i = 0; i < _layers.get(c).size(); ++i) {
				double weights[] = _layers.get(c).getWeights(i);
				for (int j = 0; j < weights.length; ++j)
					_delta_w.get(c)[i][j] = 0;
			}
		}
	}

	private void evaluateWeightsDelta() {
		// evaluate delta values for each weight
		for (int c = 1; c < _layers.size(); ++c) {
			for (int i = 0; i < _layers.get(c).size(); ++i) {
				double weights[] = _layers.get(c).getWeights(i);
				for (int j = 0; j < weights.length; ++j)
					_delta_w.get(c)[i][j] += _grad_ex.get(c)[i]
							* _layers.get(c - 1).getOutput(j);
			}
		}
	}

	private void updateWeights(double learning_rate) {
		for (int c = 0; c < _layers.size(); ++c) {
			for (int i = 0; i < _layers.get(c).size(); ++i) {
				double weights[] = _layers.get(c).getWeights(i);
				for (int j = 0; j < weights.length; ++j)
					_layers.get(c).setWeight(
							i,
							j,
							_layers.get(c).getWeight(i, j)
									- (learning_rate * _delta_w.get(c)[i][j]));
			}
		}
	}

	public void batchBackPropagation(ArrayList<double[]> examples,
			ArrayList<double[]> results, double learning_rate) {
		resetWeightsDelta();

		for (int l = 0; l < examples.size(); ++l) {
			evaluate(examples.get(l));
			evaluateGradients(results.get(l));
			evaluateWeightsDelta();
		}

		updateWeights(learning_rate);
	}

	public void learn(ArrayList<double[]> examples, ArrayList<double[]> results,
			double learning_rate) {
		// this function implements a batched back propagation algorithm
		assert (false);

		double e = Double.POSITIVE_INFINITY;

		while (e > 0.001f) {

			batchBackPropagation(examples, results, learning_rate);

			e = evaluateQuadraticError(examples, results);
		}
	}
	
	public void printWeights() {
		for (int c = 0; c < _layers.size(); ++c) {
			FileWriter fstream;
			try {
				fstream = new FileWriter("/home/katarzyn/layerWeights" + (c+1), false);
				BufferedWriter out = new BufferedWriter(fstream);
				
				for (int i = 0; i < _layers.get(c).size(); ++i) {
					double weights[] = _layers.get(c).getWeights(i);
					for (int j = 0; j < weights.length; ++j) {
						BigDecimal bd = new BigDecimal(_layers.get(c).getWeight(i, j));
						if(j==weights.length-1 && i==_layers.get(c).size()-1) {
							out.write(bd.toString());
						} else {
							out.write(bd.toString() + ", ");
						}
					}
				}
				
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("");
	}
	
	public void setLayersWeights(ArrayList<double[]> layersWeights) {
		for (int c = 0; c < _layers.size(); ++c) {
			double[] currLayersWeights = layersWeights.get(c);
			int w = 0;
			for (int i = 0; i < _layers.get(c).size(); ++i) {
				for (int j = 0; j < _layers.get(c).getWeights(i).length; ++j) {
					_layers.get(c).setWeight(i, j, currLayersWeights[w]);
					w++;
				}
			}
		}
	}

	private ArrayList<Layer> _layers;
	private ArrayList<double[][]> _delta_w;
	private ArrayList<double[]> _grad_ex;
}