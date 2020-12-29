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

import React, {Component} from 'react';
import BuildingSelection from "./BuildingSelection";
import PathDirections from "./PathDirections";
import Map from "./Map";
import Button from "@material-ui/core/Button";
import "./App.css";

/**
 * This application is a path finder for the 2006 UW campus map. The
 * user can select the start and destination buildings. The shortest
 * path between the two buildings is then displayed on the map as well
 * as a list of walking directions that can be printed out.
 *
 */

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            buildings: [],

            startInput: "",
            startLong: "",
            startBuildingCoord: {x: 0, y: 0},

            endInput: "",
            endLong: "",
            endBuildingCoord: {x: 0, y: 0},

            pathDistance: 0.0,
            edges: []
        };
    }

    componentDidMount() {
        this.getAllBuildings();
    }

    /**
     * Fetches the mapping of the short names to the long
     * names of the buildings on campus, which is necessary
     * for plotting the building markers before the path is
     * drawn.
     *
     * @effect Sets the state of buildings to an object storing
     *         all the buildings as {shortN, longN, coord}.
     */
    getAllBuildings = async () => {
        try {
            // Fetches all the campus buildings names.
            let response1 = await fetch(
                "http://localhost:4567/campus-map-buildings");
            if (!response1.ok) {
                alert(`Something went wrong. STATUS CODE: ${response1.status}`);
                return;
            }

            let buildingsObject = await response1.json();
            const buildings = [];

            // Fetches all the building coordinates.
            let response2 = await fetch(
                "http://localhost:4567/building-coord");
            if (!response2.ok) {
                alert(`Something went wrong. STATUS CODE: ${response2.status}`);
                return;
            }

            let buildingCoordsObject = await response2.json();

            // Converting to array that maps the building short and long
            // names to their coordinates.
            for (let key of Object.keys(buildingsObject)) {
                buildings.push({shortN: key, longN: buildingsObject[key],
                    coord: buildingCoordsObject[key]});
            }

            this.setState({
                buildings: buildings
            });

        } catch (e) {
            alert("There was an error contacting the server.");
            console.log(e);
        }
    };

    /**
     * Fetches the shortest path between buildings as requested
     * by the user as well as the directions (N, E, S, W, etc.)
     * associated with each segment of the path.
     *
     * @effect Sets the state of edges to an object storing the
     *         path segments as {start: {x1,y1}, end: {x2,y2},
     *         cost, dir}. Sets the state of the path distance to
     *         the total distance.
     */
    makeRequest = async () => {
        try {
            // Fetches the shortest path and the directions.
            let response = await fetch(
                "http://localhost:4567/campus-map?start=" +
                      this.state.startInput + "&end=" + this.state.endInput);
            if (!response.ok) {
                alert("The start and destination must exist.");
                return;
            }

            let pathObject = await response.json();
            const pathInfo = [];
            let totalDistance = 0.0;

            // Translating the data to a more organized object.
            // Have to sum the total distance manually; getting
            // the Direction of each path segment in a single
            // data structure made retrieving the original data
            // from Path not possible.
            for (let i of Object.keys(pathObject)) {
                pathInfo.push({
                    start: pathObject[i].key.start,
                    end: pathObject[i].key.end,
                    cost: pathObject[i].key.cost,
                    dir: pathObject[i].value
                });

                // Sums the total distance.
                totalDistance += pathObject[i].key.cost;
            }

            this.setState({
                edges: pathInfo,
                pathDistance: totalDistance
            });

        } catch (e) {
            alert("There was an error contacting the server.");
            console.log(e);
        }
    };

    /**
     * Captures the start building input from the 1st
     * selection field.
     *
     * @param option: The building option that is selected.
     * @effect Sets the state of the start input, long name,
     *         and coordinates. Sets the state of the path
     *         distance and edges to zero values.
     */
    onStartInputChange = (option) => {
        // Gets the coordinate of the building.
        const coord = this.state.buildings.find(
            bldg => bldg.shortN === option.value).coord;

        this.setState({
            // option.value stores the short name.
            // option.label stores the long name.
            startInput: option.value,
            startLong: option.label,
            startBuildingCoord: coord,

            // When the input changes, the edges reset.
            pathDistance: 0.0,
            edges: [],
        });
    };

    /**
     * Captures the destination building input from the
     * 2nd selection field.
     *
     * @param option: The option that is selected.
     * @effect Sets the state of the dest. input, long name,
     *         and coordinates. Sets the state of the path
     *         distance and edges to zero values.
     */
    onEndInputChange = (option) => {
        // Gets the coordinate of the building.
        const coord = this.state.buildings.find(
            bldg => bldg.shortN === option.value).coord;

        this.setState({
            // option.value stores the short name.
            // option.label stores the long name.
            endInput: option.value,
            endLong: option.label,
            endBuildingCoord: coord,

            // When the input changes, the edges reset.
            pathDistance: 0.0,
            edges: [],
        });
    };

    /**
     * Resets the campus path and all the input fields.
     *
     * @effect Sets all the states to zero values.
     */
    resetRequest = () => {
        this.setState({
            startInput: "",
            startLong: "",
            startBuildingCoord: {x: 0, y: 0},

            endInput: "",
            endLong: "",
            endBuildingCoord: {x: 0, y: 0},

            pathDistance: 0.0,
            edges: [],
        })
    };

    /**
     * Renders the building selection and directions in the left side
     * bar, the campus map on the right, and the footer at the bottom
     * of the interface.
     */
    render() {
        // I do provide the option to clear the edges, but I realized
        // that that does not reset the zoom on the canvas. I couldn't
        // figure out how to make that work though, so I was forced to
        // make the decision to simply refresh the page as another way
        // to "reset" the entire application (the button is located at
        // the bottom-left corner of the screen).
        return (
            <div>
                <div id="sidebar">
                    <div id="sidebar-top">
                        <p id="app-title">EXPLORE THE CAMPUS</p>
                        <BuildingSelection
                            buildings={this.state.buildings}
                            startValue={this.state.startInput}
                            endValue={this.state.endInput}
                            onStartChange={this.onStartInputChange}
                            onEndChange={this.onEndInputChange}
                            onRequest={this.makeRequest}
                            onClear={this.resetRequest}
                        />
                    </div>
                    <PathDirections
                        start={this.state.startLong}
                        end={this.state.endLong}
                        distance={this.state.pathDistance}
                        edges={this.state.edges}
                    />
                </div>
                <Map
                    startBuildingCoord={this.state.startBuildingCoord}
                    endBuildingCoord={this.state.endBuildingCoord}
                    edges={this.state.edges}
                />
                <footer id="footer">
                    <Button
                        id="reset-button"
                        variant="outlined"
                        onClick={() => {window.location.reload()}}
                    >Reset Application
                    </Button>
                </footer>
            </div>
        );
    }
}

export default App;
