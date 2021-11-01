import org.json.simple.JSONObject;
import parser.HTML2JSONParser;
import parser.JSON2CodeParser;

public class ControlCenter {
    public void start(String url2Parse, String path2JSON) {
        HTML2JSONParser html2JSONParser = new HTML2JSONParser(url2Parse, path2JSON);
        html2JSONParser.setStationsLinesInterconnections();
        JSONObject jsonToFile = html2JSONParser.getJSONObject();
        html2JSONParser.createJSONFile(jsonToFile);

        JSON2CodeParser json2CodeParser = new JSON2CodeParser();
        json2CodeParser.readFile2Object(path2JSON);
        json2CodeParser.parseStations();
        json2CodeParser.parseConnections();
    }
}
