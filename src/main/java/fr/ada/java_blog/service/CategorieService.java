package fr.ada.java_blog.service;

import fr.ada.java_blog.dto.ArticleResponse;
import fr.ada.java_blog.dto.CategorieCreateRequest;
import fr.ada.java_blog.dto.CategorieResponse;
import fr.ada.java_blog.dto.CategorieUpdateRequest;
import fr.ada.java_blog.mapper.ArticleMapper;
import fr.ada.java_blog.mapper.CategorieMapper;
import fr.ada.java_blog.model.Categorie;
import fr.ada.java_blog.repository.CategorieRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategorieService {

  private final CategorieRepository categorieRepository;

  public CategorieService(CategorieRepository categorieRepository) {
    this.categorieRepository = categorieRepository;
  }

  public List<CategorieResponse> findAll() {
    return categorieRepository.findAll().stream().map(CategorieMapper::toResponse).toList();
  }

  public CategorieResponse findById(int id) {
    return categorieRepository
        .findById(id)
        .map(CategorieMapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable"));
  }

  public List<ArticleResponse> articlesPubliesByCategorieId(int id) {
    return categorieRepository.findArticlesPubliesByCategorieId(id).stream()
        .map(ArticleMapper::toResponse)
        .toList();
  }

  public CategorieResponse creer(CategorieCreateRequest body) {
    Categorie categorie = new Categorie(null, body.nom(), body.description());
    Categorie save = categorieRepository.save(categorie);
    return CategorieMapper.toResponse(save);
  }

  public CategorieResponse modifier(int id, CategorieUpdateRequest body) {
    Categorie categorie =
        categorieRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable"));

    categorie.setNom(body.nom());
    categorie.setDescription(body.description());

    if (!categorieRepository.updateById(id, categorie)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable");
    }
    return CategorieMapper.toResponse(categorie);
  }

  public void supprimer(int id) {
    if (!categorieRepository.deleteById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable");
    }
  }
}
