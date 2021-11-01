package parser;

import com.google.gson.GsonBuilder;
import model.Interconnection;
import model.Line;
import model.Station;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.util.*;

public class HTML2JSONParser {
    private final boolean DEBUG = false;
    private List<Line> lines = new ArrayList<>();
    private List<Station> stations = new ArrayList<>();
    private List<Interconnection> interconnections = new ArrayList<>();
    private JSONObject rootJSONObject = new JSONObject();
    private final String URL2HTML;
    private final String PATH2JSON;
    private Document document;

    public HTML2JSONParser(String url2HTML, String path2JSON) {
        this.URL2HTML = url2HTML;
        this.PATH2JSON = path2JSON;
        getHTML();
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Interconnection> getInterconnections() {
        return interconnections;
    }

    public JSONObject getRootJSONObject() {
        return rootJSONObject;
    }

    public void prettyPrintJSON (){
        if (!rootJSONObject.isEmpty()) {
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(rootJSONObject));
        } else {
            System.out.println("rootJSONObject is empty. Run setStationsLinesInterconnections() and getJSONObject()");
        }
    }

    private void getHTML() {
        try {
            document = Jsoup.connect(URL2HTML)
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .referrer(URL2HTML)
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .timeout(600000)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStationsLinesInterconnections() {
        Elements lines = document.select("span.js-metro-line");
        Elements jsMetrostation = document.select(
                "a[href^=/web/20210614210942/https://www.moscowmap.ru/metro/][data-metrost]");
        String lineNumber = null;
        String lineName = null;
        String stationName = null;
        String stationNumber = null;
        Elements intersections;
        String interconnectionStationName = null;
        String interconnectionLineNumber = null;
        Element station;
        int stationCounter = 0;
        for (Element line : lines) {
            lineNumber = line.attr("data-line");
            lineName = line.text();
            Line line2Add = new Line(lineName, lineNumber);
            this.lines.add(line2Add);
            for (int stationI = stationCounter; stationI < jsMetrostation.size(); stationI++) {
                station = jsMetrostation.get(stationI);
                stationNumber = station.select("span.num").text();
                stationName = station.select("span.name").text();
                Station station2Add = new Station(stationName, line2Add);
                this.stations.add(station2Add);

                if (DEBUG) {
                    System.out.println(stationNumber + " " + stationName
                            + " on line " + lineNumber + " " + lineName);
                }
                intersections = station.select("span.t-icon-metroln");
                if (!intersections.isEmpty()) {
                    TreeMap<String, String> interconnections2Add = new TreeMap<>();
                    interconnections2Add.put(line2Add.getNumber(), station2Add.getName());
                    for (Element intersection : intersections) {
                        interconnectionLineNumber = intersection.attr("class").split(" ")[1].replace("ln-", "");
                        interconnectionStationName = intersection.attr("title").split("«|»")[1];
                        if (DEBUG) {
                            System.out.println("We have intersection on line " +
                                    interconnectionLineNumber + " " + " with station " +
                                    interconnectionStationName);
                        }
                        interconnections2Add.put(interconnectionLineNumber, interconnectionStationName);
                    }
                    interconnections.add(new Interconnection(interconnections2Add));

                }

                if ((stationI + 1) < jsMetrostation.size()) {
                    if (jsMetrostation.get(stationI + 1).select("span.num").text().equals("1.") && stationI > 0) {
                        stationCounter = ++stationI;
                        break;
                    }
                }
            }
        }
        if (DEBUG) {
            interconnections.forEach(i -> {
                System.out.println(i.getLinesStations().toString());
            });
        }
    }

    public JSONObject getJSONObject() {
        if (!lines.isEmpty() && !stations.isEmpty() && !interconnections.isEmpty()) {
            JSONArray linesJSONArray = new JSONArray();
            JSONObject stationsJSONObjects = new JSONObject();
            JSONObject interconnectionsJSONObjects = new JSONObject();
            lines.forEach(line -> {
                JSONObject lineJSONObject = new JSONObject();
                lineJSONObject.put("number", line.getNumber());
                lineJSONObject.put("name", line.getName());
                linesJSONArray.add(lineJSONObject);
                JSONArray stationsJSONArray = new JSONArray();
                stations.forEach(station -> {
                    if (line.getNumber().equals(station.getLine().getNumber())) {
                        stationsJSONArray.add(station.getName());
                    }
                });
                stationsJSONObjects.put(line.getNumber(), stationsJSONArray);

            });
            JSONArray interconnectionLeve1JSONArray = new JSONArray();
            for (Interconnection interconnection : interconnections) {
                JSONArray interconnectionsLevel2JSONArray = new JSONArray();
                Set<Map.Entry<String, String>> interconnectionSet = interconnection.getLinesStations().entrySet();
                interconnectionSet.forEach(i -> {
                    JSONObject interconnectionJSONObject = new JSONObject();
                    interconnectionJSONObject.put("line", i.getKey());
                    interconnectionJSONObject.put("station", i.getValue());
                    interconnectionsLevel2JSONArray.add(interconnectionJSONObject);
                });
                interconnectionLeve1JSONArray.add(interconnectionsLevel2JSONArray);
            }
            rootJSONObject.put("lines", linesJSONArray);
            rootJSONObject.put("stations", stationsJSONObjects);
            rootJSONObject.put("connections", interconnectionLeve1JSONArray);

            return rootJSONObject;
        } else {
            System.out.println("lines, stations or interconnections is EMPTY");
            return null;
        }
    }

    public void createJSONFile(JSONObject data2Write) {
        try {
            FileWriter file = new FileWriter(this.PATH2JSON);
            file.write(data2Write.toJSONString());
            file.flush();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}