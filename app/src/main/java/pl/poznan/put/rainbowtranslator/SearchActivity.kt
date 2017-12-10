package pl.poznan.put.rainbowtranslator

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {
    companion object {
        val TAG: String = SearchActivity::class.java.simpleName
        val ARG_DOMAIN: String = "pl.poznan.put.rainbowtranslator.SearchActivity.ARG_DOMAIN"
        val ARG_PORT: String = "pl.poznan.put.rainbowtranslator.SearchActivity.ARG_port"
    }

    private lateinit var statusAdapter: StatusAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        rvStatus.layoutManager = LinearLayoutManager(this)
        statusAdapter = StatusAdapter(arrayListOf(getString(R.string.search_start)), this)
        rvStatus.adapter = statusAdapter
        statusAdapter.notifyDataSetChanged()
        search()
    }

    private fun search() {
        Observable.just(R.string.search_dns, R.string.search_local_network, R.string.search_finish, R.string.search_finish)
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
                        { startColorActivity("raspberry.pi", 8080) }
                )
    }

    private fun startColorActivity(domain: String, port: Int) {
        val intent = Intent(this, ColorActivity::class.java)
        intent.putExtra(ARG_DOMAIN, domain)
        intent.putExtra(ARG_PORT, port)
        startActivity(intent)
    }
}
