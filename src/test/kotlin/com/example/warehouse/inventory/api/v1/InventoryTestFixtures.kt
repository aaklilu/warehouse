package com.example.warehouse.inventory.api.v1

val inventoryJson = """
    {
      "inventory": [
        {
          "art_id": "1",
          "name": "leg",
          "stock": "12"
        },
        {
          "art_id": "2",
          "name": "screw",
          "stock": "17"
        },
        {
          "art_id": "3",
          "name": "seat",
          "stock": "2"
        },
        {
          "art_id": "4",
          "name": "table top",
          "stock": "1"
        }
      ]
    }
""".trimIndent()

val articleJson = """
    {
      "art_id": "1",
      "name": "leg",
      "stock": "12"
    }
""".trimIndent()
