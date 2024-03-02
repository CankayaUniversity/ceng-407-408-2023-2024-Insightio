import axios from "axios";

// todo: change base url
export const baseURL = "http://localhost:8090";

const instance = axios.create({
    baseURL: baseURL,
    timeout: 10000,
});

export default instance;
