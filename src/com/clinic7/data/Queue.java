// File: clinic7/data/Queue.java
package com.clinic7.data;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.NoSuchElementException;


public class Queue<T> implements Iterable<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;
        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public Queue() {
        this.front = this.rear = null;
        this.size = 0;
    }

    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        T data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    public void add(int index, T data) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == size){enqueue(data); return;}

        Node<T> newNode = new Node<>(data);

        if (index == 0) {
            newNode.next = front;
            front = newNode;
            if (rear == null) {
                rear = newNode;
            }
        } else {
            Node<T> current = front;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }

            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        front = rear = null;
        size = 0;
    }

    public void forEach(Consumer<? super T> action) {
        for (T item : this) {
            action.accept(item);
        }
    }

    public boolean contains(Predicate<T> condition){
        if (isEmpty()) return false;
        Node<T> current = front;
        while (current != null) {
            if (condition.test(current.data)) return true;
            current = current.next;
        }
        return false;
    }

    public boolean contains(T item) {
        return contains(x -> x.equals(item));
    }

    public T search (Predicate<T> condition) {
        if (isEmpty()) return null;
        Node<T> current = front;
        while (current != null) {
            if (condition.test(current.data)) return current.data;
            current = current.next;
        }
        return null;
    }

    public int searchIndex(Predicate<T> condition){
        if (isEmpty()) return -1;
        Node<T> current = front;
        int index = 0;
        while (current != null) {
            if (condition.test(current.data)) return index;
            current = current.next;
            index++;
        }
        return -1;
    }

    public int searchIndex(T item) {
        Node<T> current = front;
        int index = 0;
        while (current != null) {
            if (current.data.equals(item)) return index;
            current = current.next;
            index++;
        }
        return -1;
    }

    public void delete(Predicate<T> condition){
        if (isEmpty()) return;

        if (condition.test(front.data)) {
            dequeue();
            return;
        }

        Node<T> current = front;
        while (current.next != null) {
            if (condition.test(current.next.data)) {
                current.next = current.next.next;
                size--;
                if (current.next == null) {
                    rear = current;
                }
                return;
            }
            current = current.next;
        }
    }

    public void delete(T item){
        delete(x -> x.equals(item));
    }

    public void reverse(){
        Node<T> prev = null;
        Node<T> current = front;
        Node<T> next;

        while (current != null) {
            next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }

        rear = front;
        front = prev;
    }

    public Queue<T> clone(){
        Queue<T> copy = new Queue<>();

        Node<T> current = front;
        while (current != null) {
            copy.enqueue(current.data);
            current = current.next;
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Queue<?> other = (Queue<?>) o;
        if (this.size != other.size) return false;

        Node<T> currentA = this.front;
        Node<?> currentB = other.front;

        while (currentA != null) {
            if (!currentA.data.equals(currentB.data)) {
                return false;
            }
            currentA = currentA.next;
            currentB = currentB.next;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Queue: [");
        Node<T> current = front;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) sb.append(", ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = front;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
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