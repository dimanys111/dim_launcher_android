package com.example.mylauncher


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    companion object{
        val APP_PREFERENCES = "mysettings"
        val APP_PREFERENCES_NAME = "Nickname" // имя кота
        var app_start = ""
        lateinit var mSettings: SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSettings =
            getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if(mSettings.contains(APP_PREFERENCES_NAME)) {
            app_start = mSettings.getString(APP_PREFERENCES_NAME, "")!!
            if(app_start!="") {
                val launchIntent =
                    packageManager.getLaunchIntentForPackage(app_start)
                startActivity(launchIntent)
            }
        }

        val rv=findViewById<RecyclerView>(R.id.rv)
        rv.adapter = RAdapter(this)
        rv.layoutManager = GridLayoutManager(this,4)
    }

    internal class AppInfo {
        var label: CharSequence? = null
        var packageName: CharSequence? = null
        var icon: Drawable? = null
    }

    internal class RAdapter(c: Context) : RecyclerView.Adapter<RAdapter.ViewHolder?>() {
        private val appsList: MutableList<AppInfo>

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
            var textView: TextView = itemView.findViewById(R.id.text)
            var img: ImageView = itemView.findViewById(R.id.img1) as ImageView
            var img_cb: ImageView = itemView.findViewById(R.id.img_cb) as ImageView

            override fun onClick(v: View) {
                val pos: Int = adapterPosition
                val context: Context = v.context
                val launchIntent = context.packageManager.getLaunchIntentForPackage(appsList[pos].packageName.toString())
                context.startActivity(launchIntent)
                Toast.makeText(v.context, appsList[pos].label.toString(), Toast.LENGTH_LONG).show()
            }

            //This is the subclass ViewHolder which simply
            //'holds the views' for us to show on each row
            init {
                //Finds the views from our row.xml
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun onLongClick(p0: View?): Boolean {
                val pos: Int = adapterPosition
                if(app_start==appsList[pos].packageName.toString()){
                    app_start=""
                    val editor = mSettings.edit()
                    editor.remove(APP_PREFERENCES_NAME)
                    editor.apply()
                } else {
                    app_start = appsList[pos].packageName.toString()
                    val editor = mSettings.edit()
                    editor.putString(APP_PREFERENCES_NAME, app_start)
                    editor.apply()
                }
                notifyDataSetChanged()
                return true
            }
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
            if(appsList[i].packageName.toString()==app_start){
                viewHolder.img_cb.visibility=View.VISIBLE
            } else {
                viewHolder.img_cb.visibility=View.GONE
            }
            viewHolder.textView.text = appsList[i].label.toString()
            viewHolder.img.setImageDrawable(appsList[i].icon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            //This is what adds the code we've written in here to our target view
            val inflater = LayoutInflater.from(parent.context)
            val view: View = inflater.inflate(R.layout.row, parent, false)
            return ViewHolder(view)
        }

        init {
            //This is where we build our list of app details, using the app
            //object we created to store the label, package name and icon
            val pm = c.packageManager
            appsList = ArrayList()
            val i = Intent(Intent.ACTION_MAIN, null)
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            val allApps = pm.queryIntentActivities(i, 0)
            for (ri in allApps) {
                val app = AppInfo()
                app.label = ri.loadLabel(pm)
                app.packageName = ri.activityInfo.packageName
                app.icon = ri.activityInfo.loadIcon(pm)
                appsList.add(app)
            }
        }

        override fun getItemCount(): Int {
            return appsList.size
        }
    }
}
