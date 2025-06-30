package psti.unram.kkana.data

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import psti.unram.kkana.R
import psti.unram.kkana.utils.ProgressUtil
import com.google.firebase.auth.FirebaseAuth
import psti.unram.kkana.utils.DailyChallengeManager

class HurufAdapter(
    private val context: Context,
    private val listHuruf: List<Huruf>,
    private val jenisHuruf: String
) : RecyclerView.Adapter<HurufAdapter.HurufViewHolder>() {

    // Definisikan view type untuk setiap jenis layout
    private val VIEW_TYPE_DEFAULT = 0
    private val VIEW_TYPE_KANJI = 1

    inner class HurufViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val huruf: TextView = itemView.findViewById(R.id.tvHuruf)
        val romaji: TextView = itemView.findViewById(R.id.tvRomaji)
        val speaker: ImageView = itemView.findViewById(R.id.btnSpeaker)
        val itemLayout: LinearLayout = itemView.findViewById(R.id.itemHurufLayout)
        val terjemahan: TextView = itemView.findViewById(R.id.tvTerjemahan) // TextView ini ada di kedua layout
    }

    // Metode ini menentukan layout mana yang akan digunakan berdasarkan posisi/jenis data
    override fun getItemViewType(position: Int): Int {
        return if (jenisHuruf == "kanji") {
            VIEW_TYPE_KANJI
        } else {
            VIEW_TYPE_DEFAULT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HurufViewHolder {
        val layoutResId = when (viewType) {
            VIEW_TYPE_KANJI -> R.layout.item_huruf_kanji // Gunakan layout Kanji
            VIEW_TYPE_DEFAULT -> R.layout.item_huruf_default // Gunakan layout default
            else -> throw IllegalArgumentException("Invalid view type")
        }
        val view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
        return HurufViewHolder(view)
    }

    override fun onBindViewHolder(holder: HurufViewHolder, position: Int) {
        val huruf = listHuruf[position]
        holder.huruf.text = huruf.huruf
        holder.romaji.text = huruf.romaji

        // Logika untuk menampilkan terjemahan hanya jika itu Kanji
        if (holder.itemViewType == VIEW_TYPE_KANJI) {
            if (huruf.terjemahan != null && huruf.terjemahan.isNotEmpty()) {
                holder.terjemahan.text = huruf.terjemahan
                holder.terjemahan.visibility = View.VISIBLE
            } else {
                holder.terjemahan.visibility = View.GONE
            }
        } else {
            // Untuk default layout (Hiragana/Katakana), terjemahan selalu GONE
            holder.terjemahan.visibility = View.GONE
        }

        holder.itemLayout.setOnClickListener {
            val resId = context.resources.getIdentifier(huruf.suara, "raw", context.packageName)
            if (resId != 0) {
                val mp = MediaPlayer.create(context, resId)
                mp.start()
                mp.setOnCompletionListener { it.release() }

                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
                ProgressUtil.tandaiHurufDipelajari(context, jenisHuruf, huruf.romaji, uid)

                // Beri tahu DailyChallengeManager bahwa huruf telah dipelajari
                // Hanya untuk kanji, berikan karakter kanji sebenarnya sebagai identifier
                if (jenisHuruf == "kanji" || jenisHuruf == "hiragana" || jenisHuruf == "katakana") {
                    DailyChallengeManager.getInstance(context, uid).trackLearningProgress(jenisHuruf, huruf.huruf)
                }
            }
        }
    }

    override fun getItemCount(): Int = listHuruf.size
}