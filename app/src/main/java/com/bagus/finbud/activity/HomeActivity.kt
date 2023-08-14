package com.bagus.finbud.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bagus.finbud.adapter.TransactionAdapter
import com.bagus.finbud.model.Category
import com.bagus.finbud.databinding.ActivityHomeBinding
import com.bagus.finbud.databinding.HomeAvatarBinding
import com.bagus.finbud.databinding.HomeDashboardBinding
import com.bagus.finbud.model.Transaction
import com.bagus.finbud.preferences.PreferenceManager
import com.bagus.finbud.util.PrefUtil
import com.bagus.finbud.util.amountFormat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : BaseActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private lateinit var bindingAvatar: HomeAvatarBinding
    private lateinit var bindingDashboard: HomeDashboardBinding
    private lateinit var transactionAdapter: TransactionAdapter

    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupList()
        setupListener()


    }

    override fun onStart() {
        super.onStart()
        getAvatar()
        getBalance()
        getTransaction()
    }

    private fun getAvatar(){
        bindingAvatar.textAvatar.text = pref.getString(PrefUtil.pref_name)
        bindingAvatar.imageAvatar.setImageResource(pref.getInt(PrefUtil.pref_avatar)!! )
    }

    private fun getBalance(){
        var totalBalance = 0
        var totalIn = 0
        var totalOut = 0
        db.collection("transaction")
            .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
            .get()
            .addOnSuccessListener { result ->
                result.forEach { doc ->
                    totalBalance += doc.data["amount"].toString().toInt()
                    when(doc.data["type"].toString()) {
                        "IN" -> totalIn += doc.data["amount"].toString().toInt()
                        "OUT" -> totalOut += doc.data["amount"].toString().toInt()

                    }
                }
                val totalBalance = totalIn - totalOut
                bindingDashboard.textBalance.text = amountFormat( totalBalance )
                bindingDashboard.textIn.text = amountFormat( totalIn )
                bindingDashboard.textOut.text = amountFormat( totalOut )
            }
    }

    private fun getTransaction(){
        binding.progress.visibility = View.VISIBLE
        val transactions: ArrayList<Transaction> = arrayListOf()
        db.collection("transaction")
            .orderBy( "created", Query.Direction.DESCENDING)
            .whereEqualTo("username", pref.getString(PrefUtil.pref_username))
            .limit( 5 )
            .get()
            .addOnSuccessListener { result ->
                binding.progress.visibility = View.GONE
                result.forEach { doc ->
                    transactions.add(
                        Transaction(
                            id = doc.reference.id,
                            username = doc.data["username"].toString(),
                            category = doc.data["category"].toString(),
                            type = doc.data["type"].toString(),
                            amount = doc.data["amount"].toString().toInt(),
                            note = doc.data["note"].toString(),
                            created = doc.data["created"] as Timestamp
                        )
                    )
                }
                transactionAdapter.setData( transactions )
            }
    }



    private fun setupBinding(){
        setContentView( binding.root )
        bindingAvatar = binding.includeAvatar
        bindingDashboard = binding.includeDashboard
    }

    private fun setupList(){
        transactionAdapter = TransactionAdapter(arrayListOf(), object : TransactionAdapter.AdapterListener {
            override fun onClick(transaction: Transaction) {
                startActivity(
                    Intent(this@HomeActivity, UpdateActivity::class.java)
                        .putExtra("id", transaction.id)
                )
            }

            override fun onLongClick(transaction: Transaction) {
               val alertDialog = AlertDialog.Builder( this@HomeActivity)
                alertDialog.apply {
                    setTitle("Hapus")
                    setMessage("Hapus ${transaction.note} dari histori transaksi?")
                    setNegativeButton("Batal") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    setPositiveButton("Hapus") { dialogInterface, _ ->
                        deleteTransaction( transaction.id!! )
                        dialogInterface.dismiss()
                    }
                }
                alertDialog.show()
            }

        })
        binding.listTransaction.adapter = transactionAdapter
    }

    private fun setupListener(){
        bindingAvatar.imageAvatar.setOnClickListener {
            startActivity(
                Intent(this, ProfileActivity::class.java)
                    .putExtra("balance", bindingDashboard.textBalance.text.toString())
            )
        }
        binding.textTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }
        binding.fabCreate.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
        }
    }
    private fun deleteTransaction(id: String){
        db.collection("transaction")
            .document(id)
            .delete()
            .addOnSuccessListener {
                getTransaction()
                getBalance()
            }
    }
}