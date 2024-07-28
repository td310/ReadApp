package com.example.readapp.di

import com.example.readapp.data.repository.category.CategoryRepository
import com.example.readapp.data.repository.pdf_admin_edit.PdfEditRepository
import com.example.readapp.data.repository.pdf_admin.PdfListRepository
import com.example.readapp.data.repository.pdf.PdfRepository
import com.example.readapp.ui.register.RegisterViewModel
import com.example.readapp.data.repository.login_register.UserRepository
import com.example.readapp.data.repository.pdf_admin_list_detail.PdfListAdminRepository
import com.example.readapp.data.repository.pdf_admin_pdf_view.PdfViewRepository
import com.example.readapp.data.repository.pdf_user.BookRepository
import com.example.readapp.data.repository.profile.ProfileRepository
import com.example.readapp.data.repository.profile_edit.ProfileEditRepository
import com.example.readapp.ui.category.AddCategoryViewModel
import com.example.readapp.ui.dashboard_user.DashboardUserViewModel
import com.example.readapp.ui.login.LoginViewModel
import com.example.readapp.ui.pdf.PdfAddViewModel
import com.example.readapp.ui.pdf_admin.PdfListAdminViewModel
import com.example.readapp.ui.pdf_admin_edit.PdfEditViewModel
import com.example.readapp.ui.pdf_admin_list_detail.PdfListDetailViewModel
import com.example.readapp.ui.pdf_admin_pdf_view.PdfViewDetailViewModel
import com.example.readapp.ui.pdf_user.BookUserViewModel
import com.example.readapp.ui.profile.ProfileViewModel
import com.example.readapp.ui.profile_edit.ProfileEditViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseStorage.getInstance() }
    single { FirebaseDatabase.getInstance().reference }

    single { CategoryRepository() }
    single { UserRepository(get(), get()) }
    single { PdfRepository() }
    single { PdfListRepository() }
    single { PdfEditRepository(get()) }
    single { PdfListAdminRepository(get(),get()) }
    single { PdfViewRepository(get(), get()) }
    single { BookRepository(get()) }
    single { ProfileRepository(get(), get()) }
    single { ProfileEditRepository(get(), get(), get()) }

    viewModel { ProfileEditViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { DashboardUserViewModel(get()) }
    viewModel { BookUserViewModel(get()) }
    viewModel { PdfViewDetailViewModel(get()) }
    viewModel { PdfListDetailViewModel(get()) }
    viewModel { PdfEditViewModel(get()) }
    viewModel { PdfListAdminViewModel(get()) }
    viewModel { PdfAddViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { AddCategoryViewModel(get()) }
    viewModel { LoginViewModel(get()) }
}
