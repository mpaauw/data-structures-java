package tree.binaryTree.bst;

import com.sun.org.apache.xpath.internal.FoundIndex;
import list.DoublyLinkedList;
import list.queue.Queue;
import list.stack.Stack;
import tree.VisitStatus;
import tree.binaryTree.BinaryTreeNode;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Contains operational implementations for the Binary Search Tree data structure.
 */
public class BinarySearchTree<T extends Comparable<T>> {

    private BinaryTreeNode<T> root;
    private int size;

    /**
     * Recursively inserts an element into the tree.
     * @param value value to insert.
     */
    public void insert(T value) {
        this.root = insert(this.root, value);
        this.size++;
    }
    private BinaryTreeNode<T> insert(BinaryTreeNode<T> node, T value) {
        if(node == null) {
            return new BinaryTreeNode(value);
        }
        int result = value.compareTo(node.getData());
        if(result <= 0) {
            node.setLeft(insert(node.getLeft(), value));
        } else {
            node.setRight(insert(node.getRight(), value));
        }
        return node;
    }

    /**
     * Recursively removes a specified element from the tree.
     * @param value value to remove.
     */
    public void remove(T value) {
        this.root = remove(this.root, value);
        this.size--;
    }
    private BinaryTreeNode<T> remove(BinaryTreeNode<T> node, T value) {
        if(!isEmpty()) {
            while(node != null) {
                int result = value.compareTo(node.getData());
                if (result == 0) {
                    if (node.getLeft() != null && node.getRight() != null) {
                        node = findExtreme(node, BinaryTreeNode::getRight);
                        remove(node.getRight(), node.getData());
                    } else {
                        node = (node.getLeft() != null) ? node.getLeft() : node.getRight();
                    }
                } else if (result < 0) {
                    node = node.getLeft();
                } else {
                    node = node.getRight();
                }
            }
        }
        return node;
    }

    /**
     * Recursively finds a specified value within the tree, if it exists.
     * @param value value to find.
     * @return node containing the search value, null if value cannot be found.
     */
    public BinaryTreeNode<T> find(T value) {
        return find(this.root, value);
    }
    private BinaryTreeNode<T> find(BinaryTreeNode<T> node, T value) {
        if(node == null) {
            return node;
        }
        int result = value.compareTo(node.getData());
        if(result == 0) {
            return node;
        } else if(result < 0) {
            return find(node.getLeft(), value);
        } else {
            return find(node.getRight(), value);
        }
    }

    /**
     * Returns a node containing the minimum value within tree, if it exists.
     * @return value containing minimum-value node, null if value cannot be found.
     */
    public BinaryTreeNode<T> findMin() {
        return findExtreme(this.root, BinaryTreeNode::getLeft);
    }

    /**
     * Returns a node containing the maximum value within the tree, if it exists.
     * @return value containing the maximum-value node, null if value cannot be found.
     */
    public BinaryTreeNode<T> findMax() {
        return findExtreme(this.root, BinaryTreeNode::getRight);
    }

    /**
     * Uses a method reference to find an extreme value within a nested node structure (minimum or maximum in most cases).
     * @param node node to traverse.
     * @param getter function to apply to node traversal.
     * @return final node value, or extreme.
     */
    private BinaryTreeNode<T> findExtreme(BinaryTreeNode<T> node, Function<BinaryTreeNode<T>, BinaryTreeNode<T>> getter) {
        if(!isEmpty()) {
            while(getter.apply(node) != null) {
                node = getter.apply(node);
            }
            return node;
        }
        return null;
    }

    /**
     * Recursively traverses the tree using In-Order Traversal.
     * @return a Doubly-Linked List representing the traversal order.
     */
    public DoublyLinkedList<BinaryTreeNode<T>> traverseInOrder() {
        return traverseInOrder(this.root, new DoublyLinkedList<>());
    }
    private DoublyLinkedList<BinaryTreeNode<T>> traverseInOrder(BinaryTreeNode<T> node, DoublyLinkedList<BinaryTreeNode<T>> order) {
        if(node == null) {
            return order;
        }
        traverseInOrder(node.getLeft(), order);
        order.insertEnd(node);
        traverseInOrder(node.getRight(), order);
        return order;
    }

    /**
     * Recursively traverses the tree using Pre-Order traversal.
     * @return a Doubly-Linked List representing the traversal order.
     */
    public DoublyLinkedList<BinaryTreeNode<T>> traversePreOrder() {
        return traversePreOrder(this.root, new DoublyLinkedList<>());
    }
    private DoublyLinkedList<BinaryTreeNode<T>> traversePreOrder(BinaryTreeNode<T> node, DoublyLinkedList<BinaryTreeNode<T>> order) {
        if(node == null) {
            return order;
        }
        order.insertEnd(node);
        traversePreOrder(node.getLeft(), order);
        traversePreOrder(node.getRight(), order);
        return order;
    }

    /**
     * Recursively traverses the tree using Post-Order traversal.
     * @return a Doubly-Linked List representing the traversal order.
     */
    public DoublyLinkedList<BinaryTreeNode<T>> traversePostOrder() {
        return traversePostOrder(this.root, new DoublyLinkedList<>());
    }
    private DoublyLinkedList<BinaryTreeNode<T>> traversePostOrder(BinaryTreeNode<T> node, DoublyLinkedList<BinaryTreeNode<T>> order) {
        if(node == null) {
            return order;
        }
        traversePostOrder(node.getLeft(), order);
        traversePostOrder(node.getRight(), order);
        order.insertEnd(node);
        return order;
    }

    /**
     * Determines whether or not a value exists within the tree, using Depth-First Search.
     * Uses a wrapper method to initialize objects required for search traversal.
     * @param data value to search for.
     * @return true if the value exists within the tree, false if otherwise.
     */
    public boolean depthFirstSearch(T data) {
        if(getSize() <= 0) {
            return false;
        }
        Stack<BinaryTreeNode<T>> stack = new Stack();
        stack.push(this.root);
        return depthFirstSearch(stack, data);
    }
    private boolean depthFirstSearch(Stack<BinaryTreeNode<T>> stack, T data) {
        HashMap<BinaryTreeNode<T>, VisitStatus> visited = new HashMap<>();
        while(!stack.isEmpty()) {
            BinaryTreeNode<T> current = stack.pop();
            visited.put(current, VisitStatus.VISITING);
            if(current.getData().equals(data)) {
                return true;
            }
            if(current.getRight() != null) {
                if(visited.containsKey(current.getRight())) {
                    if(visited.get(current.getRight()).equals(VisitStatus.UNVISITED)) {
                        stack.push(current.getRight());
                    }
                } else {
                    stack.push(current.getRight());
                }
            }
            if(current.getLeft() != null) {
                if(visited.containsKey(current.getLeft())) {
                    if(visited.get(current.getLeft()).equals(VisitStatus.UNVISITED)) {
                        stack.push(current.getLeft());
                    }
                } else {
                    stack.push(current.getLeft());
                }

            }
            visited.put(current, VisitStatus.VISITED);
        }
        return false;
    }

    /**
     * Determines whether or not a value exists within the tree, using Breadth-First Search.
     * Uses a wrapper method to initialize objects required for search traversal.
     * @param data value to search for.
     * @return true if the value exists within the tree, false if otherwise.
     */
    public boolean breadthFirstSearch(T data) {
        if(getSize() <= 0) {
            return false;
        }
        Queue<BinaryTreeNode<T>> queue = new Queue();
        queue.enqueue(this.root);
        return breadthFirstSearch(queue, data);
    }
    public boolean breadthFirstSearch(Queue<BinaryTreeNode<T>> queue, T data) {
        HashMap<BinaryTreeNode<T>, VisitStatus> visited = new HashMap<>();
        while(!queue.isEmpty()) {
            BinaryTreeNode<T> current = queue.dequeue();
            visited.put(current, VisitStatus.VISITING);
            if(current.getData().equals(data)) {
                return true;
            }
            if(current.getLeft() != null) {
                if(visited.containsKey(current.getLeft())) {
                    if(visited.get(current.getLeft()).equals(VisitStatus.UNVISITED)) {
                        queue.enqueue(current.getLeft());
                    }
                } else {
                    queue.enqueue(current.getLeft());
                }
            }
            if(current.getRight() != null) {
                if(visited.containsKey(current.getRight())) {
                    if(visited.get(current.getRight()).equals(VisitStatus.UNVISITED)) {
                        queue.enqueue(current.getRight());
                    }
                } else {
                    queue.enqueue(current.getRight());
                }
            }
        }
        return false;
    }

    /**
     * Gets and returns the root of the tree.
     * @return a node representing the root of the tree.
     */
    public BinaryTreeNode<T> getRoot() {
        return this.root;
    }

    /**
     * Returns an array representing the current tree.
     * @param clazz underlying tree data type.
     * @return an array containing properly-ordered tree values.
     */
    public T[] toArray(Class<T> clazz) {
        return toArray((T[])Array.newInstance(clazz, this.size), 0, this.root);
    }
    private T[] toArray(T[] arr, int i, BinaryTreeNode<T> node) {
        if(node == null || i > this.size - 1) {
            return arr;
        }
        arr[i] = node.getData();
        arr = (node.getLeft() != null) ? toArray(arr, (2 * i) + 1, node.getLeft()) : arr;
        arr = (node.getRight() != null) ? toArray(arr, (2 * i) + 2, node.getRight()) : arr;
        return arr;
    }

    /**
     * Builds a tree from a specified array.
     * @param arr array of source values.
     */
    public void toTree(T[] arr) {
        for(int i = 0; i < arr.length; i++) {
            insert(arr[i]);
        }
    }

    /**
     * Determines whether or not the tree is empty.
     * @return true if tree is empty, false if otherwise.
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    /**
     * Returns the current size of the tree.
     * @return an integer representing the size of the tree.
     */
    public int getSize() {
        return this.size;
    }

}
