package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repository.AsteroidsRepository

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    lateinit var asteroidAdapter : AsteroidAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        asteroidAdapter = AsteroidAdapter(AsteroidAdapter.ClickListener {
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })

        viewModel.asteroidList.observe(viewLifecycleOwner, Observer {
            Log.d("HIEU", "asteroid List is changed")
            asteroidAdapter.submitList(it)
        })

//        viewModel.loadImageOfTheDayDone.observe(viewLifecycleOwner, Observer {
//            viewModel.loadImageOfTheDay(context?.applicationContext as Application)
//            viewModel.onRefreshImageDone()
//        })

        binding.asteroidRecycler.adapter = asteroidAdapter
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_all_asteroid -> viewModel.asteroidListFilter(FilterList.ALL)
            R.id.show_today_asteroid -> viewModel.asteroidListFilter(FilterList.TODAY)
            R.id.show_week_asteroid -> viewModel.asteroidListFilter(FilterList.WEEK)
        }
        observeAsteroids()
        return true
    }

    private fun observeAsteroids() {
        viewModel.asteroidList.observe(viewLifecycleOwner, Observer {
            asteroidAdapter.submitList(it)
        })
    }
}
