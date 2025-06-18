// File: clinic7/data/LinkedList.java
package com.clinic7.data;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.Comparator;

public class LinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    private static class Node<T>{
        T data;
        Node<T> next;

        Node(T data){
            this.data = data;
            this.next = null;
        }
    }

    public LinkedList(){
        head = null;
        tail = null;
        size = 0;
    }

    public boolean isEmpty(){
        return head == null;
    }

    public int size(){
        return size;
    }

    public void addFirst(T data){
        if (data == null) {throw new NullPointerException("Data cannot be null");}

        Node<T> newNode = new Node<>(data);
        if(isEmpty()){
            head = newNode;
            tail = newNode;
        }
        else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    public void addLast(T data){
        if (data == null) {throw new NullPointerException("Data cannot be null");}

        Node<T> newNode = new Node<>(data);
        if(isEmpty()){
            head = newNode;
            tail = newNode;
        }
        else{
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public void addSorted(T data, Comparator<T> comparator) {
        if (data == null) {
            throw new NullPointerException("Data cannot be null");
        }
        Node<T> newNode = new Node<>(data);

        if (isEmpty() || comparator.compare(data, head.data) < 0) {
            newNode.next = head;
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
        } else {
            Node<T> current = head;
            while (current.next != null && comparator.compare(data, current.next.data) >= 0) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
            if (newNode.next == null) {
                tail = newNode;
            }
        }
        size++;
    }

    public void add(T data, int index){
        if (data == null) {throw new NullPointerException("Data cannot be null");}
        if (index > size || index < 0) {throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);}
        if (index == 0) {addFirst(data); return;}
        if (index == size) {addLast(data); return;}

        Node<T> newNode = new Node<>(data);
        Node<T> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }

        newNode.next = current.next;
        current.next = newNode;
        size++;
    }

    public T set(int index, T data) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        T oldData = current.data;
        current.data = data;
        return oldData;
    }

    private Node<T> findNode(Predicate<T> match){
        Node<T> current = head;
        while (current != null) {
            if (match.test(current.data)) { return current; }
            current = current.next;
        }
        throw new NoSuchElementException("Node not found");
    }

    public T findData(Predicate<T> match){
        try {
            Node<T> current = findNode(match);
            return current.data;
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    public T findData(T data){
        return findData((x -> x.equals(data)));
    }

    public T removeFirst(){
        if (isEmpty()){
            throw new NoSuchElementException("List is empty");
        }

        T removedData = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;

        return removedData;
    }

    public T remove (Predicate<T> match){
        if (isEmpty()) return null;

        Node<T> current = head;
        Node<T> prev = null;

        while (current != null) {
            if (match.test(current.data)) {
                if (current == head) {
                    head = current.next;
                    if (head == null) {
                        tail = null;
                    }
                } else {
                    prev.next = current.next;
                    if (current == tail) {
                        tail = prev;
                    }
                }
                size--;
                return current.data;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    public T remove (T data){
        return remove(x -> x.equals(data));
    }

    public T get (int index){
        if (index < 0 || index >= size ) return null;
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public void forEach(Consumer<? super T> action){
        Node<T> current = head;
        while (current != null) {
            action.accept(current.data);
            current = current.next;
        }
    }

    public boolean contains(Predicate<T> condition){
        if(isEmpty()) return false;

        Node<T> current = head;
        while (current != null) {
            if (condition.test(current.data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean contains(T data){
        return contains(x -> x.equals(data));
    }

    public void clear(){
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            public boolean hasNext() {
                return current != null;
            }

            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}