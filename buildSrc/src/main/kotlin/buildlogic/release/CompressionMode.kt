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
    // commons-compress 的 DeflateCompressorOutputStream 默认带 zlib 头（RFC 1950）
    DEFLATE(".deflate"),
}
