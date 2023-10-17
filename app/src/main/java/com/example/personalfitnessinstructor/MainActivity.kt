package com.example.personalfitnessinstructor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.personalfitnessinstructor.ui.theme.PersonalFitnessInstructorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalFitnessInstructorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PersonalFitnessInstructorTheme {
        Greeting("Android")
    }
}
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)

        // Инициализация Firebase Remote Config
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // Установите частоту обновления данных
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Инициализация SharedPreferences для хранения ссылки
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Проверка наличия сохраненной ссылки на устройстве
        val savedLink = sharedPreferences.getString("link", "")

        if (savedLink.isNullOrEmpty()) {
            // Проверка доступности интернета
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val isConnected = connectivityManager.activeNetworkInfo?.isConnectedOrConnecting
            if (isConnected == true) {
                // Загрузка ссылки из Firebase Remote Config
                remoteConfig.fetchAndActivate()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val link = remoteConfig.getString("web_link")
                            loadWebPage(link)
                            // Сохранение ссылки локально
                            sharedPreferences.edit().putString("link", link).apply()
                        } else {
                            // Обработка ошибки загрузки данных
                            showNoInternetScreen()
                        }
                    }
            } else {
                // Показать экран "Нет интернет-соединения"
                showNoInternetScreen()
            }
        } else {
            // Загрузить локально сохраненную ссылку
            loadWebPage(savedLink)
        }
    }

    private fun loadWebPage(link: String) {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        webView.webViewClient = WebViewClient()
        webView.loadUrl(link)
    }

    private fun showNoInternetScreen() {
        // Реализуйте логику для экрана "Нет интернет-соединения"
    }
}
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : AppCompatActivity() {
    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val url = remoteConfig.getString("url")

                    if (url.isNotEmpty()) {
                        saveUrlLocally(url)
                        openUrlInWebView(url)
                    } else {
                        showPlaceholder()
                    }
                } else {
                    showError()
                }
            }
    }

    private fun saveUrlLocally(url: String) {
        // Save the URL locally for future use
    }

    private fun openUrlInWebView(url: String) {
        if (isNetworkConnected()) {
            val webView = WebView(this)
            webView.loadUrl(url)
            setContentView(webView)
        } else {
            showNoInternetError()
        }
    }

    private fun showPlaceholder() {
        // Show placeholder screen
    }

    private fun showError() {
        // Show error screen
    }

    private fun showNoInternetError() {
        // Show no internet error screen
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}