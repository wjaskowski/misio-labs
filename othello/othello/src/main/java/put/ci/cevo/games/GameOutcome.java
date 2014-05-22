package put.ci.cevo.games;

import java.util.Objects;

public class GameOutcome {

	public final double blackPlayerPoints;
	public final double whitePlayerPoints;

	public GameOutcome(double blackPlayerPoints, double whitePlayerPoints) {
		this.blackPlayerPoints = blackPlayerPoints;
		this.whitePlayerPoints = whitePlayerPoints;
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
		GameOutcome o = (GameOutcome) obj;
		return blackPlayerPoints == o.blackPlayerPoints && whitePlayerPoints == o.whitePlayerPoints;
	}

	@Override
	public int hashCode() {
		return Objects.hash(blackPlayerPoints, whitePlayerPoints);
	}

	@Override
	public String toString() {
		return "o(" + blackPlayerPoints + "," + whitePlayerPoints + ")";
	}
}
