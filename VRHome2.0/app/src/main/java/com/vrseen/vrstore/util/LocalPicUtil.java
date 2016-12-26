package com.vrseen.vrstore.util;

import com.vrseen.vrstore.R;

/**
 * 处理本地图片
 * @author John
 *
 */
public class LocalPicUtil {

	private int[] _iScoreImageArray;
	private int[] _iTypeDefaultImgArray;
	private int[] _iTypeSelectImgArray;

	public int getStars(int id){
		if(_iScoreImageArray == null)
		{
			_iScoreImageArray = new int[]{
					R.drawable.star1,
					R.drawable.star2,
					R.drawable.star3,
					R.drawable.star4,
					R.drawable.star5,};
		}
		return _iScoreImageArray[id];
	}
	/**
	 * get app type default image
	 * @param id
	 * @return
	 */
	public int getAppTypeDefaultImage(int id)
	{
		if(_iTypeDefaultImgArray != null)
		{
			_iTypeDefaultImgArray = null;
		}

		_iTypeDefaultImgArray = new int[]{
				R.drawable.app_hot_icon, R.drawable.app_new_icon, R.drawable.app_rank_icon,
				R.drawable.app_type_icon, R.drawable.app_type_icon
		};
		return _iTypeDefaultImgArray[id];
	}

}
