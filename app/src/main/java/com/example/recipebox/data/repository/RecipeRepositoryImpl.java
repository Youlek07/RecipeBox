package com.example.recipebox.data.repository;

import com.example.recipebox.data.local.EntityMapper;
import com.example.recipebox.data.local.dao.RecipeDao;
import com.example.recipebox.data.local.entity.RecipeEntity;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.RecipeRepository;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeRepositoryImpl implements RecipeRepository {

    private final RecipeDao recipeDao;
    private final SpoonacularApi api;

    public RecipeRepositoryImpl(RecipeDao recipeDao) {
        this.recipeDao = recipeDao;
        this.api = buildRetrofit().create(SpoonacularApi.class);
    }

    public RecipeRepositoryImpl(RecipeDao recipeDao, SpoonacularApi api) {
        this.recipeDao = recipeDao;
        this.api = api;
    }


    @Override
    public List<Recipe> getAllLocalRecipes() {
        List<RecipeEntity> entities = recipeDao.getAllLocalRecipes();
        return EntityMapper.toDomainRecipeList(entities);
    }

    @Override
    public Recipe getLocalRecipeById(long id) {
        RecipeEntity e = recipeDao.getById(id);
        return EntityMapper.toDomain(e);
    }

    @Override
    public long insertRecipe(Recipe recipe) {
        RecipeEntity entity = EntityMapper.toEntity(recipe);
        entity.isLocal = true;
        return recipeDao.insert(entity);
    }

    @Override
    public void updateRecipe(Recipe recipe) {
        recipeDao.update(EntityMapper.toEntity(recipe));
    }

    @Override
    public void deleteRecipe(long id) {
        recipeDao.deleteById(id);
    }


    @Override
    public void searchRecipes(String query, RecipeCallback<List<Recipe>> callback) {
        api.searchRecipes(query, 20, true, SpoonacularApi.API_KEY)
                .enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Recipe> recipes = new java.util.ArrayList<>();
                            for (RecipeDetailResponse r : response.body().results) {
                                recipes.add(r.toDomain());
                            }
                            callback.onSuccess(recipes);
                        } else {
                            callback.onError("Server error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    @Override
    public void getRecipeDetail(int apiId, RecipeCallback<Recipe> callback) {
        api.getRecipeDetail(apiId, SpoonacularApi.API_KEY)
                .enqueue(new Callback<RecipeDetailResponse>() {
                    @Override
                    public void onResponse(Call<RecipeDetailResponse> call, Response<RecipeDetailResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body().toDomain());
                        } else {
                            callback.onError("Server error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<RecipeDetailResponse> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }


    private Retrofit buildRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(SpoonacularApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

