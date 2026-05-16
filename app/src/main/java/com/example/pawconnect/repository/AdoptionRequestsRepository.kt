package com.example.pawconnect.repository

import com.google.firebase.firestore.FirebaseFirestore

object AdoptionRequestsRepository {

    /**
     * Guarda una nueva solicitud de adopción en la colección "adoptionRequests".
     */
    fun addAdoptionRequest(
        request: AdoptionRequest,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        // Crea un nuevo documento en la colección "adoptionRequests"
        val docRef = db.collection("adoptionRequests").document()
        val requestId = docRef.id

        // Asigna el ID del documento a la solicitud
        val requestWithId = request.copy(requestId = requestId)

        docRef.set(requestWithId)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.localizedMessage)
            }
    }

    /**
     * Obtiene todas las solicitudes con estado "pendiente".
     * Ajusta la consulta si deseas filtrar por refugio, petId, etc.
     */
    fun fetchPendingRequests(
        onComplete: (Boolean, List<AdoptionRequest>, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("adoptionRequests")
            .whereEqualTo("status", "pendiente")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val requests = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(AdoptionRequest::class.java)
                }
                onComplete(true, requests, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, emptyList(), e.localizedMessage)
            }
    }

    /**
     * Obtiene todas las solicitudes de adopción de un usuario filtrando por su email.
     * Ajusta la consulta si prefieres filtrar por userId o algún otro campo.
     */
    fun fetchUserRequests(
        userEmail: String,
        onComplete: (Boolean, List<AdoptionRequest>, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("adoptionRequests")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val requests = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(AdoptionRequest::class.java)
                }
                onComplete(true, requests, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, emptyList(), e.localizedMessage)
            }
    }

    /**
     * Actualiza el estado de una solicitud de adopción (aprobada, rechazada, etc.).
     * Si se aprueba, marca a la mascota como adoptada.
     */
    fun updateRequestStatus(
        requestId: String,
        petId: String, // Necesitamos el petId para actualizar su estado
        newStatus: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        // 1. Actualizar estado de la solicitud
        val requestRef = db.collection("adoptionRequests").document(requestId)
        batch.update(requestRef, "status", newStatus)

        // 2. Si se aprueba, marcar mascota como adoptada
        if (newStatus == "aprobada") {
            val petRef = db.collection("mascotas").document(petId)
            batch.update(petRef, "isAdopted", true)
        }

        batch.commit()
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.localizedMessage)
            }
    }
}
