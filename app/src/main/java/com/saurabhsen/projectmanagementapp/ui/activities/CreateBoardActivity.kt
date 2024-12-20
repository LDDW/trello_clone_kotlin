package com.trello.clone.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.trello.clone.R
import com.trello.clone.firebase.FirestoreClass
import com.trello.clone.models.Board
import com.trello.clone.utils.Constants
import kotlinx.android.synthetic.main.activity_create_board.*
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserName:  String
    private lateinit var mBoardDetails: Board
    private var mBoardImageURL: String = ""
    private var isBoardUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        if(intent.hasExtra(Constants.EDIT_BOARD)){
            mBoardDetails = intent.getParcelableExtra(Constants.EDIT_BOARD)!!
            et_board_name.setText(mBoardDetails.name)
            Glide
                .with(this)
                .load(mBoardDetails.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(iv_board_image)
            Log.i("CreateBoard[check id]",mBoardDetails.toString())
            btn_create.visibility = View.GONE
            btn_update.visibility = View.VISIBLE
            isBoardUpdate = true
        }

        iv_board_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_create.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }

        btn_update.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateBoard()
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_create_board_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }

        toolbar_create_board_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun createBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())
        val board = Board(
            et_board_name.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this, board)
    }

    private fun updateBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())
        if(mBoardImageURL == ""){
            mBoardImageURL = mBoardDetails.image
        }
        val board = Board(
            et_board_name.text.toString(),
            mBoardImageURL,
            mBoardDetails.name,
            assignedUsersArrayList,
            mBoardDetails.documentId,
            mBoardDetails.taskList
        )

        FirestoreClass().updateBoard(this, board)
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        val storageRef = FirebaseStorage.getInstance().reference.child(
            "BOARD_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(
                this,
                mSelectedImageFileUri
            )
        )

        storageRef.putFile(mSelectedImageFileUri!!)
            .addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Board Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downloadable Image File", uri.toString())
                    mBoardImageURL = uri.toString()
                    if(isBoardUpdate){
                        updateBoard()
                    }else{
                        createBoard()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            }
        } else {
            Toast.makeText(
                this,
                "Erreur de stockage",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data!!.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(iv_board_image)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}