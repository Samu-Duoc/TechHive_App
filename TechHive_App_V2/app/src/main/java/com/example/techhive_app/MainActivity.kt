package com.example.techhive_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.techhive_app.data.local.database.AppDatabase
import com.example.techhive_app.data.local.storage.UserPreferences
import com.example.techhive_app.data.remote.retrofitbuilder.RemoteModule
import com.example.techhive_app.data.repository.ProductRepository
import com.example.techhive_app.data.repository.UserRepository
import com.example.techhive_app.navigation.AppNavGraph
import com.example.techhive_app.ui.theme.TechHive_AppTheme
import com.example.techhive_app.ui.viewmodel.AuthViewModel
import com.example.techhive_app.ui.viewmodel.AuthViewModelFactory
import com.example.techhive_app.ui.viewmodel.ProductViewModel
import com.example.techhive_app.ui.viewmodel.ProductViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getInstance(context)

    // ---------- DEPENDENCIAS ----------

    // --- AUTH / USUARIOS ---
    val userDao = db.userDao()
    val authApi = RemoteModule.authApi   // MS de Auth
    val userRepository = remember { UserRepository(userDao, authApi) }

    // --- PREFERENCIAS (DataStore) ---
    val userPrefs = remember { UserPreferences(context) }

    // --- PRODUCTOS (MS productos_categorias) ---
    val productApi = RemoteModule.productApi
    val productRepository = remember { ProductRepository(productApi) }

    // ---------- VIEWMODELS ----------

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository, userPrefs)
    )

    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepository)
    )

    val navController = rememberNavController()

    TechHive_AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productViewModel = productViewModel
            )
        }
    }
}
