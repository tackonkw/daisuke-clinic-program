// File: clinic7/data/BinarySearchTree.java
package com.clinic7.data;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import com.clinic7.data.LinkedList;

public class BinarySearchTree<T> {
    private TreeNode<T> root;
    private Comparator<T> comparator;
    private int size;

    private static class TreeNode<T>{
        T data;
        TreeNode<T> left, right;

        TreeNode(T data){
            this.data = data;
            this.left = this.right = null;
        }
    }

    public BinarySearchTree(Comparator<T> comparator){
        this.root = null;
        this.comparator = comparator;
        this.size = 0;
    }

    public void insert(T data){
        root = insertRecursive(root, data);
    }

    private TreeNode<T> insertRecursive(TreeNode<T> current, T data){
        if (current == null){
            size++;
            return new TreeNode<T>(data);
        }
        int cmp = comparator.compare(data, current.data);
        if (cmp < 0) {
            current.left = insertRecursive(current.left, data);
        }
        else if (cmp > 0) {
            current.right = insertRecursive(current.right, data);
        } else {
            current.data = data;
        }
        return current;
    }

    public void inOrderTraversal(Consumer<T> action){
        inOrderRecursive(root, action);
    }

    private void inOrderRecursive(TreeNode<T> current, Consumer<T> action){
        if (current == null) return;

        inOrderRecursive(current.left, action);
        action.accept(current.data);
        inOrderRecursive(current.right, action);
    }

    private TreeNode<T> buildBalancedTree(LinkedList<T> list, int start, int end){
        if (start > end) return null;

        int mid = (start + end) / 2;
        TreeNode<T> node = new TreeNode<>(list.get(mid));
        node.left = buildBalancedTree(list, start, mid - 1);
        node.right = buildBalancedTree(list, mid + 1, end);

        return node;
    }

    public void balance(){
        LinkedList<T> sortedList = new LinkedList<>();
        inOrderTraversal(sortedList::addLast);
        if (!sortedList.isEmpty()) {
            root = buildBalancedTree(sortedList, 0, sortedList.size()-1);
        } else {
            root = null;
        }
    }

    private TreeNode<T> findNode(T target){
        TreeNode<T> current = root;

        while (current != null) {
            int cmp = comparator.compare(target, current.data);
            if (cmp == 0) return current;
            else if (cmp < 0) current = current.left;
            else current = current.right;
        }

        return null;
    }

    public T findData(T target){
        TreeNode<T> node = findNode(target);
        if (node == null) return null;
        return node.data;
    }

    public void delete(T data){
        root = deleteRecursive(root, data);
    }

    public boolean contains(T data){
        return findData(data) != null;
    }

    private TreeNode<T> deleteRecursive(TreeNode<T> current, T data){
        if (current == null) return null;

        int cmp = comparator.compare(data, current.data);
        if (cmp < 0) {
            current.left = deleteRecursive(current.left, data);
        } else if (cmp > 0){
            current.right = deleteRecursive(current.right, data);
        } else {
            if (current.left == null && current.right == null)  {
                size--;
                return null;
            }

            if (current.left == null) {
                size--;
                return current.right;
            }
            if (current.right == null) {
                size--;
                return current.left;
            }

            TreeNode<T> successor = findMinNode(current.right);
            current.data = successor.data;
            current.right = deleteRecursive(current.right, successor.data);
        }
        return current;
    }

    private TreeNode<T> findMinNode(TreeNode<T> node){
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private TreeNode<T> findMaxNode(TreeNode<T> node){
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public void clear(){
        root = null;
        size = 0;
    }

    public int height(){
        return heightRec(root);
    }

    private int heightRec(TreeNode<T> current){
        if (current == null) return -1;

        int leftHeight = heightRec(current.left);
        int rightHeight = heightRec(current.right);

        return Math.max(leftHeight, rightHeight) + 1;
    }

    public T min(){
        if (isEmpty()) throw new NoSuchElementException("Tree is empty.");
        return findMinNode(root).data;
    }

    public T max(){
        if (isEmpty()) throw new NoSuchElementException("Tree is empty.");
        return findMaxNode(root).data;
    }
}