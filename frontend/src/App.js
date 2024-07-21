import React, {useState} from "react";
import './App.css';
import Input from "./input";
import GraphComponent from "./graph";
import FunctionsComponent from "./functions";
import TableComponent from "./table";
import CodeComponent from "./code";
import InfoComponent from "./info";
import Drawing from "./drawing";

export const URL = window.location.origin.split(':8080')[0]+':8083';

export default function App() {
    const [drawingMode, setDrawingMode] = useState(false);

    const [generatedRegex, setGeneratedRegex] = useState("");
    const [alternativeRegexForm, setAlternativeRegexForm] = useState("");
    const [minimalRegexForm, setMinimalRegexForm] = useState(null);
    const [elements, setElements] = useState([]);
    //const [transitionFunctions, setTransitionFunctions] = useState(null);
    const [functionsArray, setFunctionsArray] = useState(null);
    const [functionsCount, setFunctionsCount] = useState(0)
    const [transitionTable, setTransitionTable] = useState(null);
    const [validationResult, setValidationResult] = useState(null);

    const sendRegexToJavaBackend = async (value) => {
        try {
            const response = await fetch(
                URL + `/api/input/validate/${encodeURIComponent(value)}`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({input: value}),
                }
            );

            if (response.ok) {
                //const data = await response.text();
                //console.log("Response from Java backend:", data);
                setValidationResult(true);
                await getAlternativeForm(value);
                await getMinimalForm(value);
                await getGraph(value);
                await getTransitionFunctions(value);
                await getTransitionTable(value);
            } else {
                throw new Error("Network response was not ok.");
            }
        } catch (error) {
            console.error("There was a problem with the fetch operation:", error);
            setValidationResult(false);
        }
    };

    const generateRegex = async (numCharacters, length, modified, acceptCluster, acceptAlternative, acceptVolume, acceptTransitive, acceptRepeat) => {
        try {
            const response = await fetch(
                URL + `/api/input/generate/${encodeURIComponent(numCharacters)}/${encodeURIComponent(length)}/${encodeURIComponent(modified)}/${encodeURIComponent(acceptCluster)}/${encodeURIComponent(acceptAlternative)}/${encodeURIComponent(acceptVolume)}/${encodeURIComponent(acceptTransitive)}/${encodeURIComponent(acceptRepeat)}`,
                {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            if (response.ok) {
                const generatedRegex = await response.text();
                //console.log("Alternative form", alternativeRegexForm);
                setGeneratedRegex(generatedRegex);
                sendRegexToJavaBackend(generatedRegex);
            } else {
                throw new Error("Network response was not ok.");
            }
        } catch (error) {
            console.error("There was a problem with the fetch operation:", error);
        }
    };

    const getAlternativeForm = async (value) => {
        try {
            const response = await fetch(
                URL + `/api/input/alternative/${encodeURIComponent(value)}`,
                {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            if (response.ok) {
                const alternativeRegexForm = await response.text();
                //console.log("Alternative form", alternativeRegexForm);
                setAlternativeRegexForm(alternativeRegexForm);
            } else {
                throw new Error("Network response was not ok.");
            }
        } catch (error) {
            console.error("There was a problem with the fetch operation:", error);
        }
    };

    const getMinimalForm = async (value) => {
        try {
            const response = await fetch(URL + `/api/input/minimal/${encodeURIComponent(value)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const minimalRegexForm = await response.text();
                //console.log("Minimal form", minimalRegexForm, ".");
                setMinimalRegexForm(minimalRegexForm);
            } else {
                throw new Error("Network response was not ok.");
            }
        } catch (error) {
            console.error("There was a problem with the fetch operation:", error);
        }
    };

    const getTransitionFunctions = async (value) => {
        try {
            const response = await fetch(URL + `/api/input/functions/${encodeURIComponent(value)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const transitionFunctions = await response.text();
                //console.log(JSON.parse(transitionFunctions));
                //setTransitionFunctions(JSON.parse(transitionFunctions));
                setFunctionsArray(JSON.parse(transitionFunctions).map(transition => {
                    if (transition.input === null) {
                        return `q<sub>${transition.nextState}</sub> = ${transition.value.replaceAll('.', '<span class="token">.</span>')}`;
                    } else if (transition.nextState === null) {
                        return `&delta;(q<sub>${transition.state}</sub>, ${transition.input}) = ${transition.value}`;
                    } else {
                        return `&delta;(q<sub>${transition.state}</sub>, ${transition.input}) = ${transition.value.replaceAll('.', '<span class="token">.</span>')} = q<sub>${transition.nextState}</sub>`;
                    }
                }));
                setFunctionsCount(0);
            } else {
                throw new Error("Network response was not ok.");
            }
        } catch (error) {
            console.error("There was a problem with the fetch operation:", error);
        }
    };

    const getTransitionTable = async (value) => {
        try {
            const response = await fetch(URL + `/api/input/table/${encodeURIComponent(value)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const transitionTable = await response.text();
                //console.log(transitionTable);
                //console.log(JSON.parse(transitionTable));
                //setTransitionTable(JSON.parse(transitionTable));

                const parsedData = JSON.parse(transitionTable);

                const groupedData = parsedData.reduce((acc, item) => {
                    const match = item.id.match(/^(q\d+)[A-Za-z0-9]/); // Extract the prefix excluding the last character
                    if (match) {
                        const prefix = match[1];
                        if (!acc[prefix]) {
                            acc[prefix] = [];
                        }

                        acc[prefix].push({
                            id: item.id,
                            value: item.value,
                            flag: item.flag,
                        });
                    }

                    return acc;
                }, {});

                setTransitionTable(groupedData);

                //console.log(groupedData);
            } else {
                throw new Error("Network response was not ok.");
            }
        } catch (error) {
            console.error("There was a problem with the fetch operation:", error);
        }
    };

    const getGraph = async (value) => {
        try {
            const response = await fetch(URL + `/api/input/graph/${encodeURIComponent(value)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const data = await response.text();
                setElements(JSON.parse(data));
                //console.log(JSON.parse(data));
            } else {
                throw new Error("Network response was not ok.");
            }
        } catch (error) {
            console.error("There was a problem with the fetch operation:", error);
        }
    };

    function openDrawingMode(){
        setDrawingMode(true);
    }

    return (
        <div className="App">
            <div className="navbar">
                {drawingMode !== true ? (
                    <ul className="taskbar">
                        <li onClick={() => setDrawingMode(false)} className="active">Start Page</li>
                        <li onClick={openDrawingMode}>Drawing Page</li>
                    </ul>
                ) : (
                    <ul className="taskbar">
                        <li onClick={() => setDrawingMode(false)}>Start Page</li>
                        <li onClick={() => setDrawingMode(true)} className="active">Drawing Page</li>
                    </ul>
                )}
            </div>

            {drawingMode !== true ? (
                <div className="body">
                    <Input generatedRegex={generatedRegex}
                           setGeneratedRegex={setGeneratedRegex}
                           alternativeRegexForm={alternativeRegexForm}
                           setAlternativeRegexForm={setAlternativeRegexForm}
                           minimalRegexForm={minimalRegexForm}
                           setMinimalRegexForm={setMinimalRegexForm}
                           validationResult={validationResult}
                           setValidationResult={setValidationResult}
                           setElements={setElements}
                           setFunctionsArray={setFunctionsArray}
                           setFunctionsCount={setFunctionsCount}
                           setTransitionTable={setTransitionTable}
                           sendRegexToJavaBackend={sendRegexToJavaBackend}
                           generateRegex={generateRegex}/>

                    {alternativeRegexForm !== null ? (
                        alternativeRegexForm !== "" ? (
                            <div>
                                <GraphComponent elements={elements}/>
                                <div className="transition-function-and-table">
                                    <FunctionsComponent functionsArray={functionsArray}
                                                        functionsCount={functionsCount}
                                                        setFunctionsCount={setFunctionsCount}/>
                                    <TableComponent transitionTable={transitionTable}
                                                    functionsCount={functionsCount}/>
                                </div>
                                <CodeComponent generatedRegex={generatedRegex}/>
                            </div>
                        ) : (
                            <InfoComponent/>
                        )
                    ) : (
                        <InfoComponent/>
                    )}
                </div>
            ) : (
                <div className="body">
                    <Drawing URL={URL}
                             setDrawingMode={setDrawingMode}
                             setValidationResult={setValidationResult}
                             sendRegexToJavaBackend={sendRegexToJavaBackend}
                             getAlternativeForm={getAlternativeForm}
                             getMinimalForm={getMinimalForm}
                             getGraph={getGraph}
                             setGeneratedRegex={setGeneratedRegex}
                    />
                </div>
            )}
        </div>
    );
}
