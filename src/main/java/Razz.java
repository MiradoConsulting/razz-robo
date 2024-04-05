import robocode.*;
import java.awt.Color;
import java.awt.geom.Point2D;

public class Razz extends Robot {
    double previousEnergy = 100;
    int movementDirection = 1;
    int gunDirection = 1;

    public void run() {
        setColors(Color.red, Color.blue, Color.green); // body, gun, radar

        while (true) {
            turnGunRight(360 * gunDirection);

            // Enhanced Movement
            ahead(100 * movementDirection);
            double changeInEnergy = previousEnergy - getEnergy();
            if (changeInEnergy > 0 && changeInEnergy <= 3) {
                // Enhanced evasion
                movementDirection = -movementDirection;
                ahead(150 * movementDirection);
                turnRight(45 * movementDirection); // Zigzag movement
            }
            previousEnergy = getEnergy();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double firePower = Math.min(500 / e.getDistance(), 3);
        double bulletSpeed = 20 - firePower * 3;
        long time = (long)(e.getDistance() / bulletSpeed);
        
        // Predictive Targeting Enhancements
        double futureX = getX() + e.getDistance() * Math.sin(e.getBearingRadians() + getHeading());
        double futureY = getY() + e.getDistance() * Math.cos(e.getBearingRadians() + getHeading());
        double absoluteDegree = absoluteBearing(getX(), getY(), futureX, futureY);
        
        turnGunRight(normalizeBearing(absoluteDegree - getGunHeading()));
        fire(firePower);
        
        // Adjust gunDirection for radar
        gunDirection = -gunDirection;
        scan(); // Keep scanning for other robots
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // Enhanced Evasion
        movementDirection = -movementDirection;
        ahead(150 * movementDirection);
        turnRight(45 * movementDirection); // Add turn to evade
    }

    public void onHitWall(HitWallEvent e) {
        // Improved Wall Avoidance
        movementDirection = -movementDirection;
        ahead(150 * movementDirection);
    }

    double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2 - x1;
        double yo = y2 - y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) {
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) {
            bearing = 360 + arcSin;
        } else if (xo > 0 && yo < 0) {
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) {
            bearing = 180 - arcSin;
        }

        return bearing;
    }
}
