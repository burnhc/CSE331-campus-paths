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
import {TransformComponent, TransformWrapper} from "react-zoom-pan-pinch";
import IconButton from '@material-ui/core/IconButton';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import AddRoundedIcon from '@material-ui/icons/AddRounded';
import RemoveRoundedIcon from '@material-ui/icons/RemoveRounded';
import SettingsOverscanRoundedIcon from '@material-ui/icons/SettingsOverscanRounded';
import "./App.css";

/**
 * The Map component displays the 2006 campus map from
 * campus_map.jpg and the shortest path requested by the user
 * drawn over it.
 *
 * PROPS:
 * startBuildingCoord: The start coordinate as {x,y}
 * endBuildingCoord:   The destination coordinate as {x,y}
 * edges:              The path segments as
 *                     {start: {x1,y1}, end: {x2,y2}, cost, dir}
 *
 */

class Map extends Component {
    constructor(props) {
        super(props);
        this.state = {
            backgroundImage: null,
            resetTransform: true
        };

        this.canvas = React.createRef();
    }

    /**
     * Loads ands draws the background image when the
     * component first mounts.
     */
    componentDidMount() {
        this.fetchAndSaveImage();
        this.drawBackgroundImage();
    }

    /**
     * Redraws the canvas each time the component is
     * updated.
     */
    componentDidUpdate() {
        this.redraw();
    }

    /**
     * Loads the background image.
     *
     * @effect Sets the state of the background image to
     *         the campus map image.
     */
    fetchAndSaveImage() {
        let background = new Image();

        background.onload = () => {
            this.setState({
                backgroundImage: background
            });
        };

        background.src = "./campus_map.jpg";
    }

    /**
     * Redraws the background image, edges, and the
     * start and destination markers on the canvas.
     */
    redraw() {
        let canvas = this.canvas.current;
        let ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // drawStartAndEnd is after drawEdges so that the markers
        // display over the edges in the canvas.
        this.drawBackgroundImage(ctx, canvas);
        this.drawEdges(ctx);
        this.drawStartAndEnd(ctx);
    }

    /**
     * Draws the background image.
     *
     * @param ctx:    The context of the current canvas.
     * @param canvas: The current canvas.
     * @requires this.state.backgroundImage !== null
     */
    drawBackgroundImage(ctx, canvas) {
        if (this.state.backgroundImage !== null) {
            canvas.width = this.state.backgroundImage.width;
            canvas.height = this.state.backgroundImage.height;
            ctx.drawImage(this.state.backgroundImage, 0, 0);
        }
    }

    /**
     * Draws the start and end markers on the canvas
     * as the user selects them.
     *
     * @param ctx: The context of the current canvas.
     */
    drawStartAndEnd(ctx) {
        // I found that an offset is necessary to plot the
        // building markers as the building coordinates
        // provided do not precisely map onto the campus
        // map image.
        const offsetX = 75;
        const offsetY = 120;

        // The marker image.
        const img = new Image();
        img.src = "./pin.png";

        // Resizes the marker to 100px x 136.15px.
        const pinWidth = 100;
        const pinHeight = 136.15;

        // (x1, y1) is for the start marker.
        const x1 = this.props.startBuildingCoord.x;
        const y1 = this.props.startBuildingCoord.y;

        // (x2, y2) is for the destination marker.
        const x2 = this.props.endBuildingCoord.x;
        const y2 = this.props.endBuildingCoord.y;

        ctx.drawImage(img, (x1 - offsetX), (y1 - offsetY),
            pinWidth, pinHeight);

        ctx.drawImage(img, (x2 - offsetX), (y2 - offsetY),
            pinWidth, pinHeight);
    }

    /**
     * Draws the start and end markers on the canvas
     * as the user selects them.
     *
     * @param ctx: The context of the current canvas.
     */
    drawEdges(ctx) {
        const edges = this.props.edges;

        for (let edge of edges) {

            // (x1, y1) is for the start of the path
            // segment.
            const x1 = edge.start.x;
            const y1 = edge.start.y;

            // (x2, y2) is for the end of the path
            // segment.
            const x2 = edge.end.x;
            const y2 = edge.end.y;

            // Draw the path segment in purple.
            ctx.beginPath();
            ctx.strokeStyle = "rebeccapurple";
            ctx.lineWidth = 15;
            ctx.lineCap = "round";
            ctx.moveTo(x1, y1);
            ctx.lineTo(x2, y2);
            ctx.stroke();
            ctx.closePath();
        }
    }

    /**
     * Renders the canvas on the right side of the interface.
     * Draws markers labeling the start and dest. buildings
     * and the path on the map between them. The canvas has
     * controls that allows the user to interact with the map
     * by zooming and panning, using either the buttons at the
     * upper-left corner or their mouse.
     */
    render() {
        // The TransformWrapper is a library that allows
        // for zooming and panning on the canvas element.
        return (
            <TransformWrapper
                defaultScale={1}
                pan={{paddingSize: 0}}
            >
            {({zoomIn, zoomOut, resetTransform}) => (
                <React.Fragment>
                    <div id="map-tools">
                        <ButtonGroup
                            orientation="vertical">
                            <IconButton
                                onClick={zoomIn}>
                                <AddRoundedIcon />
                            </IconButton>
                            <IconButton
                                onClick={zoomOut}>
                                <RemoveRoundedIcon />
                            </IconButton>
                            <IconButton
                                onClick={resetTransform}>
                                <SettingsOverscanRoundedIcon />
                            </IconButton>
                        </ButtonGroup>
                    </div>
                    <TransformComponent>
                        <canvas
                            ref={this.canvas}/>
                    </TransformComponent>
                </React.Fragment>
            )}
            </TransformWrapper>
        );
    }
}

export default Map;