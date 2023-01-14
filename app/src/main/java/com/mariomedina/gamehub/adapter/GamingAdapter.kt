package com.mariomedina.gamehub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariomedina.gamehub.databinding.FragmentGamingBinding
import com.mariomedina.gamehub.databinding.ItemUserLayoutBinding
import com.mariomedina.gamehub.model.UserModel

class GamingAdapter(val context : Context, val list: ArrayList<UserModel>) : RecyclerView.Adapter<GamingAdapter.GamingViewHolder>() {
    inner class GamingViewHolder(val binding: ItemUserLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamingViewHolder {

        return GamingViewHolder(ItemUserLayoutBinding.inflate(LayoutInflater.from(context)
        , parent, false))

    }

    override fun onBindViewHolder(holder: GamingViewHolder, position: Int) {

        holder.binding.textView3.text = list[position].name
        holder.binding.textView2.text = list[position].email

        Glide.with(context).load(list[position].image).into(holder.binding.userImage)

    }

    override fun getItemCount(): Int {
        return list.size
    }

}