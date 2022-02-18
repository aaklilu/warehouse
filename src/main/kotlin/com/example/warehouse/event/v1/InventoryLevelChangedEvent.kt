package com.example.warehouse.event.v1

data class InventoryLevelChangedEvent(val subjectId: String, val stockLevel: Int)
