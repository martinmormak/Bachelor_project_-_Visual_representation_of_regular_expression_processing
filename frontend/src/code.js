import React from 'react';
import {URL} from "./App";

const FileDownloadButton = ({
                     generatedRegex
                 }) => {
    const downloadCFile = async () => {
        try {
            const response = await fetch(URL + `/api/input/c-code/${encodeURIComponent(generatedRegex)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });
            const blob = await response.blob();

            // Create a Blob URL for the downloaded file
            const url = window.URL.createObjectURL(blob);

            // Create a link element and trigger a click to download the file
            const a = document.createElement('a');
            a.href = url;
            a.download = 'generated_code.c';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);

            // Release the Blob URL after the download
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Error downloading .c file:', error);
        }
    };

    const downloadPythonFile = async () => {
        try {
            const response = await fetch(URL + `/api/input/py-code/${encodeURIComponent(generatedRegex)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });
            const blob = await response.blob();

            // Create a Blob URL for the downloaded file
            const url = window.URL.createObjectURL(blob);

            // Create a link element and trigger a click to download the file
            const a = document.createElement('a');
            a.href = url;
            a.download = 'generated_code.py';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);

            // Release the Blob URL after the download
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Error downloading .py file:', error);
        }
    };

    const downloadJavaPackage = async () => {
        try {
            const response = await fetch(URL + `/api/input/java-package/${encodeURIComponent(generatedRegex)}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });
            const blob = await response.blob();

            // Create a Blob URL for the downloaded file
            const url = window.URL.createObjectURL(blob);

            // Create a link element and trigger a click to download the file
            const a = document.createElement('a');
            a.href = url;
            a.download = 'generated_package.zip';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);

            // Release the Blob URL after the download
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Error downloading .zip file:', error);
        }
    };


    return (
        <div>
            <button onClick={downloadCFile}>Download .c regex parser</button>
            <button onClick={downloadPythonFile}>Download .py regex parser</button>
            <button onClick={downloadJavaPackage}>Download .java regex parser</button>
        </div>
    );
}

export default FileDownloadButton;