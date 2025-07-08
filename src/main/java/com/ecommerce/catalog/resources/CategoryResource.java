package com.ecommerce.catalog.resources;

import com.ecommerce.catalog.db.CategoryDAO;
import com.ecommerce.catalog.db.ProductDAO;
import com.ecommerce.catalog.model.Category;
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
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Path("/catalog/v2/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "categories", description = "Operations for category management")
public class CategoryResource {
    
    private final CategoryDAO categoryDAO;
    private final ProductDAO productDAO;
    
    public CategoryResource(SessionFactory sessionFactory) {
        this.categoryDAO = new CategoryDAO(sessionFactory);
        this.productDAO = new ProductDAO(sessionFactory);
    }
    
    @GET
    @ApiOperation(value = "Get all categories", notes = "Returns list of all categories")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Category.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getAllCategories(@Auth Principal user,
                                   @Context UriInfo uriInfo) {
        try {
            List<Category> categories = categoryDAO.findAll();
            long totalCount = categoryDAO.count();
            
            return Response.ok(categories)
                .header("X-Total-Count", totalCount)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to retrieve categories: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @GET
    @Path("/{id}")
    @ApiOperation(value = "Get category by ID", notes = "Returns a single category")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Category.class),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Category not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getCategory(@Auth Principal user,
                              @ApiParam(value = "Category ID", required = true) @PathParam("id") @NotNull Long id,
                              @Context UriInfo uriInfo) {
        try {
            Optional<Category> category = categoryDAO.findById(id);
            
            if (category.isPresent()) {
                return Response.ok(category.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Category not found with id: " + id + "\"}")
                    .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to retrieve category: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @POST
    @ApiOperation(value = "Create new category", notes = "Creates a new category")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Category created", response = Category.class),
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response createCategory(@Auth Principal user,
                                 @ApiParam(value = "Category data", required = true) @Valid @NotNull Category category,
                                 @Context UriInfo uriInfo) {
        try {
            Category createdCategory = categoryDAO.save(category);
            return Response.status(Response.Status.CREATED)
                .entity(createdCategory)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to create category: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @ApiOperation(value = "Update category", notes = "Updates an existing category")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Category updated", response = Category.class),
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Category not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response updateCategory(@Auth Principal user,
                                 @ApiParam(value = "Category ID", required = true) @PathParam("id") @NotNull Long id,
                                 @ApiParam(value = "Category data", required = true) @Valid @NotNull Category category,
                                 @Context UriInfo uriInfo) {
        try {
            Optional<Category> existingCategory = categoryDAO.findById(id);
            
            if (!existingCategory.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Category not found with id: " + id + "\"}")
                    .build();
            }
            
            category.setId(id);
            Category updatedCategory = categoryDAO.save(category);
            
            return Response.ok(updatedCategory).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to update category: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Delete category", notes = "Deletes a category")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Category deleted"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Category not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response deleteCategory(@Auth Principal user,
                                 @ApiParam(value = "Category ID", required = true) @PathParam("id") @NotNull Long id,
                                 @Context UriInfo uriInfo) {
        try {
            Optional<Category> category = categoryDAO.findById(id);
            
            if (!category.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Category not found with id: " + id + "\"}")
                    .build();
            }
            
            categoryDAO.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to delete category: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @GET
    @Path("/root")
    @ApiOperation(value = "Get root categories", notes = "Returns categories without parent")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Category.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getRootCategories(@Auth Principal user,
                                    @Context UriInfo uriInfo) {
        try {
            List<Category> rootCategories = categoryDAO.findRootCategories();
            
            return Response.ok(rootCategories).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to retrieve root categories: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @GET
    @Path("/{id}/children")
    @ApiOperation(value = "Get child categories", notes = "Returns subcategories of specified category")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Category.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Category not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getChildCategories(@Auth Principal user,
                                     @ApiParam(value = "Parent category ID", required = true) @PathParam("id") @NotNull Long id,
                                     @Context UriInfo uriInfo) {
        try {
            Optional<Category> parentCategory = categoryDAO.findById(id);
            
            if (!parentCategory.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Parent category not found with id: " + id + "\"}")
                    .build();
            }
            
            List<Category> childCategories = categoryDAO.findByParentId(id);
            
            return Response.ok(childCategories).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to retrieve child categories: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @GET
    @Path("/{id}/products")
    @ApiOperation(value = "Get products in category", notes = "Returns products belonging to specified category")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Product.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Category not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Response getProductsInCategory(@Auth Principal user,
                                        @ApiParam(value = "Category ID", required = true) @PathParam("id") @NotNull Long id,
                                        @ApiParam(value = "Page offset", defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
                                        @ApiParam(value = "Page limit", defaultValue = "20") @DefaultValue("20") @QueryParam("limit") int limit,
                                        @Context UriInfo uriInfo) {
        try {
            Optional<Category> category = categoryDAO.findById(id);
            
            if (!category.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Category not found with id: " + id + "\"}")
                    .build();
            }
            
            List<Product> products = productDAO.findByCategoryId(id, offset, limit);
            
            return Response.ok(products)
                .header("X-Category-Id", id)
                .header("X-Offset", offset)
                .header("X-Limit", limit)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Failed to retrieve products in category: " + e.getMessage() + "\"}")
                .build();
        }
    }
}