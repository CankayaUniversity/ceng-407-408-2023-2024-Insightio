import instance from './instance'
import hash from '../functions/hash'

export async function userLogin(username, password) {
    // Change the function when it is fixed
    let hashedPassword = await hash(password);
    return instance.post(`/users/login`, { username: username, password: hashedPassword }).then((res) => {
        return res.data.response;
    });
}

export async function createUser(user) {
    return instance.post('/users', user).then((res) => {
        return res.data;
    })
}

export async function updateUser(user) {
    return instance.put(`/users/${user.id}`, user).then((res) => {
        return res.data;
    })
}