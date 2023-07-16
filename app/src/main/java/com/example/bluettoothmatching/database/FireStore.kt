package com.example.bluettoothmatching.database

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.bluettoothmatching.ItemListAdapter
import com.example.bluettoothmatching.bluetooth.tmpList
import com.example.firestoresample_todo.database.Profile
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

var imageRef: String? = null

class FireStore {
    private val db = Firebase.firestore


    fun insertData(userAddress: String, userName: String, userInfo: String) {
        val profile = Profile(
            userAddress,
            userName,
            userInfo,

            // Todo userAddressを追加、FireStoreにも追加される
        )
        db.collection("users").document(uid!!)
            .set(profile)
            .addOnSuccessListener { } //成功したとき
            .addOnFailureListener { } // 失敗したとき
    }


        fun getData(itemListAdapter: ItemListAdapter, fragment: Fragment) {
            Log.d("FUJI", "true")

            val profileList = ArrayList<Profile>() // [Profile(address="", name="", message=""),...]
            val tasks = mutableListOf<Task<QuerySnapshot>>() // 非同期タスクのリストを作成

            db.collection("users") // CollectionReference
                .addSnapshotListener { profile, e -> // profileは取得されたドキュメントのsnapshot addSnapshotでリアルタイム更新
                    tmpList.observe(fragment.viewLifecycleOwner, { // todo fragmentのインスタンスの取得が遅れるとnullになって、ライフサイクルエラーになる
                        val size = tmpList.value?.size ?: 0
                        for (i in 0 until size) {
                            val item = tmpList.value?.get(i)
                            val collectionRef =
                                db.collection("users")
                            val query = collectionRef.whereEqualTo(
                                "address",
                                item
                            ).orderBy(
                                "address",
                                Query.Direction.ASCENDING
                            ) // addressがtmpListの中にあれば、そのコレクションを参照
                            val task = query.get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) { // クエリ結果が空ではない場合にのみログを出力
                                        Log.d("FUJI", "成功" + item)
                                        for (documentSnapshot in querySnapshot.documents) {
                                            val profile = documentSnapshot.toObject(Profile::class.java)
                                            profile?.let {
                                                if (!profileList.contains(profile)) {
                                                    profileList.add(profile)
                                                    //if (!allList.contains(profile)) {
                                                      //  allList.add(profile)
                                                   // } // todo フラグメントを遷移すると要素が重複する。しかもリストが消える
                                                    //todo ここからinsertAllDataの処理
                                                    val allUserRef = db.collection("allusers")
                                                    allUserRef.add(profile)
                                                        .addOnSuccessListener { documentReference ->
                                                        /*
                                                            documentReference.get()
                                                                .addOnSuccessListener { documentSnapshot ->
                                                                    val addedProfile = documentSnapshot.toObject(Profile::class.java)
                                                                    addedProfile?.let {
                                                                        allList.add(addedProfile)
                                                                        itemListAdapter.submitList(
                                                                            allList) // todo このクエリをpastFragmentでやる
                                                                    }


                                                                }
                                                            */
                                                        }
                                                        .addOnFailureListener {
                                                            Log.d("FUJI", "追加できていません")
                                                        }

                                                }
                                            }
                                        }
                                    } else {
                                        // Log.d("FUJI", "失敗" + item)
                                    }
                                    // Log.d("FUJI", profileList.toString())
                                }
                                .addOnFailureListener { }
                            tasks.add(task) // タスクをリストに追加
                        }
                        Tasks.whenAllSuccess<DocumentSnapshot>(tasks) // すべての非同期タスクが完了するまで待機
                            .addOnSuccessListener {
                                itemListAdapter.submitList(profileList) // UIの更新
                            }
                            .addOnFailureListener {} // 参照の取得に失敗したとき
                    })
                }
        }


}
