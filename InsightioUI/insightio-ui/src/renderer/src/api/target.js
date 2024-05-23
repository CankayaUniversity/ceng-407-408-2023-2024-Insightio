import instance from "./tracker";

export async function getTargetCurrentCount(cameraId, target) {
    return instance.get(`current_count_${cameraId}_${target}`).then((res) => {
        return res.data;
    });
}

export async function getTargetTotalCount(cameraId, target) {
    return instance.get(`total_count_${cameraId}_${target}`).then((res) => {
        return res.data;
    });
}