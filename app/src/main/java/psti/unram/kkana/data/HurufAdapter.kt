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
import psti.unram.kkana.utils.ProgressUtil
import com.google.firebase.auth.FirebaseAuth

class HurufAdapter(
    private val context: Context,
    private val listHuruf: List<Huruf>,
    private val jenisHuruf: String // diperlukan untuk mencatat progress
) : RecyclerView.Adapter<HurufAdapter.HurufViewHolder>() {

    inner class HurufViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val huruf: TextView = itemView.findViewById(R.id.tvHuruf)
        val romaji: TextView = itemView.findViewById(R.id.tvRomaji)
        val speaker: ImageView = itemView.findViewById(R.id.btnSpeaker)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HurufViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_huruf, parent, false)
        return HurufViewHolder(view)
    }

    override fun onBindViewHolder(holder: HurufViewHolder, position: Int) {
        val huruf = listHuruf[position]
        holder.huruf.text = huruf.huruf
        holder.romaji.text = huruf.romaji

        holder.speaker.setOnClickListener {
            val resId = context.resources.getIdentifier(huruf.suara, "raw", context.packageName)
            if (resId != 0) {
                val mp = MediaPlayer.create(context, resId)
                mp.start()
                mp.setOnCompletionListener { it.release() }

                // Catat progress jika belum pernah dipelajari
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
                ProgressUtil.tandaiHurufDipelajari(context, jenisHuruf, huruf.romaji, uid)
            }
        }
    }

    override fun getItemCount(): Int = listHuruf.size
}
