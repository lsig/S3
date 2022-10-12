/*************************************************************************
 *************************************************************************/

import edu.princeton.cs.algs4.*;

import java.util.Arrays;

public class KdTree {
    private Node root;

    private static class Node {
        private Point2D p;
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

        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return x.size;
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
        /*StdDraw.setCanvasSize(400, 400);
        StdDraw.setXscale(0, 100);
        StdDraw.setYscale(0, 100);
        StdDraw.setPenRadius(0.005);*/

        draw_recursive(root, 0, 0, 1, 0, 1);
    }

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
        draw_node(node);
    }

    private void draw_node(Node node) {
        StdDraw.setPenRadius(0.015);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.point(node.p.x(), node.p.y());
        StdDraw.setPenRadius();
    }

    private void draw_line(double left, double right, double down, double up) {
        StdDraw.line(left, down, right, up);
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        LinkedQueue<Point2D> pointSet = new LinkedQueue<Point2D>();
        return range(root, rect, 0, pointSet);
    }

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
        if (cmpMax >= 0 && cmpMin >= 0) points = range(x.right, rect, rank + 1, points);
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

    private Point2D nearest(Node x, Point2D p, Point2D champion, int rank) {
        if (x == null) return champion;
        RectHV line;
        Node firstSubtree;
        Node secondSubtree;
        double cmp;
        if (p.distanceSquaredTo(x.p) < p.distanceSquaredTo(champion)) champion = x.p;
        if (rank % 2 == 0) cmp = p.x() - x.p.x();
            //line = new RectHV(x.p.x(), 0, x.p.x(), 1);
        else cmp = p.y() - x.p.y();
        //line = new RectHV(0, x.p.y(), 1, x.p.y());
        if (cmp < 0) {
            //champion = nearest(x.left, p, champion, rank + 1);
            firstSubtree = x.left;
            secondSubtree = x.right;
        } else {
            //champion = nearest(x.right, p, champion, rank + 1);
            firstSubtree = x.right;
            secondSubtree = x.left;
        }
        //if (line.distanceSquaredTo(p) <= p.distanceSquaredTo(champion)) {
        champion = nearest(firstSubtree, p, champion, rank + 1);
        if (secondSubtree != null) {
            line = makeLine(secondSubtree.p, rank + 1);
            if (line.distanceSquaredTo(p) < p.distanceSquaredTo(champion)) {
                champion = nearest(secondSubtree, p, champion, rank + 1);
            }
            //}
        }
        return champion;
    }

    private RectHV makeLine(Point2D subp, int rank) {
        if (rank % 2 == 0) return new RectHV(subp.x(), 0, subp.x(), 1);
        else return new RectHV(0, subp.y(), 1, subp.y());
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
