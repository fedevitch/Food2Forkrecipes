package SearchInterface;

import ListDataContainer.RecipesList;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Lyubomyr on 24.07.2015.
 */
public interface SearchQuery {
    @GET("/search")
    public void getList(@Query("key") String key, @Query("q") String SearchQuery, retrofit.Callback<RecipesList> response);
}
