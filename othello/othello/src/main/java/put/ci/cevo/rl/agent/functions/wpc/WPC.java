package put.ci.cevo.rl.agent.functions.wpc;

import java.io.Serializable;
import java.util.Arrays;

public class WPC implements Serializable {

	private static final long serialVersionUID = 8899559906200648797L;

	private final double weights[];

	public WPC(int numWeights) {
		this.weights = new double[numWeights];
	}

	public WPC(double weights[]) {
		this.weights = weights.clone();
	}

	public double[] getWeights() {
		return weights.clone();
	}

	public double get(int idx) {
		return weights[idx];
	}

	public int getSize() {
		return weights.length;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(weights);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		WPC other = (WPC) obj;
		return Arrays.equals(weights, other.weights);
	}

	@Override
	public String toString() {
		String s = "[" + weights.length + "]";
		for (int i = 0; i < weights.length; ++i) {
			s += String.format(" %.2f,", weights[i]);
		}
		return s;
	}
}
