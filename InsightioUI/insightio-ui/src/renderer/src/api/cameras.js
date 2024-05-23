import instance from "./instance";

export async function getTargetCountData(id) {
    return instance.get(`/count-reports/audit?cameraId=${id}`).then((res) => {
        return res.data;
    })
}

export async function getSingleCameraSetting(id) {
    return instance.get(`/cameras/${id}`).then((res) => {
        return res.data;
    })
}

export async function getAllCameraSettings() {
    return instance.get("/cameras/all").then((res) => {
        return res.data;
    })
}

export async function createCameraSetting(cameraSetting) {
    return instance.post("/cameras", cameraSetting).then((res) => {
        return res.data;
    })
}

export async function updateCameraSetting(cameraSetting) {
    return instance.put(`/cameras/${cameraSetting.id}`, cameraSetting).then((res) => {
        return res.data;
    })
}

export async function deleteCameraSetting(id) {
    return instance.delete(`/cameras/${id}`).then((res) => {
        return res.data;
    });
}