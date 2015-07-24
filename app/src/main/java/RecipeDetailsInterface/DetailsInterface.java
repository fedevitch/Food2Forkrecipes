package RecipeDetailsInterface;

import RecipeDataContainer.RecipeDetails;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Lyubomyr on 24.07.2015.
 */
public interface DetailsInterface {
    @GET("/get")
    public void getList(@Query("key") String key, @Query("rId") String rId, retrofit.Callback<RecipeDetails> response);
}
