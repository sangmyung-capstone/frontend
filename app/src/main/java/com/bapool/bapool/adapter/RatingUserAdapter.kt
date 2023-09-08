package com.bapool.bapool.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.databinding.RatinguserItemBinding
import com.bapool.bapool.retrofit.data.*

class RatingViewHolder(val binding: RatinguserItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class RatingUserAdapter(
    private val datas: MutableList<GetRatingUserResponse.GetRatingUserResultUser>,
    val postRatingUserRequest: PostRatingUserRequest
) : RecyclerView.Adapter<RatingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val binding = RatinguserItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RatingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val selectedHashtags: MutableSet<Int> = mutableSetOf()
        val Loglist = datas[position]
        val binding = holder.binding
        // Set the data to the views
        binding.nickname.text = Loglist.nickname

        var userData = postRatingUserRequest.users.find { it.user_id == Loglist.user_id }
            ?: UserData(user_id = Loglist.user_id, rating = 0f, hashtag = listOf())


        fun updateUserData(updatedData: UserData) {
            val updatedList =
                postRatingUserRequest.users.filterNot { it.user_id == updatedData.user_id }
                    .plus(updatedData)
            postRatingUserRequest.users = updatedList.toList()
        }

        fun toggleHashtagSelection(hashtag: Int) {
            val selectedHashtags =
                postRatingUserRequest.users.find { it.user_id == Loglist.user_id }?.hashtag?.toMutableList()
            if (selectedHashtags != null) {
                if (selectedHashtags.contains(hashtag)) {
                    selectedHashtags.remove(hashtag)
                } else {
                    selectedHashtags.add(hashtag)
                }

                userData.hashtag = selectedHashtags.toList()

                updateUserData(userData)
            }
        }

        fun updateRating(userId: Long, newRating: Float) {
            val userDataToUpdate =
                postRatingUserRequest.users.find { it.user_id == userId }?.copy(rating = newRating)

            if (userDataToUpdate != null) {
                updateUserData(userDataToUpdate)
            }
        }

        // Add listeners for image buttons
        binding.angrybutton.setOnClickListener {
            userData.rating = 1f // Set rating to 1 when angry button is clicked
            updateUserData(userData)
            binding.angrybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.main))
            binding.sadbutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.muebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.smilebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.happybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }

        binding.sadbutton.setOnClickListener {
            userData.rating = 2f // Set rating to 2 when sad button is clicked
            updateUserData(userData)
            binding.angrybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.sadbutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.main))
            binding.muebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.smilebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.happybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }

        binding.muebutton.setOnClickListener {
            userData.rating = 3f // Set rating to 3 when meh button is clicked
            updateUserData(userData)
            binding.angrybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.sadbutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.muebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.main))
            binding.smilebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.happybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }

        binding.smilebutton.setOnClickListener {
            userData.rating = 4f // Set rating to 4 when smile button is clicked
            updateUserData(userData)
            binding.angrybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.sadbutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.muebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.smilebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.main))
            binding.happybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }

        binding.happybutton.setOnClickListener {
            userData.rating = 5f // Set rating to 5 when happy button is clicked
            updateUserData(userData)
            binding.angrybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.sadbutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.muebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.smilebutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            binding.happybutton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.main))
        }

        binding.chip.setOnClickListener {
            toggleHashtagSelection(1)
        }

        binding.chip2.setOnClickListener {
            toggleHashtagSelection(2)
        }
        binding.chip3.setOnClickListener {
            toggleHashtagSelection(3)

        }
        binding.chip4.setOnClickListener {
            toggleHashtagSelection(4)
        }
    }

    override fun getItemCount(): Int = datas.size
}