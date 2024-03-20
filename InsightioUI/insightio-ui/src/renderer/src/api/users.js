import instance from './instance'
import hash from '../functions/hash'

export async function userLogin(username, password) {
    // Change the function when it is fixed
    let hashedPassword = await hash(password);
    return instance.post(`/users/login`, { username: username, password: hashedPassword }).then((res) => {
        return res.data.response;
    });
}