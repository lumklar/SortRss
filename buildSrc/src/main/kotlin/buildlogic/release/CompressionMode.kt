package buildlogic.release

enum class CompressionMode(val suffix: String) {
    // 不压缩，无后缀
    NONE(""),

    // 标准压缩格式
    GZIP(".gz"),
    BZIP2(".bz2"),
    XZ(".xz"),
    LZMA(".lzma"),
    ZSTANDARD(".zst"),
    DEFLATE(".deflate"),          // 原始 Deflate 流（无 zlib 头）
}
