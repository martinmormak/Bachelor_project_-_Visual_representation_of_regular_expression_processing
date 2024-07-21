import './save.css';

const base64toBlob = (base64, mime) => {
    const byteString = atob(base64.split(",")[1]);
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    for (let i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    return new Blob([ab], {type: mime});
};

const blobToSVG = (blob) => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = function () {
            const img = new Image();
            img.src = reader.result;
            img.onload = function () {
                const canvas = document.createElement("canvas");
                const ctx = canvas.getContext("2d");
                canvas.width = img.width;
                canvas.height = img.height;
                ctx.drawImage(img, 0, 0);
                const svg = `
                <svg xmlns="http://www.w3.org/2000/svg" width="${
                    img.width
                }" height="${img.height}">
                <image href="data:image/png;base64,${
                    reader.result.split(",")[1]
                }" x="0" y="0" width="${img.width}" height="${img.height}" />
                </svg>
                `;
                resolve(svg);
            };
        };
        reader.onerror = reject;
        reader.readAsDataURL(blob);
    });
};
const Save = ({cyRef}) => {
    const handleSaveSVG = () => {
        const cy = cyRef.current;
        if (cy) {
            const base64URI = cy.png({scale: 2, full: true}); // Get the PNG image as a base64 string
            const blob = base64toBlob(base64URI, "image/png");
            blobToSVG(blob)
                .then((svg) => {
                    const svgBlob = new Blob([svg], {type: "image/svg+xml"});
                    const svgUrl = URL.createObjectURL(svgBlob);
                    const a = document.createElement("a");
                    a.href = svgUrl;
                    a.download = "image.svg";
                    a.style.display = "none";
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    URL.revokeObjectURL(svgUrl);
                })
                .catch((error) => {
                    console.error("Error:", error);
                });
        }
    };

    const handleSavePNG = () => {
        const cy = cyRef.current;
        if (cy) {
            const base64URI = cy.png({scale: 2, full: true});
            const blob = base64toBlob(base64URI, "image/png");
            const url = URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = "graph.png";
            a.style.display = "none";
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        }
    };

    return (
        <div className="transition-diagram-button">
            <button onClick={handleSaveSVG}>Save as SVG</button>
            <button onClick={handleSavePNG}>Save as PNG</button>
        </div>
    );
};

export default Save;
