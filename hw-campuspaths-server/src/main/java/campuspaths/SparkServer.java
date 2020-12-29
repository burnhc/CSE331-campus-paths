package campuspaths;

import java.util.*;
import com.google.gson.Gson;
import spark.Spark;
import campuspaths.utils.CORSFilter;
import pathfinder.CampusMap;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import pathfinder.textInterface.CoordinateProperties;
import pathfinder.textInterface.Direction;

/**
 * SparkServer is a server that transforms campus data from
 * Pathfinder to JSON objects for use by App.js in campuspaths.
 *
 */

public class SparkServer {

    // This class does not represent an ADT.

    public static void main(String[] args) {
        CORSFilter corsFilter = new CORSFilter();
        corsFilter.apply();
        CampusMap campusMap = new CampusMap();

        /*
           Returns the shortest path between two buildings as
           provided by their short names.

           ROUTE: /campus-map?start=SHORTNAME&end=SHORTNAME
        */
        Spark.get("/campus-map", (request, response) -> {
            String startString = request.queryParams("start");
            String endString = request.queryParams("end");

            if (startString == null || endString == null) {
                Spark.halt(400, "Must specify a starting" +
                                " point and a destination.");
            }

            if (!campusMap.shortNameExists(startString) ||
                    !campusMap.shortNameExists(endString)) {
                Spark.halt(400, "The starting point" +
                        " and the destination must both exist.");
            }

            // Gets the shortest path.
            Path<Point> shortestPath =
                    campusMap.findShortestPath(startString, endString);

            // Iterates through the path segments (omitting the
            // start and total cost fields) and creates a new list
            // of the path segments to their respective compass
            // direction.
            Iterator<Path<Point>.Segment> iterator = shortestPath.iterator();
            List<Map.Entry<Path<Point>.Segment,
                    Direction>> path = new ArrayList<>();

            while (iterator.hasNext()) {
                Path<Point>.Segment segment = iterator.next();
                Direction dir = Direction.resolveDirection(
                        segment.getStart().getX(),
                        segment.getStart().getY(),
                        segment.getEnd().getX(),
                        segment.getEnd().getY(),
                        CoordinateProperties.INCREASING_DOWN_RIGHT);

                Map.Entry<Path<Point>.Segment, Direction> newEntry =
                        new AbstractMap.SimpleEntry<>(segment, dir);

                path.add(newEntry);
            }

            Gson gson = new Gson();
            return gson.toJson(path);
        });

        /*
           Returns a mapping of all the buildings' short and long names.

           ROUTE: /campus-map-buildings
        */
        Spark.get("/campus-map-buildings", (request, response) -> {

            // Ordered for alphabetical dropdown options.
            Map<String,String> buildingNames = new TreeMap<>(
                    campusMap.buildingNames());

            Gson gson = new Gson();
            return gson.toJson(buildingNames);
        });

        /*
           Returns a mapping of all the buildings' coordinates.

           ROUTE: /building-coord
        */
        Spark.get("/building-coord", (request, response) -> {
            Map<String, Point> buildingCoords = new HashMap<>();

            // For each building's short name, get its coordinate.
            for (String buildingName : campusMap.
                    buildingNames().keySet()) {

                Point buildingPoint = campusMap.
                        getPointFromShortName(buildingName);
                buildingCoords.put(buildingName, buildingPoint);
            }

            Gson gson = new Gson();
            return gson.toJson(buildingCoords);
        });
    }
}