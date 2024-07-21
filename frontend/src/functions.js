import React, {useEffect} from "react";
import "./functions.css";

const FunctionsComponent = ({functionsArray, functionsCount, setFunctionsCount}) => {
    useEffect(() => {
        const functionDisplay = document.getElementById("functionDisplay");
        generateFunctionHTML();
        var functions = document.querySelectorAll('.function-container');
        functions.forEach(function (func, i) {
            func.style.display = i <= functionsCount ? 'block' : 'none';
        });
        addEventListenerToDynamicElement();

        function generateFunctionHTML() {
            if (functionsArray != null && functionDisplay != null) {
                var html = '';
                for (var i = 0; i < functionsArray.length; i++) {
                    var inputString = functionsArray[i];
                    var match = inputString.match(/q<sub>(\d+)<\/sub>,\s*([a-zA-Z0-9])/);
                    if (match) {
                        var number = match[1];
                        var letter = match[2];
                        html += '<div class="function-container" id="q' + number + '-function">';
                        html += '<a class="function" id="q' + number + letter + '-function">' + functionsArray[i] + '</a>';
                        html += '</div>';
                    } else {
                        //console.log(inputString);
                        html += '<div class="function-container" id="q0-function">';
                        html += '<a class="function" id="q0-function">' + functionsArray[i] + '</a>';
                        html += '</div>';
                    }
                }
                functionDisplay.innerHTML = html;
            } else if (functionDisplay != null) {
                functionDisplay.innerHTML = null;
            }
        }

        function addEventListenerToDynamicElement() {
            // Wait for the DOMContentLoaded event
            var dynamicElements = document.querySelectorAll('.transition-function-functions a');

            dynamicElements.forEach(function (dynamicElement) {
                dynamicElement.addEventListener('mouseover', function () {
                    // Get the full function ID from the hovered function
                    var fullFunctionId = dynamicElement.id;
                    //console.log(fullFunctionId)

                    // Split the ID using the '-' character
                    var parts = fullFunctionId.split('-');

                    // Extract the relevant part (assuming it's always the first part)
                    var functionId = parts[0];

                    // Form the corresponding result cell ID in the table
                    var resultCellId = functionId + '-cell';
                    //console.log(resultCellId)

                    // Find the corresponding result cell in the table
                    var resultCell = document.getElementById(resultCellId);
                    //console.log(resultCell)

                    // Add your action here, for example, changing the background color
                    if (resultCell) {
                        dynamicElement.style.backgroundColor = '#525B76CC';
                        dynamicElement.style.color = 'white';
                        resultCell.style.backgroundColor = '#525B76CC';
                        resultCell.style.color = 'white';
                    }
                });

                dynamicElement.addEventListener('mouseout', function () {
                    // Get the full function ID from the hovered function
                    var fullFunctionId = dynamicElement.id;
                    //console.log(fullFunctionId)

                    // Split the ID using the '-' character
                    var parts = fullFunctionId.split('-');

                    // Extract the relevant part (assuming it's always the first part)
                    var functionId = parts[0];

                    // Form the corresponding result cell ID in the table
                    var resultCellId = functionId + '-cell';
                    //console.log(resultCellId)

                    // Find the corresponding result cell in the table
                    var resultCell = document.getElementById(resultCellId);
                    //console.log(resultCell)

                    // Add your action here, for example, changing the background color
                    if (resultCell) {
                        dynamicElement.style.backgroundColor = '';
                        dynamicElement.style.color = 'black';
                        resultCell.style.backgroundColor = '';
                        resultCell.style.color = 'black';
                    }
                });
            });
        }
    }, [functionsArray, functionsCount]);

    function showAllFunctions() {
        if (functionsArray != null) {
            setFunctionsCount(functionsArray.length - 1);
            //generateFunctionHTML(); // Regenerate HTML to show up to the selected index
            /*var functions = document.querySelectorAll('.function-container');
            functions.forEach(function (func, i) {
                func.style.display = i <= functionsCount ? 'block' : 'none';
            });
            addEventListenerToDynamicElement()*/
        }
    }

    function showNextFunction() {
        if (functionsArray != null) {
            if (functionsCount < functionsArray.length - 1) {
                setFunctionsCount(functionsCount + 1);
            }
            //generateFunctionHTML(); // Regenerate HTML to show up to the selected index
            /*var functions = document.querySelectorAll('.function-container');
            functions.forEach(function (func, i) {
                func.style.display = i <= functionsCount ? 'block' : 'none';
            });
            addEventListenerToDynamicElement()*/
        }
    }

    function showPreviousFunction() {
        if (functionsArray != null) {
            if (functionsCount > 0) {
                setFunctionsCount(functionsCount - 1);
            }
            /*//generateFunctionHTML(); // Regenerate HTML to show up to the selected index
            var functions = document.querySelectorAll('.function-container');
            functions.forEach(function (func, i) {
                func.style.display = i <= functionsCount ? 'block' : 'none';
            });
            addEventListenerToDynamicElement()*/
        }
    }

    //generateFunctionHTML();
    /*var functions = document.querySelectorAll('.function-container');
    functions.forEach(function (func, i) {
        func.style.display = i <= functionsCount ? 'block' : 'none';
    });
    addEventListenerToDynamicElement()*/

    /*function addEventListenerToDynamicElement() {
        // Wait for the DOMContentLoaded event
        var dynamicElements = document.querySelectorAll('.transition-function-functions a');

        dynamicElements.forEach(function (dynamicElement) {
            dynamicElement.addEventListener('mouseover', function () {
                // Get the full function ID from the hovered function
                var fullFunctionId = dynamicElement.id;
                //console.log(fullFunctionId)

                // Split the ID using the '-' character
                var parts = fullFunctionId.split('-');

                // Extract the relevant part (assuming it's always the first part)
                var functionId = parts[0];

                // Form the corresponding result cell ID in the table
                var resultCellId = functionId + '-cell';
                //console.log(resultCellId)

                // Find the corresponding result cell in the table
                var resultCell = document.getElementById(resultCellId);
                //console.log(resultCell)

                // Add your action here, for example, changing the background color
                if (resultCell) {
                    dynamicElement.style.backgroundColor = '#525B76CC';
                    dynamicElement.style.color = 'white';
                    resultCell.style.backgroundColor = '#525B76CC';
                    resultCell.style.color = 'white';
                }
            });

            dynamicElement.addEventListener('mouseout', function () {
                // Get the full function ID from the hovered function
                var fullFunctionId = dynamicElement.id;
                //console.log(fullFunctionId)

                // Split the ID using the '-' character
                var parts = fullFunctionId.split('-');

                // Extract the relevant part (assuming it's always the first part)
                var functionId = parts[0];

                // Form the corresponding result cell ID in the table
                var resultCellId = functionId + '-cell';
                //console.log(resultCellId)

                // Find the corresponding result cell in the table
                var resultCell = document.getElementById(resultCellId);
                //console.log(resultCell)

                // Add your action here, for example, changing the background color
                if (resultCell) {
                    dynamicElement.style.backgroundColor = '';
                    dynamicElement.style.color = 'black';
                    resultCell.style.backgroundColor = '';
                    resultCell.style.color = 'black';
                }
            });
        });
    }*/

    return (
        <div className="transition-function">
            <div className="transition-function-functions" id="functionDisplay"></div>
            <div className="transition-function-buttons">
                <button className="next" onClick={showNextFunction}>Next</button>
                <button className="previous" onClick={showPreviousFunction}>Previous</button>
                <button className="show-all" onClick={showAllFunctions}>Show All</button>
            </div>
        </div>
    );
};

export default FunctionsComponent;