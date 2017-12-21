package pl.poznan.put.rainbowtranslator.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_search.*
import pl.poznan.put.rainbowtranslator.R
import pl.poznan.put.rainbowtranslator.color.ColorActivity


class SearchActivity : AppCompatActivity() {
    companion object {
        val TAG: String = SearchActivity::class.java.simpleName
        val ARG_DOMAIN: String = "pl.poznan.put.rainbowtranslator.search.SearchActivity.ARG_DOMAIN"
        val ARG_PORT: String = "pl.poznan.put.rainbowtranslator.search.SearchActivity.ARG_port"
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
        search()
    }

    private fun search() {
        startColorActivity("raspberrypi", null)
//        val client = OkHttpClient() //TODO add checking okhttp response
//        val request = Request.Builder()
//                .url("raspberrypi")
//                .build()
//        client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call?, response: Response?) {
//                if (response?.isSuccessful == true) {
//                    val jsonObject = JSONObject(response.body()?.string())
//                    if (jsonObject.getString("version") == "1.0.0") {
//                        startColorActivity("raspberrypi", null)
//                    }
//                }
//            }
//            override fun onFailure(call: Call?, e: IOException?) {
//                e?.printStackTrace()
//            }
//        })
    }

    private fun startColorActivity(domain: String, port: Int?) {
        val preferencesEditor = sharedPreferences.edit()
        preferencesEditor.putString(ARG_DOMAIN, domain)
        if (port != null)
            preferencesEditor.putInt(ARG_PORT, port)
        preferencesEditor.apply()

        val intent = Intent(this, ColorActivity::class.java)
        intent.putExtra(ARG_DOMAIN, domain)
        intent.putExtra(ARG_PORT, port)
        startActivity(intent)
        finish()
    }
}
