const patterns = {
    username: `^[a-zA-Z0-9]{4,}$`,
    email: `^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$`,
    password: `^[a-zA-Z0-9]{8,}$`
}

export default patterns;