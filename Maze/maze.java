import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.*;

/*
 * If you want to generate a different maze to play, 
 * you can press "R" to reset the game. 
 * If you want to solve the maze using depth-first search (DFS), press "D".
 * press "B" to solve using BFS
 * Press "M" to solve manually 
 * the maze can be resized using the constant interface and changing the Hight and Width.
 * Stack and Queue are in the Deque file
 * 
 */

//Comparator class to compare edges based on their weights
class CompareEdges implements IComparator<Edge> {
  public boolean apply(Edge e1, Edge e2) {
    return e1.weight < e2.weight;
  }
}

//Abstract search class
abstract class Search {
  // HashMap to store the previous vertex for each visited vertex
  HashMap<Integer, Vertex> cameFromEdge;

  // Method to reconstruct the path from start to end using the cameFromEdge
  // HashMap
  void reconstruct(HashMap<Integer, Vertex> h, Vertex next) {
    while (h.containsKey(next.identifier())) {
      next.path = true;
      next = h.get(next.identifier());
    }
  }
}

//Breadth First Search implementation
class BreadthFirst extends Search {
  Queue<Vertex> worklist;

  // Constructor to initialize the worklist,
  // visited flag for start vertex, and cameFromEdge HashMap
  BreadthFirst(IList<Vertex> list) {
    this.worklist = new Queue<Vertex>();
    worklist.enqueue(list.getData());
    list.getData().visited = true;
    cameFromEdge = new HashMap<Integer, Vertex>();
  }

  // Method to check if there are more vertices to visit
  public boolean hasNext() {
    return !worklist.isEmpty();
  }

  // Method to visit the next vertex and update the worklist and cameFromEdge
  // HashMap
  public Queue<Vertex> next() {
    Vertex u = worklist.dequeue();
    for (Edge e : u.outEdges) {
      if (!e.to.visited) {
        cameFromEdge.put(e.to.identifier(), e.from);
        if (e.to.x == MazeGameWorld.width - 1 && e.to.y == MazeGameWorld.height - 1) {
          reconstruct(cameFromEdge, e.to);
          worklist = new Queue<Vertex>();
        }
        else {
          e.to.visited = true;
          worklist.enqueue(e.to);
        }
      }
    }
    return worklist;
  }
}

//Depth First Search implementation
class DepthFirst extends Search {
  Stack<Vertex> worklist;

  // Constructor to initialize the worklist,
  // visited flag for start vertex, and cameFromEdge HashMap
  DepthFirst(IList<Vertex> list) {
    this.worklist = new Stack<Vertex>();
    worklist.push(list.getData());
    list.getData().visited = true;
    cameFromEdge = new HashMap<Integer, Vertex>();
  }

  // Method to check if there are more vertices to visit
  public boolean hasNext() {
    return !worklist.isEmpty();
  }

  // Method to visit the next vertex and update the worklist and cameFromEdge
  // HashMap
  public Stack<Vertex> next() {
    Vertex u = worklist.pop();
    for (Edge e : u.outEdges) {
      if (!e.to.visited) {
        cameFromEdge.put(e.to.identifier(), e.from);
        if (e.to.x == MazeGameWorld.width - 1 && e.to.y == MazeGameWorld.height - 1) {
          // If the end vertex is found, reconstruct the path and reset the worklist
          reconstruct(cameFromEdge, e.to);
          worklist = new Stack<Vertex>();
        }
        else {
          // Add the current vertex back to the worklist,
          // mark the new vertex as visited, and add it to the worklist
          worklist.push(u);
          e.to.visited = true;
          worklist.push(e.to);
          break;
        }
      }
    }
    return worklist;
  }

}



//class representing vertex 
class Vertex {
  int x;
  int y;
  boolean visited;
  boolean path;
  ArrayList<Edge> outEdges;

  // constructor for vertex
  Vertex(int x, int y) {
    this.x = x;
    this.y = y;
    this.outEdges = new ArrayList<Edge>();
    this.visited = false;
    this.path = false;
  }

  // identifies vertices
  int identifier() {
    return 1000 * y + x;
  }
}

//class representing vertex
class Edge {
  Vertex from;
  Vertex to;
  int weight;

  // constructor for vertex
  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }
}

//constant values used throughout MazeGameWorld
interface Constants {
  int width = 10;
  int height = 6;
}

//class representing maze world
class MazeGameWorld extends World implements Constants {
  // Declare instance variables of the MazeGameWorld class
  int scale = 10;
  boolean bfs;
  boolean dfs;
  boolean manual;
  BreadthFirst breadth;
  DepthFirst depp;
  Player p;
  IList<Vertex> vertices;
  IList<Edge> walls;

  // Constructor for the MazeGameWorld class
  MazeGameWorld() {
    // Call the setup() method
    setup();
  }

  // Method to set up the initial state of the game
  void setup() {
    // Generate the initial vertices of the maze
    ArrayList<ArrayList<Vertex>> v = generateInitVertices();
    // Get all the edges from the vertices
    ArrayList<Edge> allEdges = getEdges(v);
    // Generate a minimum spanning tree using Kruskal's algorithm
    v = kruskalVertice(v);
    // Get the walls of the maze
    walls = getWalls(v, allEdges);
    // Initialize an empty list of vertices
    vertices = new Empty<Vertex>();
    // Add all the vertices to the list
    for (ArrayList<Vertex> vList : v) {
      for (Vertex vt : vList) {
        vertices = vertices.add(vt);
      }
    }
    // Set the initial search algorithm flags to false
    bfs = false;
    dfs = false;
    manual = false;
    // Initialize the BreadthFirst, DepthFirst, and Player objects
    breadth = new BreadthFirst(vertices);
    depp = new DepthFirst(vertices);
    p = new Player(vertices);
  }

  // Method to get the walls of the maze
  IList<Edge> getWalls(ArrayList<ArrayList<Vertex>> v, ArrayList<Edge> all) {
    // Initialize an empty list of walls
    IList<Edge> w = new Empty<Edge>();
    // Loop through all the edges
    for (Edge e : all) {
      boolean valid = true;
      // Loop through all the vertices
      for (ArrayList<Vertex> l : v) {
        for (Vertex vt : l) {
          // Loop through all the outgoing edges of the vertices
          for (Edge e2 : vt.outEdges) {
            // Check if the edges are the same or they are each other's reverse
            if (e.equals(e2) || (e.to == e2.from && e.from == e2.to)) {
              valid = false;
            }
          }
        }
      }
      // If the edge is valid, add it to the list of walls
      if (valid) {
        w = w.add(e);
      }
    }
    // Return the list of walls
    return w;
  }

  // Method to get all the edges from the vertices
  ArrayList<Edge> getEdges(ArrayList<ArrayList<Vertex>> v) {
    // Initialize an empty list of edges
    ArrayList<Edge> all = new ArrayList<Edge>();
    // Loop through all the vertices
    for (ArrayList<Vertex> verts : v) {
      for (Vertex vt : verts) {
        // Loop through all the outgoing edges of the vertices
        for (Edge ed : vt.outEdges) {
          // Add the edges to the list
          all.add(ed);
        }
      }
    }
    // Return the list of edges
    return all;
  }

  // creates the vertices in maze
  ArrayList<ArrayList<Vertex>> generateInitVertices() {
    ArrayList<ArrayList<Vertex>> vertices = new ArrayList<ArrayList<Vertex>>();
    for (int x = 0; x < width; x++) {
      ArrayList<Vertex> temp = new ArrayList<Vertex>();
      for (int y = 0; y < height; y++) {
        temp.add(new Vertex(x, y));
      }
      vertices.add(temp);
    }
    Random r = new Random();
    for (ArrayList<Vertex> vList : vertices) {
      for (Vertex v : vList) {
        if (v.x != 0) {
          v.outEdges.add(new Edge(v, vertices.get(v.x - 1).get(v.y), r.nextInt(1000)));
        }
        if (v.x != width - 1) {
          v.outEdges.add(new Edge(v, vertices.get(v.x + 1).get(v.y), r.nextInt(1000)));
        }
        if (v.y != 0) {
          v.outEdges.add(new Edge(v, vertices.get(v.x).get(v.y - 1), r.nextInt(1000)));
        }
        if (v.y != height - 1) {
          v.outEdges.add(new Edge(v, vertices.get(v.x).get(v.y + 1), r.nextInt(1000)));
        }
      }
    }
    return vertices;
  }

  // uses the kruskals algorithm to generate a maze
  ArrayList<ArrayList<Vertex>> kruskalVertice(ArrayList<ArrayList<Vertex>> v) {
    ArrayList<Edge> allEdges = getEdges(v);
    for (ArrayList<Vertex> i : v) {
      for (Vertex j : i) {
        j.outEdges = new ArrayList<Edge>();
      }
    }
    int totalCells = height * width;
    IList<Edge> sT = new Empty<Edge>();
    ArrayList<Edge> allEdgesSorted = sort(allEdges);
    HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
    for (int i = 0; i <= (1000 * height) + width; i++) {
      hash.put(i, i);
    }
    ArrayList<Edge> l = allEdgesSorted;
    while (sT.len() < totalCells - 1) {
      Edge e = l.get(0);
      if (this.find(hash, e.to.identifier()) != this.find(hash, e.from.identifier())) {
        sT = sT.add(e);
        e.from.outEdges.add(e);
        e.to.outEdges.add(new Edge(e.to, e.from, e.weight));
        int temp = (find(hash, e.to.identifier()));
        hash.remove(find(hash, e.to.identifier()));
        hash.put(temp, find(hash, e.from.identifier()));
      }
      l.remove(0);
    }
    return v;
  }

  // find helper method for kurskalVertice
  int find(HashMap<Integer, Integer> hashmap, int x) {
    if (hashmap.get(x) == x) {
      return x;
    }
    else {
      return find(hashmap, hashmap.get(x));
    }
  }

  // sorts an array of edges
  ArrayList<Edge> sort(ArrayList<Edge> l) {
    if (l.size() <= 1) {
      return l;
    }
    ArrayList<Edge> l1 = new ArrayList<Edge>();
    ArrayList<Edge> l2 = new ArrayList<Edge>();
    for (int i = 0; i < l.size() / 2; i++) {
      l1.add(l.get(i));
    }
    for (int i = l.size() / 2; i < l.size(); i++) {
      l2.add(l.get(i));
    }
    l1 = sort(l1);
    l2 = sort(l2);
    return merge(l1, l2);
  }

  // merges two arrrays of edges
  ArrayList<Edge> merge(ArrayList<Edge> l1, ArrayList<Edge> l2) {
    ArrayList<Edge> l3 = new ArrayList<Edge>();
    IComparator<Edge> c = new CompareEdges();
    while (l1.size() > 0 && l2.size() > 0) {
      if (c.apply(l1.get(0), l2.get(0))) {
        l3.add(l1.get(0));
        l1.remove(0);
      }
      else {
        l3.add(l2.get(0));
        l2.remove(0);
      }
    }
    while (l1.size() > 0) {
      l3.add(l1.get(0));
      l1.remove(0);
    }
    while (l2.size() > 0) {
      l3.add(l2.get(0));
      l2.remove(0);
    }
    return l3;
  }

  // generates a color based on given vertex
  Color generateColor(Vertex v) {
    if (v.x == width - 1 && v.y == height - 1) {
      return Color.magenta;
    }
    else if (v.path) {
      return Color.blue;
    }
    else if (v.x == 0 && v.y == 0) {
      return Color.green;
    }
    else if (v.visited) {
      return Color.cyan;
    }
    else {
      return Color.gray;
    }
  }

  // updates values of bfs and dfs when it ticks
  public void onTick() {
    if (bfs) {
      if (breadth.hasNext()) {
        breadth.next();
      }
    }
    if (dfs) {
      if (depp.hasNext()) {
        depp.next();
      }
    }
  }

  // changes movement based on keys pressed
  public void onKeyEvent(String ke) {
    if (ke.equals("b")) {
      bfs = true;
      dfs = false;
      manual = false;
      reset();
    }
    else if (ke.equals("d")) {
      bfs = false;
      dfs = true;
      manual = false;
      reset();
    }
    else if (ke.equals("m")) {
      bfs = false;
      dfs = false;
      manual = true;
      reset();
    }
    else if (ke.equals("r")) {
      setup();
    }
    else if (manual) {
      if (p.hasNext()) {
        if (ke.equals("left")) {
          p.moveLeft();
        }
        else if (ke.equals("up")) {
          p.moveUp();
        }
        else if (ke.equals("right")) {
          p.moveRight();
        }
        else if (ke.equals("down")) {
          p.moveDown();
        }
      }
    }
  }

  // rests the maze world
  public void reset() {
    for (Vertex v : vertices) {
      v.path = false;
      v.visited = false;
    }
    breadth = new BreadthFirst(vertices);
    depp = new DepthFirst(vertices);
    p = new Player(vertices);
  }

  // draws the maze scene
  public WorldScene makeScene() {
    WorldScene w = new WorldScene(width * scale, height * scale);
    for (Vertex v : vertices) {
      Color col = generateColor(v);
      w.placeImageXY(new RectangleImage(scale, scale, OutlineMode.SOLID, col),
          (v.x * scale) + (scale * 1 / 2), (v.y * scale) + (scale * 1 / 2));
    }
    for (Edge e : walls) {
      if (e.to.x == e.from.x) {
        w.placeImageXY(new RectangleImage(scale, scale / 10, OutlineMode.SOLID, Color.black),
            (e.to.x * scale) + (scale * 1 / 2),
            ((e.to.y + e.from.y) * scale / 2) + (scale * 1 / 2));
      }
      else {
        w.placeImageXY(new RectangleImage(scale / 10, scale, OutlineMode.SOLID, Color.black),
            ((e.to.x + e.from.x) * scale / 2) + (scale * 1 / 2),
            (e.to.y * scale) + (scale * 1 / 2));
      }
    }
    return w;
  }
}

class ExamplesMazeGame {

  // creates the game
  void testGame(Tester t) {
    MazeGameWorld m = new MazeGameWorld();
    m.bigBang(m.width * m.scale, m.height * m.scale, 0.005);
  }

  Edge edge1;
  Edge edge2;
  Edge edge3;
  Edge edge4;

  Vertex vertex1;
  Vertex vertex2;
  Vertex vertex3;
  Vertex vertex4;

  MazeGameWorld game;

  // initializes maze world
  void initMaze() {
    vertex1 = new Vertex(0, 0);
    vertex2 = new Vertex(0, 0);
    vertex3 = new Vertex(1, 0);
    vertex4 = new Vertex(1, 1);

    edge1 = new Edge(vertex1, vertex4, 1);
    edge2 = new Edge(vertex4, vertex1, 3);
    edge3 = new Edge(vertex2, vertex2, 1);
    edge4 = new Edge(vertex4, vertex1, 5);

    game = new MazeGameWorld();

  }

  void testMazeGameWorld(Tester t) {
    MazeGameWorld m = new MazeGameWorld();
    t.checkExpect(m.vertices.len(), MazeGameWorld.width * MazeGameWorld.height);

    HashMap<Integer, Integer> h = new HashMap<Integer, Integer>();
    h.put(1, 1);
    h.put(2, 1);

    t.checkExpect(m.find(h, 1), 1);
    t.checkExpect(m.find(h, 2), 1);
  }

  // tests identifier
  void testToIdentifier(Tester t) {
    initMaze();
    t.checkExpect(vertex1.identifier(), 0);
    t.checkExpect(vertex2.identifier(), 0);
    t.checkExpect(vertex3.identifier(), 0);
    t.checkExpect(vertex4.identifier(), 1);
  }

  // test getwalls
  void testGetWalls(Tester t) {
    initMaze();
    ArrayList<ArrayList<Vertex>> v = game.generateInitVertices();
    ArrayList<Edge> theEdges = game.getEdges(v);
    v = game.kruskalVertice(v);
    game.walls = game.getWalls(v, theEdges);
    t.checkExpect(((HashMap<Integer, Vertex>) game.walls).size(),
        theEdges.size() - (game.height * game.width));
  }

  // tests allEdges
  void testAllEdges(Tester t) {
    initMaze();
    t.checkExpect(game.getEdges(game.generateInitVertices()).size(),
        game.width * game.height * 4 - (game.width + game.height) * 2);
  }

  // test generateinitvertices
  void testGenerateInitVertices(Tester t) {
    initMaze();
    t.checkExpect(game.generateInitVertices().size(), game.width);
    t.checkExpect(game.generateInitVertices().get(0).get(0).outEdges.size(), 1);
    t.checkExpect(game.generateInitVertices().get(0).get(2).outEdges.size(), 2);

  }

  /*
   * boolean hasPathTo(Vertex dest) { for (Edge e : this.outEdges) { if ( e.to ==
   * dest // can get there in just one step || e.to.hasPathTo(dest)) { // can get
   * there on a path through e.to return true; } } return false; }
   */

  // tests kruskalvertice
  void testKruskalVertice(Tester t) {
    initMaze();
    t.checkExpect(game.kruskalVertice(game.generateInitVertices()).size(),
        game.generateInitVertices().size());
    ArrayList<Edge> allEdges = new ArrayList<Edge>();
    for (ArrayList<Vertex> v : game.kruskalVertice(game.generateInitVertices())) {
      for (Vertex vertice : v) {
        for (Edge e : vertice.outEdges) {
          if (!e.to.outEdges.contains(e)) {
            allEdges.add(e);
          }
        }
      }
    }
  }

  // tests sort
  //test sorting algorithm
  void testSort(Tester t) {
    MazeGameWorld m = new MazeGameWorld();

    Edge e1 = new Edge(null, null, 20);
    Edge e2 = new Edge(null, null, 15);
    Edge e3 = new Edge(null, null, 25);
    Edge e4 = new Edge(null, null, 30);
    Edge e5 = new Edge(null, null, 26);

    ArrayList<Edge> unsorted = new ArrayList<Edge>();
    ArrayList<Edge> sorted = new ArrayList<Edge>();

    unsorted.add(e1);
    unsorted.add(e2);
    unsorted.add(e3);
    unsorted.add(e4);
    unsorted.add(e5);

    sorted.add(e2);
    sorted.add(e1);
    sorted.add(e3);
    sorted.add(e5);
    sorted.add(e4);

    t.checkExpect(m.sort(unsorted), sorted);
  }

  // tests merge
  void testmerge(Tester t) {
    initMaze();
    ArrayList<Edge> edgeList1 = new ArrayList<Edge>();
    ArrayList<Edge> edgeList2 = new ArrayList<Edge>();
    ArrayList<Edge> edgeList1and2 = new ArrayList<Edge>();

    edgeList1.add(edge1);
    edgeList1.add(edge2);
    edgeList2.add(edge3);
    edgeList2.add(edge4);
    edgeList1and2.add(edge1);
    edgeList1and2.add(edge2);
    edgeList1and2.add(edge3);
    edgeList1and2.add(edge4);

    t.checkExpect(game.merge(edgeList1, edgeList2), edgeList1and2);
  }

  // tests gnerate color
  void testgenerateColor(Tester t) {
    initMaze();
    t.checkExpect(game.generateColor(vertex1), Color.green);
    t.checkExpect(game.generateColor(vertex3), Color.gray);
    t.checkExpect(game.generateColor(vertex4), Color.gray);

  }

  // tests onkeyevent
  void testonKeyEvent(Tester t) {
    initMaze();
    game.onKeyEvent("r");
    t.checkExpect(this.game.bfs, false);
    t.checkExpect(this.game.dfs, false);
    game.onKeyEvent("d");
    t.checkExpect(this.game.bfs, false);
    t.checkExpect(this.game.dfs, false);
    game.onKeyEvent("b");
    t.checkExpect(this.game.bfs, true);
    t.checkExpect(this.game.dfs, false);
  }

  // test ontick
  void testOnTick(Tester t) {
    initMaze();
    t.checkExpect(!game.bfs, false);
    game.onTick();
    t.checkExpect(!game.dfs, false);
    game.onTick();
    t.checkExpect(game.bfs, true);
  }

  // tests reset
  void testReset(Tester t) {
    initMaze();
    t.checkExpect(null, null);
  }

  // test makescene
  void testMakeScene(Tester t) {
    initMaze();
    game.setup();
    WorldScene scene = new WorldScene(game.width * game.scale, game.height * game.scale);
    for (Vertex vertex : game.vertices) {
      Color color = game.generateColor(vertex);
      scene.placeImageXY(new RectangleImage(game.scale, game.scale, OutlineMode.SOLID, color),
          (vertex.x * game.scale) + (game.scale * 1 / 2),
          (vertex.y * game.scale) + (game.scale * 1 / 2));
    }
    t.checkExpect(game.makeScene(), scene);
  }
}