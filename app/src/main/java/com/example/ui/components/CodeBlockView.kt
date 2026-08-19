package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CodeBlockView(
    code: String,
    language: String = "python",
    modifier: Modifier = Modifier,
    title: String? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CodeBgDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161B22))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Code",
                        tint = OpenAIGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = title ?: language.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = ObsidianText
                    )
                }

                FilledTonalButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Copied Code", code)
                        clipboard.setPrimaryClip(clip)
                        copied = true
                        Toast.makeText(context, "تم نسخ الكود البرمجي!", Toast.LENGTH_SHORT).show()
                        coroutineScope.launch {
                            delay(2000)
                            copied = false
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (copied) OpenAIGreenDark else ObsidianCard,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "نسخ الكود",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copied) "تم النسخ!" else "نسخ الكود",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                    )
                }
            }

            // Code Content with syntax coloring
            val annotatedCode = remember(code, language) {
                highlightSyntax(code, language)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(14.dp)
            ) {
                Text(
                    text = annotatedCode,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private fun highlightSyntax(code: String, language: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val keywords = setOf(
            "import", "from", "def", "class", "return", "if", "elif", "else",
            "for", "while", "in", "with", "as", "try", "except", "finally",
            "True", "False", "None", "async", "await", "lambda", "val", "var", "fun"
        )
        val lines = code.lines()

        for ((lineIdx, line) in lines.withIndex()) {
            val trimmed = line.trimStart()
            if (trimmed.startsWith("#") || trimmed.startsWith("//")) {
                withStyle(SpanStyle(color = CodeComment)) {
                    append(line)
                }
            } else {
                var inString = false
                var stringQuote = ' '
                val wordBuilder = StringBuilder()

                for (char in line) {
                    if (inString) {
                        wordBuilder.append(char)
                        if (char == stringQuote) {
                            withStyle(SpanStyle(color = CodeString)) {
                                append(wordBuilder.toString())
                            }
                            wordBuilder.clear()
                            inString = false
                        }
                    } else if (char == '"' || char == '\'') {
                        // Flush any pending word
                        if (wordBuilder.isNotEmpty()) {
                            appendWordWithHighlight(wordBuilder.toString(), keywords)
                            wordBuilder.clear()
                        }
                        inString = true
                        stringQuote = char
                        wordBuilder.append(char)
                    } else if (char.isLetterOrDigit() || char == '_') {
                        wordBuilder.append(char)
                    } else {
                        if (wordBuilder.isNotEmpty()) {
                            appendWordWithHighlight(wordBuilder.toString(), keywords)
                            wordBuilder.clear()
                        }
                        withStyle(SpanStyle(color = ObsidianText)) {
                            append(char.toString())
                        }
                    }
                }
                if (wordBuilder.isNotEmpty()) {
                    if (inString) {
                        withStyle(SpanStyle(color = CodeString)) {
                            append(wordBuilder.toString())
                        }
                    } else {
                        appendWordWithHighlight(wordBuilder.toString(), keywords)
                    }
                }
            }
            if (lineIdx < lines.size - 1) {
                append("\n")
            }
        }
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendWordWithHighlight(
    word: String,
    keywords: Set<String>
) {
    when {
        word in keywords -> withStyle(SpanStyle(color = CodeKeyword, fontWeight = FontWeight.Bold)) {
            append(word)
        }
        word.toDoubleOrNull() != null -> withStyle(SpanStyle(color = CodeNumber)) {
            append(word)
        }
        word.startsWith("OpenAI") || word.startsWith("client") || word.startsWith("chat") -> withStyle(SpanStyle(color = CodeFunction)) {
            append(word)
        }
        else -> withStyle(SpanStyle(color = ObsidianText)) {
            append(word)
        }
    }
}
