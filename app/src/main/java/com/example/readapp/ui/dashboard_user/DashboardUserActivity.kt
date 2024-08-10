package com.example.readapp.ui.dashboard_user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.readapp.data.model.ModelCategory
import com.example.readapp.databinding.ActivityDashboardUserBinding
import com.example.readapp.ui.login.LoginActivity
import com.example.readapp.ui.pdf_user.PdfUserFragment
import com.example.readapp.ui.profile.ProfileActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private val viewModel: DashboardUserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        viewModel.categories.observe(this) { categories ->
            setupWithViewPagerAdapter(categories)
        }

        viewModel.fetchCategoriesWithDefaults()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupWithViewPagerAdapter(categories: List<ModelCategory>) {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        categories.forEach { category ->
            viewPagerAdapter.addFragment(
                PdfUserFragment.newInstance(category.id, category.category, category.uid),
                category.category
            )
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = viewPagerAdapter.getTitle(position)
        }.attach()
    }

    class ViewPagerAdapter(
        fragmentActivity: FragmentActivity
    ) : FragmentStateAdapter(fragmentActivity) {

        private val fragmentsList: MutableList<Fragment> = ArrayList()
        private val fragmentTitleList: MutableList<String> = ArrayList()

        override fun getItemCount(): Int = fragmentsList.size

        override fun createFragment(position: Int): Fragment = fragmentsList[position]

        fun addFragment(fragment: Fragment, title: String) {
            fragmentsList.add(fragment)
            fragmentTitleList.add(title)
        }

        fun getTitle(position: Int): String = fragmentTitleList[position]
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            binding.subTitleTv.text = "Not Logged In"
            binding.profileBtn.visibility = View.GONE
        } else {
            val email = firebaseUser.email
            binding.subTitleTv.text = email
            binding.profileBtn.visibility = View.VISIBLE
        }
    }


}


