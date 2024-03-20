import Toastify from 'toastify-js'
import 'toastify-js/src/toastify.css';

export const success = (message) => {
    Toastify({
        text: message,
        duration: 2000,
        gravity: "top", // `top` or `bottom`
        position: 'right', // `left`, `center` or `right`
        style: { background: "#15CD72" },
        className: "info",
    }).showToast();
};

export const warn = (message) => {
    Toastify({
        text: message,
        duration: 2000,
        gravity: "top",
        position: 'right',
        style: { background: "#FFA500" },
        className: "info",
    }).showToast();
};

export const failure = (message) => {
    Toastify({
        text: message,
        duration: 2000,
        gravity: "top",
        position: 'right',
        style: { background: "#ED4F32" },
        className: "info",
    }).showToast();
};

export const notify = (message) => {
    Toastify({
        text: message,
        duration: 2000,
        gravity: "top",
        position: 'right',
        style: { background: "linear-gradient(to right, #00b09b, #96c93d)" },
        className: "info",
    }).showToast();
};