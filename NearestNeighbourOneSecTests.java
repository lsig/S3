import edu.princeton.cs.algs4.*;

public class NearestNeighbourOneSecTests {
    public static void main(String[] args) {
        String[] files = new String[]{"input100k.txt", "input1m.txt"};

        for (String file : files) {
            In in = new In(file);
            //KdTree structure = new KdTree();
            PointSET structure = new PointSET();

            StdOut.println("inserting points");
            for (int i = 0; !in.isEmpty(); i++) structure.insert(new Point2D(in.readDouble(), in.readDouble()));

            StdOut.println("points inserted, starting test.");
            Stopwatch watch = new Stopwatch();
            int counter = 0;

            do {
                structure.nearest(new Point2D(StdRandom.uniformDouble(), StdRandom.uniformDouble()));
                counter++;
            } while (watch.elapsedTime() < 1);

            StdOut.printf("For file %s: kdTree got %d iterations of nearest neighbour in 1 second.\n", file, counter);
        }
    }
}
