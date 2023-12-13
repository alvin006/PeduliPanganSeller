package com.alvintio.pedulipanganseller.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alvintio.pedulipanganseller.adapter.NotificationsAdapter
import com.alvintio.pedulipanganseller.databinding.FragmentNotificationsBinding
import com.alvintio.pedulipanganseller.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val notificationsRecyclerView: RecyclerView = binding.recyclerView
        notificationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val currentUserUid = auth.currentUser?.uid
        Log.d("Firestore", "Current User UID: $currentUserUid")

        firestore.collection("notifications")
            .get()
            .addOnSuccessListener { result ->
                val notifications = mutableListOf<Notification>()

                for (document in result) {
                    val date = document.getString("date")
                    val message = document.getString("message")
                    val namaRestoran = document.getString("namaRestoran")

                    Log.d("Firestore", "Date: $date, Message: $message, Nama Restoran: $namaRestoran")

                    if (date != null && message != null && namaRestoran != null) {
                        // Cocokkan namaRestoran dengan koleksi "users_seller"
                        checkRestaurant(date, message, namaRestoran, notificationsRecyclerView, notifications)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching notifications", exception)
            }

        return root
    }

    private fun checkRestaurant(
        date: String,
        message: String,
        namaRestoran: String,
        notificationsRecyclerView: RecyclerView,
        notifications: MutableList<Notification>
    ) {
        firestore.collection("users_seller")
            .whereEqualTo("name", namaRestoran)
            .get()
            .addOnSuccessListener { userResult ->
                if (!userResult.isEmpty) {
                    val userDocument = userResult.documents.first()
                    val sellerName = userDocument.getString("name")
                    val customerName = userDocument.getString("namaPemesan")
                    notifications.add(Notification(message, date, sellerName ?: " ", customerName ?: ""))

                    val adapter = NotificationsAdapter(notifications)
                    notificationsRecyclerView.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error checking restaurant in users_seller collection", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
