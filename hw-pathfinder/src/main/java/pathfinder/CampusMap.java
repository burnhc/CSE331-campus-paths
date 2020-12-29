/*
 * Copyright (C) 2020 Hal Perkins.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Winter Quarter 2020 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package pathfinder;

import graph.DirectLGraph;
import graph.Edge;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import pathfinder.parser.CampusBuilding;
import pathfinder.parser.CampusPath;
import pathfinder.parser.CampusPathsParser;

import java.util.*;

/**
 * <b>CampusMap</b> is an immutable representation of
 * the 2006 UW campus storing coordinate points and path
 * distances across the campus.
 *
 */
public class CampusMap implements ModelAPI {

    // ABSTRACTION FUNCTION:
    //      CampusMap is a graph that stores 2D coordinate points and
    //      one-way, weighted edges between points that are not always
    //      buildings. buildingsList is a list of buildings on the
    //      campus which stores non-null CampusBuildings from
    //      [0..size - 1].
    //
    //      AF(r) = CampusMap as a DirectLGraph<Point, Double> g such that
    //          "Graph is empty" = if g is empty.
    //          "(x,y):" = if g contains Point (x,y) with no children.
    //          "(x,y): ((x,y) --[weight]--> (x2,y2))" = if g contains Point
    //          (x,y) connected to Point (x2,y2) one-way by an edge with
    //          weight as a double value.

    // REPRESENTATION INVARIANT:
    //      All points and edges in campusGraph != null.
    //      All CampusBuildings in buildingsList != null.
    //      All edge weights are non-negative.
    //      No duplicate points are stored.
    //      No two edges share the same source, destination, and label.


    // Debug flag; change to TRUE to enable checkReps.
    private static final boolean DEBUG = false;

    private List<CampusBuilding> buildingsList;
    private DirectLGraph<Point, Double> campusGraph;
    private List<CampusPath> pathsList;
    private Map<String, String> buildingNames;

    /**
     * Constructs a new CampusMap from data provided in
     * campus_buildings.tsv and campus_paths.tsv.
     *
     * @spec.effects Constructs a new CampusMap.
     */
    public CampusMap () {
        this.buildingsList = CampusPathsParser.
                parseCampusBuildings("campus_buildings.tsv");
        this.pathsList = CampusPathsParser.
                parseCampusPaths("campus_paths.tsv");

        this.campusGraph = buildGraph(new DirectLGraph<>());
        this.buildingNames = new HashMap<>();
        this.buildingNames = buildingNames();

        checkRep();
    }

    /**
     * Builds a new CampusMap storing the coordinate
     * points contained in the map and the distances
     * between them.
     *
     * @return a new DirectLGraph<Point, Double> storing
     * the campus map data.
     */
    public DirectLGraph<Point, Double> buildGraph (
            DirectLGraph<Point, Double> campusGraph) {

        // Add each path to this graph as a new Point.
        for (CampusPath path : this.pathsList) {
            campusGraph.addNode(new Point(path.getX1(),
                    path.getY1()));
        }

        // Add edges from one Point to its outgoing Point. The
        // edge label is the distance of the path between points.
        for (CampusPath path : this.pathsList) {
            Point startPoint = new Point(path.getX1(),
                    path.getY1());
            Point endPoint = new Point(path.getX2(),
                    path.getY2());

            campusGraph.addEdge(startPoint, endPoint,
                    path.getDistance());
        }

        return campusGraph;
    }

    @Override
    public Map<String, String> buildingNames() {
        for (CampusBuilding building : this.buildingsList) {
            this.buildingNames.put(building.getShortName(),
                    building.getLongName());
        }

        checkRep();
        return new HashMap<>(this.buildingNames);
    }

    @Override
    public boolean shortNameExists(String shortName) {
        return this.buildingNames.containsKey(shortName);
    }

    @Override
    public String longNameForShort(String shortName) {
        checkRep();
        if (!shortNameExists(shortName)) {
            throw new IllegalArgumentException ("Building doesn't exist.");
        }

        return this.buildingNames.get(shortName);
    }

    // HELPER: Returns a new Point corresponding to the x and y
    // coordinates of a building's short name.
    public Point getPointFromShortName(String shortName) {

        double x = 0.0;
        double y = 0.0;

        for (CampusBuilding building : this.buildingsList) {
            if (building.getShortName().equals(shortName)) {
                x = building.getX();
                y = building.getY();
            }
        }

        return new Point(x, y);
    }

    @Override
    public Path<Point> findShortestPath(String startShortName,
                                        String endShortName) {
        checkRep();
        if (!shortNameExists(startShortName)) {
            throw new IllegalArgumentException ("Start doesn't exist.");
        } else if (!shortNameExists(endShortName)) {
            throw new IllegalArgumentException ("Destination doesn't exist.");
        }

        Point startBuilding = getPointFromShortName(startShortName);
        Point endBuilding = getPointFromShortName(endShortName);

        checkRep();
        return ShortestPathFinder.findShortestPath(campusGraph,
                startBuilding, endBuilding);
    }

    // Checks if the rep invariant holds. Throws exception if the
    // rep invariant is violated.
    private void checkRep() {

        // Check for duplicate coordinates.
        Set<Point> checkNodes = new HashSet<>(campusGraph.getNodes());
        assert (checkNodes.size() == campusGraph.getNodes().size());

        // All points in campusGraph != null.
        assert !(campusGraph.hasNode(null));

        if (DEBUG) {
            // All edges in campusGraph are non-null and have
            // non-negative edge weights.
            for (Point point : campusGraph.getNodes()) {
                for (Edge<Point,Double> edge :
                        campusGraph.getEdges(point)) {
                    assert edge != null;
                    assert edge.getLabel() >= 0.0;
                }
            }

            // Check for any identical edges.
            for (Point point : checkNodes) {
                Set<Edge<Point,Double>> checkEdges = new HashSet<>
                        (campusGraph.getEdges(point));
                assert (checkEdges.size() == campusGraph.
                        getEdges(point).size());

            }
        }
    }
}