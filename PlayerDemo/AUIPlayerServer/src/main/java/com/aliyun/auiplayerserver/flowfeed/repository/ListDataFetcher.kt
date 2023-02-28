package com.aliyun.auiplayerserver.flowfeed.repository

import github.leavesc.reactivehttp.datasource.RemoteExtendDataSource
import github.leavesc.reactivehttp.viewmodel.IUIActionEvent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class ListDataFetcher<Api : Any>(
    iActionEvent: IUIActionEvent?,
    apiServiceClass: Class<Api>
) : RemoteExtendDataSource<Api>(iActionEvent, apiServiceClass) {
    companion object {
        private val httpClient: OkHttpClient by lazy {
            createHttpClient()
        }

        private fun createHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
                .readTimeout(1000L, TimeUnit.MILLISECONDS)
                .writeTimeout(1000L, TimeUnit.MILLISECONDS)
                .connectTimeout(1000L, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(FilterInterceptor())
//                .addInterceptor(MonitorInterceptor(MainApplication.context))
            return builder.build()
        }
    }

    override val baseUrl: String
        get() = "https://restapi.amap.com/v3/"

    override fun showToast(msg: String) {
    }

    override fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

class FilterInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val httpBuilder = originalRequest.url.newBuilder()
        httpBuilder.addEncodedQueryParameter(HttpConfig.KEY, HttpConfig.KEY_MAP)
        val requestBuilder = originalRequest.newBuilder()
            .url(httpBuilder.build())
        return chain.proceed(requestBuilder.build())
    }

}