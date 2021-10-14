const __UPPER =["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"];
const __LOWER = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"];
const __NUMBER = ["0","1","2","3","4","5","6","7","8","9"];
const __SPECIAL_COMMON = ["!","#", "$", "%", "?", "&", "*"];
const __SPECIAL_UNCOMMON = ["(", ")", "<", ">", ",", ".", ";","[", "]", "-", "+", "=","\\", "|"];


function initCharArray(useUpper, useLower, useNumber, useSpecialCommon, useSpecialUncommon){
    let chars = [];

    if(useUpper){
        chars = chars.concat(__UPPER);
    }
    if(useLower){
        chars = chars.concat(__LOWER);
    }
    if(useNumber){
        chars = chars.concat(__NUMBER);
    }
    if(useSpecialCommon){
        chars = chars.concat(__SPECIAL_COMMON);
    }
    if(useSpecialUncommon){
        chars = chars.concat(__SPECIAL_UNCOMMON)
    }

    return chars;
}

async function generatePass(useUpper, useLower, useNumber, useSpecialCommon, useSpecialUncommon, size) {

    const charsArray = initCharArray(useUpper, useLower, useNumber, useSpecialCommon, useSpecialUncommon);

    const pass = new Array(size);

    for(var i = 0; i < size; i++){
        const randomValue = Math.floor(Math.random() * charsArray.length);

        pass[i] = charsArray[randomValue];
    }

     return pass.join('');
}