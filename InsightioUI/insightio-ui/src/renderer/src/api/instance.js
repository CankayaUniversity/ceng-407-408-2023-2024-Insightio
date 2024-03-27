import axios from "axios";
import { failure } from "../functions/toastifyWrapper";

// todo: change base url
export const baseURL = "http://localhost:8091";

const instance = axios.create({
    baseURL: baseURL,
    timeout: 10000,
});

instance.interceptors.response.use(
    (res) => {
        return res;
    },
    (err) => {
        if (err.response.status != 401 && err.response.status != 403) {
            failure("Something went wrong with the API request.");
            console.error(err)
        }
        return err.response
    }
);

export default instance;
