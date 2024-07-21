import React, {useRef} from "react";
import './graph.css'
import CytoscapeComponent from "react-cytoscapejs";

import Save from "./save";

const GraphComponent = ({elements}) => {
    const cyRef = useRef(null);

    const resetView = async () => {
        cyRef.current.fit();
    }

    return (
        <div className="transition-diagram">
            <CytoscapeComponent className="transition-diagram-field"
                                elements={elements}
                                stylesheet={[
                                    {
                                        selector: "node",
                                        style: {
                                            label: "data(value)",
                                            "text-halign": "center",
                                            "text-valign": "center",
                                            "background-color": "#f0f0f0",
                                            "border-width": 2,
                                            "border-color": "#000000",
                                        },
                                    },
                                    {
                                        selector: "node[flag='empty']",
                                        style: {
                                            width: 3,
                                            height: 3,
                                            label: "",
                                            "border-width": 0,
                                            "background-color": "#000000",
                                        },
                                    },
                                    {
                                        selector: "node[flag='terminal']",
                                        style: {},
                                    },
                                    {
                                        selector: "edge",
                                        style: {
                                            width: 3,
                                            "line-color": "#000000",
                                            "target-arrow-color": "#000000",
                                            "target-distance-from-node": 15,
                                            "target-arrow-shape": "triangle",
                                            "curve-style": "taxi",

                                            //failed "S" shape settings
                                            "target-endpoint": [0, 0],
                                            "source-endpoint": [0, 0],
                                            // "curve-style": "unbundled-bezier",
                                            "control-point-distances": [-0, -80],
                                            "control-point-weights": [0.1, 0.1],
                                        },
                                    },
                                    {
                                        selector: "edge[flag='normal']",
                                        style: {},
                                    },
                                    {
                                        selector: "edge[flag='sourceEmpty']",
                                        style: {
                                            "source-distance-from-node": 0,
                                        },
                                    },
                                    {
                                        selector: "edge[flag='targetEmpty']",
                                        style: {
                                            "target-distance-from-node": 0,
                                        },
                                    },
                                    {
                                        selector: "edge[flag='targetEmptyWithoutArrow']",
                                        style: {
                                            "target-arrow-shape": "none",
                                            "target-distance-from-node": 0,
                                        },
                                    },
                                    {
                                        selector: "edge[flag='bothEmptyWithoutArrow']",
                                        style: {
                                            "target-arrow-shape": "none",
                                            "target-distance-from-node": 0,
                                            "source-distance-from-node": 0,
                                        },
                                    },
                                    {
                                        selector: "edge[flag='bothEmpty']",
                                        style: {
                                            "target-distance-from-node": 0,
                                            "source-distance-from-node": 0,
                                        },
                                    },
                                ]}
                                cy={(cy) => {
                                    cyRef.current = cy;
                                    cy.fit();
                                }}
            />
            <div className="side-button-page">
                <button onClick={resetView}>Reset diagram view</button>
                <Save cyRef={cyRef}/>
            </div>
        </div>
    );
};

export default GraphComponent;
