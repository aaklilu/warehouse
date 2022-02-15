package com.example.warehouse.inventory.api.v1

import com.example.warehouse.inventory.models.ArticleDto
import com.example.warehouse.inventory.models.InventoryArticlesDto
import com.example.warehouse.inventory.service.ArticleService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/inventory")
class InventoryController(private val articleService: ArticleService) {

    /**
     * `POST` : Create inventory in bulk
     *
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createScheduledFlight(@RequestBody @Validated inventoryArticlesDto: InventoryArticlesDto) {
        inventoryArticlesDto.inventory.map(ArticleDto::toArticle).let {
            articleService.createArticles(it)
        }
    }
}
