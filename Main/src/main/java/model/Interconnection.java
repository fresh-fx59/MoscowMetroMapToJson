package model;

import java.util.TreeMap;

public class Interconnection {
    private TreeMap<String, String> linesStations;

    public Interconnection(TreeMap<String, String> toLinesStations) {
        this.linesStations = toLinesStations;
    }

    public void addLineStation(String line, String station) {
        linesStations.put(line, station);
    }

    public void setLinesStations(TreeMap<String, String> linesStations) {
        this.linesStations = linesStations;
    }

    public TreeMap<String, String> getLinesStations() {
        return linesStations;
    }

}
