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
            // Enhanced Radar Management
            turnRadarRight(360);

            // Continuous Movement with Variation
            ahead(100 * movementDirection);
            double changeInEnergy = previousEnergy - getEnergy();
            if (changeInEnergy > 0 && changeInEnergy <= 3) {
                // Advanced Evasion on being hit
                movementDirection *= -1;
                ahead(150 * movementDirection);
                turnRight(45 * movementDirection); // Adding randomness to the turn
            }
            previousEnergy = getEnergy();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
    double firePower = Math.min(500 / e.getDistance(), Math.max(1, getEnergy() / 15));
    
    // Calculating radar turn in degrees
    double radarTurn = getHeading() + e.getBearing() - getRadarHeading();
    // Ensure the radar turn is within the -180 to 180 range
    radarTurn = normalizeBearing(radarTurn);
    
    turnRadarRight(radarTurn);
    
    // Aim and fire at the calculated future position
    turnGunRight(normalizeBearing(e.getBearing() + getHeading() - getGunHeading()));
    fire(firePower);
}

    public void onHitByBullet(HitByBulletEvent e) {
        // Enhanced Evasion
        double bearing = e.getBearing(); // Get the bearing of the incoming bullet
        if (Math.random() > 0.5) {
            turnRight(normalizeBearing(bearing + 90 - (30 * Math.random())));
        } else {
            turnRight(normalizeBearing(bearing + 90 + (30 * Math.random())));
        }
        ahead((150 + (Math.random() * 50)) * movementDirection);
    }

    public void onHitWall(HitWallEvent e) {
        // Improved Wall Avoidance
        movementDirection *= -1;
        ahead(100 * movementDirection);
    }

    double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    double normalizeBearingRadians(double radians) {
        while (radians > Math.PI) radians -= 2 * Math.PI;
        while (radians < -Math.PI) radians += 2 * Math.PI;
        return radians;
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
