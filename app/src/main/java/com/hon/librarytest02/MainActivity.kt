package com.hon.librarytest02

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hon.librarytest02.audiomessage.AudioMessageActivity
import com.hon.librarytest02.jobschedule.JobSchedulerActivity
import com.hon.librarytest02.service.ServiceActivity
import com.hon.librarytest02.text.TextActivity
import com.hon.librarytest02.timelineview.TimelineViewActivity
import com.hon.librarytest02.webview.WebActivity
import com.hon.librarytest02.workmanager.WorkManagerActivity

class MainActivity : AppCompatActivity() {

    private var recyclerView:RecyclerView?=null

    private var titles= arrayOf("WorkManager","Service","Job Scheduler","ButterKnife",
            "WebView","Timeline View","Audio Message","Text")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView=findViewById(R.id.rv_main)
        recyclerView?.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        recyclerView?.layoutManager=LinearLayoutManager(this)
        recyclerView?.adapter=MainAdapter(this,titles,object :MainAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                when(position){
                    0->startActivity(Intent(this@MainActivity,WorkManagerActivity::class.java))
                    1->startActivity(Intent(this@MainActivity,ServiceActivity::class.java))
                    2->startActivity(Intent(this@MainActivity,JobSchedulerActivity::class.java))
                    3->startActivity(Intent(this@MainActivity,Test02Activity::class.java))
                    4->startActivity(Intent(this@MainActivity,WebActivity::class.java))
                    5->startActivity(Intent(this@MainActivity,TimelineViewActivity::class.java))
                    6->startActivity(Intent(this@MainActivity,AudioMessageActivity::class.java))
                    7->startActivity(Intent(this@MainActivity,TextActivity::class.java))
                }
            }
        })
    }

    fun onClick(view:View){

        val intent=Intent(this,WorkManagerActivity::class.java)

        startActivity(intent)
    }

    class MainAdapter(val context:Context,val titleList:Array<String>,val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MainViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_main_item,parent,false))
        }

        override fun getItemCount(): Int {
            return titleList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val mainViewHolder:MainViewHolder=holder as MainViewHolder
            mainViewHolder.bindView(titleList[position],onItemClickListener,position)
        }

        interface OnItemClickListener{
            fun onItemClick(position: Int)
        }

        class MainViewHolder(item:View):RecyclerView.ViewHolder(item){

            fun bindView(titleStr:String,onItemClickListener: OnItemClickListener,position: Int){
                val title:TextView=itemView.findViewById(R.id.tv_title)
                title.text=titleStr
                itemView.setOnClickListener {
                    onItemClickListener.onItemClick(position)
                }
            }
        }
    }
}
