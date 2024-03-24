package br.dev.abrindoportas.appweb.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.dev.abrindoportas.appweb.dtos.ProductRecordDto;
import br.dev.abrindoportas.appweb.models.ProductModel;
import br.dev.abrindoportas.appweb.repositories.ProductRepository;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {
  
  @Autowired
  ProductRepository productRepository;

  @PostMapping("/products")
  public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
    //TODO: process POST request
    var productModel = new ProductModel();   
    BeanUtils.copyProperties(productRecordDto, productModel);   
    return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
  }
  
  @GetMapping("/products")
  public ResponseEntity<List<ProductModel>> getAllProducts() {
    List<ProductModel> productList = productRepository.findAll();
    if (!productList.isEmpty()) {
      for (ProductModel productModel : productList) {
        UUID id = productModel.getIdProduct();
        productModel.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
      }
    }
    return ResponseEntity.status(HttpStatus.OK).body(productList);
  }
  
  @GetMapping("/products/{id}")
  public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
    Optional<ProductModel> productO = productRepository.findById(id);
    if (productO.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }
    productO.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
    return ResponseEntity.status(HttpStatus.OK).body(productO.get());
  }

  
  @PutMapping("/products/{id}")
  public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id, 
                                              @RequestBody  @Valid ProductRecordDto productRecordDto) {
      //TODO: process PUT request
      Optional<ProductModel> productO = productRepository.findById(id);
      if (productO.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
      }
      var productModel = productO.get();
      BeanUtils.copyProperties(productRecordDto, productModel);
      return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
  }

  @DeleteMapping("/products/{id}")
  public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){
    Optional<ProductModel> productO = productRepository.findById(id);
    if (productO.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }   
    productRepository.delete(productO.get());
    return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
  }
  
}
