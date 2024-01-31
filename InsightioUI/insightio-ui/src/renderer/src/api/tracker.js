import axios from "axios";

// todo: change base url
export const baseURL = "http://localhost:65444";

const instance = axios.create({
    baseURL: baseURL,
    timeout: 10000,
});

export default instance;
