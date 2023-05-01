package com.example.shelfie_app.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.models.BookLoan
import com.example.shelfie_app.databinding.FragmentUserLoansListBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.view.listeners.BookOnClickListener
import com.example.shelfie_app.view.adapters.LoansAdapter
import com.example.shelfie_app.view.listeners.BookLoanOnClickListener
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserLoansListFragment : Fragment(), BookLoanOnClickListener {
    private lateinit var binding: FragmentUserLoansListBinding
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var loansAdapter: LoansAdapter
    val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserLoansListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(context)

        viewModel.userActiveBookLoans.observe(viewLifecycleOwner) { activeBookLoans ->
            println(viewModel.userActiveBookLoans.value!!.size)
            viewModel.getBooksByBookLoan(activeBookLoans)
            activeBookLoans.forEach { bookLoan ->
                viewModel.getBookCover(bookLoan.idBook)
            }

            viewModel.loanedBooks.observe(viewLifecycleOwner) { loanedBooks ->
                Handler(Looper.getMainLooper()).postDelayed({
                    if (viewModel.userActiveBookLoans.value!!.isNotEmpty()){
                        binding.noLoansTV.visibility = View.INVISIBLE
                    } else binding.noLoansTV.visibility = View.VISIBLE
                    loansAdapter = LoansAdapter(activeBookLoans, loanedBooks, this, viewModel)
                    setupRecyclerView()
                    binding.shimmerViewContainer.visibility = View.INVISIBLE

                }, 1000)
            }
        }
    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = loansAdapter
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.visibility = View.VISIBLE

    }

    override fun onExtendTimeListener(bookLoan: BookLoan) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val oldReturnDate = LocalDate.parse(bookLoan.endDate, formatter)
        val newReturnDate = oldReturnDate.plusDays(30)
        val newReturnDateString = newReturnDate.format(formatter)

        val extendedBookLoan = BookLoan(bookLoan.idUser, bookLoan.idBook,bookLoan.startDate, newReturnDateString)

        viewModel.putBookLoan(bookLoan.idUser,extendedBookLoan)

        Toast.makeText(requireContext(), "Extended time by 30 days", Toast.LENGTH_SHORT).show()
        viewModel.getUserLoans(bookLoan.idUser)
    }

    override fun onReviewListener(book: Book) {
        val toReview = UserProfileFragmentDirections.actionUserProfileFragmentToMakeReviewFragment(book.idBook, "loan")
        findNavController().navigate(toReview)
    }

    override fun onReturnListener(bookLoan: BookLoan) {
        viewModel.deleteBookLoan(bookLoan.idUser,bookLoan.idBook)
        viewModel.getUserLoans(bookLoan.idUser)
    }
}