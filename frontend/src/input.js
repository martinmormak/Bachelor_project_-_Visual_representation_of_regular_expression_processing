import React from "react";
import "./input.css";

const Input = ({
                   generatedRegex,
                   setGeneratedRegex,
                   alternativeRegexForm,
                   setAlternativeRegexForm,
                   minimalRegexForm,
                   setMinimalRegexForm,
                   validationResult,
                   setValidationResult,
                   setElements,
                   setFunctionsArray,
                   setFunctionsCount,
                   setTransitionTable,
                   sendRegexToJavaBackend,
                   generateRegex
               }) => {

    const handleInputChange = (event) => {
        setGeneratedRegex(event.target.value);
        if (event.target.value === null || event.target.value === "") {
            setValidationResult(null);
            setAlternativeRegexForm(null);
            setMinimalRegexForm(null);
        } else {
            sendRegexToJavaBackend(event.target.value);
        }
    };

    const getRandomInt = (min, max) => {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    };

    const genRegex = () => {
        const length = getRandomInt(10,15);
        generateRegex(getRandomInt(length,62),length,false,true,true,true,true,true);
    };

    const clearRegex = () => {
        setGeneratedRegex("");
        setValidationResult(null);
        setMinimalRegexForm(null);
        setAlternativeRegexForm(null);
        setElements([]);
        setFunctionsArray(null);
        setFunctionsCount(0);
        setTransitionTable(null);
    };

    const inputStyle = {
        borderColor:
            validationResult === true
                ? "green"
                : validationResult === false
                    ? "red"
                    : "white",
        outline: "none",
    };

    return (
        <div>
            <div className="regex-input-textbox">
                <h3 htmlFor="regex">Input regular expression</h3>
                <input
                    type="text"
                    title={"regexInput"}
                    placeholder={"regular(expression)?"}
                    value={generatedRegex}
                    onChange={handleInputChange}
                    style={inputStyle}
                ></input>
            </div>
            <div className="regex-input-buttons">
                <button onClick={genRegex}>Generate Example Regex</button>
                <button onClick={clearRegex}>Clear Input</button>
            </div>
            <div className="regex-informative-text">
                {validationResult === true ? (
                    alternativeRegexForm !== null ? (
                        alternativeRegexForm !== "" ? (
                            <div className="alternative_regex_form">
                                <p>Alternative form</p>
                                <p>{alternativeRegexForm}</p>
                            </div>
                        ) : (
                            <div className="no-regex-entered">
                                <p>No regex entered</p>
                                <p>Please enter your regex</p>
                            </div>
                        )
                    ) : (
                        <div className="no-regex-entered">
                            <p>No regex entered</p>
                            <p>Please enter your regex</p>
                        </div>
                    )
                ) : (
                    generatedRegex !== "" ? (
                        <div className="wrong-regex-entered">
                            <p>Wrong regex entered</p>
                            <p>Please correct your regex</p>
                        </div>
                    ) : (
                        <div className="no-regex-entered">
                            <p>No regex entered</p>
                            <p>Please enter your regex</p>
                        </div>
                    )
                )}
                {minimalRegexForm !== null && validationResult === true &&
                    (minimalRegexForm !== "" ? (
                        <div className="minimal_regex_form">
                        <p>Minimal form</p>
                            <p>{minimalRegexForm}</p>
                        </div>
                    ) : (
                        generatedRegex !== "" && (
                            <div className="regex-in-minimal-form">
                                <p>Entered regex have minimal form</p>
                            </div>
                        )
                    ))}
            </div>
        </div>
    );
};

export default Input;
