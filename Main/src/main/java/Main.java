public class Main {
    public static void main(String[] args) {
        final String URL = "https://web.archive.org/web/20210614210942/https://www.moscowmap.ru/metro.html#lines";
        final String PATH2JSON = "/Users/a/Desktop/JAVA/github/MoscowMetroMapToJson/Main/data/metro-map.json";

        ControlCenter controlCenter = new ControlCenter();
        controlCenter.start(URL, PATH2JSON);
    }
}