import instance from './instance'
import hash from '../functions/hash'

export async function userLogin(username, password) {
    // Change the function when it is fixed
    let hashedPassword = await hash(password);
    return instance.get(`/username/${username}?password=${hashedPassword}`);
}