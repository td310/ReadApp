package com.example.readapp.ui.dashboard_user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.readapp.data.model.ModelCategory
import com.example.readapp.databinding.ActivityDashboardUserBinding
import com.example.readapp.ui.login.LoginActivity
import com.example.readapp.ui.pdf_user.PdfUserFragment
import com.example.readapp.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private val viewModel: DashboardUserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.categories.observe(this) { categories ->
            setupWithViewPagerAdapter(binding.viewPager, categories)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        viewModel.fetchCategoriesWithDefaults()
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupWithViewPagerAdapter(viewPager: ViewPager, categories: List<ModelCategory>) {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)

        categories.forEach { category ->
            viewPagerAdapter.addFragment(
                PdfUserFragment.newInstance(category.id, category.category, category.uid),
                category.category
            )
        }

        viewPager.adapter = viewPagerAdapter
    }

    class ViewPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
        private val fragmentsList: ArrayList<PdfUserFragment> = ArrayList()
        private val fragmentTitleList: ArrayList<String> = ArrayList()

        override fun getCount(): Int {
            return fragmentsList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentsList[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList[position]
        }

        fun addFragment(fragment: PdfUserFragment, title: String) {
            fragmentsList.add(fragment)
            fragmentTitleList.add(title)
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            //not login, user can stay in user dashboard without login
            binding.subTitleTv.text = "Not Logged In"
            binding.profileBtn.visibility = View.GONE
        } else {
            // loggin, get & show user info
            val email = firebaseUser.email
            binding.subTitleTv.text = email
            binding.profileBtn.visibility = View.VISIBLE
        }
    }
}
