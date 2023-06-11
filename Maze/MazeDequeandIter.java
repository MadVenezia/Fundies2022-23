import java.util.Iterator;

//interface defines methods and properties that a list should have.
interface IList<T> extends Iterable<T> {
  // returns a boolean indicating whether the list is empty or not.
  boolean isEmpty();

  // returns the length of the list.
  int len();

  //returns an iterator for the list.
  Iterator<T> iterator();

  // adds an element of type T to the list and returns the updated list.
  IList<T> add(T t);

  //returns the element of the list at the given index.
  T get(int i);

  //appends the given l list to the current list and returns the updated list.
  IList<T> append(IList<T> l);

  //returns a boolean indicating whether the iterator has a next element or not.
  boolean hasNext();

  // returns the current element in the iteration.
  T getData();

  // returns the next element in the iteration.
  IList<T> getNext();
}

//empty list
class Empty<T> implements IList<T>, Iterable<T> {
  // returns the length of the list.
  public int len() {
    return 0;
  }

  // returns a boolean indicating whether the list is empty or not.
  public boolean isEmpty() {
    return true;
  }

  //returns an iterator for the list.
  public Iterator<T> iterator() {
    return new ListIterator<T>(this);
  }

  // adds an element of type T to the list and returns the updated list.
  public IList<T> add(T t) {
    return new Cons<T>(t, new Empty<T>());
  }

  //returns the element of the list at the given index.
  public T get(int i) {
    return null;
  }

  public IList<T> append(IList<T> l) {
    return l;
  }

  //returns a boolean indicating whether the iterator has a next element or not.
  public boolean hasNext() {
    return false;
  }

  // returns the current element in the iteration.
  public T getData() {
    return null;
  }

  // returns the next element in the iteration.
  public IList<T> getNext() {
    throw new UnsupportedOperationException();
  }
}

//non empty list
class Cons<T> implements IList<T>, Iterable<T> {

  T first;

  IList<T> rest;

  Cons(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  public int len() {
    return 1 + this.rest.len();
  }

  public boolean isEmpty() {
    return false;
  }

  public Iterator<T> iterator() {
    return new ListIterator<T>(this);
  }

  public IList<T> add(T t) {
    if (rest.isEmpty()) {
      rest = new Cons<T>(t, new Empty<T>());
    }
    else {
      rest = rest.add(t);
    }
    return this;
  }

  public T get(int i) {
    if (i == 0) {
      return this.first;
    }
    else {
      return this.rest.get(i - 1);
    }
  }

  public IList<T> append(IList<T> l) {
    if (rest.isEmpty()) {
      rest = l;
    }
    else {
      rest.append(l);
    }
    return this;
  }

  public boolean hasNext() {
    return true;
  }

  public T getData() {
    return this.first;
  }

  public IList<T> getNext() {
    return this.rest;
  }
}

class ListIterator<T> implements Iterator<T> {
  IList<T> curr;

  ListIterator(IList<T> curr) {
    this.curr = curr;
  }

  public boolean hasNext() {
    return curr.hasNext();
  }

  public T next() {
    T temp = this.curr.getData();
    this.curr = this.curr.getNext();
    return temp;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}

class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> s) {
    this.header = s;
  }

  int size() {
    return this.header.next.size();
  }

  void addAtHead(T t) {
    this.header.next = new Node<T>(t, this.header.next, this.header);
  }

  void addAtTail(T t) {
    this.header.prev = new Node<T>(t, this.header, this.header.prev);
  }

  T removeFromTail() {
    if (this.header.closedLoop()) {
      throw new RuntimeException("Can't remove from an empty list!");
    }
    else {
      return this.header.prev.remove();
    }
  }

  T removeFromHead() {
    if (this.header.closedLoop()) {
      throw new RuntimeException("Can't remove from an empty list!");
    }
    else {
      return this.header.next.remove();
    }
  }

  void removeNode(ANode<T> that) {
    this.header.next.removeNode(that);
  }
}

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  abstract int size();

  boolean closedLoop() {
    return (this.next == this && this.prev == this);
  }

  abstract void removeNode(ANode<T> that);

  abstract T remove();
}

class Sentinel<T> extends ANode<T> {

  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  int size() {
    return 0;
  }

  void removeNode(ANode<T> that) {
    throw new RuntimeException("cannot remove from empty list");
  }

  T remove() {
    return null;
  }
}

class Node<T> extends ANode<T> {
  T data;

  Node(T t) {
    this.data = t;
    this.next = null;
    this.prev = null;
  }

  Node(T t, ANode<T> next, ANode<T> previous) {
    this.data = t;
    this.next = next;
    this.prev = previous;
    if (next == null || previous == null) {
      throw new IllegalArgumentException("Node cannot be null");
    }
    else {
      this.next.prev = this;
      this.prev.next = this;
    }
  }

  int size() {
    return 1 + this.next.size();
  }

  void removeNode(ANode<T> that) {
    if (this == that) {
      this.remove();
    }
    else {
      this.next.removeNode(that);
    }
  }

  T remove() {
    this.next.prev = this.prev;
    this.prev.next = this.next;
    return this.data;
  }
}

interface IPred<T> {
  boolean apply(T t);
}

interface IComparator<T> {
  boolean apply(T t1, T t2);
}

// Stack class with a generic type parameter T
class Stack<T> {

  // A Deque to hold the stack's contents
  Deque<T> contents;

  // Constructor that initializes an empty stack
  Stack() {
    this.contents = new Deque<T>();
  }

  // Constructor that initializes a stack with items from a list
  Stack(IList<T> ts) {
    contents = new Deque<T>();
    // Adds each item in the list to the stack
    for (T t : ts) {
      contents.addAtTail(t);
    }
  }

  // Pushes an item onto the top of the stack
  void push(T item) {
    contents.addAtHead(item);
  }

  // Checks if the stack is empty
  boolean isEmpty() {
    return contents.size() == 0;
  }

  // Removes and returns the item at the top of the stack
  T pop() {
    return contents.removeFromHead();
  }
}

// Queue class with a generic type parameter T
class Queue<T> {

  // A Deque to hold the queue's contents
  Deque<T> contents;

  // Constructor that initializes an empty queue
  Queue() {
    this.contents = new Deque<T>();
  }

  // Constructor that initializes a queue with items from a list
  Queue(IList<T> ts) {
    contents = new Deque<T>();
    // Adds each item in the list to the queue
    for (T t : ts) {
      contents.addAtTail(t);
    }
  }

  // Enqueues an item at the back of the queue
  void enqueue(T item) {
    contents.addAtTail(item);
  }

  // Checks if the queue is empty
  boolean isEmpty() {
    return contents.size() == 0;
  }

  // Removes and returns the item at the front of the queue
  T dequeue() {
    return contents.removeFromTail();
  }
}
