package pl.poznan.put.rainbowtranslator.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.*
import org.json.JSONObject
import pl.poznan.put.rainbowtranslator.R
import pl.poznan.put.rainbowtranslator.color.ColorActivity
import java.io.IOException
import java.util.concurrent.TimeUnit


class SearchActivity : AppCompatActivity() {
    companion object {
        val TAG: String = SearchActivity::class.java.simpleName
        val ARG_DOMAIN: String = "pl.poznan.put.rainbowtranslator.search.SearchActivity.ARG_DOMAIN"
        val ARG_PORT: String = "pl.poznan.put.rainbowtranslator.search.SearchActivity.ARG_PORT"
        val SHARED_PREFS: String = "pl.poznan.put.rainbowtranslator.SHARED_PREFS"
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var statusAdapter: StatusAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_search)
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        rvStatus.layoutManager = LinearLayoutManager(this)
        statusAdapter = StatusAdapter(arrayListOf(getString(R.string.search_start)), this)
        rvStatus.adapter = statusAdapter
        statusAdapter.notifyDataSetChanged()
        val rotation = RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotation.duration = 2000
        rotation.repeatCount = -1
        ivWaiting.startAnimation(rotation)
        search()
    }

    private fun search() {
        Observable.just(R.string.search_dns)
                .zipWith(Observable.interval(2, TimeUnit.SECONDS))
                .map { (status, _) -> getString(status) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { status ->
                            statusAdapter.addStatus(status)
                            rvStatus.scrollToPosition(statusAdapter.itemCount - 1)
                        },
                        { Snackbar.make(clContainer, R.string.error, Snackbar.LENGTH_LONG).show() },
                        { checkAddress("raspberrypi", 80) }
                )
    }

    private fun checkAddress(url: String, port: Int?) {
        val builder: HttpUrl.Builder = HttpUrl.Builder()
                .scheme("http")
                .host(url)
                .addPathSegment("version")
        if (port != null)
            builder.port(port)
        val httpUrl = builder.build()
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(httpUrl)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                if (response?.isSuccessful == true) {
                    val jsonObject = JSONObject(response.body()?.string())
                    if (jsonObject.getString("version") == "1.0.0") {
                        startColorActivity(url, port, httpUrl)
                    }
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }

    private fun startColorActivity(domain: String, port: Int?, httpUrl: HttpUrl) {
        val preferencesEditor = sharedPreferences.edit()
        preferencesEditor.putString(ARG_DOMAIN, domain)
        if (port != null)
            preferencesEditor.putInt(ARG_PORT, port)
        preferencesEditor.apply()

        val intent = Intent(this, ColorActivity::class.java)
        Observable.timer(2, TimeUnit.SECONDS, Schedulers.newThread())
                .repeat(2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            statusAdapter.addStatus(getString(R.string.search_finish, httpUrl))
                        },
                        { Snackbar.make(clContainer, R.string.error, Toast.LENGTH_LONG).show() },
                        {
                            intent.putExtra(ARG_DOMAIN, domain)
                            intent.putExtra(ARG_PORT, port)
                            startActivity(intent)
                            finish()
                        }
                )
    }
}
