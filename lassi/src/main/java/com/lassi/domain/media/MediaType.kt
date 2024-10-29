package com.lassi.domain.media

enum class MediaType(val value: Int) {
    IMAGE(1),
    VIDEO(2),
    AUDIO(3),
    DOC(4),
    FILE_TYPE_WITH_SYSTEM_VIEW(5),
    PHOTO_PICKER(6),
    VIDEO_PICKER(7),
    PHOTO_VIDEO_PICKER(8),
}