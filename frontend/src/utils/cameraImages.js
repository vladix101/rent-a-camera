import dslr from "../assets/cameras/dslr.svg"
import mirrorless from "../assets/cameras/mirrorless.svg"
import compact from "../assets/cameras/compact.svg"
import action from "../assets/cameras/action.svg"
import instant from "../assets/cameras/instant.svg"
import video from "../assets/cameras/video.svg"
import defaultImage from "../assets/cameras/default.svg"

const CATEGORY_IMAGES = {
    "DSLR": dslr,
    "Mirrorless": mirrorless,
    "Compact": compact,
    "Action camera": action,
    "Instant": instant,
    "Video camera": video,
}

export const getCameraImage = (categoryName) => CATEGORY_IMAGES[categoryName] || defaultImage
