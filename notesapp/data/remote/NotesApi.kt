package com.example.notesapp.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface NotesApi {
    @POST("/sync_notes")
    suspend fun syncNotes(@Body body: SyncRequest): SyncResponse

    @GET("/fetch_notes")
    suspend fun fetchNotes(): FetchResponse

    @POST("/delete_notes")
    suspend fun deleteNotes(@Body req: DeleteRequest): BasicResponse
}
