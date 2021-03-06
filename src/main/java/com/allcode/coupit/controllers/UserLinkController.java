package com.allcode.coupit.controllers;

import com.allcode.coupit.handlers.ErrorResponse;
import com.allcode.coupit.models.Merchant;
import com.allcode.coupit.models.Product;
import com.allcode.coupit.models.User;
import com.allcode.coupit.models.UserLink;
import com.allcode.coupit.repositories.UserLinkRepository;
import com.allcode.coupit.repositories.UserRepository;
import com.allcode.coupit.repositories.ProductRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user_links")
public class UserLinkController {

    @Autowired
    private UserLinkRepository userLinkRepository;

    @GetMapping
    public Iterable<UserLink> getUserLinks(){ return userLinkRepository.findAll(); }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUserLink(@RequestBody String json) {
        // Post Params
        JSONObject request = new JSONObject(json);
        long productId = request.getLong("product_id");
        long userId = request.getLong("user_id");
        String[] fieldsToValidate = new String[] { "productId", "userId" };
        Long id = new Long(0);
        List<String> errors = this.validateUserLink(id, productId, userId, fieldsToValidate);
        if(errors.size() == 0){
            Product product = productRepository.findById(productId).get();
            User user = userRepository.findById(userId).get();
            String uid = UUID.randomUUID().toString();
            UserLink userLink = new UserLink(uid, user, product);
            UserLink savedUserLink = userLinkRepository.save(userLink) ;

            if(savedUserLink.getId().equals(null))
            {
                ErrorResponse error = new ErrorResponse("Error when saving the product");
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.BAD_REQUEST);
            }
            else{
                return new ResponseEntity<UserLink>(savedUserLink, HttpStatus.CREATED);
            }
        }
        else{
            ErrorResponse errorResponse = new ErrorResponse(String.join(", ", errors));
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    private List<String> validateUserLink(long id, long productId, long userId, String[] fieldsToValidate){
        List<String> errors = new ArrayList<>();


        try{
            User user = userRepository.findById(userId).get();
            if (user.equals(null) && Arrays.asList(fieldsToValidate).contains("userId")){
                errors.add("User not exists");
            }
        }
        catch (Exception ex){ if(Arrays.asList(fieldsToValidate).contains("userId")){ errors.add("User not exists"); } }

        try{
            Product product = productRepository.findById(productId).get();
            if (product.equals(null) && Arrays.asList(fieldsToValidate).contains("productId")){
                errors.add("Product not exists");
            }
        }
        catch (Exception ex){ if(Arrays.asList(fieldsToValidate).contains("productId")){ errors.add("Product not exists"); } }


        return errors;
    }
}