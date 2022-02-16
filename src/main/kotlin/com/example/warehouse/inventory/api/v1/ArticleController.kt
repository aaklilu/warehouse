package com.example.warehouse.inventory.api.v1

import com.example.warehouse.inventory.data.Article
import com.example.warehouse.inventory.models.ArticleDto
import com.example.warehouse.inventory.service.ArticleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/inventory/articles")
class ArticleController(@Autowired private val articleService: ArticleService) {

    /**
     * `POST` : Create inventory in bulk
     *
     * @return
     */
    @PostMapping
    fun createArticle(@RequestBody @Validated articleDto: ArticleDto): ResponseEntity<ArticleDto> {
        return ResponseEntity.ok(articleService.createArticle(articleDto.toArticle()).let(Article::toArticleDto))
    }
}
