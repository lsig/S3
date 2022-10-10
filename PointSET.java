/****************************************************************************
 *  Compilation:  javac PointSET.java
 *  Execution:
 *  Dependencies:
 *  Author:
 *  Date:
 *
 *  Data structure for maintaining a set of 2-D points,
 *    including rectangle and nearest-neighbor queries
 *
 *************************************************************************/

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;


public class PointSET {
    private int size;
    private SET<Point2D> pointSet;

    // construct an empty set of points
    public PointSET() {
        this.pointSet = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return this.size == 0;
    }

    // number of points in the set
    public int size() {
        return this.size;
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (!contains(p)) {
            pointSet.add(p);
            this.size++;
        }
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return pointSet.contains(p);
    }

    // draw all of the points to standard draw
    public void draw() {
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        SET<Point2D> pointsInRectangle = new SET<Point2D>();
        for (Point2D point : this.pointSet) {
            if (rect.contains(point)) {
                pointsInRectangle.add(point);
            }
        }
        return pointsInRectangle;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        if (isEmpty()) return null;
        Point2D champion = this.pointSet.max();
        double championDistance = Double.POSITIVE_INFINITY;
        for (Point2D point : this.pointSet) {
            double newDistance = p.distanceSquaredTo(point);
            if (newDistance < championDistance) {
                championDistance = newDistance;
                champion = point;
            }
        }
        return champion;
    }

    public static void main(String[] args) {
    }

}
