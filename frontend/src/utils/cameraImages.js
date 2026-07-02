import dslr from "../assets/cameras/dslr.svg"
import mirrorless from "../assets/cameras/mirrorless.svg"
import kompaktni from "../assets/cameras/kompaktni.svg"
import akciona from "../assets/cameras/akciona.svg"
import instant from "../assets/cameras/instant.svg"
import video from "../assets/cameras/video.svg"
import defaultImage from "../assets/cameras/default.svg"

const CATEGORY_IMAGES = {
    "DSLR": dslr,
    "Mirrorless": mirrorless,
    "Kompaktni": kompaktni,
    "Akciona kamera": akciona,
    "Instant": instant,
    "Video kamera": video,
}

export const getCameraImage = (kategorijaNaziv) => CATEGORY_IMAGES[kategorijaNaziv] || defaultImage
