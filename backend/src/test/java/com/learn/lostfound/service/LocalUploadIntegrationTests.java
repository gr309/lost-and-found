// package com.learn.lostfound.service;

// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.time.LocalDate;
// import java.util.UUID;
// import com.learn.lostfound.dto.ItemRequest;
// import com.learn.lostfound.entity.User;
// import com.learn.lostfound.enums.ItemType;
// import com.learn.lostfound.repository.ItemRepository;
// import com.learn.lostfound.repository.UserRepository;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.transaction.support.TransactionTemplate;
// import tools.jackson.databind.ObjectMapper;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest(properties = {"app.upload-dir=target/integration-uploads", "debug=false"})
// @ActiveProfiles("test")
// @AutoConfigureMockMvc
// class LocalUploadIntegrationTests {
//     @Autowired MockMvc mvc;
//     @Autowired UserRepository users;
//     @Autowired ItemRepository items;
//     @Autowired ItemService service;
//     @Autowired JwtService jwt;
//     @Autowired ObjectMapper json;
//     @Autowired PlatformTransactionManager transactionManager;

//     private User user() {
//         User user = new User();
//         user.setName("Upload test");
//         user.setEmail(UUID.randomUUID() + "@example.com");
//         user.setPassword("test hash");
//         return users.saveAndFlush(user);
//     }

//     private Path path(String url) {
//         return Path.of("target/integration-uploads").resolve(url.substring("/uploads/".length()));
//     }

//     @Test void authenticatedUploadPublicDownloadAndOwnerDeletion() throws Exception {
//         User user = user();
//         String token = "Bearer " + jwt.generateToken(user);
//         String body = mvc.perform(multipart("/api/items/")
//                 .file(LocalFileStorageServiceTests.png()).header("Authorization", token)
//                 .param("title", "Wallet").param("description", "Found wallet")
//                 .param("type", "FOUND").param("location", "Library")
//                 .param("eventDate", "2020-01-01"))
//                 .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
//         var item = json.readTree(body);
//         String url = item.path("imageUrl").asText();
//         long id = item.path("id").asLong();
//         assertTrue(Files.exists(path(url)));
//         mvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().contentType("image/jpeg"));
//         mvc.perform(head(url)).andExpect(status().isOk());
//         mvc.perform(delete("/api/items/" + id + "/").header("Authorization", "Bearer " + jwt.generateToken(user())))
//                 .andExpect(status().isForbidden());
//         assertTrue(Files.exists(path(url)));
//         mvc.perform(delete("/api/items/" + id + "/").header("Authorization", token))
//                 .andExpect(status().isNoContent());
//         assertFalse(Files.exists(path(url)));
//         mvc.perform(get(url)).andExpect(status().isNotFound());
//     }

//     @Test void rolledBackCreationRemovesFileAndDatabaseRow() throws Exception {
//         User user = user();
//         String[] url = new String[1];
//         Long[] id = new Long[1];
//         var image = LocalFileStorageServiceTests.png();
//         new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
//             try {
//                 var result = service.createItem(new ItemRequest("Wallet", "Wallet", ItemType.LOST,
//                         "Library", LocalDate.of(2020, 1, 1)), image, user.getId());
//                 url[0] = result.imageUrl();
//                 id[0] = result.id();
//                 assertTrue(Files.exists(path(url[0])));
//                 status.setRollbackOnly();
//             } catch (Exception ex) { throw new RuntimeException(ex); }
//         });
//         assertFalse(Files.exists(path(url[0])));
//         assertFalse(items.existsById(id[0]));
//     }

//     @Test void rolledBackDeletionKeepsImage() throws Exception {
//         User user = user();
//         var item = service.createItem(new ItemRequest("Wallet", "Wallet", ItemType.FOUND,
//                 "Library", LocalDate.of(2020, 1, 1)), LocalFileStorageServiceTests.png(), user.getId());
//         new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
//             service.deleteItem(item.id(), user.getId());
//             status.setRollbackOnly();
//         });
//         assertTrue(items.existsById(item.id()));
//         assertTrue(Files.exists(path(item.imageUrl())));
//         service.deleteItem(item.id(), user.getId());
//     }

//     @Test void anonymousRequestsCannotUploadOrReadPrivateRoutes() throws Exception {
//         mvc.perform(multipart("/api/items/").file(LocalFileStorageServiceTests.png()))
//                 .andExpect(status().isUnauthorized());
//         for (String url : new String[]{"/api/claims/", "/api/notifications/", "/api/user/me"}) {
//             mvc.perform(get(url)).andExpect(status().isUnauthorized());
//         }
//         mvc.perform(get("/health")).andExpect(status().isOk());
//         mvc.perform(get("/health/")).andExpect(status().isOk());
//         mvc.perform(get("/api/items/")).andExpect(status().isOk());
//     }

//     @Test void fakeImageIsAClientError() throws Exception {
//         mvc.perform(multipart("/api/items/")
//                 .file(new org.springframework.mock.web.MockMultipartFile("image", "fake.png", "image/png", new byte[]{1, 2, 3}))
//                 .header("Authorization", "Bearer " + jwt.generateToken(user()))
//                 .param("title", "Wallet").param("description", "Wallet").param("type", "LOST")
//                 .param("location", "Library").param("eventDate", "2020-01-01"))
//                 .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Invalid image"));
//     }
// }
