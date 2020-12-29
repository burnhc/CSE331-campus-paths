import React, {Component} from 'react';
import PrintDirections from "./PrintDirections";
import ReactToPrint from "react-to-print";
import IconButton from "@material-ui/core/IconButton";
import PrintIcon from '@material-ui/icons/Print';
import "./App.css";

/**
 * The PathDirections component is a list of directions produced
 * after the path has been found.
 *
 * PROPS:
 * start:    The long name of the start building
 * end:      The long name of the destination building
 * distance: The total distance of the path
 * edges:    The path segments as
 *           {start: {x1,y1}, end: {x2,y2}, cost, dir}
 *
 */

class PathDirections extends Component {

    /**
     * Translates the direction abbreviation to
     * the full word (e.g. "N" to "north").
     *
     * @returns {string} The full name of the direction.
     */
    printDir(dir) {
        const directions = {
            NORTH: 'N',
            NORTHWEST: 'NW',
            NORTHEAST: 'NE',
            SOUTH: 'S',
            SOUTHWEST: 'SW',
            SOUTHEAST: 'SE',
            EAST: 'E',
            WEST: 'W'
        };

        let direction = "";

        switch(dir) {
            case directions.NORTH:
                direction = "north";
                break;
            case directions.NORTHWEST:
                direction = "northwest";
                break;
            case directions.NORTHEAST:
                direction = "northeast";
                break;
            case directions.SOUTH:
                direction = "south";
                break;
            case directions.SOUTHWEST:
                direction = "southwest";
                break;
            case directions.SOUTHEAST:
                direction = "southeast";
                break;
            case directions.EAST:
                direction = "east";
                break;
            case directions.WEST:
                direction = "west";
                break;
            //no default
            }

        return direction;
    }

    /**
     * Produces the directions when the fields are non-empty
     * and the start and destination buildings are unique
     * (distance > 0). The directions are in the form:
     * "{step #}. Walk {x} feet {direction}."
     *
     * @returns {object} The output as an object of strings with
     *          the fields {header, subheader, body, footer}.
     */
    output() {
        let output = {
            header: "",
            subheader: "",
            body: "",
            footer: ""
        };

        if (this.props.distance > 0) {

            // Average walking speed is ~272.8 ft/min.
            const speed = 272.8;

            // Distance is in feet.
            const distance = Math.round(this.props.distance);

            // Time is in minutes.
            const time = Math.round(this.props.distance / speed);

            output.header = `${this.props.start} to ${this.props.end}`;
            output.subheader = `estimated travel time: ${time}`;

            // Switches the plural/singular form of "minute"
            // depending on the number of minutes.
            if (time <= 1) {
                output.subheader += " minute";
            } else {
                output.subheader += " minutes";
            }

            // stepNum produces the step number as a prefix for
            // each direction.
            let stepNum = 0;
            for (let segment of this.props.edges) {
                stepNum++;
                const segmentOutput =
                    `Walk ${Math.round(segment.cost)} feet ${this.printDir(segment.dir)}.`;

                output.body += `${stepNum}.\t ${segmentOutput}\n`;
            }

            output.footer = `${distance} feet`;

        }

        return output;
    }

    /**
     * Renders a scrollable list of directions from
     * the path segments underneath the selection fields,
     * as well as the option to print the list out.
     */
    render() {
        const output = {
            header: this.output().header,
            subheader: this.output().subheader,
            body: this.output().body,
            footer: this.output().footer
        };

        // This flag indicates when to switch between displaying
        // the default input and the directions.
        const hidden = (output.body === "");

        // The div containing PrintDirections is hidden because
        // that component is not to be displayed, but still be
        // stored in the DOM to be printed out.
        return (
            <div
                id="directions-wrapper">
                <p id="directions-header">{output.header}</p>
                <div hidden={hidden}>
                    <ReactToPrint
                        trigger={() =>
                            <IconButton
                                id="print-button">
                                <PrintIcon />
                            </IconButton>}
                        content={() => this.printRef}
                    />
                </div>

                <p id="directions-default" hidden={!hidden}>
                    Please select two different buildings to find directions.</p>
                <div hidden={hidden}>
                    <hr/>
                    <p id="directions-subheader">{output.subheader}</p>
                    <p id="directions-body">{output.body}</p>
                    <p id="directions-footer">
                        <strong>Total walking distance: </strong>
                        {output.footer}</p>
                </div>

                <div hidden={true}>
                    <PrintDirections
                        ref={el => (this.printRef = el)}
                        header={output.header}
                        subheader={output.subheader}
                        body={output.body}
                        footer={output.footer}
                    />
                </div>
            </div>
        );
    }
}

export default PathDirections;