package com.palavrizar.tec.palavrizapp.utils.repositories

import android.content.Context
import com.palavrizar.tec.palavrizapp.models.Product

class StoreRepository(val context: Context) {


    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)
    private var sessionManager = SessionManager(context)

    fun saveProduct(product: Product, onCompletion: () -> Unit){
        realtimeRepository.saveProduct(product, onCompletion)
    }

    fun getProductByValue(value: String, onCompletion: (Product?) -> Unit){
        realtimeRepository.getProductByValue(value, onCompletion)
    }

    fun getProducts(onCompletion: (ArrayList<Product>) -> Unit){
        realtimeRepository.getProducts(onCompletion)
    }

    fun editProducs(productId: String, product: Product, onCompletion: () -> Unit){
        realtimeRepository.editProduct(productId, product, onCompletion)
    }

    fun deleteProducs(productId: String){
        realtimeRepository.deleteProduct(productId)
    }

    fun giveUserSoloCredits(numCredits: Int, userId: String, onCompletion: () -> Unit){
        realtimeRepository.giveUserCredits(numCredits, userId, onCompletion)
    }
}