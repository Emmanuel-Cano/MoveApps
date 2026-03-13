package mx.edu.utez.moveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.edu.utez.moveapp.ui.Navigation

import mx.edu.utez.moveapp.ui.theme.MoveAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveAppTheme {
                Navigation()

            }
        }
    }
}