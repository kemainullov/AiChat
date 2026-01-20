package com.example.aichat.data.remote.api

import com.example.aichat.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object GigaChatClient {
    private const val AUTH_BASE_URL = "https://ngw.devices.sberbank.ru:9443/"
    private const val API_BASE_URL = "https://gigachat.devices.sberbank.ru/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Trust all certificates (GigaChat uses Russian Ministry certificates)
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    private val sslContext = SSLContext.getInstance("SSL").apply {
        init(null, trustAllCerts, SecureRandom())
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val authRetrofit = Retrofit.Builder()
        .baseUrl(AUTH_BASE_URL)
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiRetrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: GigaChatAuthService by lazy {
        authRetrofit.create(GigaChatAuthService::class.java)
    }

    val apiService: GigaChatApiService by lazy {
        apiRetrofit.create(GigaChatApiService::class.java)
    }

    fun generateRqUID(): String = UUID.randomUUID().toString()

    fun getBasicAuth(): String = "Basic ${BuildConfig.GIGACHAT_AUTH_KEY}"
}

// Token manager for caching access token
class GigaChatTokenManager(
    private val authService: GigaChatAuthService
) {
    private var accessToken: String? = null
    private var expiresAt: Long = 0

    suspend fun getAccessToken(): String {
        val currentTime = System.currentTimeMillis()

        // Check if token is still valid (with 1 minute buffer)
        if (accessToken != null && currentTime < expiresAt - 60_000) {
            return accessToken!!
        }

        // Get new token
        val response = authService.getAccessToken(
            authorization = GigaChatClient.getBasicAuth(),
            rqUID = GigaChatClient.generateRqUID()
        )

        accessToken = response.accessToken
        expiresAt = response.expiresAt

        return accessToken!!
    }

    fun getBearerAuth(token: String): String = "Bearer $token"
}
