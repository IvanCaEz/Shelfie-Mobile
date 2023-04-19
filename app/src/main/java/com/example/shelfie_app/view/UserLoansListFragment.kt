package com.example.shelfie_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentUserLoansListBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.view.adapters.BookOnClickListener
import com.example.shelfie_app.view.adapters.LoansAdapter
import com.example.shelfie_app.view.adapters.ReviewAdapter
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking

class UserLoansListFragment : Fragment(), BookOnClickListener {
    private lateinit var binding: FragmentUserLoansListBinding
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var loansAdapter: LoansAdapter
    val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentUserLoansListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(context)
        viewModel.getUserLoans("1")



        viewModel.userActiveBookLoans.observe(viewLifecycleOwner){ activeBookLoans ->
            println(viewModel.userActiveBookLoans.value!!.size)
            runBlocking {
                viewModel.getBooksByBookLoan(activeBookLoans)
                activeBookLoans.forEach { bookLoan  ->
                    viewModel.getBookCover(bookLoan.idBook)
                }
            }
            viewModel.loanedBooks.observe(viewLifecycleOwner){ loanedBooks ->
                Handler(Looper.getMainLooper()).postDelayed({
                    loansAdapter = LoansAdapter(activeBookLoans, loanedBooks,this, viewModel)
                    setupRecyclerView()
                    binding.shimmerViewContainer.visibility = View.INVISIBLE
                }, 1000)
            }
        }
    }
    private fun setupRecyclerView() {
        val manager =  LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = loansAdapter
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.visibility = View.VISIBLE

    }

    override fun onClick(book: Book) {
        TODO("Not yet implemented")
    }
}