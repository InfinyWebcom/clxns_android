package com.clxns.app.ui.main.plan.listview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.UserDetails
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ListViewFragmentBinding
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListViewFragment : Fragment() {

    private val viewModel: ListViewViewModel by viewModels()
    private var _binding: ListViewFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var listViewAdapter: ListViewAdapter
    private lateinit var tempContactList: MutableList<UserDetails>

    @Inject
    lateinit var sessionManager: SessionManager


    companion object {
        fun newInstance() = ListViewFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ListViewFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
//        setAdapter()
    }


    private fun setObserver() {
        viewModel.getMyPlanList(sessionManager.getString(Constants.TOKEN)!!)
        viewModel.response.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (!response.data?.error!!) {
                        if (response.data.total!! > 0) {
                            binding.txtNoData.visibility = View.GONE

                            binding.recyclerContacts.apply {
                                adapter = TempAdapter2(requireContext(), response.data.data)
                            }
                        } else {
                            binding.txtNoData.visibility = View.VISIBLE
                        }
                    } else {
                        requireContext().toast(response.data.title!!)
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    requireContext().toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    // show a progress bar
                }
            }
        }
    }


//    private fun setAdapter() {
//        tempContactList = mutableListOf()
//        val user1 = UserDetails()
//        user1.name = "Sharukh Shaikh"
//        user1.amount = "₹ 26,540"
//        user1.status = "Resident Shifted"
//        user1.pinCodeCityDate = "SBI"
//        user1.loanID = "565215"
//        user1.mobileNumber = "9768577945"
//        user1.address = "Nehru Nagar, Kurla (E), Mumbai - 400024"
//        val user2 = UserDetails()
//        user2.name = "Nivesh Saxena"
//        user2.amount = "₹ 50,500"
//        user2.status = "Open"
//        user2.mobileNumber = "8956784568"
//        user2.pinCodeCityDate = "Kotak Bank"
//        user2.loanID = "254125"
//        user2.address = "B1/406, 1, Green Land Apartments, J.b Nagar, Andheri (west)"
//        val user3 = UserDetails()
//        user3.name = "Aatish Kumar"
//        user3.amount = "₹ 16,000"
//        user3.status = "Locked"
//        user3.mobileNumber = "7858946577"
//        user3.pinCodeCityDate = "ICICI Bank"
//        user3.loanID = "454521"
//        user3.address = "76, Welfare Centre, Sardar Nagar-2, Opp Rawli Camp Gurudwara, Sion"
//        val user4 = UserDetails()
//        user4.name = "Pratap Singh"
//        user4.amount = "₹ 6,500"
//        user4.status = "PTP"
//        user4.pinCodeCityDate = "BOI"
//        user4.loanID = "221354"
//        user4.mobileNumber = "8586849878"
//        user4.address =
//            "A/4, 160, Bankar Bldg, Alibhai Premji Marg, Minarva Talkies/opp Dilbar Hotel, Tardeo"
//        val user5 = UserDetails()
//        user5.name = "Kiran Bose"
//        user5.amount = "₹ 85,540"
//        user5.status = "Message Left"
//        user5.pinCodeCityDate = "Allahabad Bank"
//        user5.loanID = "564152"
//        user5.mobileNumber = "9658945726"
//        user5.address = "1st Floor, 49/3, Janmabhoomi Marg, Fort"
//        val user6 = UserDetails()
//        user6.name = "Sartaj Khan"
//        user6.amount = "₹ 36,000"
//        user6.status = "Amount Dispute"
//        user6.pinCodeCityDate = "HDFC Bank"
//        user6.loanID = "023151"
//        user6.mobileNumber = "8986548465"
//        user6.address = "71-c, Ajay Apt., Sarswati Rd., Opp.gokul Icecream, Santacruz (west)"
//        val user7 = UserDetails()
//        user7.name = "Siddhant Tayade"
//        user7.amount = "₹ 65,800"
//        user7.status = "Address not found"
//        user7.pinCodeCityDate = "Bank of Maharashtra"
//        user7.loanID = "001235"
//        user7.mobileNumber = "8898494765"
//        user7.address = "101, Udyog Mandir No.2, Mogul Lane, Mahim"
//        val user8 = UserDetails()
//        user8.name = "Akhilesh Jaiswal"
//        user8.amount = "₹ 40,000"
//        user8.status = "Not available but resides here"
//        user8.pinCodeCityDate = "Axis Bank"
//        user8.loanID = "884756"
//        user8.mobileNumber = "7712453546"
//        user8.address = "Radha Bai Cottage, R.k. Vaidya Rd, Opp Plaza Theatre, Dadar (west)"
//        val user9 = UserDetails()
//        user9.name = "Priya"
//        user9.amount = "₹ 86,500"
//        user9.status = "RTP"
//        user9.pinCodeCityDate = "Canara Bank"
//        user9.loanID = "553366"
//        user9.mobileNumber = "7658492354"
//        user9.address = "2, Girnar Apt, 60 Ft Rd, Opp Anand Nagar, Bhayander (west)"
//        val user10 = UserDetails()
//        user10.name = "Rani Jain"
//        user10.amount = "₹ 30,000"
//        user10.status = "Asset Repossessed"
//        user10.pinCodeCityDate = "Central Bank of India"
//        user10.loanID = "651114"
//        user10.mobileNumber = "8855314689"
//        user10.address = "B/2, Flight View, Vakola Bridge, Santacruz(e)"
//        val user11 = UserDetails()
//        user11.name = "Faisal Khan"
//        user11.amount = "₹ 1,10,500"
//        user11.status = "Settlement"
//        user11.pinCodeCityDate = "DBS Bank India Limited"
//        user11.loanID = "009456"
//        user11.mobileNumber = "7689451255"
//        user11.address =
//            "20/21m, Nit, Fruit Garden, Railway Road, Ner Green Automobile, Faridabad, Faridabad"
//        val user12 = UserDetails()
//        user12.name = "Vinaya Dangle"
//        user12.amount = "₹ 65,950"
//        user12.status = "Cash Collected"
//        user12.pinCodeCityDate = "IDBI Bank Limited"
//        user12.loanID = "567432"
//        user12.mobileNumber = "9694856205"
//        user12.address =
//            "Radha ' Nr.sardar Bhavan Lane, Raopura Road, 'radha ', Nr.sardar Bhavan Lane, Raopura Road"
//
//        tempContactList.add(user1)
//        tempContactList.add(user2)
//        tempContactList.add(user3)
//        tempContactList.add(user4)
//        tempContactList.add(user5)
//        tempContactList.add(user6)
//        tempContactList.add(user7)
//        tempContactList.add(user8)
//        tempContactList.add(user9)
//        tempContactList.add(user10)
//        tempContactList.add(user11)
//        tempContactList.add(user12)
//        binding.recyclerContacts.apply {
//            adapter = TempAdapter2(requireContext(), tempContactList)
//        }
//
//        /*listViewFragmentBinding.recyclerContacts.adapter = listViewAdapter.withLoadStateFooter(
//            footer = HeaderFooterAdapter { retry() }
//        )
//
//        listViewAdapter.addLoadStateListener { loadState ->
//
//            Log.i(javaClass.name,"addLoadStateListener--->"+loadState.refresh)
//            if (loadState.refresh is LoadState.Loading) {
//                showProgressBar()
//            } else {
//                hideProgressBar()
//                val errorState = when {
//                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
//                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
//                    loadState.refresh is LoadState.Error -> {
//                        listViewFragmentBinding.btnRetry.visibility = View.VISIBLE
//                        loadState.refresh as LoadState.Error
//                    }
//                    else -> null
//                }
//                errorState?.let {
//                    Toast.makeText(ctx, it.error.message, Toast.LENGTH_LONG).show()
//                }
//            }
//        }*/
//    }


    private fun showProgressBar() {
        // progressDialog = CommonUtils.showProgressDialog(progressDialog, ctx)
    }

    private fun hideProgressBar() {
        //  CommonUtils.hideLoading(progressDialog)
    }

    private fun retry() {
        listViewAdapter.retry()
    }


}