import React, {useEffect} from "react";
import "./table.css"

const TableComponent = ({transitionTable, functionsCount}) => {
    useEffect(() => {
        // Function to create and update the transition table
        const createTransitionTable = (data) => {
            const tableContainer = document.getElementById('transition-table-container');

            // Check if table already exists, and if so, remove it
            const existingTable = document.querySelector('.transition-table-table');
            if (existingTable) {
                tableContainer.removeChild(existingTable);
            }
            if (data) {
                const table = document.createElement('table');
                table.className = 'transition-table-table';

                const tbody = document.createElement('tbody');

                // Create header row
                const headerRow = document.createElement('tr');
                headerRow.appendChild(document.createElement('td')); // Empty cell
                //console.log(data["q0"].map(item => item.id.slice(-6, -5)))

                data["q0"].map(item => item.id.slice(-6, -5)).forEach(character => {
                    const cell = document.createElement('th');
                    cell.id = character + '-cell';
                    cell.textContent = character;
                    headerRow.appendChild(cell);
                });
                /*Object.keys(data["q0"].map(item => item.id.slice(-6,-5))).forEach(character => {
                    console.log(character)
                    const cell = document.createElement('td');
                    cell.id = character + '-cell';
                    cell.textContent = character;
                    headerRow.appendChild(cell);
                });*/
                tbody.appendChild(headerRow);

                var unlockedFunctions = 0;
                var displayedFunctions = 0;
                var displayedRows = 0;

                Object.keys(data).forEach(state => {
                    const transition = data[state];
                    if (displayedRows <= unlockedFunctions) {
                        const row = document.createElement('tr');
                        const emptyCell = document.createElement('td');
                        emptyCell.id = state + 'empty-cell';
                        emptyCell.innerHTML = 'q<sub>' + state.slice(1) + '</sub>';
                        row.appendChild(emptyCell);

                        transition.forEach(item => {
                            const cell = document.createElement('td');
                            //const character = item.id.slice(2, -5); // Extract character from id
                            if (displayedFunctions < functionsCount) {
                                cell.id = item.id;
                                cell.className = item.flag;
                                if (item.value !== "⊥") {
                                    cell.innerHTML = 'q<sub>' + item.value.slice(1) + '</sub>';
                                    if (unlockedFunctions < item.value.slice(1)) {
                                        unlockedFunctions = unlockedFunctions + 1;
                                    }
                                } else {
                                    cell.innerHTML = '&perp;';
                                }
                            } else {
                                cell.innerHTML = '';
                            }
                            row.appendChild(cell);
                            displayedFunctions = displayedFunctions + 1;
                        });

                        tbody.appendChild(row);
                        displayedRows = displayedRows + 1;
                    }
                });

                // Create rows for each state
                /*data.transitions.forEach(transition => {
                    if(displayedRows<=unlockedFunctions) {
                        const row = document.createElement('tr');
                        const emptyCell = document.createElement('td');
                        emptyCell.id = transition.state + 'empty-cell';
                        emptyCell.innerHTML = 'q<sub>' + transition.state.slice(1) + '</sub>';
                        row.appendChild(emptyCell);

                        Object.keys(transition.transitions).forEach(character => {
                            const cell = document.createElement('td');
                            if(dispeyedFunctios<functionsCount) {
                                cell.id = transition.transitions[character].id;
                                if (transition.transitions[character].nextState !== "⊥") {
                                    cell.innerHTML = 'q<sub>' + transition.transitions[character].nextState.slice(1) + '</sub>';
                                    console.log(transition.transitions[character].nextState.slice(1));
                                    if(displayedRows<transition.transitions[character].nextState.slice(1)){
                                        console.log(transition.transitions[character].nextState.slice(1));
                                        unlockedFunctions=unlockedFunctions+1;
                                    }
                                } else {
                                    cell.innerHTML = '&perp;';
                                }
                            }else {
                                cell.innerHTML = '';
                            }
                            row.appendChild(cell);
                            dispeyedFunctios=dispeyedFunctios+1;
                        });

                        tbody.appendChild(row);
                        displayedRows=displayedRows+1;
                    }
                });*/

                table.appendChild(tbody);
                tableContainer.appendChild(table);
            }
        }

        function addEventListenerToDynamicElement() {

            var dynamicElements = document.querySelectorAll('.transition-table-table td');

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
                    var resultCellId = functionId + '-function';
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
                    var resultCellId = functionId + '-function';
                    //console.log(resultCellId)

                    // Find the corresponding result cell in the table
                    var resultCell = document.getElementById(resultCellId);
                    //console.log(resultCell)

                    // Add your action here, for example, changing the background color
                    if (resultCell) {
                        dynamicElement.style.backgroundColor = '';
                        if(dynamicElement.className==="end")
                        {
                            dynamicElement.style.color = 'red';
                        }else {
                            dynamicElement.style.color = 'black';
                        }
                        resultCell.style.backgroundColor = '';
                        resultCell.style.color = 'black';
                    }
                });
            });
        }

        // Call the function with your JSON data
        createTransitionTable(transitionTable);

        addEventListenerToDynamicElement();

    }, [transitionTable, functionsCount]);

    return (
        <div className="transition-table" id="transition-table-container"></div>
    );
};

export default TableComponent;