package com.lassi.app.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lassi.app.R
import com.lassi.common.extenstions.inflate
import com.lassi.common.extenstions.loadImage
import com.lassi.common.utils.ImageUtils
import com.lassi.data.media.MiMedia
import kotlinx.android.synthetic.main.row_selected_media.view.*

class SelectedMediaAdapter(private val onItemClicked: (miMedia: MiMedia) -> Unit) :
    RecyclerView.Adapter<SelectedMediaAdapter.MediaViewHolder>() {

    private val selectedMedias = ArrayList<MiMedia>()

    fun setList(selectedMedias: List<MiMedia>?) {
        selectedMedias?.let {
            this.selectedMedias.clear()
            this.selectedMedias.addAll(selectedMedias)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(parent.inflate(R.layout.row_selected_media))
    }

    override fun getItemCount() = this.selectedMedias.size

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(this.selectedMedias[position])
    }

    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(miMedia: MiMedia) {
            itemView.ivSelectedMediaThumbnail.loadImage(ImageUtils.getThumb(miMedia))
            itemView.setOnClickListener {
                onItemClicked(miMedia)
            }
        }
    }
}