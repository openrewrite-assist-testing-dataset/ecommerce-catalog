package com.ecommerce.catalog.resources;

import com.ecommerce.catalog.db.ProductDAO;
import com.ecommerce.catalog.model.Product;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.SessionFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Path("/catalog/v2/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "products", description = "Operations for product management")
public class ProductResource {
    
    private final ProductDAO productDAO;
    
    public ProductResource(SessionFactory sessionFactory) {
        this.productDAO = new ProductDAO(sessionFactory);
    }
    
    @GET
    @ApiOperation(value = "Get all products", notes = "Returns paginated list of products")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Product.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getAllProducts(@Auth Principal user,
                                 @ApiParam(value = "Page offset", defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
                                 @ApiParam(value = "Page limit", defaultValue = "20") @DefaultValue("20") @QueryParam("limit") int limit,
                                 @Context UriInfo uriInfo) {
        try {
            List<Product> products = productDAO.findAll(offset, limit);
            long totalCount = productDAO.count();
            
            return Response.ok(products)
                .header("X-Total-Count", totalCount)
                .header("X-Offset", offset)
                .header("X-Limit", limit)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to retrieve products: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @GET
    @Path("/{id}")
    @ApiOperation(value = "Get product by ID", notes = "Returns a single product")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Product.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Product not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getProduct(@Auth Principal user,
                             @ApiParam(value = "Product ID", required = true) @PathParam("id") @NotNull Long id,
                             @Context UriInfo uriInfo) {
        try {
            Optional<Product> product = productDAO.findById(id);
            
            if (product.isPresent()) {
                return Response.ok(product.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Product not found with id: " + id + "\"}")
                    .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to retrieve product: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @POST
    @ApiOperation(value = "Create new product", notes = "Creates a new product")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Product created", response = Product.class),
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response createProduct(@Auth Principal user,
                                @ApiParam(value = "Product data", required = true) @Valid @NotNull Product product,
                                @Context UriInfo uriInfo) {
        try {
            Product createdProduct = productDAO.save(product);
            return Response.status(Response.Status.CREATED)
                .entity(createdProduct)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to create product: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @ApiOperation(value = "Update product", notes = "Updates an existing product")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product updated", response = Product.class),
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Product not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response updateProduct(@Auth Principal user,
                                @ApiParam(value = "Product ID", required = true) @PathParam("id") @NotNull Long id,
                                @ApiParam(value = "Product data", required = true) @Valid @NotNull Product product,
                                @Context UriInfo uriInfo) {
        try {
            Optional<Product> existingProduct = productDAO.findById(id);
            
            if (!existingProduct.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Product not found with id: " + id + "\"}")
                    .build();
            }
            
            product.setId(id);
            Product updatedProduct = productDAO.save(product);
            
            return Response.ok(updatedProduct).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to update product: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Delete product", notes = "Deletes a product")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Product deleted"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Product not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response deleteProduct(@Auth Principal user,
                                @ApiParam(value = "Product ID", required = true) @PathParam("id") @NotNull Long id,
                                @Context UriInfo uriInfo) {
        try {
            Optional<Product> product = productDAO.findById(id);
            
            if (!product.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Product not found with id: " + id + "\"}")
                    .build();
            }
            
            productDAO.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to delete product: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @GET
    @Path("/search")
    @ApiOperation(value = "Search products", notes = "Search products by name or description")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Product.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response searchProducts(@Auth Principal user,
                                 @ApiParam(value = "Search term", required = true) @QueryParam("q") @NotNull String searchTerm,
                                 @ApiParam(value = "Page offset", defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
                                 @ApiParam(value = "Page limit", defaultValue = "20") @DefaultValue("20") @QueryParam("limit") int limit,
                                 @Context UriInfo uriInfo) {
        try {
            List<Product> products = productDAO.search(searchTerm, offset, limit);
            
            return Response.ok(products)
                .header("X-Search-Term", searchTerm)
                .header("X-Offset", offset)
                .header("X-Limit", limit)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to search products: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @GET
    @Path("/by-price")
    @ApiOperation(value = "Get products by price range", notes = "Returns products within specified price range")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Product.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getProductsByPriceRange(@Auth Principal user,
                                          @ApiParam(value = "Minimum price") @QueryParam("minPrice") BigDecimal minPrice,
                                          @ApiParam(value = "Maximum price") @QueryParam("maxPrice") BigDecimal maxPrice,
                                          @ApiParam(value = "Page offset", defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
                                          @ApiParam(value = "Page limit", defaultValue = "20") @DefaultValue("20") @QueryParam("limit") int limit,
                                          @Context UriInfo uriInfo) {
        try {
            List<Product> products = productDAO.findByPriceRange(minPrice, maxPrice, offset, limit);
            
            return Response.ok(products)
                .header("X-Min-Price", minPrice != null ? minPrice.toString() : "null")
                .header("X-Max-Price", maxPrice != null ? maxPrice.toString() : "null")
                .header("X-Offset", offset)
                .header("X-Limit", limit)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to retrieve products by price range: " + e.getMessage() + "\"}")
                .build();
        }
    }
}