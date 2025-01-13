package org.ms.produitservice.controller;

import lombok.RequiredArgsConstructor;
import org.ms.produitservice.entities.Produit;
import org.ms.produitservice.repository.ProduitRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProduitController {
    
    private final ProduitRepository produitRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('product:read')")
    public List<Produit> getAllProducts() {
        return produitRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:create')")
    public Produit createProduct(@RequestBody Produit produit) {
        return produitRepository.save(produit);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:update')")
    public Produit updateProduct(@PathVariable Long id, @RequestBody Produit produit) {
        produit.setId(id);
        return produitRepository.save(produit);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public void deleteProduct(@PathVariable Long id) {
        produitRepository.deleteById(id);
    }

    @GetMapping("/{id}/check-availability")
    @PreAuthorize("hasAuthority('product:read')")
    public boolean checkProductAvailability(@PathVariable Long id, @RequestParam long quantity) {
        Produit product = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getQuantity() >= quantity;
    }

    @PutMapping("/{id}/quantity")
    @PreAuthorize("hasAuthority('product:update')")
    public void updateProductQuantity(@PathVariable Long id, @RequestBody long newQuantity) {
        Produit product = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(newQuantity);
        produitRepository.save(product);
    }
}
