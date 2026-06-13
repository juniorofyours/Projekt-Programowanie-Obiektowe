package org.example;

/**
 * Record przechowujący dane potrzebne do wyrenderowania danego obiektu na ekranie.
 * @param x Położenie obiektu na osi poziomej.
 * @param y Położenie obiektu na osi pionowej.
 * @param type Typ obiektu do wyrenderowania.
 */
public record ObjectToRender(int x, int y, ObjectType type){}