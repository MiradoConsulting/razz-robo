import robocode.*;
import java.awt.Color;
import java.awt.geom.Point2D;

public class Razz extends Robot {
    double previousEnergy = 100;
    int movementDirection = 1;
    int gunDirection = 1;

    public void run() {
        setColors(Color.red, Color.blue, Color.green); // body,gun,radar

        while (true) {
            turnGunRight(360 * gunDirection);

            // Change movementDirection if we've seen a drop in energy.
            double changeInEnergy = previousEnergy - getEnergy();
            if (changeInEnergy > 0 && changeInEnergy <= 3) {
                movementDirection = -movementDirection;
                ahead((Math.random() * 100 + 100) * movementDirection);
            }
            previousEnergy = getEnergy();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double firePower = Math.min(500 / e.getDistance(), 3);
        double bulletSpeed = 20 - firePower * 3;
        long time = (long)(e.getDistance() / bulletSpeed); // Calculates how long the bullet will take to reach the target
        double futureX = e.getVelocity() * Math.sin(e.getHeadingRadians()) * time; // Predicts the future x coordinate of the enemy
        double futureY = e.getVelocity() * Math.cos(e.getHeadingRadians()) * time; // Predicts the future y coordinate of the enemy
        double absoluteDegree = absoluteBearing(getX(), getY(), futureX, futureY);
        turnGunRight(normalizeBearing(absoluteDegree - getGunHeading()));

        fire(firePower);
        gunDirection = -gunDirection;
        scan(); // Might catch another robot while waiting for the bullet to travel
    }

    public void onHitByBullet(HitByBulletEvent e) {
        movementDirection = -movementDirection;
        ahead((Math.random() * 100 + 100) * movementDirection);
    }

    public void onHitWall(HitWallEvent e) {
        movementDirection = -movementDirection;
        ahead((Math.random() * 100 + 100) * movementDirection);
    }

    // Normalizes a bearing to between +180 and -180
    double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    // Calculates the absolute bearing between two points
    double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2 - x1;
        double yo = y2 - y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }
}
