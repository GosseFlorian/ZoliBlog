package fr.ada.java_blog.controller;

import fr.ada.java_blog.dto.CategorieCreateRequest;
import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.dto.CategorieUpdateRequest;
import fr.ada.java_blog.mapper.CategorieMapper;
import fr.ada.java_blog.model.Categorie;
import fr.ada.java_blog.repository.CategorieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategorieController {

    private final CategorieRepository categorieRepository;

    public AdminCategorieController(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    @PostMapping
    public ResponseEntity<CategorieResponse> create(@Valid @RequestBody CategorieCreateRequest body) {
        Categorie categorie = new Categorie(null, body.nom(), body.description());
        Categorie save = categorieRepository.save(categorie);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CategorieMapper.toResponse(save));
    }

    @PutMapping("/{id}")
    public CategorieResponse update(@PathVariable int id, @Valid @RequestBody CategorieUpdateRequest body) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Catégorie introuvable"));

        categorie.setNom(body.nom());
        categorie.setDescription(body.description());

        if (!categorieRepository.updateById(id, categorie)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable");
        }
        return CategorieMapper.toResponse(categorie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!categorieRepository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable");
        }
        return ResponseEntity.noContent().build();
    }
}