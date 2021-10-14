package com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.databinding.AddedImageBinding

class AddImageAdapter(var removePhotoListener: removePhoto, var ctx: Context) :
    ListAdapter<Uri, AddImageAdapter.MyViewHolder>(PojoDiffUtil()) {


    class MyViewHolder(itemView: AddedImageBinding) : RecyclerView.ViewHolder(itemView.root) {
        var addedImageBinding: AddedImageBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(AddedImageBinding.inflate(LayoutInflater.from(parent.context)))
    }

    interface removePhoto {
        fun removePhoto(uri: Uri)
        fun addPhoto()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val uri = getItem(position)
        Log.i(javaClass.name, "uri--->$uri")

        if (!uri.toString().equals("blank_uri")) {
//            holder.addedImageBinding.imgAddedImage.setBackground(null)

            holder.addedImageBinding.imgCamera.visibility = View.GONE
            holder.addedImageBinding.imgDeletePhoto.visibility = View.VISIBLE

            holder.addedImageBinding.imgAddedImage.setImageURI(uri)
            holder.addedImageBinding.imgDeletePhoto.setOnClickListener {
                Log.i(javaClass.name, "position--->$position")
                removePhotoListener.removePhoto(uri)
            }
        } else {
//            holder.addedImageBinding.imgCamera.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_baseline_photo_camera_24));
            holder.addedImageBinding.imgAddedImage.background = ContextCompat.getDrawable(
                ctx,
                R.drawable.background_green_border
            )
            holder.addedImageBinding.imgCamera.visibility = View.VISIBLE
            holder.addedImageBinding.imgDeletePhoto.visibility = View.GONE

            holder.addedImageBinding.imgCamera.setOnClickListener {
                Log.i(javaClass.name, "position--->$position")
                removePhotoListener.addPhoto()
            }
        }


    }

    override fun submitList(list: List<Uri>?) {
        Log.d("sdvsdvswwe", "submitList:1 "+list?.size)

        var list2 = ArrayList(list)
        if (list2.isNotEmpty()) {
            list2.add(0, Uri.parse("blank_uri"))
        }
        Log.d("sdvsdvswwe", "submitList:2 "+list2.size)
        super.submitList(list2?.let { ArrayList(it) })
    }


    class PojoDiffUtil : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {

            Log.i(javaClass.name, "isAbsolute----oldItem----" + oldItem.path)
            Log.i(javaClass.name, "isAbsolute----newItem----" + newItem.path)

            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {

            Log.i(javaClass.name, "isAbsolute-areContentsTheSame---oldItem----" + oldItem.path)
            Log.i(javaClass.name, "isAbsolute-areContentsTheSame---newItem----" + newItem.path)
            return oldItem == newItem
        }
    }

    override fun getItemCount(): Int {
        Log.i(javaClass.name, "getItemCount--->" + super.getItemCount())
        return super.getItemCount()

    }

}

