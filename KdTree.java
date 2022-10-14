/*************************************************************************
 *************************************************************************/

import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {// object overhead 16 bytes
    private Node root;// reference 8 bytes

    private static class Node {// object overhead 16 bytes
        private Point2D p;// reference 8 bytes + object 32 bytes
        private Node left;// reference 8 bytes
        private Node right;// reference 8 bytes
        private int size;// int 4 bytes

        // each node uses 16 (overhead) + 3*8 (reference) + 4 (int) + 32 (Point2d) + 4 (padding) = 80 bytes of memory.

        public Node(Point2D p, int size) {
            this.p = p;
            this.size = size;
        }
    }
    /* each kd tree uses 16 (overhead) + 8 (reference) + 80 (bytes/node) * N (nodes) bytes of memory. That is 24 + 80 N bytes.
    That gives a tilde notation of T(n) ~ 80n bytes.
     */

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
        root = insert(root, p, 0);
    }

    // recursively searches through the tree and adds the point if it doesn't exist.
    private Node insert(Node x, Point2D p, int rank) {
        if (x == null) return new Node(p, 1); // constant
        if (x.p.equals(p)) return x; // constant

        double cmp; // constant

        if (rank % 2 == 0) cmp = p.x() - x.p.x(); // constant
        else cmp = p.y() - x.p.y(); // constant

        if (cmp < 0) x.left = insert(x.left, p, rank + 1); // log n best case, n worst case.
        else x.right = insert(x.right, p, rank + 1); // log n best case, n worst case.

        x.size = 1 + nodeSize(x.left) + nodeSize(x.right);  // constant
        return x;
    }

    // returns the size of each node, null protected.
    private int nodeSize(Node x) {
        if (x == null) return 0;
        else return x.size;
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return contains(root, p, 0);
    }

    // recursively searches through the 2d-tree until either the node to search for is found, or it must not exist in the tree.
    private boolean contains(Node x, Point2D p, int rank) {
        if (x == null) return false;
        if (x.p.equals(p)) return true;

        double cmp;
        if (rank % 2 == 0) cmp = p.x() - x.p.x();
        else cmp = p.y() - x.p.y();

        if (cmp < 0) return contains(x.left, p, rank + 1);

        else return contains(x.right, p, rank + 1);
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        LinkedQueue<Point2D> pointSet = new LinkedQueue<Point2D>();
        return range(root, rect, 0, pointSet);
    }

    /* Recursively searches through the 2d-tree, finding all points that can be placed in a given rectangle.
     * Ignoring the subtrees that are on the other side of the "current" node from the rectangle.*/
    private LinkedQueue<Point2D> range(Node x, RectHV rect, int rank, LinkedQueue<Point2D> points) {
        double cmpMax;
        double cmpMin;
        if (x == null) return points;
        if (rect.contains(x.p)) {
            points.enqueue(x.p);
        }
        if (rank % 2 == 0) {
            cmpMax = rect.xmax() - x.p.x();
            cmpMin = rect.xmin() - x.p.x();
        } else {
            cmpMax = rect.ymax() - x.p.y();
            cmpMin = rect.ymin() - x.p.y();
        }
        if (cmpMin >= 0) points = range(x.right, rect, rank + 1, points);

        else if (cmpMax >= 0 && cmpMin <= 0) {
            points = range(x.left, rect, rank + 1, points);
            points = range(x.right, rect, rank + 1, points);
        } else points = range(x.left, rect, rank + 1, points);

        return points;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        if (isEmpty()) return null;
        return nearest(root, p, root.p, 0);
    }

    /* recursively searches through the 2d-tree, looking for a closest node.
    Disregards subtrees that are in rectangles further away than the "current" closest point.*/
    private Point2D nearest(Node node, Point2D p, Point2D champion, int rank) {
        if (node == null) return champion;
        double rectDist;

        if (p.distanceSquaredTo(node.p) < p.distanceSquaredTo(champion)) champion = node.p;

        if (rank % 2 == 0) rectDist = node.p.x() - p.x();
        else rectDist = node.p.y() - p.y();

        double rectDistSquared = rectDist * rectDist;

        if (p.distanceSquaredTo(champion) < rectDistSquared) {
            if (rectDist > 0) champion = nearest(node.left, p, champion, rank + 1);
            else champion = nearest(node.right, p, champion, rank + 1);
        } else {
            champion = nearest(node.left, p, champion, rank + 1);
            champion = nearest(node.right, p, champion, rank + 1);
        }

        return champion;
    }


    /*
     * draw implementation
     */

    // draw all of the points to standard draw
    public void draw() {
        draw_recursive(root, 0, 0, 1, 0, 1);
    }

    // recursively draws all nodes on the canvas, with a line outlining each nodes' rectangle.
    private void draw_recursive(Node node, int rank, double left, double right, double down, double up) {
        if (node == null) return;
        if (rank % 2 == 0) {
            StdDraw.setPenColor(StdDraw.RED);
            draw_line(node.p.x(), node.p.x(), down, up);
            draw_recursive(node.left, rank + 1, left, node.p.x(), down, up);
            draw_recursive(node.right, rank + 1, node.p.x(), right, down, up);
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            draw_line(left, right, node.p.y(), node.p.y());
            draw_recursive(node.left, rank + 1, left, right, down, node.p.y());
            draw_recursive(node.right, rank + 1, left, right, node.p.y(), up);
        }
        draw_point(node);
    }

    // draws a point to StdDraw
    private void draw_point(Node node) {
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.point(node.p.x(), node.p.y());
        StdDraw.setPenRadius();
    }

    // draws a line through under a point, ending on an intersection with another line / the edge of the canvas.
    private void draw_line(double left, double right, double down, double up) {
        StdDraw.line(left, down, right, up);
    }

    /*******************************************************************************
     * Test client
     ******************************************************************************/
    public static void main(String[] args) {

        /*In in = new In("../packet/SomeInputs/input10k.txt");
        KdTree tree = new KdTree();
        for (int i = 0; !in.isEmpty(); i++) {
            tree.insert(new Point2D(in.readDouble(), in.readDouble()));
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.clear();
        tree.draw();
        StdDraw.show();*/


        /*int[] N_s = new int[]{10_000, 20_000, 40_000, 80_000, 160_000, 320_000, 640_000, 1_280_000, 2_560_000, 5_120_000};
        for (int N : N_s) {
            Point2D[] pointArray = new Point2D[N];

            for (int i = 0; i < N; i++) {
                pointArray[i] = new Point2D(StdRandom.uniformDouble(), StdRandom.uniformDouble());
            }
            KdTree tree = new KdTree();

            Stopwatch watch = new Stopwatch();
            for (Point2D point : pointArray)
                tree.insert(point);
            double time = watch.elapsedTime();

            StdOut.printf("time for %d points: %f seconds\n", N, time);
        }*/




        /*In in = new In();
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

        out.println();*/
    }

}
