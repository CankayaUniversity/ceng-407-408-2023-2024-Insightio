const patterns = {
    username: `^[a-zA-Z0-9]{4,}$`,
    email: `^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$`,
    password: `^[a-zA-Z0-9]{8,}$`,
    url: `^(https?:\\/\\/)((localhost|[\\da-z\\.-]+)(\\.[a-z\\.]{2,6})?|[0-9]{1,5})(:[0-9]{1,5})?(\\/[^\\s]*)?$`,
    urlTopLevel: `^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})(:[0-9]{1,5})?(\\/[^\\s]*)?$`
}

export function test(str, pattern) {
    const regex = new RegExp(patterns[pattern])
    return regex.test(str)
}

export default patterns;