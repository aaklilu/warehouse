package com.example.warehouse.inventory

import java.util.UUID

data class InventoryLevelChangedEvent(val subjectId: UUID, val stockLevel: Int)
