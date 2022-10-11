/*************************************************************************
 *************************************************************************/

import edu.princeton.cs.algs4.*;

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
        root = insert(root, p, 0);
    }

    // recursively searches through the tree and adds the point if it doesn't exist.
    private Node insert(Node x, Point2D p, int rank) {
        if (x == null) return new Node(p, 1);
        if (x.p.equals(p)) return x;

        double cmp;
        if (rank % 2 == 0) cmp = p.x() - x.p.x();
        else cmp = p.y() - x.p.y();

        if (cmp < 0) x.left = insert(x.left, p, rank + 1);
        else x.right = insert(x.right, p, rank + 1);

        x.size = 1 + x.left.size + x.right.size;
        return x;
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return contains(root, p, 0);
    }

    private boolean contains(Node x, Point2D p, int rank) {
        if (x == null) return false;
        if (x.p.equals(p)) return true;

        double cmp;
        if (rank % 2 == 0) cmp = p.x() - x.p.x();
        else cmp = p.y() - x.p.y();

        if (cmp < 0) return contains(x.left, p, rank + 1);

        else return contains(x.right, p, rank + 1);
    }

    // draw all of the points to standard draw
    public void draw() {
        StdDraw.setCanvasSize(400, 400);
        StdDraw.setXscale(0, 100);
        StdDraw.setYscale(0, 100);
        StdDraw.setPenRadius(0.005);

        draw_recursive(root, root);
    }

    private void draw_recursive(Node node, Node oldNode) {
        if (node == null) return;
        draw_node(node);
        draw_line();
        draw_recursive(node.left, node.p);
        draw_recursive(node.right, node.p);
    }

    private void draw_node(Node node) {
        StdDraw.point(node.p.x(), node.p.y());
    }

    private void draw_line(Node newNode, Node oldNode) {
        if (oldNode.size % 2 == 0) {
            StdDraw.line(0, newNode.p.y(), 1, newNode.p.y());
        } else StdDraw.line(newNode.p.x(), 0, newNode.p.x(), 1);

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
