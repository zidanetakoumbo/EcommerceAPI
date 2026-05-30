package com.zizou.EcommerceAPI.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Service.LivreService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/livres")
@CrossOrigin(origins = "*")
public class LivreController {

    private final LivreService lvService;

    public LivreController(LivreService lvs) {
        this.lvService = lvs;
    }

    // GET /api/livres
    @GetMapping
    public ResponseEntity<List<Livre>> getAll() {
        return ResponseEntity.ok(lvService.getAll());
    }

    // GET /api/livres/paginated?page=0&size=10&sort=id,desc
    @GetMapping("/paginated")
    public ResponseEntity<Page<Livre>> getAllPaginated(
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(lvService.getAllPaginated(pageable));
    }
    /**
     * GET /api/livres/rechercher?titre=&autheurId=&categorieId=&prixMin=&prixMax=
     * Tous les paramètres sont optionnels et peuvent être combinés.
     */
    @GetMapping("/rechercher")
    public ResponseEntity<Page<Livre>> rechercher(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) Long autheurId,
            @RequestParam(required = false) Long categorieId,
            @RequestParam(required = false) Double prixMin,
            @RequestParam(required = false) Double prixMax,
            @PageableDefault(size = 10, page = 0, sort = "titre") Pageable pageable) {
        return ResponseEntity.ok(lvService.rechercher(titre, autheurId, categorieId, prixMin, prixMax, pageable));
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<Livre> findById(@PathVariable Long id) {
        Livre livre = lvService.getById(id);
        return (livre != null) ? ResponseEntity.ok(livre) : ResponseEntity.notFound().build();
    }

    // POST /api/livres
    @PostMapping
    public ResponseEntity<Livre> create(@RequestBody Livre l) {
        Livre created = lvService.create(l);
        return ResponseEntity.status(201).body(created);
    }

    // PUT /api/livres/12
    @PutMapping("/{id}")
    public ResponseEntity<Livre> updateLivre(@PathVariable Long id, @RequestBody Livre livre) {
        Livre updated = lvService.update(id, livre);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/livres/12
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        boolean deleted = lvService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}