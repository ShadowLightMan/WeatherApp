package com.weather.weatherapp.ui.home

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.weather.weatherapp.R
import com.weather.weatherapp.databinding.FragmentHomeBinding
import com.weather.weatherapp.domain.models.UiForecast
import com.weather.weatherapp.domain.models.UiWeatherData
import com.weather.weatherapp.ui.base.BaseFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject


class HomeFragment : BaseFragment(), HomeMvpView{

    private lateinit var binding: FragmentHomeBinding

    @Inject
    @InjectPresenter
    lateinit var mainPresenter: HomePresenter
    private val forecastAdapter = ForecastAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme_NoActionBar)

        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(localInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initForecasts()
        initDrawer()
        initBackButton()
    }

    private fun initBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (binding.drawer.isDrawerOpen(GravityCompat.START)){
                    binding.drawer.closeDrawer(GravityCompat.START)
                }else{
                    requireActivity().finish()
                }
            }
        })
    }

    private fun initDrawer() {
        val toggle = ActionBarDrawerToggle(
            activity!!, binding.drawer, binding.toolbar, R.string.app_name, R.string.app_name
        )
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initForecasts() {
        binding.rvMainForecast.adapter = forecastAdapter
        binding.rvMainForecast.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }


    override fun showForecastData(forecasts: List<UiForecast>) {
        forecastAdapter.setData(forecasts)
    }

    override fun showWeatherData(weatherData: UiWeatherData) {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = weatherData.name
        binding.toolbar.setNavigationIcon(R.drawable.ic_menu)
        binding.tvMainTemp.text = weatherData.temp
        binding.tvMainShortDesc.text = weatherData.weather.description
        binding.tvMainFeelsLikeValue.text = weatherData.feelsLike
        binding.tvMainHumidityValue.text = weatherData.humidity
        binding.tvMainPressureValue.text = weatherData.pressure
        binding.tvMainWindValue.text = weatherData.wind
        binding.tvMainPressureValue.text = weatherData.pressure
        binding.tvMainVisibilityValue.text = weatherData.visibility
        binding.sunViewHomeSunLife.sunRise = weatherData.sunRise
        binding.sunViewHomeSunLife.sunSet = weatherData.sunSet
        binding.sunViewHomeSunLife.init(weatherData.minutesBetween, weatherData.currentMinute)
    }

    override fun showSunRising() {
        binding.sunViewHomeSunLife.startAnim()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_settings -> {
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            }
            else -> {
                binding.drawer.openDrawer(GravityCompat.START)
            }
        }
        return true
    }

    @ProvidePresenter
    fun providePresenter(): HomePresenter = mainPresenter

}