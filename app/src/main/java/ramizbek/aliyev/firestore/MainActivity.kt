package ramizbek.aliyev.firestore

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import ramizbek.aliyev.firestore.databinding.ActivityMainBinding
import ramizbek.aliyev.firestore.databinding.ItemDialogBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: Adapter
    lateinit var list: ArrayList<User>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        list = ArrayList()
        adapter = Adapter(list, object : Adapter.RVClickCourses {
            override fun onClick(user: User, view: View, position: Int) {
                val popupMenu = PopupMenu(this@MainActivity, view)
                popupMenu.inflate(R.menu.menu_rv)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit -> {
                            val alertDialog = AlertDialog.Builder(this@MainActivity, R.style.NewDialog).create()
                            val itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
                            itemDialogBinding.edtName.setText(user.name)
                            itemDialogBinding.edtNumber.setText(user.number)
                            itemDialogBinding.btnSave.setOnClickListener {
                                val name = itemDialogBinding.edtName.text.toString().trim()
                                val number = itemDialogBinding.edtNumber.text.toString().trim()
                                val document =
                                    firestore.collection("contact").document(user.id.toString())
                                val hashMap = HashMap<String, Any>()
                                hashMap["name"] = name
                                hashMap["number"] = number
                                document.update(hashMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Updated successfully",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        alertDialog.cancel()
                                        getCollection()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Update failed",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }


                            }

                            itemDialogBinding.btnCancel.setOnClickListener {
                                Toast.makeText(this@MainActivity, "Cancel", Toast.LENGTH_SHORT)
                                    .show()

                                alertDialog.cancel()
                            }
                            alertDialog.setView(itemDialogBinding.root)
                            alertDialog.show()

                        }
                        R.id.delete -> {
                            val document =
                                firestore.collection("contact").document(user.id.toString())
                            document.delete()

                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Deleted successfully",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    getCollection()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Delete failed",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }

                        }
                    }
                    true
                }
                popupMenu.show()
            }

            override fun onClickNumber() {
            }
        })

        binding.apply {
            rv.adapter = adapter

            //Add
            btnAdd.setOnClickListener {
                val alertDialog = AlertDialog.Builder(this@MainActivity, R.style.NewDialog)
                    .create()
                val itemDialog = ItemDialogBinding.inflate(layoutInflater)
                itemDialog.btnSave.setOnClickListener {
                    val name = itemDialog.edtName.text.toString().trim()
                    val number = itemDialog.edtNumber.text.toString().trim()
                    //user
                    val user = User(name = name, number = number)

                    firestore.collection("contact")
                        .add(user)
                        //Success
                        .addOnSuccessListener {
                            //Edit collection & put id to collections
                            val collections = firestore.collection("contact").document(it.id)
                            //Update collection id
                            val map = HashMap<String, Any>()
                            map.put("id", it.id)
                            collections.update(map)
                                .addOnSuccessListener {
                                    Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT)
                                        .show()
                                    getCollection()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@MainActivity,
                                        it.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            alertDialog.cancel()
                        }
                        //Fail
                        .addOnFailureListener {
                            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                            alertDialog.cancel()
                        }
                }

                itemDialog.btnCancel.setOnClickListener {
                    alertDialog.cancel()
                }
                alertDialog.setView(itemDialog.root)
                alertDialog.show()
            }
            //Read collection
            getCollection()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun getCollection() {
        firestore.collection("contact")
            .get()
            .addOnCompleteListener {
                list.clear()
                if (it.isSuccessful) {
                    it.result.forEach { values ->
                        val value = values.toObject(User::class.java)
                        list.add(value)
                    }

                }
                adapter.notifyDataSetChanged()
            }
    }
}
