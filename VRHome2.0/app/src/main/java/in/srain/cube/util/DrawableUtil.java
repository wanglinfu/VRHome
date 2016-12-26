package in.srain.cube.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by John on 16/1/15.
 */
public class DrawableUtil {

    public static StateListDrawable getPopUpListDrawable(Context context,
                                                         int paramDrawableId)
    {

        StateListDrawable stateListDrawable = new StateListDrawable();

        Drawable above = context.getResources().getDrawable(paramDrawableId);

        Drawable bleow = context.getResources().getDrawable(paramDrawableId);

        stateListDrawable.addState(new int[]{android.R.attr.state_above_anchor},above);

        stateListDrawable.addState(new int[]{},bleow);

        return  stateListDrawable;


    }
}
