package com.example.warehouse.inventory.data

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArticleRepository : JpaRepository<Article, UUID>
