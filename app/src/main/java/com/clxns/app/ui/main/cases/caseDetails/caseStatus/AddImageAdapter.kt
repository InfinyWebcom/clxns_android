package com.clxns.app.ui.main.cases.caseDetails.caseStatus

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.databinding.AddedImageBinding
import com.clxns.app.utils.hide
import com.clxns.app.utils.show
import timber.log.Timber

class AddImageAdapter(
    private var removePhotoListener : RemovePhotoListener,
    private var ctx : Context
) :
    ListAdapter<Uri, AddImageAdapter.MyViewHolder>(PojoDiffUtil()) {


    class MyViewHolder(itemView : AddedImageBinding) : RecyclerView.ViewHolder(itemView.root) {
        var addedImageBinding : AddedImageBinding = itemView
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MyViewHolder {
        return MyViewHolder(AddedImageBinding.inflate(LayoutInflater.from(parent.context)))
    }

    interface RemovePhotoListener {
        fun removePhoto(uri : Uri)
        fun addPhoto()
    }

    override fun onBindViewHolder(holder : MyViewHolder, position : Int) {
        val uri = getItem(position)
        holder.addedImageBinding.imgAddedImage.setImageURI(null)
        if (uri.toString() != "blank_uri") {
            holder.addedImageBinding.imgCamera.hide()
            holder.addedImageBinding.imgDeletePhoto.show()

            holder.addedImageBinding.imgAddedImage.setImageURI(uri)
            holder.addedImageBinding.imgDeletePhoto.setOnClickListener {
                removePhotoListener.removePhoto(uri)
            }
        } else {
            holder.addedImageBinding.imgAddedImage.background = ContextCompat.getDrawable(
                ctx,
                R.drawable.background_green_border
            )
            holder.addedImageBinding.imgCamera.show()
            holder.addedImageBinding.imgDeletePhoto.hide()

            holder.addedImageBinding.imgCamera.setOnClickListener {
                removePhotoListener.addPhoto()
            }
        }


    }

    override fun submitList(list : List<Uri>?) {
        val list2 = ArrayList(list)
        if (list2.isNotEmpty()) {
            list2.add(0, Uri.parse("blank_uri"))
        }
        super.submitList(ArrayList(list2))
    }


    class PojoDiffUtil : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem : Uri, newItem : Uri) : Boolean {

            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem : Uri, newItem : Uri) : Boolean {
            return oldItem == newItem
        }
    }

}

