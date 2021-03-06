package com.example.resdelivery.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resdelivery.R
import com.example.resdelivery.databinding.CartFragmentBinding
import com.example.resdelivery.models.Meal
import org.koin.android.viewmodel.ext.android.getViewModel

class CartFragment : Fragment(), View.OnClickListener, CartAdapter.OnItemClickListener {


    private lateinit var binding: CartFragmentBinding
    private var carts: MutableList<Meal> = mutableListOf()
    private val adapter = CartAdapter(carts, this)
    private lateinit var viewModel : CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.cart_fragment, container, false)
        binding.toolBar.setupWithNavController(this.findNavController())
        animateCart()
        setUpRecyclerView()
        subscribeToObserver()
        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.completeButton.setOnClickListener(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adapter
    }

    private fun subscribeToObserver() {
        viewModel = getViewModel()
        viewModel.showEmptyCart.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.INVISIBLE
                showEmptyCart()
            } else {
                hideEmptyCart()
            }
        })
        viewModel.cartItems.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visibility = View.INVISIBLE
            adapter.setCartItems(it as MutableList<Meal>)
            carts = it
        })
    }

    private fun animateCart() {
        binding.emptyCartAnimation.setMinAndMaxProgress(0.0f, 0.5f)
    }

    override fun onItemClick(position: Int) {
        this.findNavController().navigate(
            CartFragmentDirections.actionCartFragmentToDetailFragment(carts[position].id)
        )
    }

    private fun showEmptyCart() {
        binding.completeButton.visibility = View.INVISIBLE
        binding.emptyCartAnimation.visibility = View.VISIBLE
        binding.emptyCartText.visibility = View.VISIBLE
    }

    private fun hideEmptyCart() {
        binding.emptyCartAnimation.visibility = View.INVISIBLE
        binding.emptyCartText.visibility = View.INVISIBLE
    }

    override fun onCloseImgClick(position: Int) {
        viewModel.removeFromCart(carts[position])
        carts.removeAt(position)
        adapter.notifyItemRemoved(position)
        if (carts.isEmpty()) {
            showEmptyCart()
            animateCart()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.complete_button -> {
                this.findNavController().navigate(
                    CartFragmentDirections.actionCartFragmentToMapFragment()
                )
            }
        }
    }

}
