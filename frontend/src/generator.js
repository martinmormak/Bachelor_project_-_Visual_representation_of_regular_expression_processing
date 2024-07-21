const GeneratePartOfRegex = (type, generated, randomRegexLength) => {
    const normalSpecialChars = ["*", "+", "?", "|", "(", ")"];
    const modifiedSpecialChars = ["|", "(", ")", "[", "]", "{", "}"];
    const charTypes = [
        "0123456789",
        "abcdefghijklmnopqrstuvwxyz",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
    ];

    let regexPattern = "";
    let wasbeforeCharacterOrCluster = false;
    if (type === 0) {
        while (generated < randomRegexLength) {
            const choice = getRandomInt(10);
            if (
                choice === 0 &&
                wasbeforeCharacterOrCluster === true &&
                generated < randomRegexLength - 1
            ) {
                const special = normalSpecialChars[getRandomInt(6)];
                if (special === "(" || special === ")") {
                    regexPattern += "(";
                    const result = GeneratePartOfRegex(
                        type,
                        generated,
                        randomRegexLength
                    );
                    regexPattern += result.regexPattern;
                    generated = result.generated;
                    regexPattern += ")";
                    wasbeforeCharacterOrCluster = true;
                } else {
                    regexPattern += special;
                    wasbeforeCharacterOrCluster = false;
                }
            } else {
                const randomRange = charTypes[getRandomInt(charTypes.length)];
                const randomChar = randomRange.charAt(getRandomInt(randomRange.length));
                regexPattern += randomChar;
                generated++;
                wasbeforeCharacterOrCluster = true;
            }
        }
    } else {
        while (generated < randomRegexLength) {
            const choice = getRandomInt(10);
            if (
                choice === 0 &&
                wasbeforeCharacterOrCluster === true &&
                generated < randomRegexLength - 1
            ) {
                const special = modifiedSpecialChars[getRandomInt(7)];
                if (special === "(" || special === ")") {
                    regexPattern += "(";
                    const result = GeneratePartOfRegex(
                        type,
                        generated,
                        randomRegexLength
                    );
                    regexPattern += result.regexPattern;
                    generated = result.generated;
                    regexPattern += ")";
                    wasbeforeCharacterOrCluster = true;
                } else if (special === "{" || special === "}") {
                    regexPattern += "{";
                    const result = GeneratePartOfRegex(
                        type,
                        generated,
                        randomRegexLength
                    );
                    regexPattern += result.regexPattern;
                    generated = result.generated;
                    regexPattern += "}";
                    wasbeforeCharacterOrCluster = true;
                } else if (special === "[" || special === "]") {
                    regexPattern += "[";
                    const result = GeneratePartOfRegex(
                        type,
                        generated,
                        randomRegexLength
                    );
                    regexPattern += result.regexPattern;
                    generated = result.generated;
                    regexPattern += "]";
                    wasbeforeCharacterOrCluster = true;
                }
            } else {
                const randomRange = charTypes[getRandomInt(charTypes.length)];
                const randomChar = randomRange.charAt(getRandomInt(randomRange.length));
                regexPattern += randomChar;
                generated++;
                wasbeforeCharacterOrCluster = true;
            }
        }
    }
    return {regexPattern, generated};
};

const getRandomInt = (max) => {
    return Math.floor(Math.random() * Math.floor(max));
};

const RandomRegexGenerator = () => {
    const randomRegexLength = getRandomInt(20) + 10;
    const type = getRandomInt(2);
    //console.log("Generated length:", randomRegexLength);
    //console.log("Generated type:", type);

    const result = GeneratePartOfRegex(type, 0, randomRegexLength);
    return result.regexPattern;
};

export default RandomRegexGenerator;
