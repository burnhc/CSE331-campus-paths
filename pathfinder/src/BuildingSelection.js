import React, {Component} from 'react';
import Select from 'react-select';
import Button from '@material-ui/core/Button';
import ExploreIcon from '@material-ui/icons/Explore';
import "./App.css";

/**
 * The BuildingSelection component contains selection fields for
 * the user to choose the start and destination buildings.
 *
 * PROPS:
 * buildings:     An object storing all the buildings as
 *                {shortN, longN, coord}
 * startValue:    The input in the start selection field
 * endValue:      The input in the destination selection field
 * onStartChange: onStartInputChange() in App.js
 * onEndChange:   onEndInputChange() in App.js
 * onRequest:     makeRequest() in App.js
 * onClear:       resetRequest() in App.js
 *
 */

class BuildingSelection extends Component {

    constructor(props) {
        super(props);
        this.state = {
            startValue: "",
            endValue: ""
        };
    }

    /**
     * Updates the start (1st) field.
     *
     * (This seems redundant, but I found this necessary
     * instead of just passing the prop as an onChange
     * event. If I just did that, then the value displayed
     * inside the Selection field would remain blank.)
     *
     * @param newValue: The value that gets selected.
     * @effect Sets the state of the start value to the new value.
     */
    updateStart = (newValue) => {
        this.setState({
             startValue: newValue
        });

        this.props.onStartChange(newValue);
    };

    /**
     * Updates the destination (2nd) field.
     *
     * @param newValue: The value that gets selected.
     * @effect Sets the state of the end value to the new value.
     */
    updateEnd = (newValue) => {
        this.setState({
            endValue: newValue
        });

        this.props.onEndChange(newValue);
    };

    /**
     * Makes the request when the 'Get Directions'
     * button is pressed and the start and destination
     * buildings are both selected.
     *
     * @requires this.props.startValue is not empty and
     *           this.props.endValue is not empty.
     */
    onRequest() {
        if (this.props.startValue !== "" &&
            this.props.endValue !== "") {

            this.props.onRequest();
        }
    }

    /**
     * Resets the values in the selection fields and
     * make the request to reset the internal data as well.
     *
     * @effect Sets the state of the start and end values
     *         to null.
     */
    onReset(){
        this.setState({
            startValue: null,
            endValue: null
        });

        this.props.onClear();
    }

    /**
     * Renders two selection fields that display the buildings'
     * long names in a dropdown menu for the user to select from.
     * Underneath the fields are two buttons: one to get directions
     * and the other to clear the path.
     */
    render() {
        // Options for the selection fields that maps the
        // long names (for display) to the short names (for
        // the actual values associated with each name).
        const options = this.props.buildings.map(
            opt => ({label: opt.longN, value: opt.shortN}));

        // Customizes the dropdown menu of the selection field
        // for when there are no building suggestions.
        const NoOptionsMessage = () => {
            return (
                <p id="no-options-message">Building not found.
                    Try another one!</p>
            );
        };

        // The selection fields can either be typed into or
        // selected from.
        return (
            <div id="building-selection">
                <Select
                    id="select"
                    value={this.state.startValue}
                    options={options}
                    onChange={this.updateStart}
                    components={{NoOptionsMessage}}
                    placeholder={"Select start building..."}
                    theme={theme => ({
                        ...theme,
                        colors: {
                            ...theme.colors,
                            primary: '#4b2e83',
                            primary25: '#e6e1f0',
                        },
                    })}
                />
                <Select
                    id="select"
                    value={this.state.endValue}
                    options={options}
                    onChange={this.updateEnd}
                    components={{NoOptionsMessage}}
                    placeholder={"Select destination building..."}
                    theme={theme => ({
                        ...theme,
                        colors: {
                            ...theme.colors,
                            primary: '#4b2e83',
                            primary25: '#e6e1f0',
                        },
                    })}
                />
                <br/>
                <Button
                    id="get-directions-button"
                    variant="contained"
                    startIcon={<ExploreIcon />}
                    onClick={() => {this.onRequest();}}>Get Directions
                </Button>
                <Button
                    id="clear-button"
                    variant="text"
                    onClick={() => {this.onReset();}}>Clear
                </Button>
            </div>
        );
    }
}

export default BuildingSelection;