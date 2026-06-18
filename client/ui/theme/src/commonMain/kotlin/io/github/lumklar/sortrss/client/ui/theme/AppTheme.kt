package io.github.lumklar.sortrss.client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.github.lumklar.theme.generated.resources.Res
import com.github.lumklar.theme.generated.resources.noto
import org.jetbrains.compose.resources.Font


// 预留基础配色，可按需扩展品牌色
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false, // 后续可接入系统主题自动切换
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    //TODO 仅web平台加载字体解决无中文字体问题？
    // 1. 加载字体
    val notoFontFamily = FontFamily(
        Font(Res.font.noto, FontWeight.Normal)
    )

    // 2. 定义自定义排版（覆盖需要修改的样式）
    val customTypography = Typography(
        displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = notoFontFamily),
        displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = notoFontFamily),
        displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = notoFontFamily),
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = notoFontFamily),
        headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = notoFontFamily),
        headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = notoFontFamily),
        titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = notoFontFamily),
        titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = notoFontFamily),
        titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = notoFontFamily),
        labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = notoFontFamily),
        labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = notoFontFamily),
        labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = notoFontFamily),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = notoFontFamily),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = notoFontFamily),
        bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = notoFontFamily)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = customTypography,
        content = content
    )
}
