package com.bapool.bapool.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

        fun updateUserData(userData: UserData) {
            val updatedList = postRatingUserRequest.users.toMutableList()
            val index = postRatingUserRequest.users.indexOfFirst { it.user_id == userData.user_id }
            if (index != -1) {
                updatedList[index] = userData
            } else {
                updatedList.add(userData)
            }
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

                val userData = UserData(
                    user_id = Loglist.user_id,
                    rating = binding.ratingBar.rating,
                    hashtag = selectedHashtags.toList()
                )

                updateUserData(userData)
            }
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            val userData = UserData(
                user_id = Loglist.user_id,
                rating = rating,
                hashtag = selectedHashtags.toList()
            )

            updateUserData(userData)
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