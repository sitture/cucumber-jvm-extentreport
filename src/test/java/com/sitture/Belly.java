package com.sitture;

public class Belly {

    private int cukes;

    public Belly() {
        cukes = 0;
    }

    public void eat(int cukes) {
        this.cukes += cukes;
    }

    public int getCukes() {
        return this.cukes;
    }
}
