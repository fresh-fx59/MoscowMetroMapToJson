package parser;

import misc.NaturalOrderComparator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;

public class JSON2CodeParser {
    private JSONObject rootJSON;
    public void readFile2Object(String path2File) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(path2File)) {
            Object metro = jsonParser.parse(reader);
            rootJSON = (JSONObject) metro;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void parseStations() {
        List<String> output = new ArrayList<>();

        JSONObject stationsJSONObject = (JSONObject) rootJSON.get("stations");
        stationsJSONObject.forEach((lineNumber, stationsArray) ->{
            JSONArray stationsJSONArray = (JSONArray) stationsArray;
            int stationCounter = stationsJSONArray.size();
            output.add("Line " + lineNumber + " has "
                    + stationCounter + " stations");
        });
        output.sort(new NaturalOrderComparator());
        output.forEach(System.out::println);
    }

    public void parseConnections() {
        JSONArray stationsJSONObject = (JSONArray) rootJSON.get("connections");
        HashSet<String> connectionMap = new HashSet<>();
        for (Object connections : stationsJSONObject) {
            JSONArray connectionsArray = (JSONArray) connections;
            for (Object connection : connectionsArray) {
                JSONObject connectionJSONObject = (JSONObject)connection;
                connectionMap.add(connectionJSONObject.get("line").toString() +
                                connectionJSONObject.get("station").toString()
                        );
            }
        }
        System.out.println("There are " + connectionMap.size() + " connections in metro.");
    }
}
