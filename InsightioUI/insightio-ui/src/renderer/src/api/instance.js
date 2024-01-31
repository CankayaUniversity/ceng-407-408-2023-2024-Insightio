import axios from "axios";

// todo: change base url
export const baseURL = "https://example.org";

const instance = axios.create({
    baseURL: baseURL,
    timeout: 10000,
});

export default instance;
