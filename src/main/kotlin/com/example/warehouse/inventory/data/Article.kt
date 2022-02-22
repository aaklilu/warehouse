package com.example.warehouse.inventory.data

import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    name = "article",
    indexes = [
        Index(name = "idx_article__article_id_name", columnList = "articleId, name")
    ]
)
class Article(
    @Id @GeneratedValue var id: UUID? = null,
    @Column(nullable = false, unique = true) var articleId: String,
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var stockLevel: Int = 0
) {
    @CreatedDate
    var createdAt: LocalDateTime? = null
}
