package com.black.amoledwallpapers.screens.image

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.util.ImageUtil
import com.black.amoledwallpapers.util.ImgurUtil
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : Fragment() {
    private var viewModel: ImageViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(ImageViewModel::class.java) }
        loading.visibility = View.VISIBLE
        viewModel?.loadImage(arguments?.getString(PARAMS_ID) ?: "") {
            ImageUtil.loadImage(
                url = ImgurUtil.createUrl(it.url),
                imageView = photoView,
                color = Color.parseColor(it.color)
            ) { loading.visibility = View.GONE }
        }
    }

    companion object {
        private const val PARAMS_ID = "id"

        fun newInstance(id: String): Fragment {
            val data = Bundle()
            data.putString(PARAMS_ID, id)
            val fragment = ImageFragment()
            fragment.arguments = data
            return fragment
        }
    }


}