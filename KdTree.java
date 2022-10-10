/*************************************************************************
 *************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.Arrays;

public class KdTree {
    private Node root;

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node left;
        private Node right;
        private int size;

        public Node(Point2D p, int size) {
            this.p = p;
            this.size = size;
        }
    }


    // construct an empty set of points
    public KdTree() {
    }

    // is the set empty?
    public boolean isEmpty() {
        return this.size() == 0;
    }

    // number of points in the set
    public int size() {
        return root.size;
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (!contains(p)) root = insert(root, p, 0);
    }

    private Node insert(Node x, Point2D p, int rank) {
        double cmp = 0;
        if (x == null) return new Node(p, 1);
        if (rank % 2 == 0) cmp = p.x() - x.p.x();
        else if (rank % 2 == 1) cmp = p.y() - x.p.y();
        if (cmp < 0) x.left = insert(x.left, p, rank + 1);
        else if (cmp >= 0) x.right = insert(x.right, p, rank + 1);
        x.size = 1 + x.left.size + x.right.size;
        return x;
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return get(p) != null;
    }

    private Node get(Point2D p) {
        return get(root, p, 0);
    }

    private Node get(Node x, Point2D p, int rank) {
        double cmp = 0;
        if (x == null) return null;
        if (x.p.equals(p)) return x;
        if (rank % 2 == 0) cmp = p.x() - x.p.x();
        else if (rank % 2 == 1) cmp = p.y() - x.p.y();
        if (cmp < 0) x.left = get(x.left, p, rank + 1);
        else if (cmp >= 0) x.right = get(x.right, p, rank + 1);
        return x; //don't know if this makes sense
    }

    // draw all of the points to standard draw
    public void draw() {

    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        return null;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        return p;
    }

    /*******************************************************************************
     * Test client
     ******************************************************************************/
    public static void main(String[] args) {
        In in = new In();
        Out out = new Out();
        int nrOfRecangles = in.readInt();
        int nrOfPointsCont = in.readInt();
        int nrOfPointsNear = in.readInt();
        RectHV[] rectangles = new RectHV[nrOfRecangles];
        Point2D[] pointsCont = new Point2D[nrOfPointsCont];
        Point2D[] pointsNear = new Point2D[nrOfPointsNear];
        for (int i = 0; i < nrOfRecangles; i++) {
            rectangles[i] = new RectHV(in.readDouble(), in.readDouble(),
                    in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsCont; i++) {
            pointsCont[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsNear; i++) {
            pointsNear[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        KdTree set = new KdTree();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble(), y = in.readDouble();
            set.insert(new Point2D(x, y));
        }
        for (int i = 0; i < nrOfRecangles; i++) {
            // Query on rectangle i, sort the result, and print
            Iterable<Point2D> ptset = set.range(rectangles[i]);
            int ptcount = 0;
            for (Point2D p : ptset)
                ptcount++;
            Point2D[] ptarr = new Point2D[ptcount];
            int j = 0;
            for (Point2D p : ptset) {
                ptarr[j] = p;
                j++;
            }
            Arrays.sort(ptarr);
            out.println("Inside rectangle " + (i + 1) + ":");
            for (j = 0; j < ptcount; j++)
                out.println(ptarr[j]);
        }
        out.println("Contain test:");
        for (int i = 0; i < nrOfPointsCont; i++) {
            out.println((i + 1) + ": " + set.contains(pointsCont[i]));
        }

        out.println("Nearest test:");
        for (int i = 0; i < nrOfPointsNear; i++) {
            out.println((i + 1) + ": " + set.nearest(pointsNear[i]));
        }

        out.println();
    }
}
