import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    private static final int MAX_FILE_DEPTH = 7;

    private static final double[] DEPTH_LON_DPP;
    static {
        DEPTH_LON_DPP = new double[MAX_FILE_DEPTH + 1];

        DEPTH_LON_DPP[0] = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / MapServer.TILE_SIZE;

        for (int i = 1; i <= MAX_FILE_DEPTH; i++) {
            DEPTH_LON_DPP[i] = DEPTH_LON_DPP[i - 1] / 2;
        }
    }

    public Rasterer() {

    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results = new HashMap<>();

        double ullon = params.get("ullon");
        double ullat = params.get("ullat");
        double lrlon = params.get("lrlon");
        double lrlat = params.get("lrlat");
        double width = params.get("w");

        if (!queryBoxIsValid(ullon, ullat, lrlon, lrlat)) {
            results.put("query_success", false);
            return results;
        }

        // Longitudinal distance per pixel
        double londpp = (lrlon - ullon) / width;

        int depth = getDepth(londpp);
        int numTiles = (int) Math.pow(2, depth);

        // Distance per tile
        double londpt = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / numTiles;
        double latdpt = (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT) / numTiles;

        int xStart = (int) ((ullon - MapServer.ROOT_ULLON) / londpt);
        if (xStart < 0) {
            xStart = 0;
        }

        int xEnd = (int) ((lrlon - MapServer.ROOT_ULLON) / londpt);
        if (xEnd >= numTiles) {
            xEnd = numTiles - 1;
        }

        int yStart = (int) ((MapServer.ROOT_ULLAT - ullat) / latdpt);
        if (yStart < 0) {
            yStart = 0;
        }

        int yEnd = (int) ((MapServer.ROOT_ULLAT - lrlat) / latdpt);
        if (yEnd >= numTiles) {
            yEnd = numTiles - 1;
        }

        int cols = xEnd - xStart + 1;
        int rows = yEnd - yStart + 1;

        String[][] renderGrid = new String[rows][cols];

        for (int row = 0, y = yStart; row < rows; ++row, ++y) {
            for (int col = 0, x = xStart; col < cols; ++col, ++x) {
                renderGrid[row][col] = getFileName(depth, x, y);
            }
        }

        double rullon = MapServer.ROOT_ULLON + (xStart * londpt);
        double rullat = MapServer.ROOT_ULLAT - (yStart * latdpt);
        double rlrlon = MapServer.ROOT_ULLON + ((xEnd + 1) * londpt);
        double rlrlat = MapServer.ROOT_ULLAT - ((yEnd + 1) * latdpt);

        results.put("render_grid", renderGrid);
        results.put("raster_ul_lon", rullon);
        results.put("raster_ul_lat", rullat);
        results.put("raster_lr_lon", rlrlon);
        results.put("raster_lr_lat", rlrlat);
        results.put("depth", depth);
        results.put("query_success", true);

        return results;
    }

    private boolean queryBoxIsValid(double ullon, double ullat, double lrlon, double lrlat) {
        return (ullon < MapServer.ROOT_LRLON
                && ullat > MapServer.ROOT_LRLAT
                && lrlon > MapServer.ROOT_ULLON
                && lrlat < MapServer.ROOT_ULLAT
                && ullon < lrlon && lrlat < ullat);
    }

    private int getDepth(double queryLonDpp) {
        for (int depth = 0; depth <= MAX_FILE_DEPTH; ++depth) {
            if (DEPTH_LON_DPP[depth] <= queryLonDpp) {
                return depth;
            }
        }

        return MAX_FILE_DEPTH;
    }

    private String getFileName(int depth, int x, int y) {
        return String.format("d%d_x%d_y%d.png", depth, x, y);
    }
}
