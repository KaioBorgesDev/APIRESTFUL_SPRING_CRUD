package com.example.springcrud.controller;

import com.example.springcrud.dtos.ProductRecordsDTO;
import com.example.springcrud.models.ProductModel;
import com.example.springcrud.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordsDTO productRecordDto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/products")
        public ResponseEntity<List<ProductModel>> getAllProduct(){
            List<ProductModel> productModelList = productRepository.findAll();
            if (!productModelList.isEmpty()){
                for(ProductModel productModel : productModelList){
                    UUID id = productModel.getIdProduct();

                    productModel.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(productModelList);
        }


        @GetMapping("/products/{id}")
        public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id){
            Optional<ProductModel> product0 = productRepository.findById(id);
            if (product0.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
            }

            product0.get().add(linkTo(methodOn(ProductController.class).getAllProduct()).withSelfRel());
            return ResponseEntity.status(HttpStatus.OK).body(product0.get());
        }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductRecordsDTO productRecordsDTO) {
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        var productModel = product0.get();
        BeanUtils.copyProperties(productRecordsDTO, productModel);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        productRepository.delete(product0.get());

        return ResponseEntity.status(HttpStatus.OK).body("Product deleted sucessfully.");
    }


}
