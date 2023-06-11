import java.util.HashMap;

class Player extends Search {
  Vertex current; // Current vertex of the player
  boolean finished; // Flag to determine if player has finished the search
  HashMap<Integer, Vertex> cameFromEdge; // Map to store the
  // parent vertex for each visited vertex

  // Constructor
  Player(IList<Vertex> list) {
    current = list.getData(); // Set current vertex
    // to the first vertex in the list
    cameFromEdge = new HashMap<Integer, Vertex>(); // Initialize the map
    // for storing parent vertices
    finished = false; // Set finished flag to false as the player has
    // not finished the search yet

  }

  // Method to check if there is a next move for the player
  public boolean hasNext() {
    return !finished; // Return true if player has not finished the search, else false
  }

  // Method to move the player based on the given boolean condition and edge
  public Vertex move(boolean b, Edge e) {
    if (b) {
      current.visited = true; // Set visited flag of current vertex to true
      current.path = false; // Set path flag of current vertex to false
      if (!e.to.visited) {
        cameFromEdge.put(e.to.identifier(), e.from); // Store the parent
        // vertex of e.to in the map
      }
      if (e.to.x == MazeGameWorld.width - 1 && e.to.y == MazeGameWorld.height - 1) {
        reconstruct(cameFromEdge, e.to); // Call reconstruct method
        // if player has reached the goal
        // vertex
      }
      else {
        current = e.to; // Set current vertex to the next vertex in the search
        current.path = true; // Set path flag of current vertex to true
      }
    }
    return current; // Return the current vertex after the move
  }

  // Method to move the player left
  public Vertex moveLeft() {
    for (Edge e : current.outEdges) {
      move(e.to.x == current.x - 1, e); // Call move method
      // with boolean condition for moving left
    }
    return current; // Return the current vertex after the move
  }

  // Method to move the player right
  public Vertex moveRight() {
    for (Edge e : current.outEdges) {
      move(e.to.x == current.x + 1, e); // Call move method with
      // boolean condition for moving right
    }
    return current; // Return the current vertex after the move
  }

  // Method to move the player down
  public Vertex moveDown() {
    for (Edge e : current.outEdges) {
      move(e.to.y == current.y + 1, e); // Call move method with
      // boolean condition for moving down
    }
    return current; // Return the current vertex after the move
  }

  // Method to move the player up
  public Vertex moveUp() {
    for (Edge e : current.outEdges) {
      move(e.to.y == current.y - 1, e); // Call move method with
      // boolean condition for moving up
    }
    return current; // Return the current vertex after the move
  }

  // Method to reconstruct the path from start to goal vertex
  void reconstruct(HashMap<Integer, Vertex> h, Vertex next) {
    while (h.containsKey(next.identifier())) {
      next.path = true; // Set path flag of next vertex to true
      next = h.get(next.identifier()); // Get the parent vertex of next from the map
    }
  }
}
