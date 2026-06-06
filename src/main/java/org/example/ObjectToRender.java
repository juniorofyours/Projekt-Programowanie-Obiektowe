package org.example;

//automatyczne tworzenie metod equals(), toString(), hashCode() dzieki public record:
public record ObjectToRender(
    int x,
    int y,
    String type
){}