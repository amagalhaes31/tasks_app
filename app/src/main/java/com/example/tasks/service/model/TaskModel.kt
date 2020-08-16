package com.example.tasks.service.model

import com.google.gson.annotations.SerializedName

class TaskModel {

    @SerializedName("Id")
    var id: Int = 0

    @SerializedName("PriorityId")
    var priorityId: Int = 0

    @SerializedName("Description")
    var descripttion: String = ""

    @SerializedName("DueDate")
    var dueData: String = ""

    @SerializedName("Complete")
    var complete: Boolean = false
}