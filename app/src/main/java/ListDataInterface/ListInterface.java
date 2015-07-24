package ListDataInterface;


import ListDataContainer.RecipesList;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Lyubomyr on 24.07.2015.
 */
public interface ListInterface {
    @GET("/search")
    public void getList(@Query("key") String key, @Query("page") int page, @Query("sort") String sort, retrofit.Callback<RecipesList> response);
}
