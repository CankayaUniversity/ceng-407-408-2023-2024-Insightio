async function hash(text) {
    // Encode the text as a Uint8Array
    const encoder = new TextEncoder();
    const data = encoder.encode(text);

    // Hash the text using SHA-256
    const hash = await crypto.subtle.digest('SHA-256', data);

    // Convert the hash to a hex string (for easier display/storage)
    const hashArray = Array.from(new Uint8Array(hash));
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');

    return hashHex;
}

export default hash;