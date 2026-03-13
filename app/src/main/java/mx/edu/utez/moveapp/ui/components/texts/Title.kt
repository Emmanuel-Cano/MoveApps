package mx.edu.utez.moveapp.ui.components.texts

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun Title(
    text: String,
    fontSize: Int = 45,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = Color(0xFF000000),
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        color = color,
        textAlign = textAlign,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier

    )
}