import React from "react";
import "./info.css";

const InfoComponent = () => {
    return (
        <div className="info-field">
            <div className="notations">Supported notations</div>
            <div className="info-table">
                <table>
                    <tr>
                        <th></th>
                        <th>Simplified</th>
                        <th>Regular</th>
                    </tr>
                    <tr>
                        <td>Group:</td>
                        <td>(rs)</td>
                        <td>(rs)</td>
                    </tr>
                    <tr>
                        <td>
                            Alternative:
                        </td>
                        <td>r|s</td>
                        <td>r|s</td>
                    </tr>
                    <tr>
                        <td>
                            Optional:
                        </td>
                        <td>r?</td>
                        <td>[r]</td>
                    </tr>
                    <tr>
                        <td>
                            Transitive:
                        </td>
                        <td>r*</td>
                        <td>&#123;s&#125;</td>
                    </tr>
                    <tr>
                        <td>One or more:</td>
                        <td>r+</td>
                        <td>s&#123;s&#125;</td>
                    </tr>
                </table>
            </div>
        </div>
    );
};

export default InfoComponent;
