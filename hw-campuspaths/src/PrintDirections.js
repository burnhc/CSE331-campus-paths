import React, {Component} from 'react';
import "./App.css";

/**
 * The PrintDirections component reformats the directions in
 * a printer-friendly format. This component remains hidden but
 * is displayed when the user presses the printer button upon
 * finding directions.
 *
 * PROPS:
 * header:    The header, containing the start and dest. buildings
 * subheader: The subheader, containing the estimated travel time
 * body:      The body, containing the list of directions
 * footer:    The footer, containing the total distance
 *
 */

class PrintDirections extends Component {

    render() {
        return (
            <div id="directions-print">
                <p id="directions-header">{this.props.header}</p>
                <p id="directions-subheader">{this.props.subheader}</p>
                <p id="directions-body-print">{this.props.body}</p>
                <p id="directions-footer-print">
                    <strong>Total walking distance: </strong>
                    {this.props.footer}</p>
            </div>
        );
    }
}

export default PrintDirections;