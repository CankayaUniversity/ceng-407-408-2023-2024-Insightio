export const metadataCategories = {
    UnscaledZoneDrawings: '661a5d65bb184667c6623772'
}

export default function getMetadata(obj, key) {
    if (obj != null && obj != undefined && obj.metadata != null && obj.metadata != undefined && obj.metadata.length != 0) {
        const res = obj.metadata.find(o => o.categoryId == metadataCategories[key])
        return res ? res.value : null
    }
    return null
}