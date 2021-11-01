package model;

public class Station {
    private String name;
    private Line line;

    public Station(String name, Line line) {
        this(name);
        this.line = line;
    }

    public Station(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
