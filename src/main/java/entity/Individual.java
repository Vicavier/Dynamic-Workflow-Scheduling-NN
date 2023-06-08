package entity;

public class Individual {
    private Chromosome c1;
    private Chromosome c2;

    public Individual(Chromosome c1, Chromosome c2){
        this.c1 = c1;
        this.c2 = c2;
    }
    public Individual(){};

    public Chromosome getC1() {
        return c1;
    }

    public Chromosome getC2() {
        return c2;
    }

    public void setC1(Chromosome c1) {
        this.c1 = c1;
    }

    public void setC2(Chromosome c2) {
        this.c2 = c2;
    }
}
