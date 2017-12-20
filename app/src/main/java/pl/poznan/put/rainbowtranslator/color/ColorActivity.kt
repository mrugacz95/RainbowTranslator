package pl.poznan.put.rainbowtranslator.color

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_color.*
import org.json.JSONArray
import org.json.JSONObject
import pl.poznan.put.rainbowtranslator.R
import pl.poznan.put.rainbowtranslator.model.ColorData
import pl.poznan.put.rainbowtranslator.search.SearchActivity
import java.net.URI


class ColorActivity : AppCompatActivity() {
    private lateinit var socket: Socket
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var currentColor: ColorData

    companion object {
        val TAG: String = ColorActivity::class.java.simpleName
        val RESPONSE = "response"
        val SAVED_COLORS = "saved_colors"
        val DELETE_ALL = "delete_all"
        val TOGGLE_LIGHT = "click"
        val REQUEST = "request"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)
        historyAdapter = HistoryAdapter(arrayListOf())
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter
        openSocket()
        ibToggleLight.setOnClickListener {
            socket.emit(TOGGLE_LIGHT, arrayListOf<String>())
        }
        ibDeleteAll.setOnClickListener {
            socket.emit(DELETE_ALL, arrayListOf<String>())
            historyAdapter.setData(arrayListOf())
        }
        fabInsertColor.setOnClickListener {
            val colorJson = JSONObject()
            colorJson.put("rgb", currentColor.rgb)
            colorJson.put("color", currentColor.color)
            socket.emit("insert", colorJson)
            historyAdapter.addData(currentColor)
        }
        srl_refresh.setOnRefreshListener {
            socket.emit("colors", arrayListOf<String>())
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        super.onDestroy()
    }

    private fun openSocket() {
        val domain = intent.getStringExtra(SearchActivity.ARG_DOMAIN)
        val port = intent.getIntExtra(SearchActivity.ARG_PORT, 80)
        val uri = URI(getString(R.string.socket_address, domain, port))
        socket = IO.socket(uri)
        socket.connect()
        socket.emit(REQUEST, arrayListOf<String>())
        socket.on(RESPONSE) { args -> onResponse(args) }
        socket.on(SAVED_COLORS) { args -> onColorList(args) }
    }

    private fun onResponse(args: Array<out Any>) = run {
        Observable.just(args)
                .map { args[0] as JSONObject }
                .map { it -> ColorData(it.getString("color"), it.getString("rgb")) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { color ->
                            currentColor = color
                            vColor.setBackgroundColor(Color.parseColor(color.rgb))
                            tvColorName.text = color.color
                            socket.emit(REQUEST, arrayListOf<String>())
                        }
                )
    }

    private fun onColorList(args: Array<out Any>) {
        Observable.just(args)
                .map { it[0] as JSONArray }
                .map {
                    val colors: ArrayList<ColorData> = ArrayList()
                    (0..(it.length() - 1))
                            .map { i -> it.getJSONObject(i) }
                            .mapTo(colors) { ColorData(it.getString("color"), it.getString("rgb")) }
                    Observable.just(colors)
                }
                .flatMap { it -> it }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    historyAdapter.setData(it)
                    srl_refresh.isRefreshing = false
                }
    }

}
