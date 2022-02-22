package com.example.warehouse.inventory.api.v1

import com.example.warehouse.inventory.article.models.ArticleDto
import com.example.warehouse.inventory.article.models.ArticlesPageDto
import com.example.warehouse.inventory.article.models.PageDto
import com.example.warehouse.inventory.data.Article
import com.example.warehouse.inventory.service.ArticleService
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/inventory/articles")
class ArticleController(
    private val articleService: ArticleService,
    private val environment: Environment
) {

    @PostMapping
    fun createArticle(@RequestBody @Validated articleDto: ArticleDto): ResponseEntity<ArticleDto> {
        return ResponseEntity.ok(articleService.createArticle(articleDto.toArticle()).let(Article::toArticleDto))
    }

    @GetMapping("/{id}")
    fun getArticle(@PathVariable("id") id: UUID): ResponseEntity<ArticleDto> {
        return ResponseEntity.ok(articleService.getArticle(id).let(Article::toArticleDto))
    }

    @GetMapping
    fun findArticles(@PageableDefault(page = 0, size = 100) page: Pageable,): ResponseEntity<ArticlesPageDto> {
        val pageRequest = PageRequest.of(
            page.pageNumber, page.pageSize,
            page.getSortOr(
                Sort.by("createdAt").descending()
            )
        )
        val articles = articleService.findArticles(pageRequest)
        return ResponseEntity.ok(
            ArticlesPageDto(
                items = articles.content.map(Article::toArticleDto),
                page = PageDto(
                    pageNumber = articles.number,
                    pageSize = page.pageSize,
                    totalElements = articles.totalElements,
                    totalPages = articles.totalPages
                )
            )
        )
    }

    @DeleteMapping
    fun deleteAll(): HttpStatus {
        if (environment.acceptsProfiles(Profiles.of("dev", "test"))) {
            articleService.deleteAll()
            return HttpStatus.OK
        }
        return HttpStatus.METHOD_NOT_ALLOWED
    }
}
