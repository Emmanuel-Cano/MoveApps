package mx.edu.utez.moveapp.ui.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun SecundaryButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF2F2F2)
        )

    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF000000))
    }
}