import React, {useEffect, useRef, useState} from "react";
import cytoscape from "cytoscape";
import edgehandles from "cytoscape-edgehandles";
import './drawing.css';

cytoscape.use(edgehandles);

const Drawing = ({
                     URL,
                     setDrawingMode,
                     setValidationResult,
                     sendRegexToJavaBackend,
                     getAlternativeForm,
                     getMinimalForm,
                     getFromJavaBackend,
                     setGeneratedRegex,
                     getTransitionFunctions,
                     getTransitionTable,
                     drawingInfoPage,
                     setDrawingInfoPage
                 }) => {

    const cyRef = useRef();
    const [cy, setCy] = useState(null);
    const [nodeID] = useState(1);
    const [alternativeNodeID, setAlternativeNodeID] = useState(1);
    const [volumeNodeID, setVolumeNodeID] = useState(1);
    const [transparencyNodeID, setTransparencyNodeID] = useState(1);
    const [nodes] = useState([
        {
            data: {id: "start", value: "s", flag: "empty"},
            position: {x: 0, y: 0},
        },
    ]);

    const [edges] = useState();

    function getXPosition() {
        var x = nodes[0].position.x;
        if (cy != null && cy.nodes != null) {
            cy.nodes().forEach(function (node) {
                var position = node.position();
                if (position.x > x) {
                    x = position.x
                }
            });
        }
        return x;
    }

    function getYPosition() {
        return nodes[0].position.y;
    }

    const generateRegex = async () => {
        //console.log(cy.json())
        //console.log(cy.json().elements)
        try {
            const response = await fetch(
                URL + `/api/graph/validate/`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(cy.json().elements),
                }
            );

            if (response.ok) {
                const data = await response.text();
                //console.log("Response from Java backend:", data);
                setDrawingMode(false)
                setValidationResult(true);
                setGeneratedRegex(data)
                sendRegexToJavaBackend(data)
                /*getAlternativeForm();
                getMinimalForm();
                getFromJavaBackend();
                getTransitionFunctions();
                getTransitionTable();*/
            } else {
                throw new Error("Network response was not ok.");
            }
        } catch (error) {
            console.error("There was a problem with the fetch operation:", error);
        }
    };

    const createAlternative = () => {
        const x = getXPosition();
        const y = getYPosition();
        cy.add([
            {
                group: "nodes",
                data: {
                    id: "alternativeStart" + alternativeNodeID,
                    value: "a",
                    flag: "empty",
                },
                position: {x: x + 150, y: y},
            },
        ]);
        cy.add([
            {
                group: "nodes",
                data: {
                    id: "alternativeEnd" + alternativeNodeID,
                    value: "a",
                    flag: "empty",
                },
                position: {x: x + 300, y: y},
            },
        ]);
        setAlternativeNodeID(alternativeNodeID + 1)
    }
    const createVolume = () => {
        const x = getXPosition();
        const y = getYPosition();
        cy.add([
            {
                group: "nodes",
                data: {
                    id: "volumeStart" + volumeNodeID,
                    value: "v",
                    flag: "empty",
                },
                position: {x: x + 150, y: y},
            },
        ]);
        cy.add([
            {
                group: "nodes",
                data: {
                    id: "volumeEnd" + volumeNodeID,
                    value: "v",
                    flag: "empty",
                },
                position: {x: x + 300, y: y},
            },
        ]);
        cy.add([
            {
                group: "edges",
                data: {
                    id: "volumeStart" + volumeNodeID + "volumeEnd" + volumeNodeID,
                    value: "volume",
                    flag: "targetEmpty",
                    source: "volumeStart" + volumeNodeID,
                    target: "volumeEnd" + volumeNodeID,
                },
            },
        ]);
        setVolumeNodeID(volumeNodeID + 1)
    }
    const createTransparency = () => {
        const x = getXPosition();
        const y = getYPosition();
        cy.add([
            {
                group: "nodes",
                data: {
                    id: "transparencyStart" + transparencyNodeID,
                    value: "t",
                    flag: "empty",
                },
                position: {x: x + 150, y: y},
            },
        ]);
        cy.add([
            {
                group: "nodes",
                data: {
                    id: "transparencyEnd" + transparencyNodeID,
                    value: "t",
                    flag: "empty",
                },
                position: {x: x + 300, y: y},
            },
        ]);
        cy.add([
            {
                group: "edges",
                data: {
                    id: "transparencyStart" + transparencyNodeID + "transparencyEnd" + transparencyNodeID,
                    value: "transparency",
                    flag: "targetEmpty",
                    target: "transparencyEnd" + transparencyNodeID,
                    source: "transparencyStart" + transparencyNodeID,
                },
            },
        ]);
        setTransparencyNodeID(transparencyNodeID + 1)
    }

    function findCorrespondingNode(nodeId) {
        const parts = nodeId.split(/(Start|End)/);
        if (parts.length === 3) {
            const alternativePrefix = parts[0];
            const number = parts[2];
            const correspondingNode = alternativePrefix + (parts[1] === "Start" ? "End" : "Start") + number;
            //console.log(correspondingNode)
            return correspondingNode;
        }
        return null;
    }

    const resetView = async () => {
        cy.fit();
    }

    useEffect(() => {
        const newCy = cytoscape({
            container: cyRef.current,
            style: [
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
                        label: "data(id)",
                        "text-valign": "top",
                        "border-width": 0,
                        "background-color": "#000000",
                    },
                },
                {
                    selector: "node[id='start']",
                    style: {
                        label: "data(id)",
                        "text-valign": "top",
                    },
                },
                {
                    selector: "edge",
                    style: {
                        width: 3,
                        label: "data(value)",
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
            ],
            elements: {nodes, edges},
            //layout: layoutConfig
        });

        newCy.on("ehcomplete", (event, sourceNode, targetNode, addedEdge) => {
            /*
             * When a new link is created, create a new node, add a label, and update the edges list
             */
            /*const newEdges = newCy.edges().map((edge) => edge.data());
            setEdges(newEdges);*/

            const source = sourceNode.data().id;
            const target = targetNode.data().id;
            //console.log(source);
            //console.log(target);
            //console.log(event);
            //console.log(sourceNode);
            //console.log(targetNode);
            //console.log(addedEdge);

            if (targetNode.data().id === "start") {
                newCy.remove(addedEdge);
            }

            if (targetNode.data().flag === "empty") {
                newCy.remove(addedEdge);

                newCy.add([
                    {
                        group: "edges",
                        data: {
                            id: source + target,
                            flag: "targetEmpty",
                            source: source,
                            target: target,
                        },
                    },
                ]);
            }
        });

        let nodeid = 1;

        // Handle left click to add a new node
        newCy.on("tap", (event) => {
            if (event.target === newCy) {
                const currentNodeId = nodeid++;
                const position = event.position;

                //console.log(nodeID);

                let label = prompt("Enter label for the new node:");

                while (label !== null && label.length !== 1 && label.length !== 0) {
                    label = prompt(
                        "Please enter either 0 or 1 characters.\nEnter label for the new node:"
                    );

                    if (label === null) {
                        // Handle Cancel button or closing the prompt
                        break;
                    }

                    if (label.length !== 1 && label.length !== 0) {
                        alert("Please enter either 0 or 1 characters.");
                    }
                }
                //console.log(label);

                if (label !== null && label.trim() !== "") {
                    newCy.add([
                        {
                            group: "nodes",
                            data: {
                                id: currentNodeId,
                                value: label,
                                flag: "terminal",
                            },
                            position,
                        },
                    ]);
                }
            }
        });

        // Handle right click to remove a node
        newCy.on("cxttap", (event) => {
            try {
                const clickedElement = event.target;

                if (clickedElement.isNode() && clickedElement.data().id !== "start") {
                    const nodeIdToDelete = clickedElement.id();

                    // Check if the clicked element is an "alternative" node
                    if (nodeIdToDelete.startsWith("alternative") || nodeIdToDelete.startsWith("volume") || nodeIdToDelete.startsWith("transparency")) {
                        const correspondingNode = findCorrespondingNode(nodeIdToDelete);
                        if (correspondingNode) {
                            newCy.remove(`node#${nodeIdToDelete}`);
                            newCy.remove(`node#${correspondingNode}`);
                        }
                    } else {
                        newCy.remove(`node#${nodeIdToDelete}`);
                    }
                } else if (clickedElement.isEdge()) {
                    const edgeIdToDelete = clickedElement.id();
                    if (clickedElement.data().value !== "volume" && clickedElement.data().value !== "transparency") {
                        newCy.remove(`edge#${edgeIdToDelete}`);
                    }
                }
            } catch (error) {
            }
        });


        document.getElementById("draw-on").style.display = "inline";
        document.getElementById("draw-off").style.display = "none";

        var eh = newCy.edgehandles();

        document
            .querySelector("#draw-on")
            .addEventListener("click", function () {
                eh.enableDrawMode();
                document.getElementById("draw-on").style.display = "none";
                document.getElementById("draw-off").style.display = "inline";
            });
        document
            .querySelector("#draw-off")
            .addEventListener("click", function () {
                eh.disableDrawMode();
                document.getElementById("draw-on").style.display = "inline";
                document.getElementById("draw-off").style.display = "none";
            });

        setCy(newCy)
        return () => {
            newCy.destroy();
        };
    }, [nodes, edges, nodeID]);

    return (
        <div>
            <div>
                <button id="draw-on">Draw mode on</button>
                <button id="draw-off">Draw mode off</button>
                <button onClick={resetView}>Reset diagram view</button>
                <button onClick={generateRegex}>Convert graph to regex</button>
                <button onClick={createAlternative}>Crate alternative</button>
                <button onClick={createVolume}>Crate volume</button>
                <button onClick={createTransparency}>Crate transparency</button>
            </div>
            <div className="drawing-part-with-info">
                <div ref={cyRef} style={{height: "90vh",width: "100%", borderRadius: "1rem", backgroundColor: "#f0f0f0"}}></div>
                <div className="info-field">
                    <div className="notations">Building instructions</div>
                    <div className="info-table">
                        <table className="info-build">
                            <tr>
                                <td className="number">1.</td>
                                <td className="text">To create a node left mouse button tap the canvas, then enter only one character</td>
                            </tr>

                            <tr>
                                <td className="number">2.</td>
                                <td className="text">To create edges first turn on draw mode</td>
                            </tr>

                            <tr>
                                <td className="number">3.</td>
                                <td className="text">To connect nodes drag from source state to target</td>
                            </tr>

                            <tr>
                                <td className="number">4.</td>
                                <td className="text">To create alternative, transition or volume click on their button</td>
                            </tr>

                            <tr>
                                <td className="number">5.</td>
                                <td className="text">To change diagram back to the regex click on Convert graph to regex button</td>
                            </tr>

                            <tr>
                                <td className="number">6.</td>
                                <td className="text">On lost diagram you can reset view with Reset diagram view button</td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Drawing;
