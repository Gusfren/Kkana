package psti.unram.kkana.data

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import psti.unram.kkana.R

class HurufAdapter(
    private val context: Context,
    private val listHuruf: List<Huruf>
) : RecyclerView.Adapter<HurufAdapter.HurufViewHolder>() {

    inner class HurufViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val huruf: TextView = itemView.findViewById(R.id.tvHuruf)
        val romaji: TextView = itemView.findViewById(R.id.tvRomaji)
        val btnPlayAudio: ImageView = itemView.findViewById(R.id.btnPlayAudio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HurufViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_huruf, parent, false)
        return HurufViewHolder(view)
    }

    override fun onBindViewHolder(holder: HurufViewHolder, position: Int) {
        val huruf = listHuruf[position]
        holder.huruf.text = huruf.huruf
        holder.romaji.text = huruf.romaji

        holder.btnPlayAudio.setOnClickListener {
            val resId = context.resources.getIdentifier(
                huruf.suara.substringBefore("."),
                "raw",
                context.packageName
            )

            if (resId != 0) {
                val mediaPlayer = MediaPlayer.create(context, resId)
                mediaPlayer.setOnCompletionListener { it.release() }
                mediaPlayer.start()
            }
        }
    }

    override fun getItemCount(): Int = listHuruf.size
}
