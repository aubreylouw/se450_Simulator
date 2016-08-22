package simulator.moveable;

import java.awt.Color;
import java.math.BigDecimal;

/**
 * A physical presence with the following attributes:
 * 	- an orientation (one of the cardinal directions)
 *  - its rear-most position on a 1D number line
 *  - its front-most position on a 1D number line
 *  
 */
public interface Moveable {
	public BigDecimal currentRearPosition();
	public BigDecimal currentFrontPosition();
	public Orientation currentOrientation();
	public MoveableStatus move(double speedGovernor);
	public BigDecimal maxVelocity();
	public BigDecimal brakeDistance();
	public BigDecimal stopDistance();
	public BigDecimal length();
	public Color color();
}