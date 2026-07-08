package buildlogic.utils

internal object NameUtils {
    /**
     * 分隔符拆分正则
     */
    private val DELIMITER_REGEX = Regex("[._-]")

    /**
     * 驼峰拆分正则：
     * (?<=[a-z])(?=[A-Z])       : 小写字母后面紧跟大写字母（如 "fooBar" -> "foo"|"Bar"）
     * |                         : 或
     * (?<=[A-Z])(?=[A-Z][a-z]) : 大写字母后面紧跟大写字母+小写字母（如 "XMLParser" -> "XML"|"Parser"）
     */
    private val CAMEL_CASE_REGEX = Regex("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])")

    /**
     * 版本格式校验正则：三组数字（允许前导零），后缀可选（连字符+至少一个字母）
     */
    private val VERSION_REGEX = Regex("^\\d+\\.\\d+\\.\\d+(-[A-Za-z]+)?$")

    /**
     * 根据 . - _ 及驼峰规则拆分字符串，返回全部由小写字母组成的单词列表。
     *
     * @param input 待拆分字符串，只能包含字母（大小写）及分隔符 . - _
     * @return 拆分后的单词列表，所有单词均为小写字母
     * @throws IllegalArgumentException 如果输入为空、包含非法字符，或拆分后的单词含有非字母字符
     */
    private fun splitToLowerCaseWords(input: String): List<String> {
        require(input.isNotEmpty()) { "Input string must not be empty" }

        val allowed = input.all { it.isLetter() || it == '.' || it == '-' || it == '_' }
        require(allowed) { "Input($input) contains invalid characters. Only letters, '.', '-', '_' are allowed." }

        val parts = input.split(DELIMITER_REGEX).filter { it.isNotEmpty() }
        val result = mutableListOf<String>()

        for (part in parts) {
            val words = splitByCamelCase(part)
            for (word in words) {
                require(word.isNotEmpty()) { "${input}:Unexpected empty word" }
                require(word.all { it.isLetter() }) {
                    "Word '$word' contains non-letter characters"
                }
                result.add(word.lowercase())
            }
        }
        //TODO 转换需不需要日志？
        return result
    }

    /**
     * 按驼峰命名拆分字符串（支持连续大写缩写，例如 "XMLParser" -> ["XML", "Parser"]）。
     */
    private fun splitByCamelCase(str: String): List<String> {
        if (str.isEmpty()) return emptyList()
        return str.split(CAMEL_CASE_REGEX).filter { it.isNotEmpty() }
    }

    /**
     * 检测版本号是否符合 "数字.数字.数字[-英文]" 格式。
     *
     * @param version 待检测的版本字符串
     * @return 如果格式合法返回 true，否则 false
     */
    fun isValidVersion(version: String): Boolean {
        return version.matches(VERSION_REGEX)
    }

    /**
     * 校验版本号合法性，若不合法则抛出异常。
     *
     * @param version 待校验的版本字符串
     * @throws IllegalArgumentException 如果版本格式不符合要求
     */
    fun validateVersion(version: String) {
        require(isValidVersion(version)) {
            "Invalid version format: '$version'. Expected: digit.digit.digit[-letters]"
        }
    }

    /**
     * 转换为首字母小写的驼峰命名（lower camel case）
     * 示例：
     *   "helloWorld"      -> "helloWorld"
     *   "foo-bar_baz"     -> "fooBarBaz"
     *   "XMLParser"       -> "xmlParser"
     *   "My.APP-Config"   -> "myAppConfig"
     */
    fun toLowerCamelCase(input: String): String {
        val words = splitToLowerCaseWords(input)
        require(words.isNotEmpty()) { "No words found" }
        return words.first() + words.drop(1).joinToString("") {
            it.replaceFirstChar(Char::uppercaseChar)
        }
    }

    /**
     * 转换为横杠连接的纯小写命名（kebab-case）
     * 示例：
     *   "helloWorld"      -> "hello-world"
     *   "foo-bar_baz"     -> "foo-bar-baz"
     *   "XMLParser"       -> "xml-parser"
     *   "My.APP-Config"   -> "my-app-config"
     */
    fun toKebabCase(input: String): String {
        val words = splitToLowerCaseWords(input)
        require(words.isNotEmpty()) { "No words found" }
        return words.joinToString("-")
    }

}
