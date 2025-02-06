package com.book.book_store.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
      private final Cloudinary cloudinary;
      @PreAuthorize("isAuthenticated()")
      public String uploadImage(MultipartFile file){
            try{
               var result = cloudinary.uploader().upload(file.getBytes(),
                       ObjectUtils.asMap("folder", "/upload",
                               "use_filename", true,
                               "unique_filename", false,
                               "resource_type", "auto"));
               return result.get("secure_url").toString();
            }catch (Exception e){
                throw new RuntimeException("Image upload fail");
            }

      }
}
